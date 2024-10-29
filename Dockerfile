FROM eclipse-temurin:21-jre-jammy
COPY target/*.jar event-management-service.jar
ENTRYPOINT ["java","-jar", "/event-management-service.jar"]