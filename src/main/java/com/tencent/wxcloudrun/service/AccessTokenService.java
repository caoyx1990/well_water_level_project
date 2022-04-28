package com.tencent.wxcloudrun.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AccessTokenService {
    private static final String TOKEN_URL =  "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxac0bbf1996e4685f&secret=09ad941d7aebeccf9d527bf3fbdcd88d";

    public static long GET_TIME = 0;

    private String token;

    public String getAccessToken() {
        long time = System.currentTimeMillis();
        if (GET_TIME == 0 || time - GET_TIME > 120 * 60 * 1000 ) {
            GET_TIME = time;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JSONObject> forEntity = restTemplate.getForEntity(TOKEN_URL, JSONObject.class);
            token = forEntity.getBody().getString("access_token");
        }
        return token;
    }
}
