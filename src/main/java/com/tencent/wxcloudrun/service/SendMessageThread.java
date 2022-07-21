package com.tencent.wxcloudrun.service;

import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class SendMessageThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageThread.class);
    private static final long TIME_STEP = 5 * 60 * 1000;

    private List<String> openidList;

    private DataService dataService;

    private MessagePushService messagePushService;
    private AccessTokenService accessTokenService;

    public SendMessageThread(List<String> openidList, DataService dataService, MessagePushService messagePushService) {
        this.openidList = openidList;
        this.dataService = dataService;
        this.messagePushService = messagePushService;
        this.accessTokenService = new AccessTokenService();
    }

    @Override
    public void run() {
        LOGGER.info("Send Message Thread start!");
        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        while (true) {
            List<String> idList = this.openidList.stream().distinct().collect(Collectors.toList());
            if (endTime - startTime >= TIME_STEP) {
                startTime = endTime;
                try {
                    JSONObject device1 = dataService.getDeviceDataByAPI(1, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830350);
                    if (device1 != null) {
                        JSONObject pressure = device1.getJSONObject("Pressure");
                        double value = pressure.getDoubleValue("value") - 7;
                        pushMessage(1, pressure.getString("timestamp"), value, idList);
                    }

                    JSONObject device2 = dataService.getDeviceDataByAPI(2, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830368);
                    if (device2 != null) {
                        JSONObject pressure  = device2.getJSONObject("Pressure");
                        double value = pressure.getDoubleValue("value") - 7;
                        pushMessage(2, pressure.getString("timestamp"), value, idList);
                    }

                    JSONObject device3 = dataService.getDeviceDataByAPI(3, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830376);
                    if (device3 != null) {
                        JSONObject pressure = device3.getJSONObject("Pressure");
                        double value = pressure.getDoubleValue("value") - 12;
                        pushMessage(3, pressure.getString("timestamp"), value, idList);
                    }
                } catch (Exception e) {
                    LOGGER.error("error send message.", e);
                }
            }
            try {
                Thread.sleep(TIME_STEP);
            } catch (InterruptedException e) {
                LOGGER.error("error", e);
            }
            endTime = System.currentTimeMillis();
        }
    }

    private void pushMessage(int id, String time, double value, List<String> idList) {
        if (value > -5) {
            for (String openid: idList) {
                messagePushService.push(accessTokenService.getAccessToken(), openid, id, time, value);
            }
        }
    }
}
