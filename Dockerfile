# SDFEater Dockerfile

# Build stage
FROM maven:3-openjdk-11-slim AS build
LABEL maintainer="Łukasz Szeremeta <l.szeremeta.dev+sdfeater@gmail.com>"

WORKDIR /app

## add project files (see .dockerignore)
COPY . .

## build and rename jar
RUN mvn package --file=pom.xml \
    && mv target/SDFEater-*-jar-with-dependencies.jar SDFEater.jar


# Package stage
FROM gcr.io/distroless/java:11-nonroot
LABEL maintainer="Łukasz Szeremeta <l.szeremeta.dev+sdfeater@gmail.com>"

WORKDIR /app

COPY --from=build /app/SDFEater.jar SDFEater.jar

ENTRYPOINT [ "java", "-jar", "SDFEater.jar" ]
