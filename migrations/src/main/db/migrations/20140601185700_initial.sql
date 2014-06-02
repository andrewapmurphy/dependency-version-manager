CREATE TABLE project (
	projectId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,

	groupId VARCHAR(255) NOT NULL,
	artifactId VARCHAR(255) NOT NULL,
	version VARCHAR(255) NOT NULL,
	
	firstSeen TIMESTAMP,
	lastUpdated TIMESTAMP,

	CONSTRAINT uniqueArtifactVersion UNIQUE(groupId, artifactId, version)
);

