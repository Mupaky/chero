FROM openjdk:21

COPY build/libs/chero-*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod" , "-jar", "app.jar"]