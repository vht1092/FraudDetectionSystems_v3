package com.fds.services;

import com.fds.entities.FdsEbankDescription;

public interface DescriptionService {
	Iterable<FdsEbankDescription> findAllByType(String type);
	public void save(String id, String desc, String type);
	public String getNextIdContentDetail();
	public void deleteById(String id);
}
