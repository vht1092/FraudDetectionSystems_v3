package com.fds.services;

import java.util.List;

import com.fds.entities.FdsEbankSysUser;

public interface SysUserService {
	public FdsEbankSysUser findAllByEmail(String email);

	public List<FdsEbankSysUser> findAllUser();
	
	public List<FdsEbankSysUser> findAllUserByActiveflagIsTrue();

	public String createNew(String userid, String email, String fullname);

	public void updateLastLogin(String userid);

	public void updateUserByUserId(String userid, String fullname, String usertype, Boolean active);

	public FdsEbankSysUser findByUserid(String userid);
}
