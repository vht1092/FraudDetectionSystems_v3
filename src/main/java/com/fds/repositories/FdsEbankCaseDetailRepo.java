package com.fds.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fds.entities.FdsEbankCaseDetail;


@Repository
public interface FdsEbankCaseDetailRepo extends JpaRepository<FdsEbankCaseDetail, String>, CaseEbankDetailRepoCustom {
	public static final String TABLE_NAME = "FDS_EBANK_CASE_DETAIL";
	public static final String CLOSED_CASE_STATUS = "'DIC','CAF'";
	public static final String DESC_CASE_STATUS = "CASE STATUS";
	
	
	
	/**
	 * Lay danh sach cac case o trang thay moi chua tiep nhan
	 */
	@Query(value = "select f from FdsEbankCaseDetail f where f.checkNew = :ischecknew")
	Page<FdsEbankCaseDetail> findAllBycheckNewIs(@Param(value = "ischecknew") String ischecknew, Pageable page);
	
	@Query(value = "SELECT A.* \r\n" + 
			"FROM SMS.FDS_EBANK_CASE_DETAIL A INNER JOIN SMS.FDS_SYS_TASK B ON A.TXN_CUSTOMER = B.OBJECTTASK  \r\n" + 
			"WHERE CHECK_NEW='N' AND B.TXN_CONFIRM='N'", nativeQuery = true)
	List<FdsEbankCaseDetail> findAllBycheckNewIsAndNotConfirmTxn();
	
	
	/**
	 * Count danh sach cac case o trang thay moi chua tiep nhan
	 */
	@Query(value = "select count(t.case_no) from {h-schema}" + TABLE_NAME + " t where t.check_new = 'N'", nativeQuery = true)
	int countBycheckNewIs();
	
	
	/**
	 * Count danh sach cac case trong 45 ngay dung cho man hinh thong ke chung
	 * 
	 * @return int
	 */
	@Query(value = "select count(a.txn_cre_tms) from  {h-schema}" + TABLE_NAME + " a  where a.case_status in (" + CLOSED_CASE_STATUS
			+ ") and a.txn_cre_tms >= (select to_number(to_char(sysdate - 45, 'yyyyMMddHH24MISSSSS')) from dual)", nativeQuery = true)
	int countByCaseStatusInAnd45Day();
	
	@Query(value = "select c.CASE_NO,c.TXN_CRE_TMS,c.TXN_USERNAME,t.IDMAKER,c.ACCOUNTNO,'' THE_THANH_TOAN,c.TXN_AMOUNT,t.CODCURR,c.USR_ID,c.UPD_TMS,c.TXN_IDREF MA_GD,t.HOSTREFRENCENO MA_CORE,c.TXN_CHANNEL KENH_GD,c.TXN_TYPE LOAI_GD,c.CODSTATUS TINH_TRANG_GD,c.CUSTOMERCODE HD_DV,c.IDPROVIDER NHA_CC_DV,c.TXN_DEST_ACC TK_NHAN_TT, TXN_DEST_CARD THE_NHAN_TT,TXN_CUSTOMER CIF,CASE_STATUS,c.CUS_TYPE,c.CUS_NAME, c.PHONENUMBER PHONE, c.EMAIL EMAIL, c.CUS_MST, c.CUS_ADDR, c.RES_STATUS from  {h-schema}FDS_EBANK_CASE_DETAIL c join {h-schema}FDS_TRANS t on c.TXN_IDREF = t.IDREF where CASE_NO=:caseno ", nativeQuery = true)
	List<Object[]> findCaseDetailByCaseno(@Param(value = "caseno") String caseno);
	
	/**
	 * Lay thoi gian hien tai theo thoi gian cua database
	 * 
	 * @return BigDecimal
	 */
	@Query(value = "select to_number(to_char(SYSDATE, 'yyyyMMddHH24MISSSSS')) from dual", nativeQuery = true)
	BigDecimal getCurrentTime();
	
	/**
	 * Thong tin case
	 * 
	 * @param caseno
	 *            So case
	 * @return FdsCaseDetail
	 */
	FdsEbankCaseDetail findOneByCaseNo(@Param(value = "caseno") String caseno);
	
	
	/**
	 * Lay danh sach case theo user id va trang thai case
	 * 
	 * @param
	 * @return Page<FdsCaseDetail>
	 * @see Inbox
	 * @see ClosedCase
	 */
	Page<FdsEbankCaseDetail> findByUsrIIgnoreCasedAndCaseStatusIn(String userid, String[] casestatus, Pageable page);
	
