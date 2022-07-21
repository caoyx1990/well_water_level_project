package com.tencent.wxcloudrun.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class DataTransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransferService.class);
    public JSONObject convertData(int id, String input) {
        //水位（压力）
        //电量
        //通信
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        JSONObject resultJson = new JSONObject();
        JSONObject json = JSONObject.parseObject(input);
        JSONArray deviceStatusList = json.getJSONArray("deviceStatusList");
        if (deviceStatusList == null) {
            return null;
        }
        resultJson.put("id", id);
        int num = 3;
        if (deviceStatusList != null) {
            JSONObject dotP = (JSONObject) deviceStatusList.stream()
                    .filter(obj -> ((JSONObject) obj).getString("datasetId").equalsIgnoreCase("Dot_P"))
                    .findAny().get();
            num = dotP.getInteger("value");
        }
        double defaultValue = 1;
        for (int i = 0; i < num; i++) {
            defaultValue *= 0.1;
        }
        double finalDefaultValue = defaultValue;
        int finalNum = num;
        deviceStatusList.stream().forEach(obj -> {
            JSONObject object = ((JSONObject) obj);
            String datasetId = object.getString("datasetId");
            if (datasetId.equals("Pressure")) {
                int pressureValue = object.getInteger("value");
                BigDecimal bg = new BigDecimal(pressureValue);
                bg = bg.multiply(BigDecimal.valueOf(finalDefaultValue));
                bg = bg.setScale(finalNum, RoundingMode.CEILING);
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
        LOGGER.info("convertData: " + resultJson.toJSONString());
        return resultJson;
    }
}
