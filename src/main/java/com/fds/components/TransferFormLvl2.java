package com.fds.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.fds.SecurityUtils;
import com.fds.SpringContextHelper;
import com.fds.entities.FdsEbankSysUser;
import com.fds.services.CaseDetailService;
import com.fds.services.CaseStatusService;
import com.fds.services.EbankCaseDetailService;
import com.fds.services.SysUserService;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Form chuyen case cho Case sang BOX "Chuyển giám sát"
 * UPDATE STATUS = 'TL2'
 */
@SpringComponent
@Scope("prototype")
public class TransferFormLvl2 extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private static final String STATUS = "TL2";
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferFormLvl2.class);

	public TransferFormLvl2(final Callback callback, final String caseno, final String txnUsername) {
		final String sTxnUsername = txnUsername;
		final String sCaseNo = caseno;
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		final EbankCaseDetailService caseDetailService = (EbankCaseDetailService) helper.getBean("ebankCaseDetailService");
		final CaseStatusService caseStatusService = (CaseStatusService) helper.getBean("caseStatusService");
		final SysUserService sysUserService = (SysUserService) helper.getBean("sysUserService");

		final String sUserId = SecurityUtils.getUserId();
		final CaseCommentForm caseCommentForm = new CaseCommentForm(sTxnUsername, sCaseNo);

		setSpacing(true);
		setMargin(true);

		final Button btSave = new Button("Lưu");
		btSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSave.setIcon(FontAwesome.SAVE);

		final Button btBack = new Button("Đóng");
		btBack.setStyleName(ValoTheme.BUTTON_QUIET);
		btBack.setIcon(FontAwesome.CLOSE);
		btBack.addClickListener(evt -> {
			callback.closeWindow();
		});

		final ComboBox cbboxUser = new ComboBox();
		cbboxUser.setNullSelectionAllowed(false);

		btSave.addClickListener(evt -> {
			// Trong databse cot comment khong cho null nen se gan gia tri rong neu usr khong nhap gi het
			final String sComment = "".equals(caseCommentForm.getComment()) ? "Chuyển case đến giám sát" : caseCommentForm.getComment();
			/*if (cbboxUser.getValue() != null) {*/
				try {
					//caseStatusService.create(sCaseNo, sComment, "", STATUS, cbboxUser.getValue().toString(), sUserId);
					caseStatusService.create(sCaseNo, sComment, "", STATUS, " ", sUserId);
					//caseDetailService.updateAssignedUser(sCaseNo, cbboxUser.getValue().toString());
					caseDetailService.closeCase(sCaseNo, "ALL", STATUS,null);
					callback.closeWindow();
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
			/*}*/ 
		});

		final GridLayout gridLayout = new GridLayout();
		gridLayout.setColumns(3);
		gridLayout.setRows(1);
		gridLayout.setSpacing(true);

		final List<FdsEbankSysUser> listUser = sysUserService.findAllUserByActiveflagIsTrue();
		for (final FdsEbankSysUser s : listUser) {
			cbboxUser.addItem(s.getUserid());
		}

		//gridLayout.addComponent(cbboxUser, 0, 0);
		gridLayout.addComponent(btSave, 0, 0);
		gridLayout.addComponent(btBack, 1, 0);

		addComponent(caseCommentForm);
		addComponent(gridLayout);
	}

	@FunctionalInterface
	public interface Callback {
		void closeWindow();
	}
}
