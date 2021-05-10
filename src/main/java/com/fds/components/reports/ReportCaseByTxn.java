package com.fds.components.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;

import com.fds.SpringContextHelper;
import com.fds.TimeConverter;
import com.fds.services.CaseDetailService;
import com.fds.services.DescriptionService;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
@Scope("prototype")
public class ReportCaseByTxn extends ReportForm {	

	private static final long serialVersionUID = 1L;
	public static final String CAPTION = "BC THEO GIAO DỊCH THẺ CỦA KH";
	private final transient CaseDetailService caseDetailService;

	public ReportCaseByTxn() {
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		caseDetailService = (CaseDetailService) helper.getBean("caseDetailService");
		final DescriptionService descService = (DescriptionService) helper.getBean("descriptionService");
		
		/*cbboxTypeCard.setVisible(true);
		descService.findAllByType("CARD").forEach(result -> {
			cbboxTypeCard.addItem(result.getId());
			cbboxTypeCard.setItemCaption(result.getId(), result.getDescription());

		});*/
		
		super.filename = "ReportCaseByTxn.jasper";
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
			parameters.put("p_fromdate", timeConverter.convertDatetime(dffromDate.getValue(), false));
			parameters.put("p_todate", timeConverter.convertDatetime(dfToDate.getValue(), true));

			/*if (cbboxTypeCard.getValue() != null) {
				parameters.put("p_crdtype", String.valueOf(cbboxTypeCard.getValue()));
			} else {
				parameters.put("p_crdtype", null);
			}*/
			return parameters;
		}
		return null;
	}
}
