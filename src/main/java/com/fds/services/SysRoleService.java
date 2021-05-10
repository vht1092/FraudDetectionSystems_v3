package com.fds.services;

import java.util.List;

import com.fds.entities.FdsEbankSysRole;

public interface SysRoleService {
	List<FdsEbankSysRole> findAllByDefaultroleIsTrue();
	List<FdsEbankSysRole> findAll();
	void update(int id, String name, Boolean defaulrole);	
}
