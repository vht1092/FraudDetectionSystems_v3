package com.fds.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the FDS_EBANK_SMS database table.
 * 
 */
@Entity
@Table(name="FDS_EBANK_SMS")
@NamedQuery(name="FdsEbankSms.findAll", query="SELECT f FROM FdsEbankSms f")
public class FdsEbankSms implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CRE_TMS", nullable = false, precision = 17)
	private BigDecimal creTms;
	
	@Column(name = "USR_ID", nullable = false, length = 12)
	private String usrId;
	
	
	@Column(name = "PHONE", nullable = false, length = 10)
	private String phone;
	
	@Column(name = "SMS_DETAIL", nullable = false, length = 255)
	private String smsDetail;
	
	@Column(name = "SMS_TYPE", nullable = false, length = 12)
	private String smsType;
	
	
	public FdsEbankSms() {
	}


	/**
	 * @return the creTms
	 */
	public BigDecimal getCreTms() {
		return creTms;
	}


	/**
	 * @param creTms the creTms to set
	 */
	public void setCreTms(BigDecimal creTms) {
		this.creTms = creTms;
	}


	/**
	 * @return the usrId
	 */
	public String getUsrId() {
		return usrId;
	}


	/**
	 * @param usrId the usrId to set
	 */
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}


	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}


	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}


	/**
	 * @return the smsDetail
	 */
	public String getSmsDetail() {
		return smsDetail;
	}


	/**
	 * @param smsDetail the smsDetail to set
	 */
	public void setSmsDetail(String smsDetail) {
		this.smsDetail = smsDetail;
	}


	/**
	 * @return the smsType
	 */
	public String getSmsType() {
		return smsType;
	}


	/**
	 * @param smsType the smsType to set
	 */
	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}


}