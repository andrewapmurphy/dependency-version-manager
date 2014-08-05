package com.cyndre.dvm.server.data.mapper;

import java.util.Date;

import com.cyndre.dvm.data.Project;

public class ProjectDBO extends Project {
	private Date firstSeen;
	private Date lastUpdated;
	private Long id = null;
	
	public ProjectDBO() {
		super();
	}
	
	public ProjectDBO(final Project project) {
		super(project);
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

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
