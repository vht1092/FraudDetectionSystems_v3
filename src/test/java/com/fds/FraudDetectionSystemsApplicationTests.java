package com.fds;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import com.fds.repositories.FdsEbankCaseDetailRepo;


@RunWith(SpringRunner.class)
@SpringBootTest

public class FraudDetectionSystemsApplicationTests {
	@Autowired
	private FdsEbankCaseDetailRepo fdsEbankDetailTxnRepository;
	@Test
	public void contextLoads() {
		fdsEbankDetailTxnRepository.findAll();

	}

}
