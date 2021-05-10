package com.fds.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fds.entities.CustomerInfo;

//private String cust_name;
//private String cust_gendr;
//private String cust_hp;
//private String cust_off_tel_1;
//private String cust_off_tel_2;
//private String cust_cif;
@Repository
public interface CustomerInfoRepo extends JpaRepository<CustomerInfo, String> {
	
	//@Query(value = "select trim(fx_ir056_name) as cust_name, case trim(fx_ir056_ttl) when 'MISS' then 'Bà' when 'MR' then 'Ông' when 'MRS' then 'Bà' else '' end cust_gendr, fx_ir056_hp as cust_hp, fx_ir056_off_tel_1 as cust_off_tel_1, fx_ir056_off_tel_2 as cust_off_tel_2, fx_ir056_email_addr as cust_email_addr, fx_ir056_cif_no as cust_cif from ir056@im.world join FDS_SYS_TASK s on trim(fx_ir056_cif_no) = s.objecttask where s.typetask =:typetask ", nativeQuery = true)
	//List<CustomerInfo> findAllTypetask(@Param("typetask") String typetask);
	
	//@Query(value = "select trim(CUS_NAME) as cust_name, ' ' cust_gendr,  PHONENUMBER as cust_hp, ' ' as cust_off_tel_1, ' ' as cust_off_tel_2,  NVL(EMAIL,' ') as cust_email_addr,  TXN_CUSTOMER as cust_cif from {h-schema}FDS_EBANK_CASE_DETAIL join {h-schema}FDS_SYS_TASK s on trim(TXN_CUSTOMER) = s.objecttask where s.typetask =:typetask ", nativeQuery = true)
	//List<CustomerInfo> findAllTypetask(@Param("typetask") String typetask);
	
	//cust_hp = TEL_EB, cust_off_tel_1 = TEL_MB, cust_off_tel_2 = EMAIL_EB, cust_email_addr = EMAIL_MB
	//@Query(value = "SELECT CUS_NAME as cust_name, NVL(SALUTION, ' ') cust_gendr, NVL(PHONENUMBER, ' ') cust_hp, NVL(CUS_TEL, ' ') cust_off_tel_1, NVL(EMAIL, ' ') cust_off_tel_2, NVL(CUS_EMAIL, ' ') cust_email_addr, CIF_CORE as cust_cif FROM VNINFOSCB.MB_CUSTOMER@FDSMBANKING, fcdb_admin_prod.mstuser a,fcdb_admin_prod.usercustrel b WHERE trim(CIF_CORE) in (select trim(objecttask) from {h-schema}FDS_SYS_TASK s WHERE s.typetask =:typetask ) and a.iduser = b.iduser and trim(b.idcust) in (select trim(objecttask) from {h-schema}FDS_SYS_TASK s WHERE s.typetask =:typetask) ", nativeQuery = true)
	/*@Query(value = " select distinct cust_name, cust_gendr, cust_hp, cust_off_tel_1, cust_off_tel_2, cust_email_addr, cust_cif from " +
				   " ( " +
						" SELECT NVL(trim(MB.CUS_NAME), IB.CUS_NAME_IB) cust_name " +
						" , IB.cust_gendr " +
						" , IB.cust_hp " +
						" , NVL(MB.CUS_TEL,' ') cust_off_tel_1 " +
						" ,IB.cust_off_tel_2 " +
						" , NVL(MB.CUS_EMAIL, ' ') cust_email_addr " +
						" , NVL(trim(MB.CIF_CORE), IB.CIF_IB) as cust_cif " + 
						" FROM VNINFOSCB.MB_CUSTOMER@mbanking MB full join " +
						" ( " +
							" select distinct b.idcust CIF_IB, b.NAMCUST CUS_NAME_IB, ' ' cust_gendr, NVL(a.PHONENUMBER, ' ') cust_hp, NVL(a.EMAIL, ' ') cust_off_tel_2 " +
							" from mstuser@fcatsmslink a inner join usercustrel@fcatsmslink b on a.iduser = b.iduser  " +
						" )IB on MB.CIF_CORE = IB.CIF_IB " +
				   " )EB join {h-schema}FDS_SYS_TASK s on trim(cust_cif) = trim(s.objecttask)  " +
				   " where s.typetask =:typetask", nativeQuery = true)
	List<CustomerInfo> findAllTypetask(@Param("typetask") String typetask);*/
	
	
	
