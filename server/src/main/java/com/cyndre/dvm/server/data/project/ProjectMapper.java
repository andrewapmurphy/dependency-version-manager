package com.cyndre.dvm.server.data.project;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ProjectMapper {
	@Select("SELECT * FROM project WHERE groupId=#{groupId} AND artifactId=#{artifactId}")
	Project getProject(@Param("groupId") final String groupId, @Param("artifactId") final String artifactId);
}
