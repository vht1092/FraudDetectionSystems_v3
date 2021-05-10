package com.fds.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fds.entities.FdsTxnDetail;
import com.fds.repositories.TxnDetailRepo;

@Service("txnDetailService")
public class TxnDetailServiceImpl implements TxnDetailService {

	@Autowired
	TxnDetailRepo txnDetailRepo;

	@Override
	public FdsTxnDetail findOneByF9Oa008CreTmsAndFxOa008UsedPan(BigDecimal cretms, String usedpan) {
		return txnDetailRepo.findOneByF9Oa008CreTmsAndFxOa008UsedPan(cretms, usedpan);
	}

	@Override
	public String findRefCdeByCreTmsAndUsedPan(BigDecimal cretms, String usedpan) {
		return txnDetailRepo.findRefCdeByCreTmsAndUsedPan(cretms, usedpan);
	}

	@Override
	public String findOneFxOa008CntryCdeByFxOa008UsedPanAndF9Oa008CreTms(String pan, BigDecimal cretms) {		
		return txnDetailRepo.findOneFxOa008CntryCdeByFxOa008UsedPanAndF9Oa008CreTms(pan, cretms);
	}
	
	@Override
	public String findEciValByCreTmsAndUsedPan(BigDecimal cretms, String usedpan) {
		return txnDetailRepo.findEciValByCreTmsAndUsedPan(cretms, usedpan);
	}

}
