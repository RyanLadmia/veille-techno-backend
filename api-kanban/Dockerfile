# Build projecet with JDK
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copy maven files and dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copy app code
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

# execute JAR (java code compiled)
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]