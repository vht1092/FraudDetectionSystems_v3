package com.fds.components;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.fds.ReloadAutoComponent;
import com.fds.ReloadComponent;
import com.fds.SecurityUtils;
import com.fds.SpringContextHelper;
import com.fds.TimeConverter;
import com.fds.entities.FdsEbankSms;
import com.fds.services.DescriptionService;
import com.fds.services.FdsEbankSmsService;
import com.fds.services.SysTaskService;
import com.fds.services.SysUserroleService;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.ValoTheme;
/**
 * tanvh1 Aug 20, 2019
 *
 */
@SpringComponent
@Scope("prototype")
public class SendSms extends CustomComponent implements ReloadAutoComponent, ReloadComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String CAPTION = "SEND SMS";
	private static final String CONTENT = "SMS DETAIL";
	private static final String SUBMIT = "SUBMIT";
	
	private static final String PHONE_NUMBER = "PHONE NUMBER";
	
	
	public final transient FormLayout formLayout = new FormLayout();
	
	
	private final SysUserroleService sysUserroleService;
	private SysTaskService sysTaskService;
	private FdsEbankSmsService fdsEbankSmsService;
	
	public final transient Grid gridContent;
	public final transient IndexedContainer containerContent;
	
	TextArea txtareaContent;
	final Button btSubmit = new Button(SUBMIT);
	
	public final transient TextField tfPhoneNumber;
	
	
	private Window confirmDialog = new Window();
	private Button bOK = new Button("OK");
	private Button bCancel = new Button("Cancel");
	private transient String sUserId;
	private int CheckUserId;
	final TimeConverter timeConverter = new TimeConverter();
	private static final Logger LOGGER = LoggerFactory.getLogger(SendSms.class);
	
	public SendSms() {
		//TAN 20190815
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		final DescriptionService descService = (DescriptionService) helper.getBean("descriptionService");
		sysUserroleService = (SysUserroleService) helper.getBean("sysUserroleService");
		sysTaskService = (SysTaskService) helper.getBean("sysTaskService");
		fdsEbankSmsService = (FdsEbankSmsService) helper.getBean("fdsEbankSmsService");
		
		this.sUserId = SecurityUtils.getUserId();
		CheckUserId = sysUserroleService.getIdRoleOfUerLogin(sUserId);
		
		tfPhoneNumber = new TextField(PHONE_NUMBER);
		tfPhoneNumber.addValidator(new StringLengthValidator("Wrong length", 0, 10, true));
		
		txtareaContent = new TextArea(CONTENT);
		txtareaContent.setSizeFull();
		txtareaContent.setWidth(600, Unit.PIXELS);
		txtareaContent.setHeight(150, Unit.PIXELS);
		txtareaContent.addValidator(new StringLengthValidator("Wrong length", 0, 255, true));
		txtareaContent.setValue("Test send SMS FDS EB");
		txtareaContent.setReadOnly(true);
		
		btSubmit.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSubmit.setIcon(FontAwesome.SEND);
		btSubmit.addClickListener(event -> {
			try {
				if (checkValidator() && tfPhoneNumber.getValue()!=null && txtareaContent.getValue()!=null) {
					final FormLayout content = new FormLayout();
		            content.setMargin(true);
		            
					confirmDialog.setCaption("Bạn có thật sự muốn gởi SMS đến " + tfPhoneNumber.getValue() + " không ?");
					confirmDialog.setWidth(400.0f, Unit.PIXELS);
					
					VerticalLayout vlayoutBtn = new VerticalLayout();
					HorizontalLayout layoutBtn = new HorizontalLayout();
					layoutBtn.setSpacing(true);
		            layoutBtn.addComponents(bOK);
		            layoutBtn.addComponents(bCancel);
		            layoutBtn.setComponentAlignment(bOK, Alignment.BOTTOM_CENTER);
		            layoutBtn.setComponentAlignment(bCancel, Alignment.BOTTOM_CENTER);
		            vlayoutBtn.addComponent(layoutBtn);
		            vlayoutBtn.setComponentAlignment(layoutBtn, Alignment.MIDDLE_CENTER);
		            content.addComponent(vlayoutBtn);
		            confirmDialog.setContent(content);

		            getUI().addWindow(confirmDialog);
		            
		            // Center it in the browser window
		            confirmDialog.center();
		            confirmDialog.setResizable(false);
				} else
					Notification.show("Cảnh báo","Vui lòng kiểm tra lại thông tin SMS", Type.ERROR_MESSAGE);
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.info("Error : " + e.getMessage());
			}
			
		});
		
		bOK.addClickListener(event -> {
			FdsEbankSms sms = new FdsEbankSms();
			sms.setUsrId(sUserId);
			sms.setCreTms(new BigDecimal(timeConverter.getCurrentTime()));
			sms.setPhone(tfPhoneNumber.getValue());
			sms.setSmsDetail(txtareaContent.getValue());
			sms.setSmsType("FDSEB");
			fdsEbankSmsService.save(sms);
			try {
				fdsEbankSmsService.sendSmsEbankFds(sms);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.error("Error : " + e.getMessage());
			}
			LOGGER.info(sUserId + " send SMS EB: Phone = " + sms.getPhone() + ", Detail = " + sms.getSmsDetail());
			Notification.show("Thông tin","Tin nhắn gởi thành công đến " + tfPhoneNumber.getValue(), Type.WARNING_MESSAGE);
			confirmDialog.close();
		});
		
		bCancel.addClickListener(event -> {
			confirmDialog.close();
		});
		
		gridContent = new Grid();
		gridContent.setSizeFull();
		gridContent.setHeightByRows(10);
		gridContent.setHeightMode(HeightMode.ROW);
		gridContent.setVisible(false);
		
		containerContent = new IndexedContainer();
		containerContent.addContainerProperty("idtask", String.class, "");
		containerContent.addContainerProperty("userid", String.class, "");
		containerContent.addContainerProperty("createddate", String.class, "");
		containerContent.addContainerProperty("loc", String.class, "");
		containerContent.addContainerProperty("comment", String.class, "");
		containerContent.addContainerProperty("blod", String.class, "");
		containerContent.addContainerProperty("sms", String.class, "");
		containerContent.addContainerProperty("status", String.class, "");
		
		gridContent.setContainerDataSource(containerContent);
		gridContent.getColumn("idtask").setHeaderCaption("CASE NO");
		gridContent.getColumn("idtask").setWidth(90);
		gridContent.getColumn("userid").setHeaderCaption("USER");
		gridContent.getColumn("userid").setWidth(90);
		gridContent.getColumn("createddate").setHeaderCaption("CREATED DATE");
		gridContent.getColumn("createddate").setWidth(154);
		gridContent.getColumn("loc").setHeaderCaption("LOC");
		gridContent.getColumn("loc").setWidth(120);
		gridContent.getColumn("comment").setHeaderCaption("COMMENT");
		gridContent.getColumn("blod").setHeaderCaption("BLOD");
		gridContent.getColumn("blod").setWidth(70);
		gridContent.getColumn("sms").setHeaderCaption("SMS");
		gridContent.getColumn("sms").setWidth(65);
		gridContent.getColumn("status").setHeaderCaption("STATUS");
		gridContent.getColumn("status").setWidth(90);
		gridContent.addItemClickListener(itemEvent -> {
			
			
		});
		
		
		refreshData();
		
		final FormLayout form = new FormLayout();
		form.setMargin(new MarginInfo(true, false, true, true));
		
		form.addComponent(tfPhoneNumber);
		form.addComponent(txtareaContent);
		form.addComponent(btSubmit);
		
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		
		mainLayout.addComponent(form);
		mainLayout.addComponent(gridContent);
		setCompositionRoot(mainLayout);
		
	}
	
	@Override
	public void eventReload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventReloadAuto() {
		// TODO Auto-generated method stub
		
	}
	
	private void refreshData() {
				
		switch(CheckUserId) {
		}
		
	}
	
	private String htmlTaskNoteFull(String content) {
		return "<p><font color='blue'>" + content.replace("\n", "</br>") + "</font></p>";
	}
	
	public boolean checkValidator() {
		try {
			tfPhoneNumber.validate();
			txtareaContent.validate();
			return true;
		} catch (InvalidValueException ex) {
			tfPhoneNumber.setValidationVisible(true);
			txtareaContent.setValidationVisible(true);			
		}
		return false;
	}

}
