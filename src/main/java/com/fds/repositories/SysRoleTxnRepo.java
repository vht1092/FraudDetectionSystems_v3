package com.fds.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fds.entities.FdsEbankSysRoletxn;


@Repository
public interface SysRoleTxnRepo extends CrudRepository<FdsEbankSysRoletxn, Long> {
	List<FdsEbankSysRoletxn> findAllByIdrole(int roleid);	
}