	@Query(value = " select distinct cust_name, cust_gendr, cust_hp, cust_off_tel_1, cust_off_tel_2, cust_email_addr, cust_cif " +
				   " from " +
				   " ( " +
				   "	SELECT NVL(trim(MB.CUS_NAME), IB.CUS_NAME_IB) cust_name " +
				   "           , IB.cust_gendr " +
				   "           , IB.cust_hp " +
				   "           , NVL(MB.CUS_TEL,' ') cust_off_tel_1 " +
				   "           , IB.cust_off_tel_2 " +
				   "           , NVL(MB.CUS_EMAIL, ' ') cust_email_addr " +
				   "           , NVL(trim(MB.CIF_CORE), IB.CIF_IB) as cust_cif " +
				   "    FROM VNINFOSCB.MB_CUSTOMER@mbanking MB full join " +
				   "    ( " +
				   "       SELECT distinct A.PARTY_ID CIF_IB, C.TXTCORPDESC CUS_NAME_IB,  ' ' cust_gendr, " +
				   "              NVL(DECODE(C.typecust,'I',C.mobile_number,'C',B.MOBILENO), ' ') cust_hp, " +
				   "              NVL(B.EMAIL, ' ') cust_off_tel_2 " +
				   "       FROM OBDX_ADMIN_PROD.DIGX_UM_USERPARTY_RELATION@FCATSMSLINK A " +
				   "            LEFT JOIN OBDX_ADMIN_PROD.DIGX_UM_USERPROFILE@FCATSMSLINK B ON B.U_NAME = A.USER_ID " +
				   "            LEFT JOIN ubs_uat4_181.FCC_VW_CUSTOMER_DETAILS@fcatfcclink C ON a.party_id = c.idcorporate " +
				   "    )IB on MB.CIF_CORE = IB.CIF_IB  " +
				   " )EB join {h-schema}FDS_SYS_TASK s on trim(cust_cif) = trim(s.objecttask)  " +
				   " where s.TYPETASK =:typetask", nativeQuery = true)
	List<CustomerInfo> findAllTypetask(@Param("typetask") String typetask);
	
	
	
	
	
	
	
	
	
	

	//@Query(value = "select trim(fx_ir056_name) as cust_name, case trim(fx_ir056_ttl) when 'MISS' then 'Bà' when 'MR' then 'Ông' when 'MRS' then 'Bà' else '' end cust_gendr, fx_ir056_hp as cust_hp, fx_ir056_off_tel_1 as cust_off_tel_1, fx_ir056_off_tel_2 as cust_off_tel_2, fx_ir056_email_addr as cust_email_addr, fx_ir056_cif_no as cust_cif from ir056@im.world where rownum = 1 and fx_ir056_cif_no = :cifno ", nativeQuery = true)
	//CustomerInfo findAll(@Param("cifno") String cifno);
	
	@Query(value = "select trim(FX_DW002_NAME) as cust_name, case trim(FX_DW002_TTL) when 'MISS' then 'Bà' when 'MR' then 'Ông' when 'MRS' then 'Bà' else '' end cust_gendr, FX_DW002_HP cust_hp, FX_DW002_OFF_TEL_1 as cust_off_tel_1, FX_DW002_OFF_TEL_2 as cust_off_tel_2, FX_DW002_EMAIL_ADDR as cust_email_addr, FX_DW002_CIF_NO as cust_cif from dw002 where rownum = 1 and FX_DW002_CIF_NO =  :cifno  ", nativeQuery = true)
	CustomerInfo findAll(@Param("cifno") String cifno);
	
