package com.potato.tools.db;


import com.potato.tools.ReflectTool;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * create by Potato
 * create time 2020/5/30
 * Description：接口Url格式化，不显示host
 */
public class ApiLogUtils {
    //截取接口名字（true：包含中文名字）
    public static String nameStyle(String url, boolean hasChinese, String port) {
        if (url.isEmpty()) return "";
        if (url.contains("jpeg") || url.contains("png")) return url;
        String[] tailArray = {"\\.com", "\\.in", "\\.net", "\\.cn", "\\.club", "\\.top", "\\.site", "\\.work", "\\.vip", "\\.org", "\\.co"};
        String[] urlArray = null;
        for (String tailStr : tailArray) {
            urlArray = url.split(tailStr);
            if (urlArray.length > 1) break;
        }
        if (urlArray.length == 1) {
            urlArray = url.split(port);
        }
//        String[] urlArray = url.split("\\.com");
//        if (urlArray.length == 1) {
//            urlArray = url.split("\\.in");
//        }
//        if (urlArray.length == 1) {
//            urlArray = url.split("8082");
//        }
//        if (urlArray.length == 1) {
//            urlArray = url.split("8080");
//        }
        if (urlArray.length == 1) {
            return "";
        }
        String name = urlArray[1];//接口路径
        if (name.contains("?")) {
            //去掉“？”
            name = name.substring(0, name.indexOf("?"));
        }
//        if (name.contains("@")) {
//            return name + "\n" + "发送绑定邮箱邮件";
//        }
        if (hasChinese) {
            //反射获取ApiLogMap中的中文名字
            GetChineseName getChineseName = new GetChineseName(name).invoke();
            if (getChineseName.is()) return name;
            name = getChineseName.getName();
        }
        return name;
    }

    private static class GetChineseName {
        private boolean myResult;
        private String name;

        public GetChineseName(String name) {
            this.name = name;
        }

        boolean is() {
            return myResult;
        }

        public String getName() {
            return name;
        }

        public GetChineseName invoke() {
            Field mapField = null;
            try {
                mapField = ReflectTool.findField("com.potato.apiLogs.ApiLogMap", "mapApi");
            } catch (NoSuchFieldException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (null == mapField) {
                myResult = true;
                return this;
            }
            Map<String, String> map = null;
            try {
                map = (Map<String, String>) mapField.get("com.potato.apiLogs.ApiLogMap");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (null == map) {
                myResult = true;
                return this;
            }
            String chineseName = map.get(name);
            if (null != chineseName) {
                name += "\n" + chineseName;
            }
            myResult = false;
            return this;
        }
    }
}
