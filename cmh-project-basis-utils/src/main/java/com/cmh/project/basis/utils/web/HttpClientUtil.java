package com.cmh.project.basis.utils.web;

import com.cmh.project.basis.utils.json.FastJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 封装了一些采用HttpClient发送HTTP请求的方法
 * <p>
 * 本工具所采用的是最新的HttpComponents-Client-4.3.1
 *
 */
public class HttpClientUtil {
    public static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static final CloseableHttpClient HTTP_CLIENT;
    private static final String CHARSET = "UTF-8";
    private static final String APPLICATION_JSON = "application/json";
    // 每个主机的最大并行链接数,为每个区设置最大的并发连接数,默认每个路由基础上的连接不超过20个
    private static final int DEFAULT_MAX_PER_ROUTE = 25;
    // 客户端总并行链接最大数。默认值总连接数不能超过500
    private static final int MAX_TOTAL = 650;
    // 超时时间单位：秒
    private static final int TIMEOUT = 8;
    //
    private static final int CONNECTION_REQUEST_TIMEOUT = TIMEOUT * 1000;
    // http连接超时时间。默认值设置为30秒
    private static final int CONNECT_TIMEOUT = TIMEOUT * 1000;
    // socket连接超时时间。默认值设置为15秒
    private static final int SOCKET_TIMEOUT = TIMEOUT * 1000;

    static {

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        cm.setMaxTotal(MAX_TOTAL);

        Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
        builder.setConnectTimeout(CONNECT_TIMEOUT);
        builder.setSocketTimeout(SOCKET_TIMEOUT);
        RequestConfig config = builder.build();

        HTTP_CLIENT = HttpClientBuilder.create().setConnectionManager(cm).setDefaultRequestConfig(config).build();
    }

    /**
     * HTTP Get 获取内容,默认编码UTF-8
     *
     * @param url    请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, params, CHARSET);
    }

    /**
     * HTTP Post 获取内容,默认编码UTF-8
     *
     * @param url 请求的url地址
     * @return 页面内容
     */
    public static String doPost(String url) {
        return doPost(url, null, CHARSET);
    }

