package com.cn;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯地图api
 *
 * @author yu
 *
 */
public class MapUtils {
    // key，需要在腾讯地图申请一个KEY
    private static final String KEY = " ";

    /**
     * @Description: 通过经纬度获取位置
     * @Param: [log, lat]
     * @return: java.lang.String
     * @Author: yu
     * @Date: 2018/9/1
     */
    public static Map<String, Object> getLocation(String lng, String lat) {

        Map<String, Object> resultMap = new HashMap<>();

        // 参数解释：lng：经度，lat：维度。KEY：腾讯地图key
        String urlString = "https://apis.map.qq.com/ws/geocoder/v1/?location=" +
                lat + "," + lng +"&key=" + KEY ;

        //根据url调用接口
        String result = getResult(urlString);

        // 转JSON格式
        JSONObject jsonObject = JSONObject.parseObject(result);
        String status = jsonObject.getString("status");
        if(!"0".equals(status)){
            resultMap.put("status",jsonObject.getString("message"));
            return resultMap;
        }
        // 获取地址（行政区划信息） 包含有国籍，省份，城市
        JSONObject a = jsonObject.getJSONObject("result");
        //地址
        String address = a.getString("address");

        JSONObject adInfo = a.getJSONObject("ad_info");
        //城市
        String city = adInfo.getString("city");
        //区域
        String region = adInfo.getString("district");

        resultMap.put("address", address);
        resultMap.put("city", city);
        resultMap.put("region", region);

        return resultMap;
    }

    /**
     * @Description: 通过位置获取经纬度
     * @return: java.lang.String
     * @Author: yu
     * @Date: 2018/9/1
     */
    public static Map<String, Object> getlngAndlat(String address) {

        Map<String, Object> resultMap = new HashMap<>();

        // 参数解释：address 地址。KEY：腾讯地图key
        String urlString = "https://apis.map.qq.com/ws/geocoder/v1/?address=" +
                address+"&key=" + KEY ;

        String result = getResult(urlString);

        // 转JSON格式
        JSONObject jsonObject = JSONObject.parseObject(result);
        String status = jsonObject.getString("status");
        if(!"0".equals(status)){
            resultMap.put("status",jsonObject.getString("message"));
            return resultMap;
        }
        //System.out.println(jsonObject);
        JSONObject a = jsonObject.getJSONObject("result");
        JSONObject location = a.getJSONObject("location");
        JSONObject addressComponents = a.getJSONObject("address_components");
        //经度
        String lng = location.getString("lng");
        //纬度
        String lat = location.getString("lat");
        //城市
        String city = addressComponents.getString("city");
        //可信度参考：值范围 1 <低可信> - 10 <高可信>,该值>=7时，解析结果较为准确
        String reliability = a.getString("reliability");
        //解析精度级别，分为11个级别，一般>=9即可采用（定位到点，精度较高
        String level = a.getString("level");

        resultMap.put("lng", lng);
        resultMap.put("lat", lat);
        resultMap.put("city", city);
        resultMap.put("reliability", reliability);
        resultMap.put("level", level);

        return resultMap;
    }

