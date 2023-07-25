FROM openjdk:17-oracle
WORKDIR /cinema-ticket-pro

COPY /build/libs/CinemaTicketPro-0.0.1-SNAPSHOT.jar CinemaTicketPro.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "CinemaTicketPro.jar"]