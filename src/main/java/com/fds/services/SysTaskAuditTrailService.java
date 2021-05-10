package com.fds.services;

import java.math.BigDecimal;
import java.util.List;

public interface SysTaskAuditTrailService {
	
	void save(String object, BigDecimal fromdate,
			BigDecimal todate, String content,
			String type, String userid, BigDecimal createTime,
			String userUpdate, String actionStatus, String txnConfirm);
	
	public String getValueIdTask();
	
	/**
	 * 
	 * @param objectTask
	 * @return
	 */
	public List<Object[]> getUserUpdate(String objectTask);
}
