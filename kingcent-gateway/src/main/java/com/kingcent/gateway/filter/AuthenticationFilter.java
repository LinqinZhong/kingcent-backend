package com.kingcent.gateway.filter;

import cn.hutool.core.text.AntPathMatcher;
import com.kingcent.gateway.config.AuthenticationConfig;
import com.kingcent.gateway.service.AuthService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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


    /**
     * 未鉴权，终止访问
     */
    private Mono<Void> unAuthentication(ServerWebExchange exchange){
        //设置状态码 未授权401
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        //终止输出访问
        return exchange.getResponse().setComplete();
    }

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        //设置跨域
        ServerHttpRequest request = exchange.getRequest();

        ServerHttpResponse httpResponse = exchange.getResponse();
        HttpHeaders headers = httpResponse.getHeaders();

        headers.add("Access-control-allow-origin","*");
        headers.add("Access-control-allow-methods","*");
        headers.add("Access-control-allow-headers","*");
        headers.add("Access-control-allow-credentials","true");


        //处理预检
        if (request.getMethod().equals(HttpMethod.OPTIONS)) {
            return exchange.getResponse().setComplete();
        }


        //放行公共路径
        String path = exchange.getRequest().getPath().value();
        for (String p : authenticationConfig.getIgnorePath()) {
            if (pathMatcher.match(p, path)) {
                return chain.filter(exchange);
            }
        }

        String authorization = request.getHeaders().getFirst("authorization");
        if(authorization == null) return unAuthentication(exchange);
        Long uid = authService.check(authorization);
        if(uid == -1L) return unAuthentication(exchange);

        AtomicReference<ServerWebExchange> mutableExchange = new AtomicReference<>();
        Thread t = new Thread(()->{
            ServerHttpRequest mutableReq = request.mutate()
                    .header("uid", uid+"")
                    .build();
            mutableExchange.set(exchange.mutate().request(mutableReq).build());
        });
        t.start();
        t.join();
        return chain.filter(mutableExchange.get());
    }

    /*
    数字越小，优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}