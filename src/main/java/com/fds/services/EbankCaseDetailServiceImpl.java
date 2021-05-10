package com.fds.services;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fds.entities.FdsCaseDetail;
import com.fds.entities.FdsEbankCaseDetail;
import com.fds.repositories.FdsEbankCaseDetailRepo;

@Service("ebankCaseDetailService")
@Transactional
public class EbankCaseDetailServiceImpl implements EbankCaseDetailService {
	// Chuyen case
	private final static String TRANSSTAT = "TRA";
	// Dong case
	private final static String DICSTAT = "DIC";
	// Mo lai case
	private final static String REOPENSTAT = "REO";
	// Goi lai sau
	private final static String CALLBACKSTAT = "CAL";
	// Giao dich fraud
	private final static String FRASTAT = "CAF";
	// Case se load trong menu case da dong
	public static final String[] CLOSEDCASESTAT = { DICSTAT, FRASTAT };
	// Case se load trong menu dang xu ly
	public static final String[] INBOXCASESTAT = { TRANSSTAT, REOPENSTAT, " " };
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
	private String sSchema;
	
	@Autowired
	private FdsEbankCaseDetailRepo fdsEbankCaseDetailRepo;

	@Override
	public Page<FdsEbankCaseDetail> findAllBycheckNew(Pageable page) {
		// TODO Auto-generated method stub
		return fdsEbankCaseDetailRepo.findAllBycheckNewIs("N", page);
	}
	
	@Override
	public List<FdsEbankCaseDetail> findAllBycheckNewIsAndNotConfirmTxn() {
		// TODO Auto-generated method stub
		return fdsEbankCaseDetailRepo.findAllBycheckNewIsAndNotConfirmTxn();
	}

	@Override
	public int countAllNewestUserNotAssigned() {
		return fdsEbankCaseDetailRepo.countBycheckNewIs();
	}

	@Override
	public int countAllClosedCase() {
		return fdsEbankCaseDetailRepo.countByCaseStatusInAnd45Day();
	}

	@Override
	public List<Object[]> findCaseDetailByCaseno(String caseno) {
		return fdsEbankCaseDetailRepo.findCaseDetailByCaseno(caseno);
	}

	@Override
	public void updateAssignedUser(String caseno, String userid) {
		FdsEbankCaseDetail fdsCaseDetail = fdsEbankCaseDetailRepo.findOneByCaseNo(caseno);
		fdsCaseDetail.setUsrId(userid.toUpperCase());
		fdsCaseDetail.setAsgTms(fdsEbankCaseDetailRepo.getCurrentTime());
		fdsCaseDetail.setUpdTms(fdsEbankCaseDetailRepo.getCurrentTime()); //Khoa add
		fdsCaseDetail.setCaseStatus(TRANSSTAT);
		fdsCaseDetail.setCheckNew(" ");
		fdsEbankCaseDetailRepo.save(fdsCaseDetail);
	}

	@Override
	public List<Object[]> findTransactionDetailByCaseNo(String caseno, int numberofmonth) {
		if (numberofmonth != -1) {
			return fdsEbankCaseDetailRepo.findTransactionByCase(caseno, numberofmonth);
		}else{
			return fdsEbankCaseDetailRepo.findAllTransactionByCase(caseno);
		}
	}

	@Override
	public FdsEbankCaseDetail findOneByCaseNo(String caseNo) {
		return fdsEbankCaseDetailRepo.findOneByCaseNo(caseNo);
	}

	@Override
	@CacheEvict(value = { "FdsEbankRule.findColorByCaseNo", "FdsEbankRule.findByCaseNo" }, key = "#caseno")
	public boolean closeCase(String caseno, String userid, String status, String autoClose) {
		FdsEbankCaseDetail fdsCaseDetail = fdsEbankCaseDetailRepo.findOneByCaseNo(caseno);
		if (!fdsCaseDetail.getCaseStatus().equals(status)) {
			fdsCaseDetail.setCaseStatus(status);
			if (fdsCaseDetail.getAsgTms() == null || fdsCaseDetail.getAsgTms().toString().equals("0")) {
				fdsCaseDetail.setAsgTms(fdsEbankCaseDetailRepo.getCurrentTime());
			}
			fdsCaseDetail.setUpdTms(fdsEbankCaseDetailRepo.getCurrentTime());

			fdsCaseDetail.setUsrId(userid.toUpperCase());
			fdsCaseDetail.setCheckNew(" ");
			fdsCaseDetail.setAutoClose(autoClose);
			fdsEbankCaseDetailRepo.save(fdsCaseDetail);
			return true;
		}
		return false;
	}

