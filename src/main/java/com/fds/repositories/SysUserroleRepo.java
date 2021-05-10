package com.fds.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fds.entities.FdsEbankSysUserrole;

@Repository
public interface SysUserroleRepo extends CrudRepository<FdsEbankSysUserrole, String> {

	@Query(value = "select f from FdsEbankSysUserrole f where f.id.iduser=:iduser")
	List<FdsEbankSysUserrole> findAllByIdUser(@Param("iduser") String iduser);

	@Query(value = "delete from {h-schema}fds_ebank_sys_userrole t where t.iduser=:iduser", nativeQuery = true)
	List<FdsEbankSysUserrole> deleteByIduser(@Param("iduser") String iduser);
	
	@Query(value = "select IDROLE from {h-schema}fds_ebank_sys_userrole f where iduser=:iduser and rownum <= 1", nativeQuery = true)
	int getIdRoleOfUerLogin(@Param("iduser") String iduser);
	

}
