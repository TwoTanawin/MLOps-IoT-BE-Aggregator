# First stage: Build the application using Maven
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

ENV TZ=Asia/Bangkok

# Copy pom.xml and download dependencies
COPY ../pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY ../src ./src
RUN mvn clean package -DskipTests

# Second stage: Create a smaller runtime image
FROM openjdk:17-alpine
WORKDIR /app

RUN apk add --no-cache tzdata
ENV TZ=Asia/Bangkok

# Java options (for Netty and Reactor)
ENV JDK_JAVA_OPTIONS="--add-opens=java.base/java.nio=ALL-UNNAMED \
                      --add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
                      --add-opens=java.base/java.util=ALL-UNNAMED \
                      --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
                      -Dio.netty.tryReflectionSetAccessible=true"

# Copy the built jar file from the build stage
COPY --from=build /app/target/aggregateService-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/app.jar"]