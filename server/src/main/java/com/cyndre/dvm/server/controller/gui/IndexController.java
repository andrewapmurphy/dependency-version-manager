package com.cyndre.dvm.server.controller.gui;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyndre.dvm.data.Project;
import com.cyndre.dvm.server.data.project.ProjectService;
import com.cyndre.dvm.server.uri.LocalRedirectView;

@Controller
public class IndexController {
	@Resource(name="projectService")
	private ProjectService projectService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public Object index(final Model model) {
        return "/index";
    }
	
	@RequestMapping(value = "/{groupId}/{artifactId}", method = RequestMethod.GET)
    @ResponseBody
    public String getAllVersions(@PathVariable("groupId") final String groupId, @PathVariable("artifactId") final String artifactId) {
        return projectService.getProject(groupId, artifactId).toString();
    }
	
	@RequestMapping(value = "/create/{groupId}/{artifactId}/{version}", method = RequestMethod.GET)
    @ResponseBody
    public Object getAllVersions(@PathVariable("groupId") final String groupId, @PathVariable("artifactId") final String artifactId, @PathVariable("version") final String version) {
        final Project project = new Project();
        project.setGroupId(groupId);
        project.setArtifactId(artifactId);
        project.setVersion(version);

        projectService.create(project);
        
        return new LocalRedirectView(allVersionsUrl(project));
    }
	
	private static final String allVersionsUrl(final Project project) {
		return "/" + project.getGroupId() + "/" + project.getArtifactId();
	}
}
