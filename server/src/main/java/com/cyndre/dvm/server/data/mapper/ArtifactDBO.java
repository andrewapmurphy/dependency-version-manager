package com.cyndre.dvm.server.data.mapper;

import java.util.Date;

import com.cyndre.dvm.data.Artifact;

public class ArtifactDBO extends Artifact {
	private Date firstSeen;
	private Long id = null;
	
	public ArtifactDBO() {
		super();
	}
	
	public ArtifactDBO(final Artifact artifact) {
		super(artifact);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFirstSeen() {
		return firstSeen;
	}

	public void setFirstSeen(Date firstSeen) {
		this.firstSeen = firstSeen;
	}
	
	public void setFirstSeenAsNow() {
		setFirstSeen(new Date());
	}
}
