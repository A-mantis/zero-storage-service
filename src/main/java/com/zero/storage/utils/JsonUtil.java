package com.zero.storage.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


/**
 * @Description:Json操作类
 * @author: wei.wang
 * @since: 2020/4/4 10:40
 * @history: 1.2020/4/4 created by wei.wang
 */
public class JsonUtil {

    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * 获取传入JSON
     *
     * @param request
     * @return
     */
    public static StringBuilder getJson(HttpServletRequest request) {
        byte[] buf = new byte[1024];
        InputStream input = null;
        StringBuilder json = null;
        try {
            input = request.getInputStream();
            json = new StringBuilder();
            int size = 0;
            do {
                size = input.read(buf);
                if (size > 0) {
                    json.append(new String(buf, 0, size, StandardCharsets.UTF_8));
                }
            } while (size > 0);
        } catch (IOException e) {
            logger.info("getJson {}", e.getMessage());
        }
        return json;
    }

    /**
     * Object转换JSON格式
     *
     * @param object
     * @return
     */
    public static String objectToJson(Object object) {
        return Optional.ofNullable(object).map(JsonUtil::doObjectToJson).orElse("objectToJson Error : object is null");
    }

    public static String doObjectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            logger.info("objectToJson Error {}", e.getMessage());
            return "objectToJson Error";
        }
    }

    public static <T> T readValue(String jsonStr, Class<T> valueType) {
        try {
            return new ObjectMapper().readValue(jsonStr, valueType);
        } catch (Exception e) {
            logger.info("objectToJson Error {}", e.getMessage());
            return null;
        }
    }

    private static <T> T doReadValue(String jsonStr, TypeReference<T> valueTypeRef) {
        if (jsonStr == null) {
            return null;
        }
        try {
            return new ObjectMapper().readValue(jsonStr, valueTypeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
