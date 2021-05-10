package com.fds.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fds.entities.FdsEbankSysUser;

@Repository
public interface SysUserRepo extends JpaRepository<FdsEbankSysUser, String> {
	FdsEbankSysUser findByEmail(String email);

	List<FdsEbankSysUser> findAllUserByActiveflagIsTrue();
}
