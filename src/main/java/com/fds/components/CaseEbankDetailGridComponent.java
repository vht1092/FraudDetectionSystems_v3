package com.fds.components;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;

import com.fds.SecurityUtils;
import com.fds.SpringContextHelper;
import com.fds.TimeConverter;
import com.fds.entities.CustomerInfo;
import com.fds.entities.FdsEbankCaseDetail;
import com.fds.entities.FdsEbankRule;
import com.fds.entities.FdsSysTask;
import com.fds.services.CustomerInfoService;
import com.fds.services.EbankCaseDetailService;
import com.fds.services.EbankRuleService;
import com.fds.services.SysTaskService;
import com.fds.services.TxnDetailService;
import com.fds.views.MainView;
import com.fds.views.SingleSelectionModelNotChecked;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Component tao danh sach case
 * 
 * @see Inbox, CaseDistribution, ClosedCase
 */

@SpringComponent
@Scope("prototype")
public class CaseEbankDetailGridComponent extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CaseEbankDetailGridComponent.class);
	private final transient TimeConverter timeConverter = new TimeConverter();
	private final transient Grid grid;
	private final transient EbankRuleService ebankRuleService;
	private final transient EbankCaseDetailService ebankCaseDetailService;
	private final transient SysTaskService sysTaskService;
	private final transient TxnDetailService txnDetailService;
	private boolean color = false;
	private final transient Page<FdsEbankCaseDetail> dataSource;
	private final transient Label lbNoDataFound;
	private final transient IndexedContainer container;
	private String getColumn;
	private final transient CustomerInfoService custInfoService;
	
	/*
	 * @color: De to mau so the theo rule mac dinh la false
	 */

	public CaseEbankDetailGridComponent(final Page<FdsEbankCaseDetail> dataSource, final boolean color, final String getColumn) {

		setSizeFull();
		this.color = color;
		this.dataSource = dataSource;
		this.getColumn = getColumn;

		// init SpringContextHelper de truy cap service bean
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		ebankRuleService = (EbankRuleService) helper.getBean("ebankRuleService");
		ebankCaseDetailService = (EbankCaseDetailService) helper.getBean("ebankCaseDetailService");
		sysTaskService = (SysTaskService) helper.getBean("sysTaskService");
		txnDetailService = (TxnDetailService) helper.getBean("txnDetailService");
		custInfoService = (CustomerInfoService) helper.getBean("customerInfoService");
		// init label
		lbNoDataFound = new Label("Không tìm thấy dữ liệu");
		lbNoDataFound.setVisible(false);
		lbNoDataFound.addStyleName(ValoTheme.LABEL_FAILURE);
		lbNoDataFound.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		lbNoDataFound.setSizeUndefined();

		// init grid
		grid = new Grid();
		grid.setVisible(false);
		grid.setSizeFull();
		grid.setHeightByRows(20);
		grid.setReadOnly(true);
		grid.setHeightMode(HeightMode.ROW);
		grid.setSelectionModel(new SingleSelectionModelNotChecked());
		// init container

		container = new IndexedContainer();
		container.addContainerProperty("fdsRules", String.class, ""); //Rule
		container.addContainerProperty("creTms", String.class, ""); //Thoi gian GD
		container.addContainerProperty("cusName", String.class, ""); //Ten KH
		container.addContainerProperty("cif", String.class, ""); //Ma KH
		container.addContainerProperty("cusPhone", String.class, "");//So DT KH tam thoi hardcode
		container.addContainerProperty("txnUsername", String.class, ""); //TK dang nhap
		container.addContainerProperty("txnAccount", String.class, ""); //Tk giao dich
		container.addContainerProperty("amount", BigDecimal.class, 0); //So tien gd
		container.addContainerProperty("txnChannel", String.class, ""); //Kenh gd
		container.addContainerProperty("txnType", String.class, ""); //Loai GD
		container.addContainerProperty("codStatus", String.class, ""); //Tinh trang gd tai SCB
		container.addContainerProperty("respStatus", String.class, ""); //Tinh trang gd tai DOI TAC
		container.addContainerProperty("txnIdRef", String.class, ""); //Ma gd
		container.addContainerProperty("customerCode", String.class, "");//Hoa don DV
		container.addContainerProperty("idProvider", String.class, ""); //Nha cung cap dv
		container.addContainerProperty("txnDescAcc", String.class, ""); //TK nhan
		container.addContainerProperty("caseNo", String.class, ""); //Case no
		container.addContainerProperty("cusType", String.class, "");//Loại KH
		
		initGrid(); //container.addContainerProperty("amount", BigDecimal.class, 0);
	}

	private void initGrid() {
		if (createDataForContainer(this.dataSource) == false) {
			if (!lbNoDataFound.isVisible() && this.dataSource != null) {
				lbNoDataFound.setVisible(true);
			}
		} else {
			if (!grid.isVisible()) {
				grid.setVisible(true);
			}
		}

		grid.setContainerDataSource(container);
		grid.getColumn("fdsRules").setHeaderCaption("RULE");
		grid.getColumn("creTms").setHeaderCaption("THỜI GIAN GD");
		grid.getColumn("cusName").setHeaderCaption("TÊN KH");
		grid.getColumn("cif").setHeaderCaption("MÃ KH");
		grid.getColumn("cusPhone").setHeaderCaption("SĐT KH");
		grid.getColumn("txnUsername").setHeaderCaption("TÀI KHOẢN ĐĂNG NHẬP");
		grid.getColumn("txnAccount").setHeaderCaption("TÀI KHOẢN GD");
		grid.getColumn("txnAccount").setRenderer(new HtmlRenderer());
		grid.getColumn("amount").setHeaderCaption("SỐ TIÊN GD");
		grid.getColumn("txnChannel").setHeaderCaption("KÊNH GD");
		grid.getColumn("txnType").setHeaderCaption("LOẠI GD");
		grid.getColumn("codStatus").setHeaderCaption("TÌNH TRẠNG GD TẠI SCB");
		grid.getColumn("respStatus").setHeaderCaption("TÌNH TRẠNG GD TẠI ĐỐI TÁC");
		grid.getColumn("txnIdRef").setHeaderCaption("MÃ GD");
		grid.getColumn("customerCode").setHeaderCaption("SỐ HÓA ĐƠN DV");
		grid.getColumn("idProvider").setHeaderCaption("NHÀ CUNG CẤP DV");
		grid.getColumn("txnDescAcc").setHeaderCaption("TÀI KHOẢN NHẬN");
		//grid.getColumn("txnDescAcc").setRenderer(new HtmlRenderer());
		grid.getColumn("caseNo").setHeaderCaption("CASE");
		grid.getColumn("cusType").setHeaderCaption("LOẠI KH");
		
		
		// Dung cho close case
		/*if (this.getColumn.equals("UpdateTime")) {
			grid.getColumn("updTms").setHidden(false);
			grid.getColumn("creTms").setHidden(true);
		} else if (this.getColumn.equals("All")) {
			grid.getColumn("updTms").setHidden(false);
			grid.getColumn("creTms").setHidden(false);
		} else {
			grid.getColumn("updTms").setHidden(true);
		}*/
		
		
		grid.addItemClickListener(evt -> {
			try {
				if(evt.getItem()!=null) {
					final String sCaseno = String.valueOf(evt.getItem().getItemProperty("caseNo").getValue());
					LOGGER.info(SecurityUtils.getUserId() + ": " + sCaseno);
					final MainView mainview = (MainView) UI.getCurrent().getNavigator().getCurrentView();
					//mainview.addTab(new CaseDetail(sCaseno), sCaseno);
					mainview.addTab(new CaseEbankDetail(sCaseno), sCaseno);
				}
			} catch (Exception e) {
				LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			}
			grid.deselectAll();

		});
		// Them tooltip mo ta rule cua case tren grid
		grid.setRowDescriptionGenerator(row -> {
			String sText = "";
			final String sCaseNo = container.getItem(row.getItemId()).getItemProperty("caseNo").getValue().toString();
			List<FdsEbankRule> result = ebankRuleService.findByCaseNo(sCaseNo);
			for (FdsEbankRule r : result) {
				sText = sText + r.getRuleId() + ": " + r.getRuleName() + "<br/>";
			}
			return sText;

		});
		grid.setCellStyleGenerator(cell -> {
			if (cell.getPropertyId().equals("amount")) {
				return "v-align-right";
			}
			return "";
		});

		addComponentAsFirst(lbNoDataFound);
		addComponentAsFirst(grid);

		// mainLayout.addComponentAsFirst(label_nodatafound);
		// mainLayout.addComponentAsFirst(grid);

	}

	private String getColorByRuleId(final String caseno) {
		return ebankRuleService.findColorByCaseNo(caseno);
	}

	public void refreshData(Page<FdsEbankCaseDetail> dataSource) {
		getUI().access(() -> {
			if (createDataForContainer(dataSource) == false) {
				if (!lbNoDataFound.isVisible()) {
					lbNoDataFound.setVisible(true);
				}
				if (grid.isVisible()) {
					grid.setVisible(false);
				}
			} else {
				if (lbNoDataFound.isVisible()) {
					lbNoDataFound.setVisible(false);
				}
				if (!grid.isVisible()) {
					grid.setVisible(true);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private boolean createDataForContainer(final Page<FdsEbankCaseDetail> listCaseDetail) {
		if (listCaseDetail != null && listCaseDetail.hasContent()) {

			container.removeAllItems();
			listCaseDetail.forEach(s -> {
				Item item = container.getItem(container.addItem());
				
				item.getItemProperty("fdsRules").setValue(createListRule(s.getFdsRules()));
				item.getItemProperty("creTms").setValue(timeConverter.convertStrToDateTime(s.getTxnCreTms().toString()));
				item.getItemProperty("cusName").setValue(s.getCusName());
				item.getItemProperty("cif").setValue(s.getTxnCustomer());
				item.getItemProperty("cusPhone").setValue(s.getPhonenumber());
				item.getItemProperty("txnUsername").setValue(s.getTxnUsername());
				//item.getItemProperty("txnAccount").setValue(s.getAccountno());
				item.getItemProperty("txnAccount").setValue(decodeCardNoWithColor(s.getAccountno(),s.getCaseNo(), s.getTxnCustomer()));
				//item.getItemProperty("amount").setValue(String.valueOf(s.getTxnAmount()));
				item.getItemProperty("amount").setValue(s.getTxnAmount());
				item.getItemProperty("txnChannel").setValue(s.getTxnChannel());
				item.getItemProperty("txnType").setValue(s.getTxnType());
				item.getItemProperty("codStatus").setValue(s.getCodstatus());
				item.getItemProperty("respStatus").setValue(s.getResStatus());
				item.getItemProperty("txnIdRef").setValue(s.getTxnIdref());
				item.getItemProperty("customerCode").setValue(s.getCustomercode());
				item.getItemProperty("idProvider").setValue(s.getIdprovider());
				item.getItemProperty("txnDescAcc").setValue(s.getTxnDestAcc());
				//item.getItemProperty("txnDescAcc").setValue(decodeCardNoWithColor(s.getTxnDestAcc(), s.getCaseNo(), s.getTxnCustomer()));
				item.getItemProperty("caseNo").setValue(s.getCaseNo());
				item.getItemProperty("cusType").setValue(s.getCusType());
				
			});
		} else {
			return false;
		}
		return true;
	}

	// Tao list rule cua case tren grid
	private String createListRule(final List<FdsEbankRule> listrule) {
		if (listrule.isEmpty()) {
			return "";
		}
		String sRule = "";
		for (final FdsEbankRule a : listrule) {
			sRule = sRule + a.getRuleId() + ", ";
		}
		return sRule.substring(0, sRule.length() - 2);
	}

	private boolean checkException(final String cifno) {
		FdsSysTask task = sysTaskService.findOneByObjectAndCurrentTime(cifno, "EXCEPTION");
		if (task != null) {
			return true;
		}
		return false;
	}

	private String decodeCardNoWithColor(final String desTxnAccount, final String caseno, final String cifno) {
		final String sCifNo = cifno;
		//final String sCardNo = ebankCaseDetailService.getDed2(cardno);
		// Format lai so the #### #### #### ####
		//String sReformatedCardNo = String.valueOf(cardno).replaceFirst("(\\d{4})(\\d{4})(\\d{4})(\\d{4})", "$1 $2 $3 $4");
		String sDesTxnAccount = desTxnAccount;
		
		if (color) {
			String sColor = getColorByRuleId(caseno);
			if (checkException(sCifNo)) {
				return "<span class='v-label-exception'> </span><span style=\"padding:7px 0px; background-color:" + sColor + "\">" + sDesTxnAccount
						+ "</span>";
			}
			return "<span style=\"padding:7px 0px; background-color:" + sColor + "\">" + sDesTxnAccount + "</span>";

		}
		return sDesTxnAccount;
	}
	
	
	
}
