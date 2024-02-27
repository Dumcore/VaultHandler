FROM openjdk:17-oracle

WORKDIR /app
COPY target/vault-handler-1.0-SNAPSHOT.jar vault-handler-1.0-SNAPSHOT.jar
COPY target/libs libs
CMD ["java","-jar","vault-handler-1.0-SNAPSHOT.jar"]