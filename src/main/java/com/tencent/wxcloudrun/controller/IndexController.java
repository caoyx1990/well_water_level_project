package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.service.*;
import com.tencent.wxcloudrun.utils.Constant;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * index控制器
 */
@RestController
public class IndexController {
  private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

  @Autowired
  private DataService dataService;

  @Autowired
  private MessagePushService messagePushService;

  @Autowired
  private OpenidService openidService;

  @Autowired
  private AlertService alertService;

  @PostConstruct
  public void init() {
//    readFileToGetOpenids();
    SendMessageThread sendMessageThread = new SendMessageThread(dataService, messagePushService,
            openidService, alertService);
    Thread thread = new Thread(sendMessageThread);
    thread.start();
  }

  @PostMapping("/openid/get")
  public String getUserInfo(@RequestBody String code) throws Exception {
    LOGGER.info("/openid/get, code" + code);
    String url = "https://api.weixin.qq.com/sns/jscode2session";
    url += "?appid=wxac0bbf1996e4685f";//自己的appid
    url += "&secret=09ad941d7aebeccf9d527bf3fbdcd88d";//自己的appSecret
    url += "&js_code=" + code;
    url += "&grant_type=authorization_code";
    url += "&connect_redirect=1";
    String res = null;
    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    // DefaultHttpClient();
    HttpGet httpget = new HttpGet(url);    //GET方式
    CloseableHttpResponse response = null;
    // 配置信息
    RequestConfig requestConfig = RequestConfig.custom()          // 设置连接超时时间(单位毫秒)
            .setConnectTimeout(5000)                    // 设置请求超时时间(单位毫秒)
            .setConnectionRequestTimeout(5000)             // socket读写超时时间(单位毫秒)
            .setSocketTimeout(5000)                    // 设置是否允许重定向(默认为true)
            .setRedirectsEnabled(false).build();           // 将上面的配置信息 运用到这个Get请求里
    httpget.setConfig(requestConfig);                         // 由客户端执行(发送)Get请求
    response = httpClient.execute(httpget);                   // 从响应模型中获取响应实体
    HttpEntity responseEntity = response.getEntity();
    LOGGER.info("响应状态为:" + response.getStatusLine());
    if (responseEntity != null) {
      res = EntityUtils.toString(responseEntity);
      LOGGER.info("响应内容长度为:" + responseEntity.getContentLength());
      LOGGER.info("响应内容为:" + res);
    }
    // 释放资源
    if (httpClient != null) {
      httpClient.close();
    }
    if (response != null) {
      response.close();
    }
    JSONObject jo = JSON.parseObject(res);
    String openid = jo.getString("openid");
    LOGGER.info("openid" + openid);
    return openid;
  }

  @GetMapping("/device/data/history/{deviceId}")
  public String getDeviceHistoryData(@PathVariable String deviceId) throws Exception {
    LOGGER.info("/device/data/history/" + deviceId);
    return dataService.getDeviceStatusHisInPage(Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830350).toJSONString();
  }

  @GetMapping("/device/data/{deviceId}")
  public String getDeviceData(@PathVariable String deviceId) throws Exception {
    if ("350".equalsIgnoreCase(deviceId)) {
      return dataService.getDeviceDataByAPI(1, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830350).toJSONString();
    } else if ("368".equalsIgnoreCase(deviceId)) {
      return dataService.getDeviceDataByAPI(2, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830368).toJSONString();
    } else if ("376".equalsIgnoreCase(deviceId)) {
      return dataService.getDeviceDataByAPI(3, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830376).toJSONString();
    }
    return "";
  }

  @GetMapping("/device/data/list")
  public String getAllDeviceData() throws Exception {
    LOGGER.info("/device/data/list");
    JSONArray jsonArray = new JSONArray();
    JSONObject deviceData1 = dataService.getDeviceDataByAPI(1, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830350);
    if (deviceData1 != null) {
      jsonArray.add(deviceData1);
    }
    JSONObject deviceData2 = dataService.getDeviceDataByAPI(2, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830368);
    if (deviceData2 != null) {
      jsonArray.add(deviceData2);
    }
    JSONObject deviceData3 = dataService.getDeviceDataByAPI(3, Constant.PRODUCT_ID, Constant.DEVICE_ID_863882045830376);
    if (deviceData3 != null) {
      jsonArray.add(deviceData3);
    }
    return jsonArray.toJSONString();
  }

  @PostMapping("/openid/send")
  public String saveOpenid(@RequestBody String openid) throws IOException {
    LOGGER.info("/openid/send, body: " + openid);
    if (!this.openidService.hasOpenid(openid)) {
      this.openidService.saveOpenid(openid);
    }
    return openid;
  }
}