	@Query(value = "select trim(FX_DW002_NAME) as cust_name, case trim(FX_DW002_TTL) when 'MISS' then 'Bà' when 'MR' then 'Ông' when 'MRS' then 'Bà' else '' end cust_gendr, FX_DW002_HP cust_hp, FX_DW002_OFF_TEL_1 as cust_off_tel_1, FX_DW002_OFF_TEL_2 as cust_off_tel_2, FX_DW002_EMAIL_ADDR as cust_email_addr, FX_DW002_CIF_NO as cust_cif from dw002 where rownum = 1 and trim(FX_DW002_CIF_NO) =?1", nativeQuery = true)
	CustomerInfo findByCifNo(String cifno);

	//@Query(value = "select trim(fx_ir056_name) as cust_name, case trim(fx_ir056_ttl) when 'MISS' then 'Bà' when 'MR' then 'Ông' when 'MRS' then 'Bà' else '' end cust_gendr, fx_ir056_hp as cust_hp, fx_ir056_off_tel_1 as cust_off_tel_1, fx_ir056_off_tel_2 as cust_off_tel_2, fx_ir056_email_addr as cust_email_addr, fx_ir056_cif_no as cust_cif from ir056@im.world where rownum = 1 and trim(fx_ir056_cif_no) =?1", nativeQuery = true)
	//CustomerInfo findOneAll(String cifno);
	
	//@Query(value = "select trim(CUS_NAME) as cust_name, ' ' as cust_gendr, PHONENUMBER as cust_hp, ' ' cust_off_tel_1, ' ' cust_off_tel_2, NVL(EMAIL,' ') as cust_email_addr, TXN_CUSTOMER as cust_cif from {h-schema}FDS_EBANK_CASE_DETAIL where rownum = 1 and trim(TXN_CUSTOMER) =?1", nativeQuery = true)
    //CustomerInfo findOneAll(String cifno);
	
	//cust_hp = TEL_EB, cust_off_tel_1 = TEL_MB, cust_off_tel_2 = EMAIL_EB, cust_email_addr = EMAIL_MB
	/*@Query(value = " SELECT NVL(trim(MB.CUS_NAME), IB.CUS_NAME_IB) cust_name " +
					      " , IB.cust_gendr " +
					      " , IB.cust_hp " +
					      " , NVL(MB.CUS_TEL,' ') cust_off_tel_1 " +
					      " ,IB.cust_off_tel_2 " +
					      " , NVL(MB.CUS_EMAIL, ' ') cust_email_addr " +
					      " , NVL(trim(MB.CIF_CORE), IB.CIF_IB) as cust_cif " +
					" FROM VNINFOSCB.MB_CUSTOMER@mbanking MB full join " +
					" ( " +
					    " select b.idcust CIF_IB, b.NAMCUST CUS_NAME_IB, NVL(a.SALUTION, ' ') cust_gendr, NVL(a.PHONENUMBER, ' ') cust_hp, NVL(a.EMAIL, ' ') cust_off_tel_2 " +
					    " from mstuser@fcatsmslink a inner join usercustrel@fcatsmslink b on a.iduser = b.iduser " +
					    " where b.idcust=?1 " +
					" )IB on MB.CIF_CORE = IB.CIF_IB " +
					" where rownum = 1 and (IB.CIF_IB =?1 or MB.CIF_CORE =?1)", nativeQuery = true)
    CustomerInfo findOneAll(String cifno);*/
	
	
	
	
	
	
	
	
	@Query(value = " select distinct cust_name, cust_gendr, cust_hp, cust_off_tel_1, cust_off_tel_2, cust_email_addr, cust_cif " +
			   " from ( " +
			   " select NVL(trim(MB.CUS_NAME), IB.CUS_NAME_IB) cust_name, IB.cust_gendr, IB.cust_hp " +
			   "         , NVL(MB.CUS_TEL, ' ') cust_off_tel_1, IB.cust_off_tel_2, NVL(MB.CUS_EMAIL, ' ') cust_email_addr " +
			   "	     , NVL(trim(MB.CIF_CORE), IB.CIF_IB) as cust_cif  " +
			   "  from (SELECT * FROM VNINFOSCB.MB_CUSTOMER@mbanking where CUS_STATUS<>'9')MB  " +
			   "  full join (SELECT distinct A.PARTY_ID CIF_IB, C.TXTCORPDESC CUS_NAME_IB, ' ' cust_gendr  " +
			   "                   , NVL(DECODE(C.typecust, 'I', C.mobile_number, 'C', B.MOBILENO), ' ') cust_hp  " +
			   "                   , NVL(B.EMAIL, ' ') cust_off_tel_2  " +
			   "              FROM OBDX_ADMIN_PROD.DIGX_UM_USERPARTY_RELATION@FCATSMSLINK A  " +
			   "              LEFT JOIN OBDX_ADMIN_PROD.DIGX_UM_USERPROFILE@FCATSMSLINK B ON B.U_NAME = A.USER_ID  " +
			   "              LEFT JOIN ubs_uat4_181.FCC_VW_CUSTOMER_DETAILS@fcatfcclink C ON a.party_id = c.idcorporate " +
			   "            ) IB  " +
			   " on MB.CIF_CORE = IB.CIF_IB " +
			   " )  " +
			   " where cust_cif = ?1" , nativeQuery = true)
	CustomerInfo findOneAll(String cifno);
	
		