	/**
	 * Lay danh sach case theo trang thai
	 * 
	 * @param status
	 *            Trang thai case
	 * @param page
	 *            PageRequest
	 * @return Page<FdsCaseDetail>
	 */
	Page<FdsEbankCaseDetail> findAllBycaseStatus(@Param("status") String status, Pageable page);
	
	Page<FdsEbankCaseDetail> findByCaseStatusIn(String[] casestatus, Pageable page);
	
	Page<FdsEbankCaseDetail> findByCaseStatusInAndAutoClose(String[] casestatus, String autoClose, Pageable page);
	
	//-----------------------------Khoa chua xu ly----------------------------------------
	/**
	 * Lay danh sach cac giao dich co lien qua theo case	 
	 * @param caseno So case
	 * @return List
	 * */
	//@Query(value ="select d.case_no, to_char(to_date(substr(td.f9_oa008_cre_tms, 0, 14),'yyyyMMddHH24MISS'),'dd/mm/yyyy HH24:MI:SS') as cre_tms, d.usr_id, (select t2.case_comment from {h-schema}fds_case_status t2 where t2.cre_tms = (select max(t.cre_tms) from {h-schema}fds_case_status t where t.case_no = d.case_no) and t2.case_no = d.case_no) as case_comment, des.description, td.fx_oa008_pos_mode pos_mode, td.f9_oa008_mcc mcc, td.f9_oa008_ori_amt amount, td.f9_oa008_crncy_cde crncy_cde, (td.f9_oa008_amt_req + td.f9_oa008_load_fee) amt_req, (select listagg(r.rule_id, ',') within group(order by r.case_no) a from {h-schema}fds_case_hit_rule_detail r where r.txn_cre_tms = td.f9_oa008_cre_tms and r.txn_enc_crd_no = td.fx_oa008_used_pan and r.case_no = d.case_no group by r.txn_cre_tms) ruleid from {h-schema}fds_txn_detail td left join {h-schema}fds_case_detail d on fx_oa008_used_pan = enc_crd_no and f9_oa008_cre_tms = txn_cre_tms left join {h-schema}fds_description des on des.id = d.case_status and des.type = '"+DESC_CASE_STATUS+"' where fx_oa008_used_pan = :enccrdno  and td.f9_oa008_amt_req >= 0 order by td.f9_oa008_cre_tms desc",nativeQuery = true)
	//List<Object[]> findTransactionByCase(@Param(value = "enccrdno") String enccrdno);
	/*@Query(value ="select fdscasedetail.case_no, to_char(to_date(substr(e.f9_oa008_cre_tms, 0, 14), 'yyyyMMddHH24MISS'), 'dd/mm/yyyy HH24:MI:SS') as cre_tms, fdscasedetail.usr_id, (select t2.case_comment from {h-schema}fds_case_status t2 where t2.cre_tms = (select max(t.cre_tms) from {h-schema}fds_case_status t where t.case_no = fdscasedetail.case_no) and t2.case_no = fdscasedetail.case_no and rownum = 1) as case_comment, fdsdescription.description, e.fx_oa008_pos_mode pos_mode, e.f9_oa008_mcc mcc, e.f9_oa008_ori_amt amount, e.f9_oa008_crncy_cde crncy_cde, (e.f9_oa008_amt_req + e.f9_oa008_load_fee) amt_req, (select listagg(r.rule_id, ',') within group(order by r.case_no) a from {h-schema}fds_case_hit_rule_detail r where r.txn_cre_tms = e.f9_oa008_cre_tms and r.txn_enc_crd_no = e.fx_oa008_used_pan and r.case_no = fdscasedetail.case_no group by r.txn_cre_tms) ruleid, substr(trim(e.fx_oa008_ref_cde),1,2) ref_cde, DECODE(e.fx_oa008_given_resp_cde,'00','00',DECODE(e.FX_OA008_CRD_BRN, 'MC',(SELECT FX_OA274_MC_RESP_CDE FROM OA274@IM WHERE TRIM(PX_OA274_REF_CDE) = SUBSTR(TRIM(e.FX_OA008_REF_CDE), 1, 2)), (SELECT FX_OA274_VS_RESP_CDE FROM OA274@IM WHERE TRIM(PX_OA274_REF_CDE) = SUBSTR(TRIM(e.FX_OA008_REF_CDE), 1, 2)))) AS RESP_CDE, NVL((SELECT FX_OA126_3D_IND FROM OA126@IM WHERE PX_OA126_PAN = e.fx_oa008_used_pan AND P9_OA126_SEQ_NUM = e.p9_oa008_seq),'N') as TXN_3D_IND,e.F9_OA008_CHRG_AMT,e.fx_oa008_merc_st_cntry,e.F9_OA008_ECI_SEC_LVL from (select fdstxndetail.fx_oa008_pos_mode, fdstxndetail.f9_oa008_mcc, fdstxndetail.f9_oa008_ori_amt, fdstxndetail.f9_oa008_crncy_cde, fdstxndetail.f9_oa008_amt_req, fdstxndetail.f9_oa008_load_fee, fdstxndetail.f9_oa008_cre_tms, fdstxndetail.fx_oa008_used_pan,fdstxndetail.fx_oa008_ref_cde,fdstxndetail.F9_OA008_CHRG_AMT,fdstxndetail.fx_oa008_merc_st_cntry,fdstxndetail.FX_OA008_CRD_BRN,fdstxndetail.p9_oa008_seq,fdstxndetail.fx_oa008_given_resp_cde,fdstxndetail.F9_OA008_ECI_SEC_LVL from {h-schema}fds_txn_detail fdstxndetail where fdstxndetail.f9_oa008_dt between to_number(to_char(add_months(sysdate,-:numberofmonth),'YYYYMMDD')) and to_number(to_char(sysdate,'YYYYMMDD')) and fdstxndetail.fx_oa008_used_pan = :enccrdno and fdstxndetail.f9_oa008_amt_req >= 0) e left join {h-schema}fds_case_detail fdscasedetail on e.fx_oa008_used_pan = fdscasedetail.enc_crd_no and e.f9_oa008_cre_tms = fdscasedetail.txn_cre_tms left join {h-schema}fds_description fdsdescription on fdsdescription.id = fdscasedetail.case_status and fdsdescription.type = '"+DESC_CASE_STATUS+"' order by e.f9_oa008_cre_tms desc ",nativeQuery = true)
	List<Object[]> findTransactionByCase(@Param(value = "enccrdno") String enccrdno, @Param(value = "numberofmonth") int numberofmonth);
	
	@Query(value ="select fdscasedetail.case_no, to_char(to_date(substr(e.f9_oa008_cre_tms, 0, 14), 'yyyyMMddHH24MISS'), 'dd/mm/yyyy HH24:MI:SS') as cre_tms, fdscasedetail.usr_id, (select t2.case_comment from {h-schema}fds_case_status t2 where t2.cre_tms = (select max(t.cre_tms) from {h-schema}fds_case_status t where t.case_no = fdscasedetail.case_no) and t2.case_no = fdscasedetail.case_no and rownum = 1) as case_comment, fdsdescription.description, e.fx_oa008_pos_mode pos_mode, e.f9_oa008_mcc mcc, e.f9_oa008_ori_amt amount, e.f9_oa008_crncy_cde crncy_cde, (e.f9_oa008_amt_req + e.f9_oa008_load_fee) amt_req, (select listagg(r.rule_id, ',') within group(order by r.case_no) a from {h-schema}fds_case_hit_rule_detail r where r.txn_cre_tms = e.f9_oa008_cre_tms and r.txn_enc_crd_no = e.fx_oa008_used_pan and r.case_no = fdscasedetail.case_no group by r.txn_cre_tms) ruleid, substr(trim(e.fx_oa008_ref_cde),1,2) ref_cde, DECODE(e.fx_oa008_given_resp_cde,'00','00',DECODE(e.FX_OA008_CRD_BRN, 'MC',(SELECT FX_OA274_MC_RESP_CDE FROM OA274@IM WHERE TRIM(PX_OA274_REF_CDE) = SUBSTR(TRIM(e.FX_OA008_REF_CDE), 1, 2)), (SELECT FX_OA274_VS_RESP_CDE FROM OA274@IM WHERE TRIM(PX_OA274_REF_CDE) = SUBSTR(TRIM(e.FX_OA008_REF_CDE), 1, 2)))) AS RESP_CDE, NVL((SELECT FX_OA126_3D_IND FROM OA126@IM WHERE PX_OA126_PAN = e.fx_oa008_used_pan AND P9_OA126_SEQ_NUM = e.p9_oa008_seq),'N') as TXN_3D_IND,e.F9_OA008_CHRG_AMT,e.fx_oa008_merc_st_cntry,e.F9_OA008_ECI_SEC_LVL from (select fdstxndetail.fx_oa008_pos_mode, fdstxndetail.f9_oa008_mcc, fdstxndetail.f9_oa008_ori_amt, fdstxndetail.f9_oa008_crncy_cde, fdstxndetail.f9_oa008_amt_req, fdstxndetail.f9_oa008_load_fee, fdstxndetail.f9_oa008_cre_tms, fdstxndetail.fx_oa008_used_pan,fdstxndetail.fx_oa008_ref_cde,fdstxndetail.F9_OA008_CHRG_AMT,fdstxndetail.fx_oa008_merc_st_cntry,fdstxndetail.FX_OA008_CRD_BRN,fdstxndetail.p9_oa008_seq,fdstxndetail.fx_oa008_given_resp_cde,fdstxndetail.F9_OA008_ECI_SEC_LVL from {h-schema}fds_txn_detail fdstxndetail where fdstxndetail.fx_oa008_used_pan = :enccrdno and fdstxndetail.f9_oa008_amt_req >= 0) e left join {h-schema}fds_case_detail fdscasedetail on e.fx_oa008_used_pan = fdscasedetail.enc_crd_no and e.f9_oa008_cre_tms = fdscasedetail.txn_cre_tms left join {h-schema}fds_description fdsdescription on fdsdescription.id = fdscasedetail.case_status and fdsdescription.type = '"+DESC_CASE_STATUS+"' order by e.f9_oa008_cre_tms desc ",nativeQuery = true)
	List<Object[]> findAllTransactionByCase(@Param(value = "enccrdno") String enccrdno);
	*/
	
