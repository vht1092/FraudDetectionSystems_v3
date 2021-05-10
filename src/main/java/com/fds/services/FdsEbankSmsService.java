package com.fds.services;

import java.sql.SQLException;

import com.fds.entities.FdsEbankSms;

public interface FdsEbankSmsService {

	public void save(FdsEbankSms sms);	
	
	public void sendSmsEbankFds(FdsEbankSms sms) throws SQLException;
}

