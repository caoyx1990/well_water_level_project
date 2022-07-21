package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.service.AccessTokenService;
import com.tencent.wxcloudrun.service.DataService;
import com.tencent.wxcloudrun.service.MessagePushService;
import com.tencent.wxcloudrun.service.SendMessageThread;
import com.tencent.wxcloudrun.utils.Constant;
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

  private final static List<String> OPENID_LIST = new CopyOnWriteArrayList<String>();

  @Autowired
  private DataService dataService;

  @Autowired
  private MessagePushService messagePushService;

  @PostConstruct
  public void init() {
//    readFileToGetOpenids();
    SendMessageThread sendMessageThread = new SendMessageThread(OPENID_LIST, dataService, messagePushService);
    Thread thread = new Thread(sendMessageThread);
    thread.start();
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

  @PostMapping("/openid")
  public String getOpenId(@RequestBody String openidList) throws IOException {
    LOGGER.info("/openid, body: " + openidList);
    if (!OPENID_LIST.contains(openidList)) {
      OPENID_LIST.add(openidList);
      URL resource = IndexController.class.getResource("/openids.txt");
      if (resource == null) {
        File file = new File("/openids.txt");
        file.createNewFile();
      }
      FileOutputStream out = new FileOutputStream(resource.getFile(), true);
      OutputStreamWriter writer = new OutputStreamWriter(out);
      for (String openid: OPENID_LIST) {
        writer.write(openid);
        writer.write("\n");
      }
      writer.flush();
      writer.close();
    }
    return openidList;
  }

  @PostMapping("/openids")
  public List<String> getOpenIdList(@RequestBody List<String> openidList) {
    LOGGER.info("/openids, body: " + openidList);
    OPENID_LIST.addAll(openidList);
    return openidList;
  }

  private synchronized void readFileToGetOpenids() {
    try {
      URL resource = IndexController.class.getResource("/openids.txt");
      if (resource == null) {
        File file = new File("/openids.txt");
        file.createNewFile();
      }
      String file = resource.getFile();
      FileReader fileReader = new FileReader(file);
      BufferedReader in = new BufferedReader(fileReader);
      String str;
      List<String> list = new ArrayList<>();
      while ((str = in.readLine()) != null) {
        list.add(str);
      }
      list = list.stream().distinct().collect(Collectors.toList());
      OPENID_LIST.addAll(list);
      in.close();
    } catch (Exception e) {
      LOGGER.error("Error read openids.txt.", e);
    }
  }

}
