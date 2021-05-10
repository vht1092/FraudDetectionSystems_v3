package com.fds.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fds.entities.FdsEbankSysUserrole;
import com.fds.entities.FdsSysUserrolePK;
import com.fds.repositories.SysUserroleRepo;

@Service("sysUserroleService")
public class SysUserroleServiceImpl implements SysUserroleService {

	@Autowired
	private SysUserroleRepo sysUserroleRepo;

	@Override
	public void save(String iduser, int idrole) {
		FdsEbankSysUserrole fdsSysUserrole = new FdsEbankSysUserrole();
		fdsSysUserrole.setId(new FdsSysUserrolePK(iduser, idrole));
		sysUserroleRepo.save(fdsSysUserrole);
	}

	@Override
	public List<FdsEbankSysUserrole> findAllByUserId(String iduser) {
		return sysUserroleRepo.findAllByIdUser(iduser);
	}

	@Override
	public void deleteByIduser(String iduser) {
		List<FdsEbankSysUserrole> fdsSysUserrole = sysUserroleRepo.findAllByIdUser(iduser);
		if (fdsSysUserrole != null) {
			sysUserroleRepo.delete(fdsSysUserrole);
		}
	}

	@Override
	public int getIdRoleOfUerLogin(String iduser) {
		return sysUserroleRepo.getIdRoleOfUerLogin(iduser);
	}

}
