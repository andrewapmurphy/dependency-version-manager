package com.cyndre.dvm.server.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import freemarker.template.TemplateException;

@Configuration
public class ViewConfig {
	public static final String FREEMARKER_TEMPLATE_PATH = "/../../WEB-INF/private/ftl/";

	@Bean
	public FreeMarkerConfigurer freemarkerConfigurer() throws IOException, TemplateException {
		final FreeMarkerConfigurationFactory fac = new FreeMarkerConfigurationFactory();
		
		fac.setTemplateLoaderPath(FREEMARKER_TEMPLATE_PATH);
		
		FreeMarkerConfigurer configer = new FreeMarkerConfigurer();
		fac.setPreferFileSystemAccess(true);
		configer.setConfiguration(fac.createConfiguration());
				
		return configer;
	}
	
	@Bean
	public FreeMarkerViewResolver freemarkerViewResolver() {
		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
		
		resolver.setCache(false);
		resolver.setPrefix("");
		resolver.setSuffix(".ftl");
		resolver.setContentType("text/html");
		resolver.setRedirectContextRelative(true);
		
		return resolver;
	}
}
