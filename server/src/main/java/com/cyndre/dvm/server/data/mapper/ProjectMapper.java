package com.cyndre.dvm.server.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import com.cyndre.dvm.data.Project;

@Component("projectMapper")
public interface ProjectMapper {
	
	@Select("SELECT * FROM project WHERE groupId=#{groupId} AND artifactId=#{artifactId} AND version=#{version}")
	List<Project> getVersion(@Param("groupId") final String groupId, @Param("artifactId") final String artifactId, @Param("version") final String version);

	@Select("SELECT * FROM project WHERE groupId=#{groupId} AND artifactId=#{artifactId}")
	List<Project> getAllVersions(@Param("groupId") final String groupId, @Param("artifactId") final String artifactId);
	
	@Insert("INSERT INTO project (groupId, artifactId, version, firstSeen, lastUpdated) VALUES(#{groupId}, #{artifactId}, #{version}, #{firstSeen}, #{lastUpdated})")
	@Options(useGeneratedKeys=true, keyProperty="projectId")
	void add(final Project project);
	
	@Update("UPDATE project SET groupId=#{groupId}, artifactId=#{artifactId}, version=#{version}, firstSeen=#{firstSeen}, lastUpdated=#{lastUpdated} WHERE projectId=#{projectId}")
	void updateById(final Project project);
	
}