	@Override
	public void reopenCase(String caseno, String userid) {
		FdsEbankCaseDetail fdsCaseDetail = fdsEbankCaseDetailRepo.findOneByCaseNo(caseno);
		fdsCaseDetail.setCaseStatus(REOPENSTAT);
		fdsCaseDetail.setUpdTms(fdsEbankCaseDetailRepo.getCurrentTime());
		fdsEbankCaseDetailRepo.save(fdsCaseDetail);
		
	}

	@Override
	public Page<FdsEbankCaseDetail> findAllClosedByUser(Pageable page, String userid) {
		return fdsEbankCaseDetailRepo.findByUsrIIgnoreCasedAndCaseStatusIn(userid, CLOSEDCASESTAT, page);
	}

	@Override
	public Page<FdsEbankCaseDetail> findAllProcessingByUser(Pageable page, String userid) {
		return fdsEbankCaseDetailRepo.findByUsrIIgnoreCasedAndCaseStatusIn(userid, INBOXCASESTAT, page);
	}

	@Override
	public boolean unAssignedCase(String caseno) {
		FdsEbankCaseDetail fdsCaseDetail = fdsEbankCaseDetailRepo.findOneByCaseNo(caseno);
		fdsCaseDetail.setUsrId(" ");
		fdsCaseDetail.setAsgTms(BigDecimal.valueOf(0));
		fdsCaseDetail.setCaseStatus("NEW");
		fdsCaseDetail.setCheckNew("N");
		try {
			fdsEbankCaseDetailRepo.save(fdsCaseDetail);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Page<FdsEbankCaseDetail> findAllByStatus(Pageable page, String status) {
		return fdsEbankCaseDetailRepo.findAllBycaseStatus(status, page);
	}

	@Override
	public Page<FdsEbankCaseDetail> findAllClosed(Pageable page) {
		return fdsEbankCaseDetailRepo.findByCaseStatusIn(CLOSEDCASESTAT, page);
	}

	@Override
	public Page<FdsEbankCaseDetail> findAllAutoClosed(Pageable page) {
		return fdsEbankCaseDetailRepo.findByCaseStatusInAndAutoClose(CLOSEDCASESTAT,"Y", page);
	}
	
	
	/*@Override
	public Page<FdsEbankCaseDetail> search(String caseno, String userid, String fromdate, String todate, String status, String tkLogin, String cifNo) {
		final String _fromdate = fromdate;
		final String _todate = todate;
		StringBuilder partitionQuery = new StringBuilder(" AND (fdscasedetail.TXN_CRE_TMS between " + fromdate + " and " + todate
				+ " OR fdscasedetail.UPD_TMS between " + fromdate + " and " + todate + ")");
		StringBuilder searchTerm = new StringBuilder();
		if (!"".equals(caseno)) {
			searchTerm.append(" AND fdscasedetail.CASE_NO='" + caseno + "'");
		}
		if (!"".equals(cifNo)) {
			searchTerm.append(" AND fdscasedetail.TXN_CUSTOMER='" + cifNo + "'");
			
		}
		if (!"".equals(userid)) {
			searchTerm.append(" AND lower(fdscasedetail.USR_ID)=lower('" + userid + "')");
		}
		if (!"".equals(tkLogin)) {
			searchTerm.append(" AND fdscasedetail.TXN_USERNAME ='" + tkLogin + "'");
		}
		if (!"".equals(status)) {
			searchTerm.append(" AND lower(fdscasedetail.case_status)=lower('" + status + "')");
			if (status.equals("DIC") || status.equals("REO") || status.equals("CAF")) {
				partitionQuery = new StringBuilder(" AND fdscasedetail.upd_tms between " + fromdate + " and " + todate);
			}
		}
		if (!searchTerm.equals("")) {
			searchTerm = searchTerm.append(partitionQuery.toString());
		} else {
			searchTerm = partitionQuery;
		}
		return fdsEbankCaseDetailRepo.searchCase(searchTerm.toString());
		//return null; //Khoa rem code mot sua
		
	}*/
	
	@Override
	public Page<FdsEbankCaseDetail> search(String caseno, String userid, String fromdate, String todate, String status, String tkLogin, String cifNo, Pageable page) {
		if (fromdate == null || fromdate.equals(""))
			fromdate = "0";
		if (todate == null || todate.equals(""))
			todate = "0";
		return fdsEbankCaseDetailRepo.searchCase(new BigDecimal(fromdate) , new BigDecimal(todate),
				caseno,	userid, status, tkLogin, cifNo, page);
	}

	@Override
	public int lockAccountEBank(String accountCustomer, String chanel, String userMaker) {
		return fdsEbankCaseDetailRepo.lockAccountEBank(accountCustomer, chanel, userMaker);
	}
	
	
	
	

}



