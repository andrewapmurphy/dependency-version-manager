package com.cyndre.dvm.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cyndre.dvm.server.data.project.ProjectService;

@Configuration
public class ServicesConfig {
	@Bean(name="projectService")
	public ProjectService createProjectService() {
		return new ProjectService();
	}
}
