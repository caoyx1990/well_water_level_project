package com.tencent.wxcloudrun.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Service
public class DataTransferService {

    public JSONObject convertData(int id, String input) {
        //水位（压力）
        //电量
        //通信
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        JSONObject resultJson = new JSONObject();
        resultJson.put("id", id);
        JSONObject json = JSONObject.parseObject(input);
        JSONArray deviceStatusList = json.getJSONArray("deviceStatusList");
        JSONObject dotP = (JSONObject) deviceStatusList.stream()
                .filter(obj -> ((JSONObject) obj).getString("datasetId").equalsIgnoreCase("Dot_P"))
                .findAny().get();
        int num = dotP.getInteger("value");
        double defaultValue = 1;
        for (int i = 0; i < num; i++) {
            defaultValue *= 0.1;
        }
        double finalDefaultValue = defaultValue;
        deviceStatusList.stream().forEach(obj -> {
            JSONObject object = ((JSONObject) obj);
            String datasetId = object.getString("datasetId");
            if (datasetId.equals("Pressure")) {
                int pressureValue = object.getInteger("value");
                BigDecimal bg = new BigDecimal(pressureValue);
                bg = bg.multiply(BigDecimal.valueOf(finalDefaultValue));
                bg = bg.setScale(num, RoundingMode.CEILING);
                object.put("value", bg.doubleValue());
                Date date = new Date(object.getLongValue("timestamp"));
                String dataString = dateFormat.format(date);// 生成格式化的日期字符串
                object.put("timestamp", dataString);
                resultJson.put(datasetId, object);
            }
            if (datasetId.equals("Batt") || datasetId.equals("Signal")) {
                Date date = new Date(object.getLongValue("timestamp"));
                String dataString = dateFormat.format(date);// 生成格式化的日期字符串
                object.put("timestamp", dataString);
                resultJson.put(datasetId, object);
            }
        });
        return resultJson;
    }
}
