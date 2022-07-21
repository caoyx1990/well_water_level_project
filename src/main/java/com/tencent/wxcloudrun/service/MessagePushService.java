package com.tencent.wxcloudrun.service;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MessagePushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagePushService.class);
    private static final String SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=";

    public String push(String token, String openid, int id, String time, double value) {
        RestTemplate restTemplate = new RestTemplate();
        String url = SEND_URL + token;
        JSONObject object = new JSONObject();
//        object.put("touser", "ouB0E45WMDCrQcrfuc5n-YEzDDpI");
        object.put("touser", openid);
        object.put("template_id", "YQvPYAx79NAox3o2zyjEmDLKn05F4feOIabb6FeJeS8");
        object.put("page", "/pages/index/index");
        object.put("miniprogram_state", "formal");
        object.put("lang", "zh_CN");
//        设备编号
//        {{number1.DATA}}
//
//        报警时间
//        {{time3.DATA}}
//
//        触发数据
//        {{character_string4.DATA}}
        JSONObject data = new JSONObject();
        JSONObject number1 = new JSONObject();
        number1.put("value", id);
        data.put("number1", number1);

        JSONObject time3 = new JSONObject();
        time3.put("value", time);
        data.put("time3", time3);

        JSONObject character_string4 = new JSONObject();
        character_string4.put("value", value);
        data.put("character_string4", character_string4);
        object.put("data", data);

        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(url, object, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        LOGGER.info("Get message send response: " + body.toString());
        return body.toString();
    }


}
