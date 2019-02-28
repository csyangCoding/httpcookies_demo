package com.csy.springboot.szfy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class ApiRestClient {
	private static final Logger logger = LoggerFactory.getLogger(ApiRestClient.class);
	@Value("szfy.cookies")
	private String cookies;

	/**
	 * 取前三个，8:00~9:30
	 * 
	 * @param dataStr
	 */
	public Boolean doHandleSchduleTime(Object dataStr, String doctorInfo) {
		JSONArray array = JSONArray.fromObject(dataStr);
		if (null == array) {
			return false;
		}
		JSONObject doctorObj = JSONObject.fromObject(doctorInfo);
		String deptCode = doctorObj.getString("deptCode");
		String deptName = doctorObj.getString("deptName");
		String doctor = doctorObj.getString("doctor");
		String isArmyMan = doctorObj.getString("isArmyMan");
		String workType = doctorObj.getString("workType");
		Boolean isSuccess = false;
		int total = 0;
		for (int i = 0; i < array.size(); i++) {
			JSONObject json = JSONObject.fromObject(array.get(i));
			if(null == json)
			{
				continue;
			}
			String sourceId = json.get("sourceId").toString();
			String status = json.get("status").toString();
			String time = json.getString("time");
			String endTime = json.getString("endTime");
			String timePoint = json.getString("timePoint");
			if ("1".equals(status) && total <4) {
				total++;
				CallCardParamVO paramVO = new CallCardParamVO();
				paramVO.setDeptCode(deptCode);
				paramVO.setDeptName(deptName);
				paramVO.setDoctor(doctor);
				paramVO.setIsArmyMan(isArmyMan);
				paramVO.setWorkType(workType);
				paramVO.setSourceId(sourceId);
				paramVO.setTime(time);
				paramVO.setEndTime(endTime);
				paramVO.setTimePoint(timePoint);
				String[] sourceIds = sourceId.split(",");
				for (int j = sourceIds.length - 1; j > 0; j++) {
					paramVO.setSourceId(sourceIds[j]);
					cachedThreadPool.submit(new myTask(paramVO));
				}
				if(total == 3)
				{
					isSuccess = true;
				}
				continue;
			}
			logger.info(">>>doHandleSchduleTime>>json:" + json.toString());
		}
		return isSuccess;
	}

	public CardInfoVO getCardInfo() {
		CardInfoVO card = new CardInfoVO();
		String result = GetHttpCookiesUtil.callRestService(card.getUrl(), "");
		JSONObject json = JSONObject.fromObject(result);
		Object obj = json.get("data");
		if (null == obj) {
			return card;
		}
		JSONArray array = JSONArray.fromObject(obj);
		JSONObject data = JSONObject.fromObject(array.get(0));
		card.setCardId(data.getString("cardId"));
		card.setPatientId(data.getString("patientId"));
		card.setUserId(data.getString("userId"));
		return card;
	}

	private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	
	public Boolean autoCallCard(Boolean isFlag, List<String> doctorParams){
		Boolean isSuccess = isFlag;
		if(isFlag)
		{
			return isSuccess;
		}
		Map<String,Future<String>> map = new HashMap<>();
		for (final String jsonParam : doctorParams) {
			Future<String> future = cachedThreadPool.submit(new Callable<String>() {
				
				@Override
				public String call() {
					return GetHttpCookiesUtil.callRestService("http://3030.ij120.zoenet.cn/api/reservation/getScheduleTime", jsonParam);
				}
			});
			map.put(jsonParam, future);
		}
		
		for (String doctorInfo : map.keySet()) {
			String result = "";
			try {
				Future<String> future = map.get(doctorInfo);
				result = future.get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error(e.getMessage(), e);
			}
			
			if(StringUtils.isBlank(result))
			{
				continue;
			}
			JSONObject data = JSONObject.fromObject(result);
			isSuccess = doHandleSchduleTime(data.get("data"), doctorInfo);
		}
		autoCallCard(isSuccess, doctorParams);
		return isSuccess;
	}

	/**
	 * 通过微信公众号页面上抓取到想要预约的医生编码，
	 * @return
	 */
	public List<String> getScheduleWorkInfo() {
		List<String> doctor = new ArrayList<>();
		JSONObject json = new JSONObject();
		json.put("clinicCode", "");
		json.put("clinicName", "");
		json.put("deptCode", "167");
		json.put("deptName", "产科专家门诊(红荔)");
		json.put("doctor", "2831");
		json.put("isArmyMan", "0");
		json.put("registerTypeName", "5");
		json.put("workDate", "2018-11-05 00:00:00");
		json.put("workType", "1");
		
		JSONObject json1 = JSONObject.fromObject(json);
		json1.put("doctor", "2915");
		
		JSONObject json2 = JSONObject.fromObject(json);
		json2.put("doctor", "2599");
		
		JSONObject json3 = JSONObject.fromObject(json);
		json3.put("doctor", "2460");// 
		
		JSONObject json4 = JSONObject.fromObject(json);
		json4.put("doctor", "2302");
		
		JSONObject json5 = JSONObject.fromObject(json);
		json5.put("doctor", "4131");
		
		JSONObject json6 = JSONObject.fromObject(json);
		json6.put("doctor", "2651");
		doctor.add(json.toString());
		doctor.add(json1.toString());
		doctor.add(json2.toString());
		doctor.add(json3.toString());
		doctor.add(json4.toString());
		doctor.add(json5.toString());
		doctor.add(json6.toString());
		return doctor;
	}

	class myTask implements Callable<String> {

		private CallCardParamVO paramVO;
		static final String url = "http://3030.ij120.zoenet.cn/api/reservation/comfirmPrecontract";

		public myTask(CallCardParamVO paramVO) {
			this.paramVO = paramVO;
		}

		@Override
		public String call() {
			String result = "";
			try {
				String[] sourceIds = paramVO.getSourceId().split(",");
				for (int i = sourceIds.length - 1; i > 0; i++) {
					paramVO.setSourceId(sourceIds[i]);
					Map<String, Object> map = GetHttpCookiesUtil.transBean2Map(paramVO);
					logger.info(">>send>>>CallCardParamVO:" + map.toString());
					result = GetHttpCookiesUtil.send(url, map);
				}
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			return result;
		}
		
	}
}
