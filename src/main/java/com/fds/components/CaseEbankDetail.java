package com.fds.components;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.fds.CaseRegister;
import com.fds.SecurityUtils;
import com.fds.SpringContextHelper;
import com.fds.TimeConverter;
import com.fds.entities.CustomerInfo;
import com.fds.entities.FdsCaseStatus;
import com.fds.entities.FdsEbankCaseDetail;
import com.fds.entities.FdsEbankRule;
import com.fds.entities.FdsSysTask;
import com.fds.services.CaseStatusService;
import com.fds.services.CustomerInfoService;
import com.fds.services.EbankCaseDetailService;
import com.fds.services.EbankRuleService;
import com.fds.services.SysTaskService;
import com.fds.services.SysUserroleService;
import com.fds.views.MainView;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@Scope("prototype")
public class CaseEbankDetail extends CustomComponent implements CaseRegister {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CaseEbankDetail.class);
	
	private final transient EbankCaseDetailService ebankCaseDetailService;
	private final transient CaseStatusService caseStatusService;
	private final transient CustomerInfoService custInfoService;
	private final transient SysTaskService sysTaskService;
	private final transient EbankRuleService ruleService;
	private final transient SysUserroleService sysUserRoleService;
	private static final String STATUS = "CASEDETAIL";
	private static final String CASE_STATUS_LV2 = "TL2";
	private static final int ID_ROLE_CAP = 1;
	private static final String ERROR_MESSAGE = "Lỗi ứng dụng";
	
	private transient String sCaseno = "";
	private transient String sCifNo = "";
	private transient String sStatus = "";
	private transient String customerChannel = ""; //kenh KH su dung la IB hay MB
	private transient String sTxnUsername = ""; //user customer login MB,IB
	private transient int idRoleUser = -1;
	private transient List<Object[]> listCaseDetail;
	private Window window;
	private final transient TimeConverter timeConverter = new TimeConverter();
	private final transient NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
	private final Grid gridHistTranx;
	private final IndexedContainer contHistTranx;
	
	private final Window confirmDialog = new Window();
	private Button bYes;
	private Button bNo;
		
	public CaseEbankDetail(final String caseno) {
		super();
		this.sCaseno = caseno;
		
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		ebankCaseDetailService = (EbankCaseDetailService) helper.getBean("ebankCaseDetailService");
		caseStatusService = (CaseStatusService) helper.getBean("caseStatusService");
		custInfoService = (CustomerInfoService) helper.getBean("customerInfoService");
		sysTaskService = (SysTaskService) helper.getBean("sysTaskService");
		ruleService = (EbankRuleService) helper.getBean("ebankRuleService");
		sysUserRoleService = (SysUserroleService) helper.getBean("sysUserroleService");
		
		window = new Window();
		window.setWidth(90f, Unit.PERCENTAGE);
		window.setHeight(70f, Unit.PERCENTAGE);
		window.center();
		window.setModal(true);
		
		// --<< KHOI TAO GRID LICH SU GIAO DICH >>--
		// Khoi tao cotainer datasource
		contHistTranx = new IndexedContainer();
		contHistTranx.addContainerProperty("colCase", String.class, "");
		contHistTranx.addContainerProperty("colRule", String.class, "");
		contHistTranx.addContainerProperty("colTxnTime", String.class, "");
		contHistTranx.addContainerProperty("colCusName", String.class, "");
		contHistTranx.addContainerProperty("colIdMaker", String.class, "");
		contHistTranx.addContainerProperty("colTxnUsername", String.class, "");
		contHistTranx.addContainerProperty("colTxnAccount", String.class, "");
		contHistTranx.addContainerProperty("colTxnType", String.class, "");
		contHistTranx.addContainerProperty("colTxnAmount", Double.class, "");
		contHistTranx.addContainerProperty("colChanelType", String.class, "");
		contHistTranx.addContainerProperty("colTxnId", String.class, "");
		contHistTranx.addContainerProperty("colCodStatus", String.class, "");
		contHistTranx.addContainerProperty("colCusCode", String.class, "");
		contHistTranx.addContainerProperty("colProviderId", String.class, "");
		contHistTranx.addContainerProperty("colReceiveAccount", String.class, "");
		contHistTranx.addContainerProperty("colReceiveCard", String.class, "");
		contHistTranx.addContainerProperty("colUuid", String.class, "");
		contHistTranx.addContainerProperty("colTime", String.class, "");
		contHistTranx.addContainerProperty("colCaseStatus", String.class, "");
		contHistTranx.addContainerProperty("colComment", String.class, "");
		
				
		// Khoi tao grid
		gridHistTranx = new Grid();
		gridHistTranx.setContainerDataSource(contHistTranx);
		gridHistTranx.setHeightMode(HeightMode.ROW);
		gridHistTranx.setHeightByRows(6);
		gridHistTranx.setWidth(100f, Unit.PERCENTAGE);
		gridHistTranx.setSelectionMode(SelectionMode.MULTI);
		gridHistTranx.getColumn("colCase").setRenderer(new ButtonRenderer(event -> {
			final String sCaseNo = gridHistTranx.getContainerDataSource().getItem(event.getItemId()).getItemProperty("colCase").getValue().toString();
			if (!"".equals(sCaseNo) && sCaseNo != null) {
				final MainView mainview = (MainView) UI.getCurrent().getNavigator().getCurrentView();
				mainview.addTab(new CaseEbankDetail(sCaseNo), sCaseNo);
			}
		}, null));
		// Can le content trong cell
		gridHistTranx.setCellStyleGenerator(cell -> {
			if ("amount".equals(cell.getPropertyId()) || "amountvnd".equals(cell.getPropertyId())) {
				return "v-align-right";
			}
			else
			{
				return "v-align-center";
			}
		});
		
		gridHistTranx.getColumn("colCase").setHeaderCaption("CaseNo");
		gridHistTranx.getColumn("colRule").setHeaderCaption("Rule");
		gridHistTranx.getColumn("colTxnTime").setHeaderCaption("Thời gian GD");
		gridHistTranx.getColumn("colCusName").setHeaderCaption("Tên KH");
		gridHistTranx.getColumn("colIdMaker").setHeaderCaption("Mã tài khoản đăng nhập");
		gridHistTranx.getColumn("colTxnUsername").setHeaderCaption("Tài khoản đăng nhập");
		gridHistTranx.getColumn("colTxnAccount").setHeaderCaption("Tài khoản GD");
		gridHistTranx.getColumn("colTxnType").setHeaderCaption("Mã GD");
		gridHistTranx.getColumn("colTxnAmount").setHeaderCaption("Số tiền GD");
		gridHistTranx.getColumn("colChanelType").setHeaderCaption("Kênh GD");
		gridHistTranx.getColumn("colTxnId").setHeaderCaption("Loại GD");
		gridHistTranx.getColumn("colCodStatus").setHeaderCaption("Tình trạng GD");
		gridHistTranx.getColumn("colCusCode").setHeaderCaption("Số Hóa đơn DV");
		gridHistTranx.getColumn("colProviderId").setHeaderCaption("Nhà cung cấp DV");
		
		gridHistTranx.getColumn("colReceiveAccount").setHeaderCaption("Tài khoản nhận thanh toán");
		gridHistTranx.getColumn("colReceiveCard").setHeaderCaption("Thẻ nhận thanh toán");
		gridHistTranx.getColumn("colUuid").setHeaderCaption("Người tiếp nhận");
		gridHistTranx.getColumn("colTime").setHeaderCaption("Thời gian tiếp nhận");
		gridHistTranx.getColumn("colCaseStatus").setHeaderCaption("Trạng thái case");
		gridHistTranx.getColumn("colComment").setHeaderCaption("Nội dung xử lý");
		
		// --<< // KHOI TAO GRID LICH SU GIAO DICH >>--
		createForm();
	}
	
	/**
	 * Thong tin chi tiet ve case
	 */
	private void createForm() {
		if (this.sCaseno != null) {
			listCaseDetail = ebankCaseDetailService.findCaseDetailByCaseno(this.sCaseno);
			idRoleUser = sysUserRoleService.getIdRoleOfUerLogin(SecurityUtils.getUserId());
		} else {
			Notification.show("Không tìm thấy dữ liệu", Type.ERROR_MESSAGE);
			LOGGER.error("Khong tim thay tham so caseno");
		}
		//----------
		if (listCaseDetail.isEmpty()) {
			Notification.show("Không tìm thấy dữ liệu", Type.ERROR_MESSAGE);
			LOGGER.error("Khong tim thay du lieu theo caseno: " + this.sCaseno);
		} else {
			String userId = userProcesing();
			if (userId == null || " ".equals(userId)) {
				registerProcessingCase();
			}
			
			window.addCloseListener(this.eventCloseWindow());

			final VerticalLayout verticalLayout = new VerticalLayout();
			verticalLayout.setMargin(true);
			verticalLayout.setSpacing(true);

			final HorizontalLayout actionLayout = new HorizontalLayout();
			actionLayout.setSpacing(true);

			final Button btDiscard = new Button("Kết thúc");
			btDiscard.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			btDiscard.addClickListener(eventClickBTDiscard());

			/*final Button btCallBack = new Button("Gọi lại");
			btCallBack.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			btCallBack.addClickListener(eventClickBTCallBack());*/

			final Button btTransfer = new Button("Chuyển theo dõi");
			btTransfer.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			btTransfer.addClickListener(eventClickBTTransfer());
			
			final Button btTransferLvl2 = new Button("Chuyển giám sát");
			btTransferLvl2.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			btTransferLvl2.addClickListener(eventClickBTTransferLvl2());
			
			final Button btReopen = new Button("Mở lại");
			btReopen.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			btReopen.addClickListener(eventClickBTReopen());

			final Button btComment = new Button("Thêm nội dung xử lý");
			btComment.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			btComment.addClickListener(eventClickBTAddComment());

			/*final CheckBox chboxFraud = new CheckBox("Giao dịch Fraud");
			chboxFraud.addValueChangeListener(eventClickChBoxFraud());*/

			final Button btunAssign = new Button("Trả Case về đang chờ xử lý");
			btunAssign.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			btunAssign.addClickListener(eventClickBTbtunAssign());
			
			final Button btLockAccount = new Button("Khóa Tài Khoản");
			btLockAccount.setStyleName(ValoTheme.BUTTON_DANGER);
			btLockAccount.addClickListener(eventLockAccount());
			
			final Label lbCaseDetail = new Label();
			lbCaseDetail.setContentMode(ContentMode.HTML);

			final Label lbHolderDetail = new Label();
			lbHolderDetail.setContentMode(ContentMode.HTML);

			final Label lbComment = new Label();
			lbComment.setContentMode(ContentMode.HTML);

			final Label lbRuleList = new Label();
			lbRuleList.setContentMode(ContentMode.HTML);

			final Label lbTransaction = new Label();
			lbTransaction.setContentMode(ContentMode.HTML);

			final Panel panelInvestNotes = new Panel("Nội dung xử lý");
			panelInvestNotes.setHeight(140, Unit.PIXELS);
			panelInvestNotes.setStyleName(Reindeer.PANEL_LIGHT);

			final Panel panelRuleList = new Panel("Rule đánh giá giao dịch");
			panelRuleList.setStyleName(Reindeer.PANEL_LIGHT);
			panelRuleList.setSizeFull();

			final Panel panelHolderDetail = new Panel("Thông tin chủ tài khoản");
			panelHolderDetail.setStyleName(Reindeer.PANEL_LIGHT);
			panelHolderDetail.setSizeFull();

			final Panel panelCaseDetail = new Panel("Chi tiết giao dịch");
			panelCaseDetail.setStyleName(Reindeer.PANEL_LIGHT);
			panelCaseDetail.setSizeFull();

			final Panel panelDetailTrans = new Panel("Lịch sử giao dịch");
			panelDetailTrans.setStyleName(Reindeer.PANEL_LIGHT);
			panelDetailTrans.setSizeFull();
			
			final String sCaseNo = listCaseDetail.get(0)[0] == null ? "" : listCaseDetail.get(0)[0].toString();
			final String sCreTms = listCaseDetail.get(0)[1] == null ? "" : listCaseDetail.get(0)[1].toString();
			final String sUserLogin = listCaseDetail.get(0)[2] == null ? "" : listCaseDetail.get(0)[2].toString(); //user login MB,IB
			final String sidMaker = listCaseDetail.get(0)[3] == null ? "" : listCaseDetail.get(0)[3].toString();
			final String sTkGd = listCaseDetail.get(0)[4] == null ? "" : listCaseDetail.get(0)[4].toString();
			final String sTheThanhToan = listCaseDetail.get(0)[5] == null ? "" : listCaseDetail.get(0)[5].toString();
			final Double dAmount = listCaseDetail.get(0)[6] == null ? 0 : Double.parseDouble(listCaseDetail.get(0)[6].toString());
			final String sCurrencyCode = listCaseDetail.get(0)[7] == null ? "" : listCaseDetail.get(0)[7].toString();
			final String sUserTiepNhan = listCaseDetail.get(0)[8] == null ? "" : listCaseDetail.get(0)[8].toString();
			final String sThoiGianTiepNhan = listCaseDetail.get(0)[9] == null ? "" : listCaseDetail.get(0)[9].toString();
			
			final String sMaGD = listCaseDetail.get(0)[10] == null ? "" : listCaseDetail.get(0)[10].toString();
			final String sMaCore = listCaseDetail.get(0)[11] == null ? "" : listCaseDetail.get(0)[11].toString();
			final String sKenhGD = listCaseDetail.get(0)[12] == null ? "" : listCaseDetail.get(0)[12].toString();
			final String sLoaiGD = listCaseDetail.get(0)[13] == null ? "" : listCaseDetail.get(0)[13].toString();
			final String sTinhTrangGD = listCaseDetail.get(0)[14] == null ? "" : listCaseDetail.get(0)[14].toString();
			final String sSoHoaDonDV = listCaseDetail.get(0)[15] == null ? "" : listCaseDetail.get(0)[15].toString();
			final String sNhaCungCapDV = listCaseDetail.get(0)[16] == null ? "" : listCaseDetail.get(0)[16].toString();
			final String sTkNhanThanhToan = listCaseDetail.get(0)[17] == null ? "" : listCaseDetail.get(0)[17].toString();
			final String sCardNhanTanhToan = listCaseDetail.get(0)[18] == null ? "" : listCaseDetail.get(0)[18].toString();
			final String sCif = listCaseDetail.get(0)[19] == null ? "" : listCaseDetail.get(0)[19].toString();
			final String sCaseStatus = listCaseDetail.get(0)[20] == null ? "" : listCaseDetail.get(0)[20].toString();
			final String sCusType = listCaseDetail.get(0)[21] == null ? "" : listCaseDetail.get(0)[21].toString();
			
			final String sCusName = listCaseDetail.get(0)[22] == null ? "" : listCaseDetail.get(0)[22].toString();
			final String sPhone = listCaseDetail.get(0)[23] == null ? "" : listCaseDetail.get(0)[23].toString();
			final String sEmail = listCaseDetail.get(0)[24] == null ? "" : listCaseDetail.get(0)[24].toString();
			final String sCusMST = listCaseDetail.get(0)[25] == null ? "" : listCaseDetail.get(0)[25].toString();
			final String sCusAddr = listCaseDetail.get(0)[26] == null ? "" : listCaseDetail.get(0)[26].toString();
			
			final String sResStatus = listCaseDetail.get(0)[27] == null ? "" : listCaseDetail.get(0)[27].toString();
			
			sStatus = sCaseStatus;//de truyen vao DiscardForm
			final String sAmount = numberFormat.format(dAmount);
			
			sCifNo = sCif.trim();
			//final CustomerInfo customerInfo = custInfoService.findByCifNo(cifTrim);
			/* ----- CHI TIET GIAO DICH ----- */
			panelCaseDetail.setCaption("Chi tiết giao dịch");
			
			sTxnUsername = sUserLogin;
			customerChannel = sKenhGD;
			
			actionLayout.removeAllComponents();
			
			// Neu case da ket thuc, hoac danh dau la giao dich fraud, se hien
			// thi button mo lai
			// Neu case da dong se khong hien thi thong bao
			actionLayout.addComponent(btLockAccount);
			
			if (("DIC".equals(sCaseStatus) || "CAF".equals(sCaseStatus))) {
				btComment.setEnabled(false);
				actionLayout.addComponent(btReopen);
				if (idRoleUser == ID_ROLE_CAP)
					btReopen.setEnabled(false);
			} else {
				if (!" ".equals(sUserTiepNhan) && !sUserTiepNhan.equals(SecurityUtils.getUserId().toUpperCase()) && !"ALL".equals(sUserTiepNhan)) {
					Notification.show(sUserTiepNhan.toUpperCase() + " đang xử lý case", Type.ERROR_MESSAGE);
				} else if ("ALL".equals(sUserTiepNhan) && !sCaseStatus.equals(CASE_STATUS_LV2)) { //case chuyen theo doi 
					actionLayout.addComponent(btDiscard);
					actionLayout.addComponent(btTransferLvl2);
					//actionLayout.addComponent(btunAssign);
					btComment.setEnabled(true);
				} else if ("ALL".equals(sUserTiepNhan) && sCaseStatus.equals(CASE_STATUS_LV2)) { //Case chuyen user level 2 chi co ket thuc
					actionLayout.addComponent(btDiscard);
				} else {
					actionLayout.addComponent(btDiscard);
					actionLayout.addComponent(btTransfer);
					actionLayout.addComponent(btTransferLvl2);
					//actionLayout.addComponent(btCallBack);
					//chboxFraud.setValue(false);
					//actionLayout.addComponent(chboxFraud);
					actionLayout.addComponent(btunAssign);
					//actionLayout.setComponentAlignment(chboxFraud, Alignment.MIDDLE_LEFT);
					btComment.setEnabled(true);
				}
			}
			
			/* @formatter:off */
			final String sHtmlCaseDetail = 
					"<table style='width:100%'>" 
					+ "<tr><th>Case no</th><td>" + sCaseNo + "</td><th>Mã GD:</th><td>" + sMaGD + "</td></tr>"
					+ "<tr><th>Thời gian tạo case:</th><td>" + timeConverter.convertStrToDtTranx(sCreTms) + "</td><th>Mã CORE:</th><td>"+sMaCore+"</td></tr>"
					+ "<tr><th>Tài khoản đăng nhập:</th><td>" + sUserLogin + "</td><th>Kênh GD:</th><td>"+sKenhGD+"</td></tr>"
					+ "<tr><th>Mã tài khoản đăng nhập:</th><td>" + sidMaker + "</td><th>Loại GD:</th><td>" + sLoaiGD + "</td></tr>" 
					+ "<tr><th>Tình trạng GD tại đối tác:</th><td>"+ sResStatus +"</td><th>Tình trạng GD tại SCB:</th><td>" + sTinhTrangGD + "</td></tr>"
					+ "<tr><th>Thẻ thanh toán:</th><td>" + sTheThanhToan+ "</td><th>Số Hóa đơn DV:</th><td>"+ sSoHoaDonDV +"</td></tr>"
					+ "<tr><th>Số tiền GD:</th><td>" + sAmount + "</td><th>Nhà Cung cấp DV:</th><td>" + sNhaCungCapDV+ "</td></tr>"
					+ "<tr><th>Loại tiền tệ:</th><td>" + sCurrencyCode + "</td><th>Tài khoản nhận thanh toán:</th><td>" + sTkNhanThanhToan+ "</td></tr>"
					+ "<tr><th>Tài khoản giao dịch:</th><td>" + sTkGd + "</td><th>Thẻ nhận thanh toán:</th><td>" + sCardNhanTanhToan + "</td></tr>"
					+ "<tr><th>Thời gian tiếp nhận:</th><td>"+ sThoiGianTiepNhan +"</td><th>Loại KH:</th><td>" + sCusType + "</td></tr>" 
					+ "<tr><th>Người tiếp nhận:</th><td>" + sUserTiepNhan + "</td></tr>" 
					+ "</table>";
			/* @formatter:on */
			lbCaseDetail.setValue(sHtmlCaseDetail);
			panelCaseDetail.setContent(lbCaseDetail);
			/* ----- END - CHI TIET GIAO DICH ----- */
			
			StringBuilder sHtmlHolderDetail = new StringBuilder("<table style='width:100%,text-align:left'>"
					+ "<tr><th>Tên KH: </th><td>" + sCusName + "</td></tr>"
					+ "<tr><th>Mã KH: </th><td>" + sCif + "</td></tr>"
					+ "<tr><th>Đơn vị quản lý KH: </th><td>" + "  " + "</td></tr>"
					+ "<tr><th>Số điện thoại 1: </th><td>" + sPhone + "</td></tr>"
					+ "<tr><th>Email: </th><td>" + sEmail + "</td></tr>"
					+ "<tr><th>Mã số thuế: </th><td>" + sCusMST + "</td></tr>"
					+ "<tr><th>Địa chỉ: </th><td>" + sCusAddr + "</td></tr>"
					);
					
			/* @formatter:on */
			
			/*
			 * Case da ket thuc se khong hien thi thong bao ngoai le
			 */
			if (sCaseStatus != null && !sCaseStatus.equals("DIC")) {
				sHtmlHolderDetail.append(checkTaskofCase());
			}
			
			sHtmlHolderDetail.append("</table>");
			lbHolderDetail.setValue(sHtmlHolderDetail.toString());
			panelHolderDetail.setContent(lbHolderDetail);
			/* ----- END - THONG TIN KHACH HANG ----- */
			
			/* ----- NOI DUNG XU LY ----- */
			final List<FdsCaseStatus> listCaseStatus = caseStatusService.findAllByCaseNo(this.sCaseno);
			StringBuilder sHtmlComment = new StringBuilder("<table>");
			String action = "";
			for (final FdsCaseStatus a : listCaseStatus) {
				if ("DIC".equals(a.getCaseAction()) || "CAF".equals(a.getCaseAction())) {
					action = "Kết thúc";
				}
				if ("REO".equals(a.getCaseAction())) {
					action = "Mở lại";
				}
				if ("TRA".equals(a.getCaseAction())) {
					action = "Chuyển";
				}
				if ("ACO".equals(a.getCaseAction())) {
					action = "Thêm nội dung";
				}
				sHtmlComment.append("<tr><td nowrap>" + timeConverter.convertStrToDateTime(a.getCreTms().toString()) + " " + a.getUserId() + " - " + action
						+ ":</td><td>" + a.getCaseComment() + "</td></tr>");
			}
			sHtmlComment.append("</table>");
			lbComment.setValue(sHtmlComment.toString());
			panelInvestNotes.setContent(lbComment);
			/* ----- END - NOI DUNG XU LY ----- */
			
			/* ----- DANH SACH RULE ----- */
			LOGGER.info("Get rule detail ruleService.findByCaseNo(sCaseNo)");
			List<FdsEbankRule> ruleResult = ruleService.findRuleByCaseNo(sCaseNo);
			LOGGER.info("List rule of case 1: " + sCaseNo + " is " + ruleResult.size());
			if (ruleResult == null || ruleResult.size() < 1)
				ruleResult =  ruleService.findRuleByCaseNo(sCaseNo);
			
			LOGGER.info("List rule of case 2: " + sCaseNo + " is " + ruleResult.size());
			
			StringBuilder ruleList = new StringBuilder();
			if (!ruleResult.isEmpty()) {
				ruleList.append("<table><tr><th>Rule</th><th>Mô tả</th></tr>");
				for (final FdsEbankRule r : ruleResult) {
					ruleList = ruleList.append("<tr><td>" + r.getRuleId() + ": &nbsp</td><td>" + r.getRuleName() + "</td></tr>");
				}
				ruleList = ruleList.append("</table>");
			} else {
				ruleList = ruleList.append("Không có rule");
			}
			lbRuleList.setValue(ruleList.toString());
			panelRuleList.setContent(lbRuleList);
			/* ----- END - DANH SACH RULE ----- */
			
			/* ----- LICH SU GIAO DICH ----- */
			final HorizontalLayout btLayout = new HorizontalLayout();
			btLayout.setSpacing(true);
			final Button btOneMonth = new Button("1 Tháng");
			final Button btThreeMonth = new Button("3 Tháng");
			final Button btSixMonth = new Button("6 Tháng");
			final Button btNineMonth = new Button("9 Tháng");
			final Button btAll = new Button("Tất cả");
			
			btOneMonth.setStyleName(ValoTheme.BUTTON_LINK);
			btOneMonth.setEnabled(false);
			btOneMonth.addClickListener(btOneMonthEvt -> {
				btOneMonth.setEnabled(false);
				btThreeMonth.setEnabled(true);
				btSixMonth.setEnabled(true);
				btNineMonth.setEnabled(true);
				btAll.setDisableOnClick(true);
				getUI().access(() -> {
					addDataToHistTranxGrid(sCaseno, 1);
				});
			});

			btThreeMonth.setStyleName(ValoTheme.BUTTON_LINK);
			btThreeMonth.addClickListener(btThreeMonthEvt -> {
				btOneMonth.setEnabled(true);
				btThreeMonth.setEnabled(false);
				btSixMonth.setEnabled(true);
				btNineMonth.setEnabled(true);
				btAll.setDisableOnClick(true);
				getUI().access(() -> {
					addDataToHistTranxGrid(sCaseno, 3);
				});
			});

			btSixMonth.setStyleName(ValoTheme.BUTTON_LINK);
			btSixMonth.addClickListener(btSixMonthEvt -> {
				btOneMonth.setEnabled(true);
				btThreeMonth.setEnabled(true);
				btSixMonth.setEnabled(false);
				btNineMonth.setEnabled(true);
				btAll.setDisableOnClick(true);
				getUI().access(() -> {
					addDataToHistTranxGrid(sCaseno, 6);
				});
			});

			btNineMonth.setStyleName(ValoTheme.BUTTON_LINK);
			btNineMonth.addClickListener(btNineMonthEvt -> {
				btOneMonth.setEnabled(true);
				btThreeMonth.setEnabled(true);
				btSixMonth.setEnabled(true);
				btNineMonth.setEnabled(false);
				btAll.setDisableOnClick(true);
				getUI().access(() -> {
					addDataToHistTranxGrid(sCaseno, 9);
				});
			});

			btAll.setStyleName(ValoTheme.BUTTON_LINK);
			btAll.addClickListener(btAllEvt -> {
				btOneMonth.setEnabled(true);
				btThreeMonth.setEnabled(true);
				btSixMonth.setEnabled(true);
				btNineMonth.setEnabled(true);
				btAll.setDisableOnClick(false);
				getUI().access(() -> {
					addDataToHistTranxGrid(sCaseno, -1);
				});
			});

			btLayout.addComponent(btOneMonth);
			btLayout.addComponent(btThreeMonth);
			btLayout.addComponent(btSixMonth);
			btLayout.addComponent(btNineMonth);
			btLayout.addComponent(btAll);

			final VerticalLayout transactionLayout = new VerticalLayout();
			transactionLayout.setSpacing(true);
			addDataToHistTranxGrid(sCaseno, 1);//khoa rem
			transactionLayout.addComponent(btLayout);
			transactionLayout.addComponent(gridHistTranx);
			panelDetailTrans.setContent(transactionLayout);
			/* ----- END - LICH SU GIAO DICH ----- */
			
			verticalLayout.addComponent(panelCaseDetail);
			verticalLayout.addComponent(panelInvestNotes);
			//tanvh1 20190808
//			verticalLayout.addComponent(btComment);
			verticalLayout.addComponent(panelRuleList);
			verticalLayout.addComponent(panelHolderDetail);
			verticalLayout.addComponent(panelDetailTrans);
			verticalLayout.addComponent(actionLayout);
			setCompositionRoot(verticalLayout);
		}
	}
	
	/**
	 * Kiem tra case da dang ky trang thai dang xu ly hay chua !
	 */
	@Override
	public String userProcesing() {
		final FdsEbankCaseDetail fdsEbankCaseDetail = ebankCaseDetailService.findOneByCaseNo(this.sCaseno);
		if (fdsEbankCaseDetail == null) {
			return "";
		}
		return fdsEbankCaseDetail.getUsrId();
	}

	/**
	 * Dang ky so case vao trang thai dang xu ly user khac khong the truy cap
	 */
	@Override
	public void registerProcessingCase() {
		// TODO Auto-generated method stub
		if (!"".equals(SecurityUtils.getUserId())) {
			// Cap nhat user dang xu ly ngay khi chon case
			ebankCaseDetailService.updateAssignedUser(this.sCaseno, SecurityUtils.getUserId());
		}
	}

	/**
	 * Xoa dang ky so case o trang thai dang xu ly
	 */
	@Override
	public void closeProcessingCase() {
		try {
			sysTaskService.delete(SecurityUtils.getUserId(), this.sCaseno, STATUS);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
	}
		
	/**
	 * Kiem tra giao dich theo so cif cua khach hang co dang ky ngoai le hay
	 * khong !?
	 * 
	 * @return String
	 */
	private String checkTaskofCase() {
		final FdsSysTask fdsSysTask = sysTaskService.findOneByObjectAndCurrentTime(sCifNo, "EXCEPTION");
		if (fdsSysTask != null) {
			final String sContent = fdsSysTask.getContenttask();
			final String sFromdate = timeConverter.convertStrToDateTime(fdsSysTask.getFromdate().toString());
			final String sTodate = timeConverter.convertStrToDateTime(fdsSysTask.getTodate().toString());
			return "<tr><td style=\"border: 2px solid rgb(255, 88, 88); color: rgb(183, 19, 19);\" colspan=\"2\">"
					+ String.format("Khách hàng yêu cầu %s - thời gian áp dụng từ %s đến %s", sContent, sFromdate, sTodate) + "</tr></td>";
		}
		return "";
	}
	
	/**
	 * Tao du lieu cho grid lich su giao dich theo so ngay
	 */
	@SuppressWarnings("unchecked")
	private void addDataToHistTranxGrid(final String sEncCrdNo, int numberofmonth) {
		final List<Object[]> listTransDetail = ebankCaseDetailService.findTransactionDetailByCaseNo(sEncCrdNo, numberofmonth);
		if (!listTransDetail.isEmpty()) {
			if (!contHistTranx.getItemIds().isEmpty()) {
				contHistTranx.removeAllItems();
			}
			for (int i = 0; i <= listTransDetail.size() - 1; i++) {
				Item item = contHistTranx.getItem(contHistTranx.addItem());
				item.getItemProperty("colCase").setValue(listTransDetail.get(i)[0] != null ? listTransDetail.get(i)[0].toString() : "");
				item.getItemProperty("colRule").setValue(listTransDetail.get(i)[1] != null ? listTransDetail.get(i)[1].toString() : "");
				item.getItemProperty("colTxnTime").setValue(listTransDetail.get(i)[2] != null ? timeConverter.convertStrToDateTime(listTransDetail.get(i)[2].toString()) : "");
				item.getItemProperty("colCusName").setValue(listTransDetail.get(i)[3] != null ? listTransDetail.get(i)[3].toString() : "");
				item.getItemProperty("colIdMaker").setValue(listTransDetail.get(i)[4] != null ? listTransDetail.get(i)[4].toString() : "");
				item.getItemProperty("colTxnUsername").setValue(listTransDetail.get(i)[5] != null ? listTransDetail.get(i)[5].toString() : "");
				item.getItemProperty("colTxnAccount").setValue(listTransDetail.get(i)[6] != null ? listTransDetail.get(i)[6].toString() : "");
				item.getItemProperty("colTxnType").setValue(listTransDetail.get(i)[7] != null ? listTransDetail.get(i)[7].toString() : "");
				item.getItemProperty("colTxnAmount").setValue(listTransDetail.get(i)[8] != null ? Double.parseDouble(listTransDetail.get(i)[8].toString()) : 0);
				item.getItemProperty("colChanelType").setValue(listTransDetail.get(i)[9] != null ? listTransDetail.get(i)[9].toString() : "");
				item.getItemProperty("colTxnId").setValue(listTransDetail.get(i)[10] != null ? listTransDetail.get(i)[10].toString() : "");
				item.getItemProperty("colCodStatus").setValue(listTransDetail.get(i)[11] != null ? listTransDetail.get(i)[11].toString() : "");
				item.getItemProperty("colCusCode").setValue(listTransDetail.get(i)[12] != null ? listTransDetail.get(i)[12].toString() : "");
				item.getItemProperty("colProviderId").setValue(listTransDetail.get(i)[13] != null ? listTransDetail.get(i)[13].toString() : "");
				item.getItemProperty("colReceiveAccount").setValue(listTransDetail.get(i)[14] != null ? listTransDetail.get(i)[14].toString() : "");
				item.getItemProperty("colReceiveCard").setValue(listTransDetail.get(i)[15] != null ? listTransDetail.get(i)[15].toString() : "");
				item.getItemProperty("colUuid").setValue(listTransDetail.get(i)[16] != null ? listTransDetail.get(i)[16].toString() : "");
				
				item.getItemProperty("colTime").setValue(listTransDetail.get(i)[17] != null ? timeConverter.convertStrToDateTime(listTransDetail.get(i)[17].toString()) : "");
				item.getItemProperty("colCaseStatus").setValue(listTransDetail.get(i)[18] != null ? getDescriptionStatus(listTransDetail.get(i)[18].toString()) : "");
				item.getItemProperty("colComment").setValue(listTransDetail.get(i)[19] != null ? listTransDetail.get(i)[19].toString() : "");
			}
		}
	}
	
	private Button.ClickListener eventClickBTbtunAssign() {
		return evt -> {
			try {
				ebankCaseDetailService.unAssignedCase(this.sCaseno);
				Notification.show("Đã chuyển case thành công", Type.WARNING_MESSAGE);
			} catch (Exception e) {
				Notification.show("Lỗi ứng dụng:"+ e.getMessage(), Type.ERROR_MESSAGE);
				LOGGER.error("ReopenClickListner -  " + e.getMessage());
			}
		};
	}
	
	/**
	 * Hien thi form mo lai case
	 */
	private Button.ClickListener eventClickBTReopen() {
		/*return evt -> {
			try {
				getUI().addWindow(createWindowComponent("Mở lại case", new ReopenForm(this::closeWindow, this.sCaseno, this.sTxnUsername)));
			} catch (Exception e) {
				Notification.show("Lỗi ứng dụng", Type.ERROR_MESSAGE);
				LOGGER.error("ReopenClickListner -  " + e.getMessage());
			}
		};*/
		return evt -> {
			try {
				getUI().addWindow(createWindowComponent("Chuyển theo dõi", new TransferForm(this::closeWindow, this.sCaseno, this.sTxnUsername)));
			} catch (Exception e) {
				Notification.show(ERROR_MESSAGE, Type.ERROR_MESSAGE);
				LOGGER.error("TransferClickListener - " + e.getMessage());
			}
		};
	}
	
	/**
	 * Hien thi form dong case
	 */
	private Button.ClickListener eventClickBTDiscard() {
		return event -> {
			final ArrayList<String> arCaseNo = new ArrayList<String>();
			try {
				gridHistTranx.getSelectedRows().forEach(item -> {
					final Property<?> caseProperty = gridHistTranx.getContainerDataSource().getContainerProperty(item, "colCase");
					try {
						if(caseProperty.getValue().toString() != null && !"".equals(caseProperty.getValue().toString())) {
							arCaseNo.add(caseProperty.getValue().toString());
						}
					} catch (NullPointerException e) {
						// Khong lam gi het
					}
				});
				// Xoa highlight dong duoc chon tren grid
				gridHistTranx.getSelectionModel().reset();

				getUI().addWindow(createWindowComponentDiscard("Đóng case", new DiscardForm(this::closeWindow, this.sCaseno, this.sTxnUsername, arCaseNo, this.sStatus)));
			} catch (Exception e) {
				Notification.show(ERROR_MESSAGE, Type.ERROR_MESSAGE);
				LOGGER.error("DisCardClickListener - " + e.getMessage());
			}
		};
	}
	
	/**
	 * Hien thi form chuyen case
	 */
	private Button.ClickListener eventClickBTTransfer() {
		return event -> {
			try {
				getUI().addWindow(createWindowComponent("Chuyển theo dõi", new TransferForm(this::closeWindow, this.sCaseno, this.sTxnUsername)));
			} catch (Exception e) {
				Notification.show(ERROR_MESSAGE, Type.ERROR_MESSAGE);
				LOGGER.error("TransferClickListener - " + e.getMessage());
			}
		};
	}
	
	/**
	 * Hien thi form chuyen case cho group giam sat
	 */
	private Button.ClickListener eventClickBTTransferLvl2() {
		  return event -> {
		   try {
		    getUI().addWindow(createWindowComponent("Chuyển giám sát", new TransferFormLvl2(this::closeWindow, this.sCaseno, this.sTxnUsername)));
		   } catch (Exception e) {
		    Notification.show(ERROR_MESSAGE, Type.ERROR_MESSAGE);
		    LOGGER.error("TransferLvl2ClickListener - " + e.getMessage());
		   }
		  };
		 }
	
	
	/**
	 * Hien thi form them noi dung xu ly
	 */
	private Button.ClickListener eventClickBTAddComment() {
		return event -> {
			try {
				getUI().addWindow(createWindowComponent("Nội dung xử lý", new AddCommentForm(this::closeWindow, this.sCaseno, this.sTxnUsername)));
			} catch (Exception e) {
				Notification.show(ERROR_MESSAGE, Type.ERROR_MESSAGE);
				LOGGER.error("AddCommentClickListner - " + e.getMessage());
			}
		};
	}
	
	/**
	 * Hien thi form mo lai case
	 */
	private Button.ClickListener eventLockAccount() {
		return event -> {
			String pAccountCus = sTxnUsername;
			String pUserLogin = SecurityUtils.getUserId();
			
			confirmDialog.setCaption("Bạn có muốn lock account " + pAccountCus + "  ?");
			confirmDialog.setWidth(600.0f, Unit.PIXELS);
            final FormLayout content = new FormLayout();
            content.setMargin(true);
            bYes = new Button("Yes");
            bNo = new Button("No");
            
            bYes.addClickListener(evt -> {
            	String pChannel = "";
            	if (customerChannel.trim().equals("MB")) //chanel Mobile
            		pChannel = "03";
            	else //chanel ODBX
            		pChannel = "01";
            	
            	int result = ebankCaseDetailService.lockAccountEBank(pAccountCus, pChannel, pUserLogin);
            	LOGGER.info("pAccountCus= " + pAccountCus + " pChannel= " + customerChannel + " pUserLogin= " + pUserLogin);
            	if (result == 1) {
            		LOGGER.info(pUserLogin + " is locked Account " + pAccountCus + " Chanel " + pChannel + " successful");
            		Notification.show(pUserLogin + " đã khóa Account " + pAccountCus + " Kênh " + customerChannel, Type.ERROR_MESSAGE);
            		confirmDialog.close();
            		//Xu ly ghi nhan yeu cau them noi dung
            		caseStatusService.create(this.sCaseno, pUserLogin + " đã khóa Account  " + pAccountCus + " Kênh " + customerChannel, "", "LOC", "", pUserLogin);
            		createForm();
            	} else {
            		LOGGER.info("Lock Account " + pAccountCus + " Chanel " + customerChannel + " failed");
            		Notification.show("Khóa Account " + pAccountCus + " Kênh " + customerChannel + " thất bại", Type.ERROR_MESSAGE);
            		confirmDialog.close();
            	}
            });
            bNo.addClickListener(evt -> {
            	confirmDialog.close();
            });
            HorizontalLayout layoutBtn = new HorizontalLayout();
            layoutBtn.addComponents(bYes, bNo);
            content.addComponent(layoutBtn);
            
            confirmDialog.setContent(content);

            this.getUI().getUI().addWindow(confirmDialog);
            // Center it in the browser window
            confirmDialog.center();
            confirmDialog.setResizable(false);
		
		};
		
		/*return evt -> {
			String pAccountCus = "";
			String pChannel = "";
			String pUserLogin = sTxnUsername;
			Notification.show(sTxnUsername + " Đã khóa đã Account", Type.ERROR_MESSAGE);
		};*/
	}
	
	/**
	 * Danh dau giao dich la fraud chuyen trang thai case status = CAF CAF; DIC
	 * duoc xem la case ket thuc
	 */
	private CheckBox.ValueChangeListener eventClickChBoxFraud() {
		return event -> {
			if (event.getProperty().getValue().equals(true)) {
				final String sUserId = SecurityUtils.getUserId();

				try {
					caseStatusService.create(this.sCaseno, "Giao dịch Fraud", "CAF", "CAF", "", sUserId);
					ebankCaseDetailService.closeCase(this.sCaseno, sUserId, "CAF",null);
					createForm();
				} catch (Exception e) {
					Notification.show(ERROR_MESSAGE, Type.ERROR_MESSAGE);
					LOGGER.error("FraudClickListner - " + e.getMessage());
				}

			}

		};
	}
	
	
	/**
	 * Tao moi cua so
	 * 
	 * @param caption
	 *            Ten cua so
	 * @param comp
	 *            Component
	 * @return Window
	 * @see ReopenClickListner
	 * @see AddCommentClickListner
	 * @see TransferClickListener
	 * @see DisCardClickListener
	 * @see CallBackClickListner
	 */
	private Window createWindowComponent(final String caption, final Component comp) {
		window.setCaption(caption);
		window.setContent(comp);
		return window;
	}
	
	private Window createWindowComponentDiscard(final String caption, final Component comp) {
		window.setCaption(caption);
		window.setContent(comp);
		window.setSizeFull();
		window.setWidth(75, Unit.PERCENTAGE);
		return window;
	}
	
	/**
	 * Dong cua so dang mo
	 */
	private Window.CloseListener eventCloseWindow() {
		return evt -> {
			// Khi window dong lam moi du lieu
			createForm();
		};
	}
		
	/**
	 * Ham nay dung de ho tro callback dong cua so<br>
	 * {@link AddCommentClickListner}<br>
	 * {@link CallBackClickListner}<br>
	 * {@link DisCardClickListener}<br>
	 * {@link ReopenClickListner}<br>
	 * {@link TransferClickListener}<br>
	 */
	private void closeWindow() {
		getUI().removeWindow(window);
	}
	
	private String getDescriptionStatus(String keyStatus) {
		String action = "";
		
		if ("DIC".equals(keyStatus) || "CAF".equals(keyStatus)) {
			action = "Kết thúc";
		}
		else if ("REO".equals(keyStatus)) {
			action = "Mở lại";
		}
		else if ("TRA".equals(keyStatus)) {
			action = "Chuyển";
		}
		else if ("ACO".equals(keyStatus)) {
			action = "Thêm nội dung";
		} 
		
		return action;
	}
	
	
	

}
