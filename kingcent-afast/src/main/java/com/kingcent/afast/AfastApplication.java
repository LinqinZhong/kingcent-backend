package com.kingcent.afast;

import com.kingcent.afast.utils.CommandUtil;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.io.IOException;

/**
 * @author rainkyzhong
 * @date 2024/10/12 19:29
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.kingcent.afast.mapper")
public class AfastApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(AfastApplication.class, args);

//        String origin = "git@gitee.com:rainkyzhong/parkchoice.git";
//        String path = "C:/Users/rainkyzhong/Desktop/parkchoice";
////        Git git = GitUtil.open(path);
////        if(git != null){
////            System.out.println("拉取");
////            git.pull();
////        } else {
////            //仓库不存在
////            System.out.println("克隆代码");
////            GitUtil.clone(origin, path, "C:/Users/rainkyzhong/Desktop/server/afast");
////        }
//
//        CommandUtil.exec(path, new String[]{
//                path+"/node_modules/.bin/vue-cli-service.cmd",
//                "serve"
//        }, new CommandUtil.CommandListener() {
//            @Override
//            public void onMessage(String msg) {
//                System.out.println(msg);
//            }
//
//            @Override
//            public void onError(String err) {
//                System.out.println(err);
//            }
//        });
    }
}
