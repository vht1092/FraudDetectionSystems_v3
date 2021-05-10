package com.fds.repositories;

import java.util.List;

import javax.persistence.EntityManager;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import com.fds.entities.FdsEbankCaseDetail;

public class CaseEbankDetailRepoImpl implements CaseEbankDetailRepoCustom {
	@PersistenceContext
	private EntityManager em;
	@Value("${spring.jpa.properties.hibernate.default_schema}")
	private String SCHEMA;
	
	/*@SuppressWarnings("unchecked")
	@Override
	public Page<FdsEbankCaseDetail> searchCase(String sQuery) {
		final StringBuilder sbQuery = new StringBuilder(
				  " select CASE_NO, ACCOUNTNO, ASG_TMS, CASE_STATUS, CODSTATUS, CHECK_NEW, CUS_NAME, CUSTOMERCODE, IDPROVIDER,PHONENUMBER "
			    + "   , TXN_AMOUNT, TXN_CHANNEL, TXN_CRE_TMS, TXN_CUSTOMER, TXN_DEST_ACC, TXN_IDREF, TXN_TYPE, TXN_USERNAME "
			    + "   , UPD_TMS, UPD_UID, USR_ID, CUS_TYPE, IDMAKER, EMAIL, CUS_ADDR, CUS_MST "
			    + " from FCDB_ADMIN_PROD.FDS_EBANK_CASE_DETAIL fdscasedetail "
			    + " where 1 = 1 "
			    + sQuery);
		//" select * from FDS_EBANK_CASE_DETAIL"); 
		sbQuery.append(" order by TXN_CRE_TMS");
		final Query query = em.createNativeQuery(sbQuery.toString(), FdsEbankCaseDetail.class);
		return new PageImpl<FdsEbankCaseDetail>(query.getResultList());
	}*/
	
	
		
	
	/*@SuppressWarnings("unchecked")
	@Override
	public Page<FdsCaseDetail> searchCase11(final String sQuery, final String fromdate, final String todate) {
		final StringBuilder sbQuery = new StringBuilder(
				"select fdscasedetail.cre_tms, fdscasedetail.upd_tms, fdscasedetail.upd_uid, fdscasedetail.asg_tms, fdscasedetail.init_usr_id, fdscasedetail.usr_id, fdscasedetail.cif_no, fdscasedetail.case_no, fdscasedetail.amount, fdscasedetail.avl_bal, fdscasedetail.avl_bal_crncy, fdscasedetail.case_status, fdscasedetail.check_new, fdscasedetail.sms_flg, fdscasedetail.enc_crd_no, fdscasedetail.mcc, fdscasedetail.init_asg_tms, fdscasedetail.txn_cre_tms, fdscasedetail.crd_brn, fdscasedetail.merc_name, fdscasedetail.crncy_cde, fdscasedetail.pos_mode, fdscasedetail.resp_cde, fdscasedetail.txn_stat, fdscasedetail.txn_3d_ind, fdscasedetail.txn_3d_eci, fdscasedetail.loc from "
						+ SCHEMA + ".fds_case_detail fdscasedetail join " + SCHEMA
						+ ".fds_txn_detail fdstxndetail on fdscasedetail.txn_cre_tms = fdstxndetail.f9_oa008_cre_tms and fdscasedetail.enc_crd_no = fdstxndetail.fx_oa008_used_pan where 1 = 1"
						+ sQuery);
		sbQuery.append(" order by fdscasedetail.cre_tms");
		final Query query = em.createNativeQuery(sbQuery.toString(), FdsCaseDetail.class);
		return new PageImpl<FdsCaseDetail>(query.getResultList());
	}*/

	
	

}