	/**
	 * Lay ten khach hang (chinh/phu) theo so the
	 */
	@Query(value = "select decode((select trim(fx_ir025_emb_lst_nm) || ' ' || trim(fx_ir025_emb_mid_nm) || ' ' || trim(fx_ir025_emb_name) from ir025@im.world where px_ir025_pan = :enccardno union select trim(fx_ir275_emb_lst_nm) || ' ' || trim(fx_ir275_emb_mid_nm) || ' ' || trim(fx_ir275_emb_name) from ir275@im.world where px_ir275_own_pan = :enccardno), null, (select trim(fx_ir025_emb_lst_nm) || ' ' || trim(fx_ir025_emb_mid_nm) || ' ' || trim(fx_ir025_emb_name) from ir025@im.world where FX_IR025_REF_PAN = :enccardno union select trim(fx_ir275_emb_lst_nm) || ' ' || trim(fx_ir275_emb_mid_nm) || ' ' || trim(fx_ir275_emb_name) from ir275@im.world where FX_IR275_REF_PAN = :enccardno ), (select trim(fx_ir025_emb_lst_nm) || ' ' || trim(fx_ir025_emb_mid_nm) || ' ' || trim(fx_ir025_emb_name) from ir025@im.world where px_ir025_pan = :enccardno union select trim(fx_ir275_emb_lst_nm) || ' ' || trim(fx_ir275_emb_mid_nm) || ' ' || trim(fx_ir275_emb_name) from ir275@im.world where px_ir275_own_pan = :enccardno)) custname from dual", nativeQuery = true)
	String getCustNameByEncCrdNo(String enccardno);

	/**
	 * Lay Loc (chinh/phu) theo so the
	 */
	@Query(value = "select decode((select f9_ir025_loc_acct from ir025@im.world where px_ir025_pan = :enccardno union select f9_ir275_loc_acct from ir275@im.world where px_ir275_own_pan = :enccardno), null, (select f9_ir025_loc_acct from ir025@im.world where fx_ir025_ref_pan = :enccardno union select f9_ir275_loc_acct from ir275@im.world where fx_ir275_ref_pan = :enccardno), (select f9_ir025_loc_acct from ir025@im.world where px_ir025_pan = :enccardno union select f9_ir275_loc_acct from ir275@im.world where px_ir275_own_pan = :enccardno)) loc from dual", nativeQuery = true)
	String getLocByEncCrdNo(String enccardno);

