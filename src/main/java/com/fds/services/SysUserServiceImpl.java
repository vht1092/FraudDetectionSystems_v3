package com.fds.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fds.TimeConverter;
import com.fds.entities.FdsEbankSysRole;
import com.fds.entities.FdsEbankSysUser;
import com.fds.entities.FdsEbankSysUserrole;
import com.fds.entities.FdsSysUserrolePK;
import com.fds.repositories.SysRoleRepo;
import com.fds.repositories.SysUserRepo;
import com.fds.repositories.SysUserroleRepo;

@Service("sysUserService")
@Transactional
public class SysUserServiceImpl implements SysUserService {

	@Autowired
	private SysUserRepo sysUserRepo;

	@Autowired
	private SysRoleRepo roleRepository;
	@Autowired
	private SysUserroleRepo userroleRepo;
	private final TimeConverter timeConverter = new TimeConverter();

	@Override
	public FdsEbankSysUser findAllByEmail(String email) {
		return sysUserRepo.findByEmail(email);
	}

	@Override
	public FdsEbankSysUser findByUserid(String userid) {
		return sysUserRepo.findOne(userid);
	}

	@Override
	public String createNew(String userid, String email, String fullname) {
		FdsEbankSysUser sysUser = new FdsEbankSysUser();
		sysUser.setUserid(userid);
		sysUser.setActiveflag(true);// Mac dinh duoc active
		sysUser.setEmail(email);
		sysUser.setFullname(fullname);
		sysUser.setUsertype("OFF");// User mac dinh la officer
		sysUser.setCreatedate(timeConverter.getCurrentTime());
		sysUser.setLastlogin(timeConverter.getCurrentTime());
		sysUserRepo.save(sysUser);
		// Gan role mac dinh
		List<FdsEbankSysRole> listDefaultRole = roleRepository.findAllByDefaultroleIsTrue();

		for (FdsEbankSysRole list : listDefaultRole) {
			FdsSysUserrolePK id = new FdsSysUserrolePK();
			id.setIdrole(list.getId());
			id.setIduser(sysUser.getUserid());
			userroleRepo.save(new FdsEbankSysUserrole(id));
		}
		return sysUser.getUserid();
	}

	@Override
	public void updateLastLogin(String userid) {
		FdsEbankSysUser user = sysUserRepo.findOne(userid);
		user.setLastlogin(timeConverter.getCurrentTime());
		sysUserRepo.save(user);
	}

	@Override
	public List<FdsEbankSysUser> findAllUser() {
		return sysUserRepo.findAll();
	}

	@Override
	public void updateUserByUserId(String userid, String fullname, String usertype, Boolean active) {
		FdsEbankSysUser fdsSysUser = sysUserRepo.findOne(userid);
		if (fdsSysUser == null) {
			fdsSysUser = new FdsEbankSysUser();
			fdsSysUser.setUserid(userid);
		}
		fdsSysUser.setFullname(fullname);
		fdsSysUser.setUsertype(usertype);
		fdsSysUser.setActiveflag(active);
		fdsSysUser.setUpdatedate(timeConverter.getCurrentTime());
		sysUserRepo.save(fdsSysUser);

	}

	@Override
	public List<FdsEbankSysUser> findAllUserByActiveflagIsTrue() {
		return sysUserRepo.findAllUserByActiveflagIsTrue();
	}

}
