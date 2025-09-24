# === STAGE 1: Build ===
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build

WORKDIR /app

# Copiar apenas pom.xml primeiro para cache de dependências
COPY pom.xml .

RUN mvn dependency:go-offline -B

# Copiar todo código depois
COPY src ./src

# Build sem testes para produção
RUN mvn clean package -DskipTests -B -V

# === STAGE 2: Runtime ===
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Variável de ambiente para Spring Profile
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
