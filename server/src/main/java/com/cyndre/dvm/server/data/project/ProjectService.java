package com.cyndre.dvm.server.data.project;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.cyndre.dvm.data.Project;
import com.cyndre.dvm.server.data.mapper.ProjectMapper;

public class ProjectService {
	@Resource(name="projectMapper")
	private ProjectMapper projectMapper;
	
	public List<Project> getProject(final String groupId, final String artifactId) {
		return projectMapper.getAllVersions(groupId, artifactId);
	}
	
	public Project create(final Project project) {
		final Date now = Calendar.getInstance().getTime();
		//project.setFirstSeen(now);
		//project.setLastUpdated(now);
		
		projectMapper.add(project);
		
		return project;
	}
}
