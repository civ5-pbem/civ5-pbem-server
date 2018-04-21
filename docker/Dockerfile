FROM openjdk:8

RUN mkdir /app
COPY civ5-pbem-server.jar /app/civ5-pbem-server.jar

EXPOSE 8080
USER 1000:0

ENTRYPOINT ["java", "-jar", "/app/civ5-pbem-server.jar"]
