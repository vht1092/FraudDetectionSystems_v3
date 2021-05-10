package com.fds.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the FDS_EBANK_CASE_DETAIL database table.
 * 
 */
@Entity
@Table(name="FDS_EBANK_CASE_DETAIL")
@NamedQuery(name="FdsEbankCaseDetail.findAll", query="SELECT f FROM FdsEbankCaseDetail f")
public class FdsEbankCaseDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CASE_NO", unique=true, nullable=false, length=50)
	private String caseNo;

	@Column(length=30)
	private String accountno;
	
	@Column(name = "ASG_TMS", nullable = false, precision = 17)
	private BigDecimal asgTms;

	@Column(name="CASE_STATUS", length=1)
	private String caseStatus;

	@Column(length=40)
	private String codstatus;
	
	@Column(name="RES_STATUS", length=10)  
	private String resStatus;
	
	@Column(name = "CHECK_NEW", nullable = false, length = 1)
	private String checkNew;

	@Column(name="CUS_NAME", length=200)
	private String cusName;

	@Column(length=50)
	private String customercode;

	@Column(length=50)
	private String idprovider;

	@Column(length=15)
	private String phonenumber;

	@Column(name="TXN_AMOUNT", precision=22, scale=3)
	private BigDecimal txnAmount;

	@Column(name="TXN_CHANNEL", length=10)
	private String txnChannel;

	@Column(name="TXN_CRE_TMS")
	private BigDecimal txnCreTms;

	@Column(name="TXN_CUSTOMER", nullable=false, length=16)
	private String txnCustomer;

	@Column(name="TXN_DEST_ACC", length=50)
	private String txnDestAcc;

	@Column(name="TXN_IDREF", nullable=false, length=20)
	private String txnIdref;

	@Column(name="TXN_TYPE", nullable=false, length=10)
	private String txnType;

	@Column(name="TXN_USERNAME", length=50)
	private String txnUsername;
	
	@Column(name="UPD_TMS")
	private BigDecimal updTms;

	@Column(name="UPD_UID", length=20)
	private String updUid;
	
	@Column(name = "USR_ID", nullable = false, length = 12)
	private String usrId;
	
	@Column(name = "CUS_TYPE", length = 50)
	private String cusType;
	
	@Column(name = "IDMAKER", length = 20)
	private String idMaker;
	
	@Column(name = "EMAIL", length = 100)
	private String email;
	
	@Column(name = "CUS_ADDR", length = 500)
	private String cusAddr;
	
	@Column(name = "CUS_MST", length = 20)
	private String cusMST;
	
	@Column(name = "AUTOCLOSE", length = 1)
	private String autoClose;

	//bi-directional many-to-many association to FdsRule
	@ManyToMany(fetch = FetchType.EAGER)	
	@JoinTable(name = "FDS_EBANK_CASE_HIT_RULES", joinColumns = { @JoinColumn(name = "CASE_NO", nullable = false) }, inverseJoinColumns = {
			@JoinColumn(name = "RULE_ID", nullable = false) })	
	private List<FdsEbankRule> fdsRules;
	
	public FdsEbankCaseDetail() {
	}

	public String getCaseNo() {
		return this.caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getAccountno() {
		return this.accountno;
	}

	public void setAccountno(String accountno) {
		this.accountno = accountno;
	}

	public BigDecimal getAsgTms() {
		return asgTms;
	}

	public void setAsgTms(BigDecimal asgTms) {
		this.asgTms = asgTms;
	}

	public String getCaseStatus() {
		return this.caseStatus;
	}

	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
	}

	public String getCodstatus() {
		return this.codstatus;
	}

	public void setCodstatus(String codstatus) {
		this.codstatus = codstatus;
	}
	
	public String getResStatus() {
		return this.resStatus;
	}

	public void setResStatus(String resStatus) {
		this.resStatus = resStatus;
	}
	
	public String getCheckNew() {
		return this.checkNew;
	}

	public void setCheckNew(String checkNew) {
		this.checkNew = checkNew;
	}

	public String getCusName() {
		return this.cusName;
	}

	public void setCusName(String cusName) {
		this.cusName = cusName;
	}

	public String getCustomercode() {
		return this.customercode;
	}

	public void setCustomercode(String customercode) {
		this.customercode = customercode;
	}

	public String getIdprovider() {
		return this.idprovider;
	}

	public void setIdprovider(String idprovider) {
		this.idprovider = idprovider;
	}

	public String getPhonenumber() {
		return this.phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public BigDecimal getTxnAmount() {
		return this.txnAmount;
	}

	public void setTxnAmount(BigDecimal txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getTxnChannel() {
		return this.txnChannel;
	}

	public void setTxnChannel(String txnChannel) {
		this.txnChannel = txnChannel;
	}

	public BigDecimal getTxnCreTms() {
		return this.txnCreTms;
	}

	public void setTxnCreTms(BigDecimal txnCreTms) {
		this.txnCreTms = txnCreTms;
	}

	public String getTxnCustomer() {
		return this.txnCustomer;
	}

	public void setTxnCustomer(String txnCustomer) {
		this.txnCustomer = txnCustomer;
	}

	public String getTxnDestAcc() {
		return this.txnDestAcc;
	}

	public void setTxnDestAcc(String txnDestAcc) {
		this.txnDestAcc = txnDestAcc;
	}

	public String getTxnIdref() {
		return this.txnIdref;
	}

	public void setTxnIdref(String txnIdref) {
		this.txnIdref = txnIdref;
	}

	public String getTxnType() {
		return this.txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getTxnUsername() {
		return this.txnUsername;
	}

	public void setTxnUsername(String txnUsername) {
		this.txnUsername = txnUsername;
	}

	public BigDecimal getUpdTms() {
		return this.updTms;
	}

	public void setUpdTms(BigDecimal updTms) {
		this.updTms = updTms;
	}
	
	public String getUpdUid() {
		return this.updUid;
	}

	public void setUpdUid(String updUid) {
		this.updUid = updUid;
	}

	public List<FdsEbankRule> getFdsRules() {
		return this.fdsRules;
	}

	public void setFdsRules(List<FdsEbankRule> fdsRules) {
		this.fdsRules = fdsRules;
	}
	
	public String getUsrId() {
		return this.usrId;
	}

	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}
	
	public String getCusType() {
		return this.cusType;
	}

	public void setCusType(String cusType) {
		this.cusType = cusType;
	}

	public String getIdMaker() {
		return idMaker;
	}

	public void setIdMaker(String idMaker) {
		this.idMaker = idMaker;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCusAddr() {
		return cusAddr;
	}

	public void setCusAddr(String cusAddr) {
		this.cusAddr = cusAddr;
	}

	public String getCusMST() {
		return cusMST;
	}

	public void setCusMST(String cusMST) {
		this.cusMST = cusMST;
	}

	/**
	 * @return the autoClose
	 */
	public String getAutoClose() {
		return autoClose;
	}

	/**
	 * @param autoClose the autoClose to set
	 */
	public void setAutoClose(String autoClose) {
		this.autoClose = autoClose;
	}

	
}