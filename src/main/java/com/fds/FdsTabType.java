package com.fds;

import com.fds.components.*;
import com.fds.components.reports.*;
import com.vaadin.ui.Component;

/**
 * Danh sach cac component duoc them vao tabsheet
 * Khi them moi cap nhat vao table FDS_DESCRIPTION de load vao menu, FDS_DESCRIPTION.DESCRIPTION can co noi dung nhu caption cua class
 * @see com.fds.views.MainView
 * */

public enum FdsTabType {

	INBOXS(Inbox.class,Inbox.CAPTION), 
	CASEDISTRIBUTION(CaseDistribution.class,CaseDistribution.CAPTION),
	CLOSECASE(ClosedCase.class,ClosedCase.CAPTION),
	AUTOCLOSECASE(AutoClosedCase.class,AutoClosedCase.CAPTION),
	EXCEPTIONCASE(ExceptionCase.class,ExceptionCase.CAPTION),
	SEARCH(Search.class,Search.CAPTION),
	FOLLOWUP(FollowUp.class,FollowUp.CAPTION),
	TRANSFERLVL2(TransferLvl2.class,TransferLvl2.CAPTION),
	ROLELIST(RoleList.class,RoleList.CAPTION),
	RULELIST(RuleList.class,RuleList.CAPTION),
	USERMANAGER(UserManager.class,UserManager.CAPTION),	
	ACTIONLIST(ActionList.class,ActionList.CAPTION),
	SENDSMS(SendSms.class,SendSms.CAPTION),
	//Reports
	REPORTCASEACTIONBYUSER(ReportCaseActionByUser.class,ReportCaseActionByUser.CAPTION),
	REPORTCASE(ReportCaseTotal.class,ReportCaseTotal.CAPTION),
	REPORTCASEBYTXN(ReportCaseByTxn.class,ReportCaseByTxn.CAPTION),
	REPORTCASEBYSTATUS(ReportCaseStatus.class,ReportCaseStatus.CAPTION),
	REPORTCASERULEID(ReportCaseRuleId.class,ReportCaseRuleId.CAPTION),
	REPORTCASETXNCRDDET(ReportCaseTxnCrdDet.class,ReportCaseTxnCrdDet.CAPTION),	
	REPORTLOCKACCOUNT(ReportLockAccount.class,ReportLockAccount.CAPTION),
	REPORTEXPTIONCASE(ReportExptionCase.class,ReportExptionCase.CAPTION),
	REPORTTXNMONITOREB(ReportTransactionMonitorEbank.class,ReportTransactionMonitorEbank.CAPTION)
	;

	private final String caption;
	private final Class<? extends Component> classComponent;

	private FdsTabType(Class<? extends Component> classComponent,String caption) {
		this.caption = caption;
		this.classComponent = classComponent;
	}

	public String getCaption() {
		return caption;
	}

	public Class<? extends Component> getClassComponent() {
		return classComponent;
	}	
	
	public static FdsTabType getTabType(final String caption){
		FdsTabType result=null;
		for (FdsTabType tabType:values()){
			if(tabType.getCaption().equals(caption)){
				result=tabType;
				break;
			}
		}
		return result;
	}
	

}
