package com.tencent.wxcloudrun.service;

import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SendMessageThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageThread.class);
    private static final long TIME_STEP = 5 * 60 * 1000;

    private List<String> openidList;

    private DataService dataService;

    private MessagePushService messagePushService;
    private AccessTokenService accessTokenService;

    public SendMessageThread(List<String> openidList, DataService dataService, MessagePushService messagePushService) {
        this.openidList = openidList;
        this.openidList.add("ouB0E45WMDCrQcrfuc5n-YEzDDpI");
        this.dataService = dataService;
        this.messagePushService = messagePushService;
        this.accessTokenService = new AccessTokenService();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        while (true) {
            if (endTime - startTime >= TIME_STEP) {
                startTime = endTime;
                try {
                    JSONObject device1 = dataService.getDeviceDataByAPI(1, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830350);
                    JSONObject pressure = device1.getJSONObject("Pressure");
                    long value = pressure.getLong("value") - 7;
                    pushMessage(1, pressure.getString("timestamp"), value);

                    JSONObject device2 = dataService.getDeviceDataByAPI(2, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830368);
                    pressure = device2.getJSONObject("Pressure");
                    value = pressure.getLong("value") - 7;
                    pushMessage(2, pressure.getString("timestamp"), value);

                    JSONObject device3 = dataService.getDeviceDataByAPI(3, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830376);
                    pressure = device3.getJSONObject("Pressure");
                    value = pressure.getLong("value") - 12;
                    pushMessage(3, pressure.getString("timestamp"), value);
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

    private void pushMessage(int id, String time, long value) {
        if (value < 0) {
            for (String openid: openidList) {
                messagePushService.push(accessTokenService.getAccessToken(), openid, id, time, value);
            }
        }
    }
}