	/*@Query(value ="select CD.CASE_NO, HR.RULE_ID, CD.TXN_CRE_TMS TG_GD, CD.CUS_NAME TEN_KH, TR.IDMAKER MA_TK_DN, CD.TXN_USERNAME TK_DN, CD.ACCOUNTNO TK_GD, CD.TXN_IDREF MA_GD, CD.TXN_AMOUNT, CD.TXN_CHANNEL, TR.TXNID LOAI_GD, CD.CODSTATUS TT_GD, CD.CUSTOMERCODE HD_DV, CD.IDPROVIDER MA_NCC, TXN_DEST_ACC TK_NHAN, TXN_DEST_CARD THE_NHAN, USR_ID NGUOI_TIEP_NHAN, ASG_TMS TIME_TIEP_NHAN from {h-schema}FDS_EBANK_CASE_DETAIL CD left join {h-schema}FDS_EBANK_CASE_HIT_RULES HR on CD.CASE_NO = HR.CASE_NO left join {h-schema}FDS_TRANS TR on CD.TXN_IDREF = TR.IDREF WHERE TXN_USERNAME = (select TXN_USERNAME from {h-schema}FDS_EBANK_CASE_DETAIL where CASE_NO = :caseno) and to_number(substr(CRE_TMS, 0, 8)) between to_number(to_char(add_months(sysdate,-:numberofmonth),'YYYYMMDD')) and to_number(to_char(sysdate,'YYYYMMDD')) ",nativeQuery = true)
	List<Object[]> findTransactionByCase(@Param(value = "caseno") String caseno, @Param(value = "numberofmonth") int numberofmonth);
	
	@Query(value ="select CD.CASE_NO, HR.RULE_ID, CD.TXN_CRE_TMS TG_GD, CD.CUS_NAME TEN_KH, TR.IDMAKER MA_TK_DN, CD.TXN_USERNAME TK_DN, CD.ACCOUNTNO TK_GD, CD.TXN_IDREF MA_GD, CD.TXN_AMOUNT, CD.TXN_CHANNEL, TR.TXNID LOAI_GD, CD.CODSTATUS TT_GD, CD.CUSTOMERCODE HD_DV, CD.IDPROVIDER MA_NCC, TXN_DEST_ACC TK_NHAN, TXN_DEST_CARD THE_NHAN, USR_ID NGUOI_TIEP_NHAN, ASG_TMS TIME_TIEP_NHAN from {h-schema}FDS_EBANK_CASE_DETAIL CD left join {h-schema}FDS_EBANK_CASE_HIT_RULES HR on CD.CASE_NO = HR.CASE_NO left join {h-schema}FDS_TRANS TR on CD.TXN_IDREF = TR.IDREF WHERE TXN_USERNAME = (select TXN_USERNAME from {h-schema}FDS_EBANK_CASE_DETAIL where CASE_NO = :caseno) ",nativeQuery = true)
	List<Object[]> findAllTransactionByCase(@Param(value = "caseno") String caseno);
	*/
	
	
	/*@Query(value ="select distinct CD.CASE_NO, HRD.RULE_ID, HRD.TXN_CRE_TMS TG_GD, CD.CUS_NAME TEN_KH, CD.IDMAKER MA_TK_DN " +
					        ", CD.TXN_USERNAME TK_DN, CD.ACCOUNTNO TK_GD, CD.TXN_IDREF MA_GD, CD.TXN_AMOUNT " +
					        ", CD.TXN_CHANNEL, CD.TXN_TYPE LOAI_GD, CD.CODSTATUS TT_GD, CD.CUSTOMERCODE HD_DV " +
					        ", CD.IDPROVIDER MA_NCC, CD.TXN_DEST_ACC TK_NHAN, CD.TXN_DEST_CARD THE_NHAN, CD.USR_ID NGUOI_TIEP_NHAN " +
					        ", ASG_TMS TIME_TIEP_NHAN " +
					" from "+
					"( " +
					  "select distinct HRD.CASE_NO, HR.RULE_ID, HRD.TXN_CRE_TMS " +
					  "from {h-schema}FDS_EBANK_CASE_HIT_RULE_DETAIL HRD left join {h-schema}FDS_EBANK_CASE_HIT_RULES HR on HRD.CASE_NO = HR.CASE_NO and HRD.RULE_ID = HR.RULE_ID "+
					  "WHERE HRD.TXN_USERNAME = (select TXN_USERNAME from {h-schema}FDS_EBANK_CASE_DETAIL where CASE_NO = :caseno) " +
					        "and to_number(substr(HRD.TXN_CRE_TMS, 0, 8)) between to_number(to_char(add_months(sysdate, -:numberofmonth),'YYYYMMDD')) and to_number(to_char(sysdate,'YYYYMMDD')) "+
					")HRD left join {h-schema}FDS_EBANK_CASE_DETAIL CD on HRD.CASE_NO = CD.CASE_NO   "+ 
					"order by CASE_NO,RULE_ID, TG_GD ",nativeQuery = true)*/
	
