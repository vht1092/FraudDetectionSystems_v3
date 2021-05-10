package com.fds.components.reports;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;

import com.fds.TimeConverter;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
@Scope("prototype")
public class ReportExptionCase extends ReportForm {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportExptionCase.class);
	
	public static final String CAPTION = "BC GHI NHAN YEU CAU KH";
	
	public ReportExptionCase() {
		
		tfCif.setVisible(true);
		tfCif.setValidationVisible(false);
		tfCif.setImmediate(true);
		
		super.filename = "ReportEbankExptionCase.jasper";
	}
	
	@Override
	public boolean checkValidator() {
		try {
			dffromDate.validate();
			//dfToDate.validate();
			return true;
		} catch (InvalidValueException ex) {
			dffromDate.setValidationVisible(true);
		}
		return false;
	}
	
	@Override
	public Map<String, Object> getParameter() {
		final TimeConverter timeConverter = new TimeConverter();
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("p_fromdate", timeConverter.convertDatetime(dffromDate.getValue(), false));
		
		if (dffromDate.getValue() != null)
			parameters.put("p_todate", timeConverter.convertDatetime(dfToDate.getValue(), true));
		else
			parameters.put("p_todate", null);
		
		if (StringUtils.hasText(tfCif.getValue())) {
			parameters.put("p_cif", tfCif.getValue().toString());
		} else {
			parameters.put("p_cif", null);
		}
		
		return parameters;
	}
}
