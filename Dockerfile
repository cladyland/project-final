FROM openjdk:17
VOLUME /tmp

ADD build/jira.jar jira.jar
ADD resources ./resources

ENTRYPOINT ["java", "-jar", "/jira.jar"]
