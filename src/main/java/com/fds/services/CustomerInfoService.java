package com.fds.services;

import java.util.List;

import com.fds.entities.CustomerInfo;

public interface CustomerInfoService {
	
	List<CustomerInfo> findAllTypetask(String type);

	CustomerInfo findAll(String cifno);
	
	CustomerInfo findByCifNo(String cifno);

	CustomerInfo findOneAll(String crdno);

	CustomerInfo findByCrdNo(String crdno);
}
