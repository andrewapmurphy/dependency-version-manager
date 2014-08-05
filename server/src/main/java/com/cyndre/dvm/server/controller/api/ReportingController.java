package com.cyndre.dvm.server.controller.api;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyndre.dvm.data.Project;
import com.cyndre.dvm.reporting.HashGenerator;
import com.cyndre.dvm.reporting.ReportingConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

@Controller(value="/reporting")
public class ReportingController {
	private static final String SECRET_KEY = "shhsecret"; //TODO: this is temporary
	
	@Resource(name="jsonMapper")
	private ObjectMapper jsonMapper;
	
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public String report(
		final HttpServletRequest request,
		final HttpServletResponse response,
		@RequestHeader(ReportingConstants.DIGEST_HEADER) final String digest,
		@RequestBody final Project project
	) {
		if (!isAuthorized(project, digest)) {
			return notAuthorized(response);
		}
		
		return report(response, project);
	}
	
	private String report(final HttpServletResponse response, final Project project) {
		return success(response);
	}
	
	private boolean isAuthorized(final Project project, final String digest) {		
		if (Strings.isNullOrEmpty(digest)) {
			return false;
		}

		try {
			final String body = jsonMapper.writeValueAsString(project);
			
			if (Strings.isNullOrEmpty(body)) {
				return false;
			}
			
			final HashGenerator generator = new HashGenerator(SECRET_KEY);
			
			final String generatedDigest = generator.hash(body);
			
			return generatedDigest.equals(digest);
		} catch (IOException e) {
			//LOG
			return false;
		}
	}
	
	private static String success(final HttpServletResponse response) {
		response.setStatus(HttpStatus.ACCEPTED.value());
		
		return ReportingConstants.SUCCESS;
	}
	
	private static String notAuthorized(final HttpServletResponse response) {
		final String msg = "Not authorized";
		
		try {
			response.sendError(HttpStatus.UNAUTHORIZED.value(), msg);
		} catch (IOException e) {
			//TODO: Log
		}

		return msg;
	}
}
