FROM openjdk:17-oracle
ADD /build/libs/CinemaTicketPro-0.0.1-SNAPSHOT.jar CinemaTicketPro.jar

ENTRYPOINT ["java", "-jar", "CinemaTicketPro.jar"]