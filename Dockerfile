FROM eclipse-temurin:17-jdk-alpine as build

WORKDIR /app

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src /app/src

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/govservice-1.0-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java","-jar", "app.jar"]