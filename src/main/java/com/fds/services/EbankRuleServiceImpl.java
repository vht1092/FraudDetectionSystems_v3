package com.fds.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fds.entities.FdsEbankRule;
import com.fds.repositories.FdsEbankRuleRepo;

@Service("ebankRuleService")
public class EbankRuleServiceImpl implements EbankRuleService {
	@Autowired
	private FdsEbankRuleRepo ebankRuleRepo;

	@Override
	public List<FdsEbankRule> findAll() {
		return ebankRuleRepo.findAll(new Sort(Sort.Direction.ASC, "rulePriority"));
	}

	@Override
	@Cacheable(cacheNames = "FdsEbankRule.findByCaseNo", key = "#caseno")
	public List<FdsEbankRule> findByCaseNo(String caseno) {
		return ebankRuleRepo.findAllByCaseNo(caseno);
	}
	
	@Override
	public List<FdsEbankRule> findRuleByCaseNo(String caseno) {
		return ebankRuleRepo.findAllByCaseNo(caseno);
	}
	
	@Override
	@CacheEvict(cacheNames = {"FdsEbankRule.findColorByCaseNo"}, allEntries = true)
	public void updateByRuleid(String id, String color) {
		FdsEbankRule FdsEbankRuleList = ebankRuleRepo.findOneByruleId(id);
		FdsEbankRuleList.setRuleLevel(color);
		ebankRuleRepo.save(FdsEbankRuleList);
		
	}

	@Override
	@Cacheable(cacheNames = "FdsEbankRule.findColorByCaseNo", key = "#caseno")
	public String findColorByCaseNo(String caseno) {
		return ebankRuleRepo.findColorByCaseNo(caseno);
		
	}

}

/*
@Override
	@CacheEvict(cacheNames = {"FdsEbankRule.findColorByCaseNo"}, allEntries = true)
	public void updateByRuleid(String id, String color) {
		FdsEbankRule FdsEbankRuleList = ruleRepo.findOneByruleId(id);
		FdsEbankRuleList.setRuleLevel(color);
		ruleRepo.save(FdsEbankRuleList);
	}

	@Override
	@Cacheable(cacheNames = "FdsEbankRule.findColorByCaseNo", key = "#caseno")
	public String findColorByCaseNo(String caseno) {
		return ruleRepo.findColorByCaseNo(caseno);
	}

*/