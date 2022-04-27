package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.utils.Constant;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DataService {

    @Autowired
    private DataTransferService dataTransferService;

    /**
     * http POST请求示例
     */
    public String getDeviceDataByAPI(int id, String productId, String deviceId) throws Exception {
        // 下面以增加设备的API为例【具体信息请以使能平台的API文档为准】。
        //请求BODY,到文档中心->使能平台API文档打开要调用的api中，在“请求BODY”中查看
        String bodyString = String.format("{\"productId\": \"%s\", \"deviceId\": \"%s\"}", productId, deviceId);
        CloseableHttpClient httpClient = null;
        HttpResponse response = null;
        httpClient = HttpClientBuilder.create().build();
        long offset = getTimeOffset();// 获取时间偏移量，方法见前面
        // 构造请求的URL，具体参考文档中心->使能平台API文档中的请求地址和访问路径
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("https");
        uriBuilder.setHost("ag-api.ctwing.cn/aep_device_status"); //请求地址
        uriBuilder.setPath("/deviceStatusList"); //访问路径，可以在API文档中对应API中找到此访问路径
        // 在请求的URL中添加参数，具体参考文档中心->API文档中请求参数说明
        // (如果有MasterKey，将MasterKey加到head中，不加在此处)
        //uriBuilder.addParameter("productId", "9392");//如果没有其他参数，此行不要
        HttpPost httpPost = new HttpPost(uriBuilder.build());//构造post请求
        long timestamp = System.currentTimeMillis() + offset;// 获取时间戳
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dataString = dateFormat.format(date);// 生成格式化的日期字符串
        // head中添加公共参数
        httpPost.addHeader("application", Constant.APP_KEY);
        httpPost.addHeader("timestamp", "" + timestamp);
        httpPost.addHeader("version", Constant.version);
        httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.addHeader("Date", dataString);
        // 下列注释的head暂时未用到
        // httpPost.addHeader("sdk", "GIT: a4fb7fca");
        // httpPost.addHeader("Accept", "gzip,deflate");
        // httpPost.addHeader("User-Agent", "Telecom API Gateway Java SDK");
        // 构造签名需要的参数,如果参数中有MasterKey，则添加来参与签名计算,
        // 其他参数根据实际API从URL中获取,如有其他参数,写法参考get示例
        Map<String, String> param = new HashMap<String, String>();
//        param.put("MasterKey", Constant.MASTER_KEY);
        // 添加签名
        httpPost.addHeader("signature", sign(param, timestamp, Constant.APP_KEY, Constant.APP_SCRECT, bodyString.getBytes()));
        //请求添加body部分
        httpPost.setEntity(new StringEntity(bodyString,"utf-8"));
        try {
            // 发送请求
            response = httpClient.execute(httpPost);
            // 从response获取响应结果
            String body = new String(EntityUtils.toByteArray(response.getEntity()));
            System.out.println(body);
            return dataTransferService.convertData(id, body);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
        }
        return "";
    }


    /**
     * @param param    api 配置参数表
     * @param timestamp UNIX格式时间戳
     * @param application appKey,到应用管理打开应用可以找到此值
     * @param secret 密钥,到应用管理打开应用可以找到此值
     * @param body 请求body数据,如果是GET请求，此值写null
     * @return 签名数据
     */
    public static String sign(Map<String, String> param, long timestamp, String application, String secret, byte[] body) throws Exception {
        // 连接系统参数
        StringBuffer sb = new StringBuffer();
        sb.append("application").append(":").append(application).append("\n");
        sb.append("timestamp").append(":").append(timestamp).append("\n");
        // 连接请求参数
        if (param != null) {
            TreeSet<String> keys = new TreeSet<String>(param.keySet());
            Iterator<String> i = keys.iterator();
            while (i.hasNext()) {
                String s = i.next();
                String val = param.get(s);
                sb.append(s).append(":").append(val == null ? "" : val).append("\n");
            }
        }
        //body数据写入需要签名的字符流中
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(sb.toString().getBytes("utf-8"));
        if (body != null && body.length > 0) {
            baos.write(body);
            baos.write("\n".getBytes("utf-8"));
        }
        // 得到需要签名的字符串
        String string = baos.toString("utf-8");
        System.out.println("Sign string: " + string);
        // hmac-sha1编码
        byte[] bytes = null;
        SecretKey secretKey = new SecretKeySpec(secret.getBytes("utf-8"), "HmacSha1");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        bytes = mac.doFinal(string.getBytes("utf-8"));
        // base64编码
        String encryptedString = new String(Base64.encodeBase64(bytes));
        // 得到需要提交的signature签名数据
        return encryptedString;
    }

    public static long getTimeOffset() {
        long offset = 0;
        HttpResponse response = null;
        //构造httpGet请求
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpTimeGet = new HttpGet("https://ag-api.ctwing.cn/echo");
        try {
            long start = System.currentTimeMillis();
            response = httpClient.execute(httpTimeGet);
            long end = System.currentTimeMillis();
            //时间戳在返回的响应的head的x-ag-timestamp中
            Header[] headers = response.getHeaders("x-ag-timestamp");
            if (headers.length > 0) {
                long serviceTime = Long.parseLong(headers[0].getValue());
                offset = serviceTime - (start + end) / 2L;
            }
            httpClient.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return offset;
    }


}
