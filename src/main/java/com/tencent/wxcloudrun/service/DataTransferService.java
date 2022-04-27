package com.tencent.wxcloudrun.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Service
public class DataTransferService {

    public String convertData(int id, String input) {
        //水位（压力）
        //电量
        //通信
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        JSONObject resultJson = new JSONObject();
        resultJson.put("id", id);
        JSONObject json = JSONObject.parseObject(input);
        JSONArray deviceStatusList = json.getJSONArray("deviceStatusList");
        deviceStatusList.stream().forEach(obj -> {
            JSONObject object = ((JSONObject) obj);
            String datasetId = object.getString("datasetId");
            if (datasetId.equals("Batt") || datasetId.equals("Pressure") || datasetId.equals("Signal")) {
                Date date = new Date(object.getLongValue("timestamp"));
                String dataString = dateFormat.format(date);// 生成格式化的日期字符串
                object.put("timestamp", dataString);
                resultJson.put(datasetId, obj);
            }
        });
        return resultJson.toJSONString();
    }
}
