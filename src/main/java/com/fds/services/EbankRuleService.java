package com.fds.services;

import java.util.List;

import com.fds.entities.FdsEbankRule;

public interface EbankRuleService {
	
	List<FdsEbankRule> findAll();

	List<FdsEbankRule> findByCaseNo(String id);
	
	List<FdsEbankRule> findRuleByCaseNo(String id);
	
	void updateByRuleid(String id, String color);

	String findColorByCaseNo(String caseno);
}
