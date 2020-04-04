package com.cmh.project.basis.utils.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cmh.project.basis.utils.others.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FastJsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(FastJsonUtil.class);

    private static final SerializeConfig CONFIG;

    static {
        CONFIG = SerializeConfig.getGlobalInstance();
        // 使用和json-lib兼容的日期输出格式
        CONFIG.put(java.util.Date.class, new JSONLibDataFormatSerializer());
        // 使用和json-lib兼容的日期输出格式
        CONFIG.put(java.sql.Date.class, new JSONLibDataFormatSerializer());
        // 作用在可序列化的Bean的显示格式(驼峰)
        // CONFIG.setPropertyNamingStrategy(PropertyNamingStrategy.CamelCase);
        // 处理反序列化泛型T对象首字母大写转小写
        NameFilter formatName = new NameFilter() {
            public String process(Object object, String name, Object value) {
                if (StringUtil.isBlank(name)) {
                    return name;
                }
                if (StringUtil.isNotBlank(name) && Character.isUpperCase(name.charAt(0))) {
                    char chars[] = name.toCharArray();
                    chars[0] = Character.toLowerCase(chars[0]);
                    return new String(chars);
                }
                return name;
            }
        };
        CONFIG.addFilter(JSONObject.class, formatName);
    }


    private static final SerializerFeature[] FEATURES = {
            // 输出空置字段
            SerializerFeature.WriteMapNullValue,
            // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullListAsEmpty,
            // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullNumberAsZero,
            // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullBooleanAsFalse,
            // 字符类型字段如果为null，输出为""，而不是null
            SerializerFeature.WriteNullStringAsEmpty,
            // 跳过标识了TransientField
            SerializerFeature.SkipTransientField
    };


    /**
     * 转换成字符串
     *
     * @param object
     * @return
     */
    public static String obj2json(Object object) {
        return JSON.toJSONString(object, CONFIG);
    }

    /**
     * 转换成字符串 ,带有过滤器
     *
     * @param object
     * @return
     */
    public static String obj2jsonWithFeatures(Object object) {
        return JSON.toJSONString(object, CONFIG, FEATURES);
    }


    /**
     * 转成bean对象
     *
     * @param text
     * @return
     */
    public static Object json2obj(String text) {
        return JSON.parse(text);
    }

    /**
     * 转成具体的泛型bean对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toJavaObject(JSON json, Class<T> clazz) {
        return JSON.toJavaObject(json, clazz);
    }

    /**
     * 转成具体的泛型bean对象
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T json2obj(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /**
     * 转换为数组Array
     *
     * @param text
     * @param <T>
     * @return
     */
    public static <T> Object[] json2array(String text) {
        return json2array(text, null);
    }

    /**
     * 转换为具体的泛型数组Array
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Object[] json2array(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz).toArray();
    }

    /**
     * 转换为具体的泛型List
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> json2list(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

    /**
     * json字符串转化为map
     *
     * @param s
     * @return
     */
    public static Map json2map(String s) {
        Map map = JSONObject.parseObject(s);
        return map;
    }

    /**
     * 将map转化为string
     *
     * @param map
     * @return
     */
    public static String map2json(Map map) {
        String s = JSONObject.toJSONString(map);
        return s;
    }

    /**
     * 用fastjson 将jsonString 解析成 List<Map<String,Object>>
     *
     * @param jsonString
     * @return
     */
    public static List<Map<String, Object>> json2listmap(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = JSON.parseObject(jsonString, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            logger.error("====>解析异常,jsonString={}", jsonString, e);
            e.printStackTrace();
        }
        return list;
    }
}