package com.fds.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.fds.entities.FdsEbankDescription;

public interface DescriptionRepo extends CrudRepository<FdsEbankDescription, Long> {
	Iterable<FdsEbankDescription> findAllByType(String type);
	
	@Query(nativeQuery = true, value = "SELECT 'A' || (MAX(SUBSTR(ID,2,2))+1) FROM FDS_EBANK_DESCRIPTION WHERE ID LIKE 'A%' AND TYPE <> 'ACTION' AND ID<'A98'")
	public String getNextIdContentDetail();
}
