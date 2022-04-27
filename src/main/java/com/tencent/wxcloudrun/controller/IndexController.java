package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * index控制器
 */
@RestController
public class IndexController {

  private final static String PRODUCT_ID = "15194666";
  private final static String DEVICE_ID_863882045830350 = "f5b863309e2e4ac1aba126501132bde4";
  private final static String DEVICE_ID_863882045830368 = "f3cc7f06954a494c83672a4f928930c4";
  private final static String DEVICE_ID_863882045830376 = "7ade66ac38d146558c79bd21205befb2";


  @Autowired
  private DataService dataService;

  @GetMapping("/device/{deviceId}/data")
  public String getDeviceData(@PathVariable String deviceId) throws Exception {
    if ("350".equalsIgnoreCase(deviceId)) {
      return dataService.getDeviceDataByAPI(1, PRODUCT_ID, DEVICE_ID_863882045830350);
    } else if ("368".equalsIgnoreCase(deviceId)) {
      return dataService.getDeviceDataByAPI(2, PRODUCT_ID, DEVICE_ID_863882045830368);
    } else if ("376".equalsIgnoreCase(deviceId)) {
      return dataService.getDeviceDataByAPI(3, PRODUCT_ID, DEVICE_ID_863882045830376);
    }
    return "";
  }

}
