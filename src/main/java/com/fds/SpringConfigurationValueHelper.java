package com.fds;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Dung de ho tro get value tu file config cho component khong quan ly boi
 * Spring
 */
@Component
public class SpringConfigurationValueHelper {

	@Value("${path.template.report}")
	private String pathTempReport;

	@Value("${time.refresh.content}")
	private int sTimeRefreshContent;

	@Value("${user.autoclosecase}")
	private String userAutoCloseCase;
	
	public String getPathTemplateReport() {
		return pathTempReport;
	}

	public int sTimeRefreshContent() {
		return sTimeRefreshContent;
	}

	/**
	 * @return the userAutoCloseCase
	 */
	public String getUserAutoCloseCase() {
		return userAutoCloseCase;
	}

	/**
	 * @param userAutoCloseCase the userAutoCloseCase to set
	 */
	public void setUserAutoCloseCase(String userAutoCloseCase) {
		this.userAutoCloseCase = userAutoCloseCase;
	}

	
}