    //调腾讯地图api接口方法
    private static String getResult(String urlString) {
        String result = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            // 腾讯地图使用GET
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            // 获取地址解析结果
            while ((line = in.readLine()) != null) {
                result += line + "\n";
                //System.out.println(result);
            }
            in.close();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    /**
     * @Description: 通过城市获取行政区域
     * @return: java.lang.String
     * @Author: yu
     * @Date: 2018/9/1
     */
    public static Map<String, Object> getArea(String keyword) {

        Map<String, Object> resultMap = new HashMap<>();

        // 参数解释：keyword：城市关键词。KEY：腾讯地图key
        String urlString = "https://apis.map.qq.com/ws/district/v1/search?keyword=" +
                keyword+"&key=" + KEY ;

        String result = getResult(urlString);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String status = jsonObject.getString("status");
        if(!"0".equals(status)){
            resultMap.put("status",jsonObject.getString("message"));
            return resultMap;
        }
        JSONArray jsonArray  = jsonObject.getJSONArray("result");
        List<String> regions = new ArrayList<>();
        JSONArray a =  jsonArray.getJSONArray(0);
        JSONObject b = a.getJSONObject(0);
        String id = b.getString("id");

        //获取指定行政区划的子级行政区划
        String url = "https://apis.map.qq.com/ws/district/v1/getchildren?id=" +
                id+"&key=" + KEY ;

        String result2 = getResult(url);
        JSONObject jsonObject2 = JSONObject.parseObject(result2);
        String message = jsonObject.getString("status");
        if(!"0".equals(message)){
            resultMap.put("status",jsonObject.getString("message"));
            return resultMap;
        }
        JSONArray jsonArray2  = jsonObject2.getJSONArray("result");
        JSONArray jSONArray3 =  jsonArray2.getJSONArray(0);
        for(int i=0;i<jSONArray3.size();i++) {
            JSONObject region = jSONArray3.getJSONObject(i);
            String fullName = region.getString("fullname");
            regions.add(fullName);
        }


        resultMap.put("regions", regions);

        return resultMap;
    }

    /**
     * @Description: 通过ip获取经纬度,位置
     * @return: java.lang.String
     * @Author: yu
     * @Date: 2018/9/1
     */
    public static Map<String, Object> getlngAndlatByIp(String ip) {

        Map<String, Object> resultMap = new HashMap<>();

        // 参数解释：address 地址。KEY：腾讯地图key
        String urlString = "https://apis.map.qq.com/ws/location/v1/ip?ip=" +
                ip+"&key=" + KEY ;

        String result = getResult(urlString);

        // 转JSON格式
        JSONObject jsonObject = JSONObject.parseObject(result);
        String status = jsonObject.getString("status");
        if(!"0".equals(status)){
             resultMap.put("status",jsonObject.getString("message"));
            return resultMap;
        }
        JSONObject a = jsonObject.getJSONObject("result");
        JSONObject location = a.getJSONObject("location");
        JSONObject adInfo = a.getJSONObject("ad_info");
        //经度
        String lng = location.getString("lng");
        //纬度
        String lat = location.getString("lat");
        //城市
        String city = adInfo.getString("city");
        //区域
        String district = adInfo.getString("district");
        //行政区域代码
        String adcode = adInfo.getString("adcode");



        resultMap.put("lng", lng);
        resultMap.put("lat", lat);
        resultMap.put("city", city);
        resultMap.put("district", district);
        resultMap.put("adcode", adcode);

        return resultMap;
    }

//    public static void main(String[] args) {
        // 测试1
//        String lat = "30.287574000";//维度
//        String lng = "120.07875200";//经度
//
//        Map<String, Object> map = getLocation(lng, lat);
//        System.out.println(map);
//        System.out.println("address：" + map.get("address"));
//        System.out.println("city：" + map.get("city"));
//        System.out.println("region：" + map.get("region"));

        // 测试2
//        String address = "浙江省杭州市西湖区文一西路578号";
//        Map<String, Object> map = getlngAndlat(address);
//        System.out.println(map);
//        System.out.println("lng：" + map.get("lng"));
//        System.out.println("lat：" + map.get("lat"));
//        System.out.println("city：" + map.get("city"));
//        System.out.println("reliability：" + map.get("reliability"));
//        System.out.println("level：" + map.get("level"));

//        // 测试3
//        String keyword = "成都市";
//        Map<String, Object> map = getArea(keyword);
//        System.out.println(map);
//        List<String> list = (List<String>)map.get("regions");
//        for(String str:list){
//            System.out.println(str);
//        }

        // 测试4
//        String ip = "192.168.129.14";
//
//        Map<String, Object> map = getlngAndlatByIp(ip);
//        System.out.println(map);
//        System.out.println("lng：" + map.get("lng"));
//        System.out.println("lat：" + map.get("lat"));
//        System.out.println("city：" + map.get("city"));
//        System.out.println("district：" + map.get("district"));
//        System.out.println("adcode：" + map.get("adcode"));
//   }
}