package com.cmh.project.basis.base.others;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

public class StringUtil extends StringUtils {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");

    private static final String NULL_STR = "null";
    private static final String ZERO_STR = "null";
    private static final String DOT_STR = ".";

    /**
     * @param s1
     * @param s2
     * @return equals
     */
    public static boolean isEquals(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.equals(s2);
    }

    /**
     * is integer string.
     *
     * @param str
     * @return is integer
     */
    public static boolean isInteger(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return INT_PATTERN.matcher(str).matches();
    }

    /**
     * @param str
     * @return
     */
    public static int parseInteger(String str) {
        if (!isInteger(str)) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    /**
     * Returns true if s is a legal Java identifier.
     *
     * <a href="http://www.exampledepot.com/egs/java.lang/IsJavaId.html">more
     * info.</a>
     */
    public static boolean isJavaIdentifier(String s) {
        if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param values
     * @param value
     * @return contains
     */
    public static boolean isContains(String[] values, String value) {
        if (value != null && value.length() > 0 && values != null && values.length > 0) {
            for (String v : values) {
                if (value.equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * translat.
     *
     * @param src  source string.
     * @param from src char table.
     * @param to   target char table.
     * @return String.
     */
    public static String translat(String src, String from, String to) {
        if (isEmpty(src)) {
            return src;
        }
        StringBuilder sb = null;
        int ix;
        char c;
        for (int i = 0, len = src.length(); i < len; i++) {
            c = src.charAt(i);
            ix = from.indexOf(c);
            if (ix == -1) {
                if (sb != null) {
                    sb.append(c);
                }
            } else {
                if (sb == null) {
                    sb = new StringBuilder(len);
                    sb.append(src, 0, i);
                }
                if (ix < to.length()) {
                    sb.append(to.charAt(ix));
                }
            }
        }
        return sb == null ? src : sb.toString();
    }

    /**
     * split.
     *
     * @param ch char.
     * @return string array.
     */
    public static String[] split(String str, char ch) {
        List<String> list = null;
        char c;
        int ix = 0, len = str.length();
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (c == ch) {
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(str.substring(ix, i));
                ix = i + 1;
            }
        }
        if (ix > 0) {
            list.add(str.substring(ix));
        }
        return list == null ? EMPTY_STRING_ARRAY : (String[]) list.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     * join string.
     *
     * @param array String array.
     * @return String.
     */
    public static String join(String[] array) {
        if (array.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * join string like javascript.
     *
     * @param array String array.
     * @param split split
     * @return String.
     */
    public static String join(String[] array, char split) {
        if (array.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(split);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * join string like javascript.
     *
     * @param array String array.
     * @param split split
     * @return String.
     */
    public static String join(String[] array, String split) {
        if (array.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(split);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * @param coll
     * @param split
     * @return
     */
    public static String join(Collection<String> coll, String split) {
        if (coll.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String s : coll) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(split);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * @param ps
     * @return
     */
    public static String toQueryString(Map<String, String> ps) {
        StringBuilder buf = new StringBuilder();
        if (ps != null && ps.size() > 0) {
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(ps).entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && key.length() > 0 && value != null && value.length() > 0) {
                    if (buf.length() > 0) {
                        buf.append("&");
                    }
                    buf.append(key);
                    buf.append("=");
                    buf.append(value);
                }
            }
        }
        return buf.toString();
    }

    /**
     * @param camelName
     * @param split
     * @return
     */
    public static String camelToSplitName(String camelName, String split) {
        if (camelName == null || camelName.length() == 0) {
            return camelName;
        }
        StringBuilder buf = null;
        for (int i = 0; i < camelName.length(); i++) {
            char ch = camelName.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                if (buf == null) {
                    buf = new StringBuilder();
                    if (i > 0) {
                        buf.append(camelName.substring(0, i));
                    }
                }
                if (i > 0) {
                    buf.append(split);
                }
                buf.append(Character.toLowerCase(ch));
            } else if (buf != null) {
                buf.append(ch);
            }
        }
        return buf == null ? camelName : buf.toString();
    }

    /**
     * @param strArray
     * @return
     * @Description: 判断字符串是否为空
     */
    public static boolean isNotEmpty(String... strArray) {
        if (strArray == null || strArray.length <= 0) {
            return false;
        }
        for (String str : strArray) {
            if (isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param strArray
     * @return
     * @Description: 判断字符串是否为空
     */
    public static boolean isEmpty(String... strArray) {
        if (strArray == null || strArray.length <= 0) {
            return true;
        }
        for (String str : strArray) {
            if (isNotEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param strArray
     * @return
     * @Description: 判断字符串是否为空
     */
    public static boolean isNotBlank(String... strArray) {
        if (strArray == null || strArray.length <= 0) {
            return false;
        }
        for (String str : strArray) {
            if (isBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param strArray
     * @return
     * @Description: 判断字符串是否为空
     */
    public static boolean isBlank(String... strArray) {
        if (strArray == null || strArray.length <= 0) {
            return true;
        }
        for (String str : strArray) {
            if (isNotBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 截掉小数
     *
     * @param str 被判断对象
     * @Author: wangfeng
     * @CreateDate: 2014-07-18
     * @Return:
     */
    public static String cutOffDecimal(String str) {
        if (str != null && !"".equals(str.trim()) && !NULL_STR.equals(str) && !ZERO_STR.equals(str)) {
            if (str.indexOf(DOT_STR) > 0) {
                str = str.substring(0, str.indexOf(DOT_STR));
            }
        }
        return str;
    }

    /**
     * 判断字符串是否是由数字组成
     *
     * @param value
     * @return
     */
    public static boolean checkStringByNum(String value) {
        String capitalLetters = "^-?[0-9]+$";
        if (value == null || value.trim().length() < 1) {
            return false;
        }
        if (value.matches(capitalLetters)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 字符串数组转整形型数组
     *
     * @param arr
     * @return
     */
    public static Integer[] strArrayTointArray(String... arr) {
        Integer[] intArr = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            intArr[i] = Integer.parseInt(arr[i]);
        }
        return intArr;
    }

    public static Integer[] splitAnd2IntArray(String str, String sep) {
        if (str == null) {
            return new Integer[0];
        }
        String[] strArr = split(str, sep);
        Integer[] r = strArrayTointArray(strArr);
        return r;
    }

    /**
     * 返回固定位数随机数
     *
     * @param strLength 位数
     * @return
     */
    public static String getFixLenthString(int strLength) {
        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        return fixLenthString.substring(1, strLength + 1);
    }

    /**
     * @param str
     * @return
     * @Description: 验证字符串里面是否包含有效long型数据
     */
    public static boolean isValidLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为1,英文字符长度为0.5
     *
     * @param s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static double getChineseLength(String s) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        //进位取整
        return Math.ceil(valueLength);
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static int getLength(String s) {
        if (s == null) {
            return 0;
        }
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String bean2String(Object bean) {
        StringBuilder sb = new StringBuilder();
        Field[] fields = bean.getClass().getDeclaredFields();
        Field[] arr = fields;
        int len = fields.length;

        for (int i = 0; i < len; ++i) {
            Field field = arr[i];
            if (!Modifier.isStatic(field.getModifiers())) {
                sb.append(field.getName());
                sb.append("={");
                try {
                    field.setAccessible(true);
                    Iterator it;
                    if (field.get(bean) instanceof List) {
                        List<Object> list = (List) field.get(bean);
                        it = list.iterator();
                        while (it.hasNext()) {
                            Object o = it.next();
                            sb.append("[" + o.toString() + "]");
                        }
                    } else if (field.get(bean) instanceof Map) {
                        Map<Object, Object> map = (Map) field.get(bean);
                        it = map.entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry<Object, Object> entry = (Map.Entry) it.next();
                            sb.append("[key=" + entry.getKey().toString() + ",value=" + entry.getValue().toString() + "]");
                        }
                    } else {
                        sb.append(field.get(bean));
                    }
                } catch (Exception var10) {
                    ;
                }
                sb.append("};");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

}
