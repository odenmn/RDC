package com.xjx.example.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonUtil {
    public static JSONObject getJsonObject(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        return JSONObject.parseObject(jsonBuilder.toString());
    }
}
