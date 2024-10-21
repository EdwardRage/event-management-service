FROM eclipse-temurin:21-jre-jammy
ENV SPRING_PROFILES_ACTIVE=production
COPY target/*.jar event-management-service.jar
ENTRYPOINT ["java","-jar", "/event-management-service.jar"]