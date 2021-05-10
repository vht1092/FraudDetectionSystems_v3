package com.fds.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the FDS_EBANK_SYS_USERROLE database table.
 * 
 */
@Entity
@Table(name="FDS_EBANK_SYS_USERROLE")
@NamedQuery(name="FdsEbankSysUserrole.findAll", query="SELECT f FROM FdsEbankSysUserrole f")
public class FdsEbankSysUserrole implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdsSysUserrolePK id;

	public FdsEbankSysUserrole() {
	}
	

	public FdsEbankSysUserrole(FdsSysUserrolePK id) {
		super();
		this.id = id;
	}

	public FdsSysUserrolePK getId() {
		return this.id;
	}

	public void setId(FdsSysUserrolePK id) {
		this.id = id;
	}

}

/*
select * from FDS_EBANK_SYS_ROLE
select * from FDS_EBANK_SYS_USER
select * from FDS_EBANK_SYS_USERROLE
*/