	@Query(value ="select CD.CASE_NO, HR.RULE_ID, TXN_CRE_TMS TG_GD, CD.CUS_NAME TEN_KH, CD.IDMAKER MA_TK_DN  "+
				        ", CD.TXN_USERNAME TK_DN, CD.ACCOUNTNO TK_GD, CD.TXN_IDREF MA_GD, CD.TXN_AMOUNT  "+
				        ", CD.TXN_CHANNEL, CD.TXN_TYPE LOAI_GD, CD.CODSTATUS TT_GD, CD.CUSTOMERCODE HD_DV "+
				        ", CD.IDPROVIDER MA_NCC, CD.TXN_DEST_ACC TK_NHAN, CD.TXN_DEST_CARD THE_NHAN, CD.USR_ID NGUOI_TIEP_NHAN  "+
				        ", CD.ASG_TMS TIME_TIEP_NHAN  "+
				        ", CD.CASE_STATUS  "+
				        ", (select t2.CASE_COMMENT from {h-schema}fds_case_status t2 where t2.cre_tms = (select max(t.CRE_TMS) from {h-schema}fds_case_status t where t.CASE_NO = CD.case_no) and t2.CASE_NO = CD.CASE_NO and rownum = 1) as case_comment  "+
				"from {h-schema}FDS_EBANK_CASE_DETAIL CD left join {h-schema}FDS_EBANK_CASE_HIT_RULES HR on CD.CASE_NO = HR.CASE_NO  "+
				"where TXN_USERNAME = (select TXN_USERNAME from {h-schema}FDS_EBANK_CASE_DETAIL where CASE_NO = :caseno)  "+
				      "and to_number(substr(TXN_CRE_TMS, 0, 8)) between to_number(to_char(add_months(sysdate, -:numberofmonth),'YYYYMMDD')) and to_number(to_char(sysdate,'YYYYMMDD'))  "+
				"order by CD.TXN_CRE_TMS DESC ",nativeQuery = true) //CD.CASE_NO, HR.RULE_ID
	List<Object[]> findTransactionByCase(@Param(value = "caseno") String caseno, @Param(value = "numberofmonth") int numberofmonth);
	
