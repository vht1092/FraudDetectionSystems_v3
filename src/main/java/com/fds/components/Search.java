package com.fds.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fds.ReloadAutoComponent;
import com.fds.ReloadComponent;
import com.fds.SpringContextHelper;
import com.fds.TimeConverter;
import com.fds.entities.FdsEbankCaseDetail;
import com.fds.services.DescriptionService;
import com.fds.services.EbankCaseDetailService;
import com.fds.services.SysUserService;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Man hinh tim kiem
 * 
 */

@SpringComponent
@ViewScope
public class Search extends CustomComponent implements ReloadComponent {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Search.class);
	private final transient EbankCaseDetailService caseDetailService;
	
	
	private CaseEbankDetailGridComponent grid;
	private transient HorizontalLayout pagingLayout;
	private Page<FdsEbankCaseDetail> result;
	
	private static final int SIZE_OF_PAGE = 50;
	private static final int FIRST_OF_PAGE = 0;

	public static final String CAPTION = "TÌM KIẾM";
	private static final String STATUS = "TÌNH TRẠNG CASE";
	private static final String VALIDATE_NUMBER = "Chỉ nhận giá trị số";
	private static final String FROM_DATE = "TỪ NGÀY";
	private static final String USER_ID = "NGƯỜI TIẾP NHẬN";
	private static final String CASE_NO = "SỐ CASE";
	private static final String TK_LOGIN = "TK_LOGIN";
	private static final String CIF_NO = "CIF";
	//private static final String CARD_BRAND = "LOẠI THẺ"; // MasterCard, Visa
	private static final String TO_DATE = "ĐẾN NGÀY";
	private static final String INPUT_FIELD = "Vui lòng chọn giá trị";
	private static final String SEARCH = "TÌM KIẾM";
	private transient String sDateFrom = "";
	private transient String sDateTo = "";
	private transient String sUserId = "";
	private transient String sCaseNo = "";
	private transient String sTkLogin = "";
	private transient String sCifNo = "";
	private transient String sStatus = "";
	
	private final VerticalLayout mainLayout = new VerticalLayout();

	@SuppressWarnings("unchecked")
	public Search() {
		//final VerticalLayout mainLayout = new VerticalLayout();

		mainLayout.setCaption(CAPTION);
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		caseDetailService = (EbankCaseDetailService) helper.getBean("ebankCaseDetailService");
		final DescriptionService descriptionService = (DescriptionService) helper.getBean("descriptionService");
		final SysUserService sysUserService = (SysUserService) helper.getBean("sysUserService");

		//grid = new CaseEbankDetailGridComponent(null, false, "All");
		grid = new CaseEbankDetailGridComponent(getData(new PageRequest(FIRST_OF_PAGE, SIZE_OF_PAGE, Sort.Direction.DESC, "txnCreTms")), true, "");	
		
		mainLayout.setSpacing(true);
		mainLayout.setMargin(new MarginInfo(true, false, false, false));

		final FormLayout form = new FormLayout();
		form.setMargin(new MarginInfo(false, false, false, true));
		
		pagingLayout = generatePagingLayout();
		pagingLayout.setSpacing(true);

		final ComboBox cbboxStatusCase = new ComboBox(STATUS);
		cbboxStatusCase.addContainerProperty("description", String.class, "");
		cbboxStatusCase.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbboxStatusCase.setItemCaptionPropertyId("description");
		descriptionService.findAllByType("CASE STATUS").forEach(s -> {
			final Item item = cbboxStatusCase.addItem(s.getId());
			item.getItemProperty("description").setValue(s.getDescription());
		});
		// Chi nhan gia tri so
		final Validator numberValidator = new RegexpValidator("\\d*", VALIDATE_NUMBER);

		final ComboBox cbboxUserId = new ComboBox(USER_ID);
		sysUserService.findAllUserByActiveflagIsTrue().forEach(r -> {
			cbboxUserId.addItems(r.getUserid());
		});

		final TextField tfCaseNo = new TextField(CASE_NO);
		final TextField tfTkLogin = new TextField(TK_LOGIN);
		final TextField tfCifNo = new TextField(CIF_NO);
		tfTkLogin.addValidator(numberValidator);
		// Xu ly loai bo khoan trang
		tfTkLogin.setTextChangeEventMode(TextChangeEventMode.EAGER);
		tfTkLogin.addTextChangeListener(evt -> {
			final String sText = evt.getText().replaceAll(" ", "");
			tfTkLogin.setValue(sText);
		});

		final DateField dfDateFrom = new DateField(FROM_DATE);
		dfDateFrom.addValidator(new NullValidator(INPUT_FIELD, false));
		dfDateFrom.setDateFormat("dd/MM/yyyy");
		dfDateFrom.setValidationVisible(false);

		final DateField dfDateTo = new DateField(TO_DATE);
		dfDateTo.addValidator(new NullValidator(INPUT_FIELD, false));
		dfDateTo.setDateFormat("dd/MM/yyyy");
		dfDateTo.setValidationVisible(false);

		final Button btSearch = new Button(SEARCH);
		btSearch.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSearch.setIcon(FontAwesome.SEARCH);
		btSearch.addClickListener(evt -> {
			dfDateFrom.setValidationVisible(false);
			dfDateTo.setValidationVisible(false);
			try {
				dfDateFrom.validate();
				dfDateTo.validate();
				final TimeConverter timeConverter = new TimeConverter();
				sDateFrom = timeConverter.convertDatetimeYMDHMMSS(dfDateFrom.getValue(), false);
				sDateTo = timeConverter.convertDatetimeYMDHMMSS(dfDateTo.getValue(), true);
				sUserId = cbboxUserId.getValue() != null ? cbboxUserId.getValue().toString().trim() : "";
				sCaseNo = tfCaseNo.getValue() != null ? tfCaseNo.getValue().toString().trim() : "";
				sTkLogin = tfTkLogin.getValue() != null ? tfTkLogin.getValue().toString().trim() : "";
				sCifNo = tfCifNo.getValue() != null ? tfCifNo.getValue().toString().trim() : "";
				sStatus = cbboxStatusCase.getValue() != null ? cbboxStatusCase.getValue().toString() : "";
				refreshData();
				
				// Refresh paging button
				mainLayout.removeComponent(pagingLayout);
				pagingLayout = generatePagingLayout();
				pagingLayout.setSpacing(true);
				mainLayout.addComponent(pagingLayout);
				mainLayout.setComponentAlignment(pagingLayout, Alignment.BOTTOM_RIGHT);
				
			} catch (InvalidValueException e) {
				dfDateFrom.setValidationVisible(true);
				dfDateTo.setValidationVisible(true);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		});

		form.addComponent(tfCaseNo);
		form.addComponent(tfTkLogin);
		form.addComponent(tfCifNo);
		form.addComponent(cbboxUserId);
		form.addComponent(cbboxStatusCase);
		form.addComponent(dfDateFrom);
		form.addComponent(dfDateTo);
		form.addComponent(btSearch);

		mainLayout.addComponent(form);
		mainLayout.addComponent(grid);
		
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
		result = caseDetailService.search(sCaseNo, sUserId, sDateFrom, sDateTo, sStatus, sTkLogin, sCifNo, page);
		return result;
	}

	protected void refreshData() {
		grid.refreshData(getData(new PageRequest(FIRST_OF_PAGE, SIZE_OF_PAGE, Sort.Direction.DESC, "txnCreTms")));
	}

	@Override
	public void eventReload() {
		// TODO Auto-generated method stub
		grid.refreshData(getData(new PageRequest(FIRST_OF_PAGE, SIZE_OF_PAGE, Sort.Direction.DESC, "txnCreTms")));
		
		// Refresh paging button
		mainLayout.removeComponent(pagingLayout);
		pagingLayout = generatePagingLayout();
		pagingLayout.setSpacing(true);
		mainLayout.addComponent(pagingLayout);
		mainLayout.setComponentAlignment(pagingLayout, Alignment.BOTTOM_RIGHT);
	}
	
	/*private Page<FdsEbankCaseDetail> getData(final String caseno, final String userid, final String datefrom, final String dateto, final String status,
			final String tkLogin, String cifNo) {
		return caseDetailService.search(caseno, userid, datefrom, dateto, status, tkLogin, cifNo, new PageRequest(FIRST_OF_PAGE, SIZE_OF_PAGE, Sort.Direction.DESC, "txnCreTms"));
	}

	protected void refreshData() {
		grid.refreshData(getData(sCaseNo, sUserId, sDateFrom, sDateTo, sStatus, sTkLogin, sCifNo));
	}*/


}