	/*
	 * select trim(fx_ir056_name) as cust_name,
	 * decode(fx_ir056_gendr, 'F', 'Bà', 'Ông') as cust_gendr,
	 * fx_ir056_hp as cust_hp,
	 * fx_ir056_hme_tel as cust_off_tel_1,
	 * fx_ir056_off_tel_1 as cust_off_tel_2,
	 * fx_ir056_email_addr as cust_email_addr,
	 * fx_ir056_cif_no as cust_cif
	 * from ir056@im.world
	 * where p9_ir056_crn = (select f9_ir275_crn
	 * from ir275@im.world
	 * where px_ir275_own_pan = :crdno)
	 * union
	 * select trim(fx_ir056_name) as cust_name,
	 * decode(fx_ir056_gendr, 'F', 'Bà', 'Ông') as cust_gendr,
	 * fx_ir056_hp as cust_hp,
	 * fx_ir056_hme_tel as cust_off_tel_1,
	 * fx_ir056_off_tel_1 as cust_off_tel_2,
	 * fx_ir056_email_addr as cust_email_addr,
	 * fx_ir056_cif_no as cust_cif
	 * from ir056@im.world
	 * where p9_ir056_crn =
	 * (select f9_ir025_crn from ir025@im.world where px_ir025_pan = :crdno)
	 */

	@Query(value = "select trim(fx_ir056_name) as cust_name, case trim(fx_ir056_ttl) when 'MISS' then 'Bà' when 'MR' then 'Ông' when 'MRS' then 'Bà' else '' end cust_gendr, fx_ir056_hp as cust_hp, fx_ir056_hme_tel as cust_off_tel_1, fx_ir056_off_tel_1 as cust_off_tel_2, fx_ir056_email_addr as cust_email_addr, fx_ir056_cif_no as cust_cif from ir056@im.world where p9_ir056_crn = (select f9_ir275_crn from ir275@im.world where px_ir275_own_pan = :crdno) union select trim(fx_ir056_name) as cust_name, case trim(fx_ir056_ttl) when 'MISS' then 'Bà' when 'MR' then 'Ông' when 'MRS' then 'Bà' else '' end cust_gendr, fx_ir056_hp as cust_hp, fx_ir056_hme_tel as cust_off_tel_1, fx_ir056_off_tel_1 as cust_off_tel_2, fx_ir056_email_addr as cust_email_addr, fx_ir056_cif_no as cust_cif from ir056@im.world where p9_ir056_crn = (select f9_ir025_crn from ir025@im.world where px_ir025_pan = :crdno) ", nativeQuery = true)
	CustomerInfo findByCrdNo(@Param("crdno") String crdno);
}

/*
 * select trim(fx_ir056_name) as cust_name,
 * decode(fx_ir056_gendr, 'F', 'Bà', 'Ông') as cust_gendr,
 * trim(fx_ir056_hp) as cust_hp,
 * trim(fx_ir056_off_tel_1) as cust_off_tel_1,
 * trim(fx_ir056_off_tel_2) as cust_off_tel_2,
 * trim(fx_ir056_email_addr) as cust_email_addr,
 * trim(fx_ir056_cif_no) as cust_cif
 * from ir056@im.world
 * where p9_ir056_crn =
 * (select F9_IR275_CRN
 * from ir275@im.world
 * where PX_IR275_OWN_PAN = '48E219A099958F1CXXX')
 * union
 * select trim(fx_ir056_name) as cust_name,
 * decode(fx_ir056_gendr, 'F', 'Bà', 'Ông') as cust_gendr,
 * trim(fx_ir056_hp) as cust_hp,
 * trim(fx_ir056_off_tel_1) as cust_off_tel_1,
 * trim(fx_ir056_off_tel_2) as cust_off_tel_2,
 * trim(fx_ir056_email_addr) as cust_email_addr,
 * trim(fx_ir056_cif_no) as cust_cif
 * from ir056@im.world
 * where p9_ir056_crn =
 * (select F9_IR025_CRN
 * from ir025@im.world
 * where PX_IR025_PAN = '48E219A099958F1CXXX');
 */
