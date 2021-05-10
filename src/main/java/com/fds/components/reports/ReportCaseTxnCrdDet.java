package com.fds.components.reports;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;

import com.fds.SpringContextHelper;
import com.fds.TimeConverter;
import com.fds.services.DescriptionService;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Notification.Type;

@SpringComponent
@Scope("prototype")
public class ReportCaseTxnCrdDet extends ReportForm {

	private static final long serialVersionUID = 1L;
	public static final String CAPTION = "BC CHI TIẾT THEO GIAO DỊCH";

	public ReportCaseTxnCrdDet() {
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		final DescriptionService descriptionService = (DescriptionService) helper.getBean("descriptionService");

				
		tfCif.setVisible(true);
		tfCif.setValidationVisible(false);
		tfCif.setImmediate(true);
		
		tfIdmaker.setVisible(true);
		tfIdmaker.setValidationVisible(false);
		tfIdmaker.setImmediate(true);
		
		tfUserMaker.setVisible(true);
		tfUserMaker.setValidationVisible(false);
		tfUserMaker.setImmediate(true);
		
		cbboxTxnChannel.setVisible(true);
		descriptionService.findAllByType("EBANK").forEach(result -> {
			cbboxTxnChannel.addItem(result.getId());
			cbboxTxnChannel.setItemCaption(result.getId(), result.getDescription());
		});

		tfTxnId.setVisible(true);
		tfTxnId.setValidationVisible(false);
		tfTxnId.setImmediate(true);
		
		super.filename = "ReportCaseEbankDetail.jasper";
	}

	@Override
	public boolean checkValidator() {
		try {
			dffromDate.validate();
			dfToDate.validate();
			if (!StringUtils.hasText(tfCif.getValue()) && !StringUtils.hasText(tfIdmaker.getValue())
					&& !StringUtils.hasText(tfUserMaker.getValue()) && cbboxTxnChannel.getValue() == null
					&& !StringUtils.hasText(tfTxnId.getValue())) {
				Notification.show("Vui điền ít nhất một trong các tiêu chí", Type.WARNING_MESSAGE);
			}
			//Han che khoan thoi gian toi da la 30 ngay
			long diff = dfToDate.getValue().getTime() - dffromDate.getValue().getTime();
			System.out.println("So ngay tao bao cao:" + diff);
			return true;

		} catch (InvalidValueException ex) {
			dffromDate.setValidationVisible(true);
			dfToDate.setValidationVisible(true);			
		}
		return false;
	}

	@Override
	public Map<String, Object> getParameter() {
		/*if (dffromDate.getValue() != null && dfToDate.getValue() != null && tfMerchantName.getValue() != null && tfTerminalId.getValue() != null
				&& tfCardNo.getValue() != null && tfMcc.getValue() != null) {

			if (!StringUtils.hasText(tfMerchantName.getValue()) && !StringUtils.hasText(tfTerminalId.getValue())
					&& !StringUtils.hasText(tfCardNo.getValue()) && !StringUtils.hasText(tfMcc.getValue())) {
				return null;
			} else {*/
				final TimeConverter timeConverter = new TimeConverter();
				final Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("p_fromdate", timeConverter.convertDatetimeYMD(dffromDate.getValue(), false));
				parameters.put("p_todate", timeConverter.convertDatetimeYMD(dfToDate.getValue(), true));
				
				if (StringUtils.hasText(tfCif.getValue())) {
					parameters.put("p_cif", tfCif.getValue().toString());
				} else {
					parameters.put("p_cif", null);
				}
				if (StringUtils.hasText(tfIdmaker.getValue())) {
					parameters.put("p_idmaker", tfIdmaker.getValue().toString());
				} else {
					parameters.put("p_idmaker", null);
				}
				if (StringUtils.hasText(tfUserMaker.getValue())) {
					parameters.put("p_user_maker", tfUserMaker.getValue().toString());
				} else {
					parameters.put("p_user_maker", null);
				}
				
				if (cbboxTxnChannel.getValue() != null) {
					parameters.put("p_txn_channel", String.valueOf(cbboxTxnChannel.getValue()));
				} else {
					parameters.put("p_txn_channel", null);
				}
				
				if (StringUtils.hasText(tfTxnId.getValue())) {
					parameters.put("p_txnid", tfTxnId.getValue().toString());
				} else {
					parameters.put("p_txnid", null);
				}
				return parameters;
			//}
		//}
		//return null;
	}

}


