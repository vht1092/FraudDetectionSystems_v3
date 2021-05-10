package com.fds.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fds.entities.FdsEbankSysRole;

@Repository
public interface SysRoleRepo extends CrudRepository<FdsEbankSysRole, Integer> {
	List<FdsEbankSysRole> findAllByDefaultroleIsTrue();
	List<FdsEbankSysRole> findAll();
}
