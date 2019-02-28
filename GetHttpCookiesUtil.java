package com.csy.springboot.szfy;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHttpCookiesUtil {
	private static final Logger logger = LoggerFactory.getLogger(GetHttpCookiesUtil.class);

	public static String callRestService(String url, String jsonParams) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(180 * 1000)
				.setConnectionRequestTimeout(180 * 1000).setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();

		httpPost.setConfig(requestConfig);
		httpPost.setHeader("Content-Type", "application/json"); //
		httpPost.setHeader("Cookie",
				"connect.sid=s%3A-XwjErNt65H8H5KhIiQxdPiCAu840Qst.KPBFsj%2FNqyTLSsDjBtg3B8RNDwSD1ov8Up98YEa%2BDyk"); //
		try {
			httpPost.setEntity(new StringEntity(jsonParams, ContentType.create("application/json", "utf-8")));
			System.out.println("request parameters" + EntityUtils.toString(httpPost.getEntity()));
			HttpResponse response = httpClient.execute(httpPost);

			int resultCode = response.getStatusLine().getStatusCode();
			if (resultCode == 200) {
				String result = EntityUtils.toString(response.getEntity());
				System.out.println("doPostForInfobipUnsub response" + result);
				return result;
			}
			return String.valueOf(response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "post failure :caused by-->" + e.getMessage().toString();
		} finally {
			if (null != httpClient) {
				try {
					httpClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 模拟请求
	 * 
	 * @param url
	 *            资源地址
	 * @param map
	 *            参数列表
	 * @param encoding
	 *            编码
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String send(String url, Map<String, Object> map) throws ParseException, IOException {
		String body = "";

		// 创建httpclient对象
		CloseableHttpClient client = HttpClients.createDefault();
		// 创建post方式请求对象
		HttpPost httpPost = new HttpPost(url);

		// 装填参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (map != null) {
			for (Entry<String, Object> entry : map.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
			}
		}
		// 设置参数到请求对象中
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

		System.out.println("请求地址：" + url);
		System.out.println("请求参数：" + nvps.toString());

		// 设置header信息
		// 指定报文头【Content-type】、【User-Agent】
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
		httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

		// 执行请求操作，并拿到结果（同步阻塞）
		CloseableHttpResponse response = client.execute(httpPost);
		// 获取结果实体
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			// 按指定编码转换结果实体为String类型
			body = EntityUtils.toString(entity, "utf-8");
		}
		EntityUtils.consume(entity);
		// 释放链接
		response.close();
		return body;
	}

	public static Map<String, Object> transBean2Map(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				if (!key.equals("class")) {
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);
					map.put(key, value);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return map;
	}
}
