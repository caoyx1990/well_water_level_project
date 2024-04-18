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
    LOGGER.info(jsonArray.toJSONString());
    return jsonArray.toJSONString();
  }

}
