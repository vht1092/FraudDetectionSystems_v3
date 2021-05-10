package com.fds.services;

import java.util.List;

import com.fds.entities.FdsEbankRule;

public interface RuleService {
	List<FdsEbankRule> findAll();

	List<FdsEbankRule> findByCaseNo(String id);

	void updateByRuleid(String id, String color);

	String findColorByCaseNo(String caseno);

}
