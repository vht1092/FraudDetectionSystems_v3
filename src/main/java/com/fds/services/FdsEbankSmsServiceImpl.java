package com.fds.services;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fds.SpringContextHelper;
import com.fds.entities.FdsEbankSms;
import com.fds.repositories.FdsEbankSmsRepo;
import com.vaadin.server.VaadinServlet;

@Service("fdsEbankSmsService")
@Transactional
public class FdsEbankSmsServiceImpl implements FdsEbankSmsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FdsEbankSmsServiceImpl.class);

	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
	private String sSchema;
	@Autowired
	private FdsEbankSmsRepo fdsEbankSmsRepo;
	
	protected DataSource localDataSource;
	
	
	@Override
	public void save(FdsEbankSms sms) {
		// TODO Auto-generated method stub
		fdsEbankSmsRepo.save(sms);
	}
	
	@Override
	public void sendSmsEbankFds(FdsEbankSms sms) throws SQLException {
		
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		localDataSource = (DataSource) helper.getBean("dataSource");
		Connection connect = null;
		CallableStatement callableStatement = null;

		String sp_SEND_SMS_FDS = "{call sms_scb.PROC_SENDSMS_FDS@sms_scb(?,?,?)}";
				
		try {
			connect = localDataSource.getConnection();
			
			callableStatement = connect.prepareCall(sp_SEND_SMS_FDS);
			callableStatement.setString(1, sms.getPhone());
			callableStatement.setString(2, sms.getSmsDetail());
			callableStatement.setString(3, sms.getSmsType());
			
			callableStatement.executeUpdate();
		}
		catch (Exception ex){
			LOGGER.error(ex.getMessage());
		}
		finally{
			connect.close();
			callableStatement.close();
		}
		
	}


}
