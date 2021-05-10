package com.fds.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fds.ReloadComponent;
import com.fds.SecurityUtils;
import com.fds.SpringConfigurationValueHelper;
import com.fds.SpringContextHelper;
import com.fds.entities.FdsCaseDetail;
import com.fds.entities.FdsEbankCaseDetail;
import com.fds.services.CaseDetailService;
import com.fds.services.CaseStatusService;
import com.fds.services.EbankCaseDetailService;
import com.fds.services.SysUserroleService;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@Scope("prototype")
public class AutoClosedCase extends CustomComponent implements ReloadComponent {

	/**
	 * Hien thi danh sach cac case da dong
	 * 
	 */

	private static final long serialVersionUID = 1L;
	public static final String CAPTION = "CASE ĐÃ TỰ ĐỘNG ĐÓNG";
	private final EbankCaseDetailService caseDetailService;
	private final transient SysUserroleService sysUserRoleService;
	private final CaseStatusService caseStatusService;
	private final CaseEbankDetailGridComponent grid;
	private final VerticalLayout mainLayout = new VerticalLayout();
	private String sUserId = "";
	private int checkRoleUserId;
	private static final int ID_ROLE_LV2 = 4; 
	private Page<FdsEbankCaseDetail> result;
	private HorizontalLayout pagingLayout;
	private SpringConfigurationValueHelper configurationHelper;
	
	// Paging
	private static final int SIZE_OF_PAGE = 50;
	private static final int FIRST_OF_PAGE = 0;

	private static final Logger LOGGER = LoggerFactory.getLogger(AutoClosedCase.class);
	
	
	public AutoClosedCase() {
		setCaption(CAPTION);
		SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		caseDetailService = (EbankCaseDetailService) helper.getBean("ebankCaseDetailService");
		sysUserRoleService = (SysUserroleService) helper.getBean("sysUserroleService");
		configurationHelper = (SpringConfigurationValueHelper) helper.getBean("springConfigurationValueHelper");
		caseStatusService = (CaseStatusService) helper.getBean("caseStatusService");
		this.sUserId = SecurityUtils.getUserId();
		this.checkRoleUserId = sysUserRoleService.getIdRoleOfUerLogin(sUserId);

		mainLayout.setSpacing(true);
		grid = new CaseEbankDetailGridComponent(getData(new PageRequest(FIRST_OF_PAGE, SIZE_OF_PAGE, Sort.Direction.DESC, "updTms")), false, "UpdateTime");
		mainLayout.addComponent(grid);

		pagingLayout = generatePagingLayout();
		pagingLayout.setSpacing(true);
		mainLayout.addComponent(pagingLayout);
		mainLayout.setComponentAlignment(pagingLayout, Alignment.BOTTOM_RIGHT);

		setCompositionRoot(mainLayout);

	}

	private HorizontalLayout generatePagingLayout() {
		Button btLabelPaging = new Button();
		btLabelPaging.setCaption(reloadLabelPaging());
		btLabelPaging.setEnabled(false);

		final Button btPreviousPage = new Button("Trang trước");
		btPreviousPage.setIcon(FontAwesome.ANGLE_LEFT);
		btPreviousPage.setEnabled(result.hasPrevious());

		final Button btNextPage = new Button("Trang sau");
		btNextPage.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
		btNextPage.setIcon(FontAwesome.ANGLE_RIGHT);
		btNextPage.setEnabled(result.hasNext());

		btNextPage.addClickListener(evt -> {
			grid.refreshData(getData(result.nextPageable()));
			btNextPage.setEnabled(result.hasNext());
			btPreviousPage.setEnabled(result.hasPrevious());
			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					btLabelPaging.setCaption(reloadLabelPaging());
				}
			});

		});

		btPreviousPage.addClickListener(evt -> {
			grid.refreshData(getData(result.previousPageable()));
			btNextPage.setEnabled(result.hasNext());
			btPreviousPage.setEnabled(result.hasPrevious());
			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					btLabelPaging.setCaption(reloadLabelPaging());
				}
			});
		});

		final HorizontalLayout pageLayout = new HorizontalLayout();
		pageLayout.setSizeUndefined();
		pageLayout.setSpacing(true);
		pageLayout.addComponent(btLabelPaging);
		pageLayout.addComponent(btPreviousPage);
		pageLayout.addComponent(btNextPage);
		pageLayout.setDefaultComponentAlignment(Alignment.BOTTOM_RIGHT);

		return pageLayout;
	}

	private String reloadLabelPaging() {
		final StringBuilder sNumberOfElements = new StringBuilder();
				
		if (result.getSize() * (result.getNumber() + 1) >= result.getTotalElements()) {
			sNumberOfElements.append(result.getTotalElements());
		} else {
			sNumberOfElements.append(result.getSize() * (result.getNumber() + 1));
		}
		final String sTotalElements = Long.toString(result.getTotalElements());
		
		return sNumberOfElements.toString() + "/" + sTotalElements;

	}

	private Page<FdsEbankCaseDetail> getData(Pageable page) {
		
		String userAutoCloseCase = configurationHelper.getUserAutoCloseCase();
		List<FdsEbankCaseDetail> listCaseNotConfirm = caseDetailService.findAllBycheckNewIsAndNotConfirmTxn();
		for(FdsEbankCaseDetail fdscasedetail : listCaseNotConfirm) {
			caseDetailService.closeCase(fdscasedetail.getCaseNo(), userAutoCloseCase, "DIC","Y");
			caseStatusService.create(fdscasedetail.getCaseNo(), "Không xác nhận GD do KH yêu cầu", "NCR", "DIC", "", userAutoCloseCase);
			LOGGER.info("Auto close case: " + fdscasedetail.getCaseNo());
		}
		
//		if(checkRoleUserId == ID_ROLE_LV2) {
			result = caseDetailService.findAllAutoClosed(page);
//		} else {
//			result = caseDetailService.findAllClosedByUser(page, sUserId);
//		}
		return result;
	}

	@Override
	public void eventReload() {
		grid.refreshData(getData(new PageRequest(FIRST_OF_PAGE, SIZE_OF_PAGE, Sort.Direction.DESC, "updTms")));

		// Refresh paging button
		mainLayout.removeComponent(pagingLayout);
		pagingLayout = generatePagingLayout();
		pagingLayout.setSpacing(true);
		mainLayout.addComponent(pagingLayout);
		mainLayout.setComponentAlignment(pagingLayout, Alignment.BOTTOM_RIGHT);
	}

}
