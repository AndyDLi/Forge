# -- Build Stage --
FROM eclipse-temurin:21-jdk-noble AS build
WORKDIR /build

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts ./
RUN ./gradlew --no-daemon dependencies --configuration runtimeClasspath

COPY src ./src

RUN ./gradlew --no-daemon bootJar

# -- Runtime Stage --
FROM eclipse-temurin:21-jre-noble AS runtime
WORKDIR /app

RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

RUN useradd --system --uid 10001 --create-home forge

COPY --from=build --chown=forge:forge /build/build/libs/forge-*.jar /app/forge.jar
USER forge

ENTRYPOINT ["java", "-jar", "/app/forge.jar"]
