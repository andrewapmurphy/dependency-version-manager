call mvn -f ../pom.xml clean install
call mvn -f ../migrations/pom.xml db-migration:migrate

pause

c:/devel/apache-tomcat-7.0.50/bin/startup.bat