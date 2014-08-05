package com.cyndre.dvm.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cyndre.dvm.server.data.project.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ServicesConfig {
	@Bean(name="projectService")
	public ProjectService createProjectService() {
		return new ProjectService();
	}
	
	@Bean(name="jsonMapper")
	public ObjectMapper jsonMapper() {
		return new ObjectMapper();
	}
}
