package com.fds.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fds.entities.FdsEbankSysTxn;

@Repository
public interface SysTxnRepo extends CrudRepository<FdsEbankSysTxn, String> {

	@Query(value = "select txn.idtxn, txn.caption, txn.description, txn.isenable, txn.icon, txn.parent from {h-schema}fds_ebank_sys_userrole uro join {h-schema}fds_ebank_sys_roletxn rol on uro.idrole = rol.idrole join {h-schema}fds_ebank_sys_txn txn on txn.idtxn = rol.idtxn where uro.iduser = ?1 and lower(txn.isenable) = lower('Y') order by txn.weight", nativeQuery = true)
	List<Object[]> findAllByUserId(String id);

	@Query(value = "select t.idtxn,t.caption,t.description,t.icon,t.isenable,t.parent,t.weight from {h-schema}fds_ebank_sys_roletxn f join {h-schema}fds_ebank_sys_txn t on f.idtxn = t.idtxn  where f.idrole = :roleid", nativeQuery = true)
	List<Object[]> findAllByRoleId(@Param("roleid") int roleid);

	List<FdsEbankSysTxn> findAll();
}
