package com.fds.components;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fds.ReloadAutoComponent;
import com.fds.ReloadComponent;
import com.fds.SpringContextHelper;
import com.fds.entities.FdsCaseDetail;
import com.fds.entities.FdsEbankCaseDetail;
import com.fds.services.CaseDetailService;
import com.fds.services.EbankCaseDetailService;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Man hinh danh sach case chuyen giam sat
 * 
 */
@SpringComponent
@ViewScope
public class TransferLvl2 extends CustomComponent implements ReloadAutoComponent, ReloadComponent {
	private static final long serialVersionUID = 8808990975324145765L;
	public static final String CAPTION = "CASE CHUYỂN GIÁM SÁT";
	private final static String STATUS = "TL2";
	private final transient EbankCaseDetailService caseDetailService;
	private final CaseEbankDetailGridComponent grid;
	private final VerticalLayout mainLayout = new VerticalLayout();
	private Page<FdsEbankCaseDetail> result;
	// Paging
	private final static int SIZE_OF_PAGE = 50;
	private final static int FIRST_OF_PAGE = 0;
	private transient HorizontalLayout pagingLayout;

	// private final Logger logger
	// =LoggerFactory.getLogger(CaseDistribution.class);

	public TransferLvl2() {
		setCaption(CAPTION);

		SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		caseDetailService = (EbankCaseDetailService) helper.getBean("ebankCaseDetailService");

		mainLayout.setSpacing(true);
		grid = new CaseEbankDetailGridComponent(getData(new PageRequest(FIRST_OF_PAGE, SIZE_OF_PAGE, Sort.Direction.DESC, "txnCreTms")), true, "");
		mainLayout.addComponentAsFirst(grid);
		
		pagingLayout = generatePagingLayout();
		pagingLayout.setSpacing(true);
		mainLayout.addComponent(pagingLayout);
		mainLayout.setComponentAlignment(pagingLayout, Alignment.BOTTOM_RIGHT);

		setCompositionRoot(mainLayout);
	}
	
	private HorizontalLayout generatePagingLayout() {
		final Button btPaging = new Button();
		btPaging.setCaption(reloadLabelPaging());
		btPaging.setEnabled(false);

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
					btPaging.setCaption(reloadLabelPaging());
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
					btPaging.setCaption(reloadLabelPaging());
				}
			});
		});
		
		final HorizontalLayout pagingLayout = new HorizontalLayout();
		pagingLayout.setSizeUndefined();
		pagingLayout.setSpacing(true);
		pagingLayout.addComponent(btPaging);
		pagingLayout.addComponent(btPreviousPage);
		pagingLayout.addComponent(btNextPage);
		pagingLayout.setDefaultComponentAlignment(Alignment.BOTTOM_RIGHT);

		return pagingLayout;
	}

	private Page<FdsEbankCaseDetail> getData(Pageable page) {
		result = caseDetailService.findAllByStatus(page, STATUS);
		return result;
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

	@Override
	public void eventReloadAuto() {
		grid.refreshData(getData(new PageRequest(FIRST_OF_PAGE, SIZE_OF_PAGE, Sort.Direction.DESC, "txnCreTms")));
	}

	@Override
	public void eventReload() {
		grid.refreshData(getData(new PageRequest(FIRST_OF_PAGE, SIZE_OF_PAGE, Sort.Direction.DESC, "txnCreTms")));
		// Refresh paging button
		mainLayout.removeComponent(pagingLayout);
		pagingLayout = generatePagingLayout();
		pagingLayout.setSpacing(true);
		mainLayout.addComponent(pagingLayout);
		mainLayout.setComponentAlignment(pagingLayout, Alignment.BOTTOM_RIGHT);
		
	}

}
