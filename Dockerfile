FROM openjdk:17-alpine
ARG JAR_FILE=build/libs/*.jar
COPY build/libs/CinemaTicketPro-0.0.1-SNAPSHOT.jar CinemaTicketPro.jar

ENTRYPOINT ["java", "-jar", "CinemaTicketPro.jar"]