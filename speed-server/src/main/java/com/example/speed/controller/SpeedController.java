package com.example.speed.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: 吴青珂
 * @Date: 2021/01/29/20:09
 * @Description:
 */
@RestController
@Slf4j
@CrossOrigin("*")
public class SpeedController {
    @PostMapping("/uploadSpeedTest")
    public Map<String,Object> uploadSpeedTest(HttpServletRequest request){
        /*log.info("http从客户端发起的时间戳->[{}]",clientTime);
        long serverTime = new Date().getTime() ;
        log.info("http请求到客户端的时间戳->[{}]",serverTime);
        double diffTime = (double)serverTime - clientTime; //客户端到服务端的延迟（单位ms）
        log.info("客户端到服务端的延迟->[{}ms]",diffTime);
        System.out.println(diffTime/1000);
        double contentLengthLong = request.getContentLengthLong(); //http请求的长度（单位为Byte）
        double speed = contentLengthLong / (diffTime / 1000); //每秒钟发送的多少字节（单位Byte/s）
        log.info("（标准算法）客户端向服务端发送请求的速率->[{}Byte/s]",speed);
        log.info("（国家带宽算法）客户端向服务端发送请求的速率->[{}Bps]",speed * 8);
        log.info("（国家带宽算法）客户端向服务端发送请求的速率->[{}KBps]",(speed * 8) / 1024);
        log.info("（国家带宽算法）客户端向服务端发送请求的速率->[{}MBps]",(speed * 8) / 1024 / 1024);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("data",speed);*/
        long contentLength = request.getContentLengthLong();
        long endTime = new Date().getTime();
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("contentLength",contentLength);
        log.info("contentLength->[{} Byte]",contentLength);
        dataMap.put("endTime",endTime);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("msg","客户端到服务端请求");
        resMap.put("data",dataMap);
        return resMap;
    }
}