	@Query(value ="select CD.CASE_NO, HR.RULE_ID, TXN_CRE_TMS TG_GD, CD.CUS_NAME TEN_KH, CD.IDMAKER MA_TK_DN  "+
				        ", CD.TXN_USERNAME TK_DN, CD.ACCOUNTNO TK_GD, CD.TXN_IDREF MA_GD, CD.TXN_AMOUNT  "+
				        ", CD.TXN_CHANNEL, CD.TXN_TYPE LOAI_GD, CD.CODSTATUS TT_GD, CD.CUSTOMERCODE HD_DV "+
				        ", CD.IDPROVIDER MA_NCC, CD.TXN_DEST_ACC TK_NHAN, CD.TXN_DEST_CARD THE_NHAN, CD.USR_ID NGUOI_TIEP_NHAN  "+
				        ", CD.ASG_TMS TIME_TIEP_NHAN  "+
				        ", CD.CASE_STATUS  "+
				        ", (select t2.CASE_COMMENT from {h-schema}fds_case_status t2 where t2.cre_tms = (select max(t.CRE_TMS) from {h-schema}fds_case_status t where t.CASE_NO = CD.case_no) and t2.CASE_NO = CD.CASE_NO and rownum = 1) as case_comment  "+
				"from {h-schema}FDS_EBANK_CASE_DETAIL CD left join {h-schema}FDS_EBANK_CASE_HIT_RULES HR on CD.CASE_NO = HR.CASE_NO  "+
				"where TXN_USERNAME = (select TXN_USERNAME from {h-schema}FDS_EBANK_CASE_DETAIL where CASE_NO = :caseno)  "+
				"order by CD.TXN_CRE_TMS DESC  ",nativeQuery = true) //CD.CASE_NO, HR.RULE_ID, 
	List<Object[]> findAllTransactionByCase(@Param(value = "caseno") String caseno);
	//-----------------------------Khoa chua xu ly----------------------------------------
	
		
	//https://stackoverflow.com/questions/2123438/hibernate-how-to-set-null-query-parameter-value-with-hql
	@Query(value = "select f from FdsEbankCaseDetail f where f.txnCreTms between :fromDate and :toDate "
			+ "and (:caseNo is null or f.caseNo =:caseNo) "
			+ "and (:userid is null or f.usrId =:userid) "
			+ "and (:status is null or f.caseStatus =:status) "
			+ "and (:tkLogin is null or f.txnUsername =:tkLogin) "
			+ "and (:cifNo is null or f.txnCustomer =:cifNo) ")
	Page<FdsEbankCaseDetail> searchCase(@Param(value = "fromDate") BigDecimal fromDate, 
			@Param(value = "toDate") BigDecimal toDate,
			@Param(value = "caseNo") String caseNo,
			@Param(value = "userid") String userid,
			@Param(value = "status") String status,
			@Param(value = "tkLogin") String tkLogin,
			@Param(value = "cifNo") String cifNo,
			Pageable page);
	
	@Query(value = "select CW_OBDX_FDS.FN_LOCK_USER_FDS@FCATSMSLINK(:accountCustomer, :chanel, :userMaker) from dual", nativeQuery = true)
	int lockAccountEBank(@Param(value = "accountCustomer") String accountCustomer,
			             @Param(value = "chanel") String chanel,
			             @Param(value = "userMaker") String userMaker);
	
	
}

