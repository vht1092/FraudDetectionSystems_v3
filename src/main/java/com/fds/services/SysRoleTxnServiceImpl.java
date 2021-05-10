package com.fds.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fds.entities.FdsEbankSysRoletxn;
import com.fds.repositories.SysRoleTxnRepo;

@Service("sysRoleTxnService")
@Transactional
public class SysRoleTxnServiceImpl implements SysRoleTxnService {

	@Autowired
	private SysRoleTxnRepo sysRoleTxnRepo;

	@Override
	public void save(int roleid, String txnid) {
		FdsEbankSysRoletxn fdsSysRoletxn = new FdsEbankSysRoletxn();
		fdsSysRoletxn.setIdrole(roleid);
		fdsSysRoletxn.setIdtxn(txnid);
		fdsSysRoletxn.setFlgauth(true);
		fdsSysRoletxn.setFlginit(true);
		fdsSysRoletxn.setFlgview(true);
		sysRoleTxnRepo.save(fdsSysRoletxn);
	}

	@Override
	public void deleteByRoleId(int roleid) {
		List<FdsEbankSysRoletxn> fdsSysRoletxn = sysRoleTxnRepo.findAllByIdrole(roleid);
		sysRoleTxnRepo.delete(fdsSysRoletxn);
	}
}
