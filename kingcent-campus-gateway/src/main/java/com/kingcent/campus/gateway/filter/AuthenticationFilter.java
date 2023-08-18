package com.kingcent.campus.gateway.filter;
 
import cn.hutool.core.text.AntPathMatcher;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.kingcent.campus.common.entity.constant.LoginType;
import com.kingcent.campus.gateway.config.AuthenticationConfig;
import com.kingcent.campus.gateway.service.AuthService;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 鉴权过滤器
 * @author rainkyzhong
 * @date 2023/06/15
 */
@Component
@Slf4j
public class AuthenticationFilter implements WebFilter, Ordered {

    //路径匹配器
    private final static AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private AuthenticationConfig authenticationConfig;

    @Autowired
    private AuthService authService;

    @Autowired
    private RestTemplate restTemplate;


    private Mono<Void> handleFile(ServerWebExchange exchange, WebFilterChain chain, String lid, String sign, String path, LoginType loginType) {
        return chain.filter(exchange);
    }

    /**
     * 处理form数据
     * 由于form数据只能读取一次，所以读完要写回去
     * 不然放行后就不会调用下面的接口了
     */
    private Mono<Void> handleForm(ServerWebExchange exchange, WebFilterChain chain, String lid, String sign, String path, LoginType loginType){
        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        return serverRequest.formData().flatMap(
                body -> {
                    Long userId = authService.check(Long.valueOf(lid), sign, path, getKeyValue(body), loginType);
                    if(userId == -1L){
                        return unAuthentication(exchange);
                    }
                    //创建BodyInserter修改请求体
                    var bodyInserter = BodyInserters.fromPublisher(Mono.just(body), MultiValueMap.class);
                    HttpHeaders headers = new HttpHeaders();
                    headers.putAll(exchange.getRequest().getHeaders());
                    headers.remove(HttpHeaders.CONTENT_LENGTH);
                    //创建CachedBodyOutputMessage并且把请求param加入
                    CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
                    return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return outputMessage.getBody();
                            }

                            @Override
                            public HttpHeaders getHeaders() {
                                HttpHeaders httpHeaders = new HttpHeaders();
                                httpHeaders.putAll(super.getHeaders());
                                httpHeaders.add("uid", userId+"");
                                //由于修改了请求体的body，导致content-length长度不确定，因此需要删除原先的content-length
                                httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
                                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                                return httpHeaders;
                            }
                        };
                        return chain.filter(exchange.mutate().request(decorator).build());
                    }));
                }
        );
    }

    /**
     * 处理json请求
     * json数据只能读取一次，所以读完要写回去
     * 不然放行后就不会调用下面的接口了
     */
    private Mono<Void> handleJson(ServerWebExchange exchange, WebFilterChain chain, String lid, String sign, String path, LoginType loginType) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .defaultIfEmpty(new byte[0])
                .flatMap(bodyBytes -> {
                    String requestBody = new String(bodyBytes);
                    Long userId = authService.check(Long.valueOf(lid), sign, path, requestBody, loginType);
                    if(userId == -1L) return exchange.getResponse().setComplete();

                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
                            DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bodyBytes.length);
                            buffer.write(bodyBytes);
                            return Flux.just(buffer);
                        }

                        //复写getHeaders方法
                        @Override
                        public HttpHeaders getHeaders() {
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.putAll(super.getHeaders());
                            httpHeaders.add("uid", userId+"");
                            //由于修改了请求体的body，导致content-length长度不确定，因此需要删除原先的content-length
                            httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
                            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                            return httpHeaders;
                        }
                    };

                    decorator.getBody().flatMap(buffer -> {
                        System.out.println("数据"+buffer);
                        return Mono.just(buffer);
                    });
                    return chain.filter(exchange.mutate().request(decorator).build());

                });

    }

    /**
     * 处理query数据
     */
    private Mono<Void> handleQuery(ServerWebExchange exchange, WebFilterChain chain, String lid, String sign, String path, LoginType loginType) {
        Long userId = authService.check(Long.valueOf(lid), sign, path, getKeyValue(exchange.getRequest().getQueryParams()), loginType);
        if(userId != -1){
            exchange.getRequest().mutate().headers(httpHeaders -> {
                //禁止前端自主传入uid，如果有把它清空
                while (httpHeaders.containsKey("uid")){
                    httpHeaders.remove("uid");
                }
                //添加uid
                httpHeaders.add("uid", userId + "");
            });
            //放行
            return chain.filter(exchange);
        }
        return unAuthentication(exchange);
    }



    /**
     * 未鉴权，终止访问
     */
    private Mono<Void> unAuthentication(ServerWebExchange exchange){
        //设置状态码 未授权401
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        //终止输出访问
        return exchange.getResponse().setComplete();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        //设置跨域
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse httpResponse = exchange.getResponse();
        HttpHeaders headers = httpResponse.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authentication, Uid, Lid, Login-Type");
        headers.set("Access-Control-Allow-Methods", "GET, PUT, OPTIONS, POST, DELETE");


        //放行公共路径
        String path = exchange.getRequest().getPath().value();
        for (String p : authenticationConfig.getIgnorePath()) {
            if (pathMatcher.match(p, path)) {
                return chain.filter(exchange);
            }
        }

        //处理预检
        if (request.getMethod().equals(HttpMethod.OPTIONS)) {
            return exchange.getResponse().setComplete();
        }

        //获取签名
        String lid = request.getHeaders().getFirst("lid");
        String sign = request.getHeaders().getFirst("authentication");

        //获取登录类型
        String loginType = request.getHeaders().getFirst("login-type");

        if (!StringUtils.isBlank(lid) || !StringUtils.isBlank(sign)) {
            //文件数据
            if (request.getHeaders().getContentType() != null && request.getHeaders().getContentType().includes(MediaType.MULTIPART_FORM_DATA))
                return handleFile(exchange, chain, lid, sign, path, LoginType.valueOf(loginType));
                //JSON数据
            else if (MediaType.APPLICATION_JSON.equals(request.getHeaders().getContentType()))
                return handleJson(exchange, chain, lid, sign, path, LoginType.valueOf(loginType));
                //form数据
            else if (MediaType.APPLICATION_FORM_URLENCODED.equals(request.getHeaders().getContentType()))
                return handleForm(exchange, chain, lid, sign, path, LoginType.valueOf(loginType));
                //query数据
            else return handleQuery(exchange, chain, lid, sign, path, LoginType.valueOf(loginType));
        }
        return unAuthentication(exchange);
    }


    /**
     * 将请求参数排序后生成键值对
     * @param map 请求参数
     * @return 参数键值对
     */
    private String getKeyValue(MultiValueMap<String,?> map){
        SortedMap<String, List<?>> sortedMap = new TreeMap<>(map);
        return sortedMap.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    List<?> values = entry.getValue();
                    return values.stream().map(value -> key + "=" + value).collect(Collectors.toList());
                })
                .flatMap(Collection::stream)
                .collect(Collectors.joining("&"));
    }
 
    /*
    数字越小，优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}