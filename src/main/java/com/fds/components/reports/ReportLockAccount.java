package com.fds.components.reports;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;

import com.fds.SpringContextHelper;
import com.fds.TimeConverter;
import com.fds.services.SysUserService;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
@Scope("prototype")
public class ReportLockAccount extends ReportForm {
	public static final String CAPTION = "BC KHOA DICH VU EBANKING TREN FDS";
	private static final long serialVersionUID = 3180591912898254754L;
	
	public ReportLockAccount() {
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		final SysUserService sysUserService = (SysUserService) helper.getBean("sysUserService");
		
		tfCif.setVisible(true);
		tfCif.setValidationVisible(false);
		tfCif.setImmediate(true);
		
		cbboxUser.setVisible(true);
		sysUserService.findAllUserByActiveflagIsTrue().forEach(r -> {
			cbboxUser.addItems(r.getUserid());
		});
		
		tfAccountCustomer.setVisible(true);
		tfAccountCustomer.setValidationVisible(false);
		tfAccountCustomer.setImmediate(true);
		
		super.filename = "ReportEbankLockAccount.jasper";
	}
	
	@Override
	public boolean checkValidator() {
		try {
			dffromDate.validate();
			dfToDate.validate();
			return true;
		} catch (InvalidValueException ex) {
			dffromDate.setValidationVisible(true);
			dfToDate.setValidationVisible(true);			
		}
		return false;
	}
	
	@Override
	public Map<String, Object> getParameter() {
		if (dffromDate.getValue() != null && dfToDate.getValue() != null) {
			final TimeConverter timeConverter = new TimeConverter();
			final Map<String, Object> parameters = new HashMap<String, Object>();
			System.out.println("AAAA = " + timeConverter.convertDatetimeYMD(dffromDate.getValue(), false));
			System.out.println("BBBB = " + timeConverter.convertDatetimeYMD(dfToDate.getValue(), false));
			
			parameters.put("p_fromdate", timeConverter.convertDatetimeYMD(dffromDate.getValue(), false));
			parameters.put("p_todate", timeConverter.convertDatetimeYMD(dfToDate.getValue(), true));
			if (cbboxUser.getValue() != null) {
				parameters.put("p_userid", String.valueOf(cbboxUser.getValue()));
			} else {
				parameters.put("p_userid", null);
			}
			
			if (tfAccountCustomer.getValue() != null) {
				parameters.put("p_txn_username", tfAccountCustomer.getValue());
			} else {
				parameters.put("p_txn_username", null);
			}
			
			if (tfCif.getValue() != null) {
				parameters.put("p_cif", tfCif.getValue());
			} else {
				parameters.put("p_cif", null);
			}
			
			//System.out.println("from date: " + timeConverter.convertDatetimeYMD(dffromDate.getValue(), false));
			//System.out.println("To date: " + timeConverter.convertDatetimeYMD(dfToDate.getValue(), false));
			//System.out.println("User: " + String.valueOf(cbboxUser.getValue()));
			//System.out.println("CIF: " + tfCif.getValue());
			return parameters;
		}
		return null;
	}
}
