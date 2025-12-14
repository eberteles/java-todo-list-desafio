# Stage 1: Build Stage (Criação do JAR)
FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

# Copia os arquivos de build (Gradle)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copia o código fonte
COPY src src

# Garante que o Gradle Wrapper seja executável
RUN chmod +x gradlew

# Compila o projeto e gera o JAR
RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime Stage (Imagem final, menor)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Expõe a porta que o Spring Boot usa (8080 por padrão)
EXPOSE 8080

# Copia o JAR do estágio de build para a imagem final
COPY --from=builder /app/build/libs/*.jar app.jar

# Define o ponto de entrada para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
