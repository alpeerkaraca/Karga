FROM maven:4.0.0-rc-5-eclipse-temurin-21-noble AS build
WORKDIR /src/app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-ubi10-minimal
WORKDIR /app

RUN groupadd --system appgroup \
 && useradd --system --gid appgroup --home-dir /app --create-home --shell /sbin/nologin appuser


COPY src/main/resources/kafka.client.truststore.jks /app/kafka.client.truststore.jks

COPY --from=build /src/app/target/*.jar ./app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]