package com.fds.services;

import java.util.List;

import com.fds.entities.FdsEbankSysUserrole;

public interface SysUserroleService {
	void save(String iduser, int idrole);
	void deleteByIduser(String iduser);
	List<FdsEbankSysUserrole> findAllByUserId(String iduser);
	
	int getIdRoleOfUerLogin(String iduser);
}