    /**
     * HTTP Post 获取内容,默认编码UTF-8
     *
     * @param url    请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params) {
        return doPost(url, params, CHARSET);
    }

    /**
     * HTTP Post 获取内容,默认编码UTF-8
     *
     * @param url    请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String doPostFile(String url, Map<String, Object> params) {
        return doPostFile(url, params, CHARSET);
    }

    /**
     * HTTP Get 获取内容
     *
     * @param url       请求的url地址 ?之前的地址
     * @param params    请求的参数 categoryNO=A01,A02 转成 categoryNO=A01&categoryNo=A02
     * @param spiltChar 参数值的分隔符
     * @param flag      是否需要按照分隔符分割
     * @return
     */
    public static String doGet(String url, Map<String, String> params, String spiltChar, boolean flag) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (flag && spiltChar != null) {
                        String[] values = value.split(spiltChar);
                        for (String targetValue : values) {
                            pairs.add(new BasicNameValuePair(entry.getKey(), targetValue));
                        }
                    } else {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            return doGet(url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String doGet(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            long start = System.currentTimeMillis();
            URL targetUrl = new URL(url);
            URI targetUri = new URI(targetUrl.getProtocol(), targetUrl.getHost(), targetUrl.getPath(), targetUrl.getQuery(), null);
            HttpGet httpGet = new HttpGet(targetUri);
            CloseableHttpResponse response = HTTP_CLIENT.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                logger.error("HttpClientUtil,error status code:{}|request url:{}", statusCode, url);
                throw new RuntimeException("HttpClientUtil,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
                long end = System.currentTimeMillis();
                logger.info("HttpClientUtil,request url:{}|time:{}ms", url, (end - start));
                logger.debug("HttpClientUtil,response result :{}", result);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            logger.error("HttpClientUtil,error request url:{}", url);
            logger.error(e.getMessage(), e);
        }
        return null;

    }

    /**
     * HTTP Get 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> params, String charset) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            return doGet(url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params, String charset) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        // debug打印日志
        logger.debug("HttpClientUtil,request url:{} and params:{}", url, FastJsonUtil.obj2json(params));
        try {
            long start = System.currentTimeMillis();
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            URL targetUrl = new URL(url);
            URI targetUri = new URI(targetUrl.getProtocol(), targetUrl.getHost(), targetUrl.getPath(), targetUrl.getQuery(), null);
            HttpPost httpPost = new HttpPost(targetUri);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            CloseableHttpResponse response = HTTP_CLIENT.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                logger.error("HttpClient,error status code:{}|request url:{}|params:{}", statusCode, url, FastJsonUtil.obj2json(params));
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
                long end = System.currentTimeMillis();
                logger.info("HttpClientUtil,request url:{}|params:{}|time:{}ms", url, FastJsonUtil.obj2json(params), (end - start));
                logger.debug("HttpClientUtil,response result :{}", result);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            logger.error("HttpClientUtil,error request url:{}|params:{}", url, FastJsonUtil.obj2json(params));
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params, String charset, String referer) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        // debug打印日志
        logger.debug("HttpClientUtil,request url:{} and params:{}", url, FastJsonUtil.obj2json(params));
        try {
            long start = System.currentTimeMillis();
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Referer", referer);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            CloseableHttpResponse response = HTTP_CLIENT.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                logger.error("HttpClient,error status code:{}|request url:{}|params:{}", statusCode, url, FastJsonUtil.obj2json(params));
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
                long end = System.currentTimeMillis();
                logger.info("HttpClientUtil,request url:{}|params:{}|time:{}ms", url, FastJsonUtil.obj2json(params), (end - start));
                logger.debug("HttpClientUtil,response result :{}", result);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            logger.error("HttpClientUtil,error request url:{}|params:{}", url, FastJsonUtil.obj2json(params));
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 用于带文件的表单提交,也可用于普通表单提交，可多文件上传
     *
     * @param url    上传接口的url
     * @param params
     * @return String
     */
    public static String doPostFile(String url, Map<String, Object> params, String charset) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        // debug打印日志
        logger.debug("HttpClientUtil,request url:{} and params:{}", url, FastJsonUtil.obj2json(params));

        try {
            long start = System.currentTimeMillis();
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = params.get(key);
                if (value instanceof File) {
                    entityBuilder.addPart(key, new FileBody((File) value));
                } else if (value instanceof InputStream) {
                    entityBuilder.addBinaryBody(key, (InputStream) value);
                } else {
                    entityBuilder.addPart(key, new StringBody((String) value, ContentType.create("text/plain", charset)));
                }
            }

            HttpPost httpPost = new HttpPost(url);
            HttpEntity fileEntity = entityBuilder.build();
            httpPost.setEntity(fileEntity);
            CloseableHttpResponse response = HTTP_CLIENT.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                logger.error("HttpClient,error status code:{}|request url:{}|params:{}", statusCode, url, FastJsonUtil.obj2json(params));
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
                long end = System.currentTimeMillis();
                logger.info("HttpClientUtil,request url:{}|params:{}|time:{}ms", url, FastJsonUtil.obj2json(params), (end - start));
                logger.debug("HttpClientUtil,response result :{}", result);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            logger.error("HttpClientUtil,error request url:{}|params:{}", url, FastJsonUtil.obj2json(params));
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * http post json 字符串请求
     *
     * @param url
     * @param json
     * @return
     * @throws Exception
     */
    public static String doPostWithJSON(String url, String json) throws Exception {
        return doPostWithJSON(url, json, CHARSET);
    }

    public static String doPostWithJSON(String url, String json, String charset) throws Exception {

        if (StringUtils.isBlank(url)) {
            return null;
        }

        logger.debug("HttpClientUtil,request url:{} and params:{}", url, json);
        try {
            long start = System.currentTimeMillis();
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(json, charset);
            stringEntity.setContentType(APPLICATION_JSON);
            stringEntity.setContentEncoding(charset);
            httpPost.setEntity(stringEntity);
            CloseableHttpResponse response = HTTP_CLIENT.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                logger.error("HttpClient,error status code:{}|request url:{}|json:{}", statusCode, url, json);
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
                long end = System.currentTimeMillis();
                logger.info("HttpClientUtil,request url:{}|json:{}|time:{}ms", url, json, (end - start));
                logger.debug("HttpClientUtil,response result :{}" , result);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            logger.error("HttpClientUtil,error request url:{}|json:{}", url, json);
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取IP地址
     *
     * @param request
     * @return
     */
    public static final String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 下载文件
     *
     * @param url          http://www.xxx.com/img/111.jpg
     * @param destFileName xxx.jpg/xxx.png/xxx.txt
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static void downloadFile(String url, String destFileName) throws ClientProtocolException, IOException {
        if (StringUtils.isBlank(url)) {
            return;
        }
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = HTTP_CLIENT.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream in = entity.getContent();
        File file = new File(destFileName);
        try {
            FileOutputStream fout = new FileOutputStream(file);
            int len = -1;
            byte[] tmp = new byte[1024];
            while ((len = in.read(tmp)) != -1) {
                fout.write(tmp, 0, len);
            }
            fout.flush();
            fout.close();
        } finally {
            // 关闭低层流。
            in.close();
        }
        HTTP_CLIENT.close();
    }
}