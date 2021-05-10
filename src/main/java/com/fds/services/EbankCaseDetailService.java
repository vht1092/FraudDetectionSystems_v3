package com.fds.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fds.entities.FdsCaseDetail;
import com.fds.entities.FdsEbankCaseDetail;

public interface EbankCaseDetailService {
	
	/**
	 * Lay danh sach cac case o trang thay moi chua tiep nhan
	 * 
	 * @param page
	 *            PageRequest
	 * @return Page
	 */
	public Page<FdsEbankCaseDetail> findAllBycheckNew(Pageable page);
	
	public List<FdsEbankCaseDetail> findAllBycheckNewIsAndNotConfirmTxn();
	
	/**
	 * Danh sach case moi nhat chua duoc tiep nhan
	 * 
	 * @return int
	 */
	public int countAllNewestUserNotAssigned();
	
	public int countAllClosedCase();
	
	/**
	 * Thong tin cho man hinh chi tiet case
	 * 
	 * @param caseno
	 *            So case
	 * @return Object
	 * 
	 */	
	public List<Object[]> findCaseDetailByCaseno(String caseno);
	
	/**
	 * Dung khi chuyen case cho user nao do, trang thai case se la TRA
	 * 
	 * @param caseno
	 *            So case
	 * @param userid
	 *            User id can chuyen
	 */
	public void updateAssignedUser(String caseno, String userid);
	
	/**
	 * Lay danh sach cac giao dich co lien quan theo case
	 * 
	 * @param caseno
	 *            So case
	 * @return List
	 */
	public List<Object[]> findTransactionDetailByCaseNo(String caseno, int numberofmonth);
	/**
	 * Thong tin case
	 * 
	 * @param caseNo
	 *            So case
	 * @return FdsEbankCaseDetail
	 */
	public FdsEbankCaseDetail findOneByCaseNo(String caseNo);
	
	/**
	 * Dong case va cap nhat trang thai
	 * 
	 * @param caseno
	 *            So case
	 * @param userid
	 *            User id
	 * @param status
	 *            Trang thai cua case: "DIC", "CAF"
	 * @return boolean
	 */
	public boolean closeCase(String caseno, String userid, String status, String autoClose);
	
	/**
	 * Mo lai case, trang thai case se la REO
	 * 
	 * @param caseno
	 *            So case
	 * @param userid
	 *            User id
	 */
	public void reopenCase(String caseno, String userid);
	
	/**
	 * Danh sach case da duoc dong boi user
	 * 
	 * @param page
	 *            PageRequest
	 * @param userid
	 *            user id
	 * @return Page
	 */
	public Page<FdsEbankCaseDetail> findAllClosedByUser(Pageable page, String userid);
	
	/**
	 * Danh sach case dang xu ly boi user
	 * 
	 * @param page
	 *            PageRequest
	 * @param userid
	 *            user id
	 * @return Page
	 */
	public Page<FdsEbankCaseDetail> findAllProcessingByUser(Pageable page, String userid);
	
	public boolean unAssignedCase(String caseno);
	
	/**
	 * Lay danh sach case theo trang thai
	 * 
	 * @param page
	 *            PageRequest
	 * @param status
	 *            Trang thai case
	 * @return Page
	 */
	public Page<FdsEbankCaseDetail> findAllByStatus(Pageable page, String status);
	
	public Page<FdsEbankCaseDetail> findAllClosed(Pageable page);
	
	public Page<FdsEbankCaseDetail> findAllAutoClosed(Pageable page);
	
	
	/**
	 * Dung cho man hinh tim kiem
	 * 
	 * @param
	 * @return Page
	 */
	public Page<FdsEbankCaseDetail> search(String caseno, String userid, String fromdate, String todate, String status, String cardno, String cardbrand, Pageable page);
	
	
	
	/**
	 * 
	 * @param accountCustomer
	 * @param chanel: Kenh thuc hien 01 ODBX, 03 MB
	 * @param userMaker: user thuc hien khoa account KH
	 * @return
	 */
	public int lockAccountEBank(String accountCustomer, String chanel, String userMaker);

}
