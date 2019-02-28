package com.csy.springboot.szfy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledService {
	private static final Logger logger = LoggerFactory.getLogger(ScheduledService.class);

	@Autowired
	private ApiRestClient apiRestClient;

//	@Scheduled(cron = "59 59 15 * * ?")
	public void scheduled() {
		logger.info("=====>>>>>使用cron  {}", System.currentTimeMillis());
		List<String> doctorParams =apiRestClient. getScheduleWorkInfo();
		apiRestClient.autoCallCard(false, doctorParams);

	}
}
