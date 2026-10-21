# Etapa 1: build
# Usa uma imagem Maven com Java 17 para compilar o projeto
FROM maven:3.9.16-eclipse-temurin-25 AS build
# Define o diretório de trabalho dentro do container
WORKDIR /opt/app
# Copia todo código-fonte para dentro do container
COPY . .
# Executa o build do projeto, gerando o JAR em /opt/app/target/
RUN mvn clean package -DskipTests

# Etapa 2: runtime
# Usa uma imagem leve do Java 17 para rodar o app
FROM eclipse-temurin:25-jre
WORKDIR /opt/app
# Copia o JAR gerado na etapa de build para a imagem final
COPY --from=build /opt/app/target/*.jar /opt/app/app.jar
# Expõe a porta 8080 (padrão do Spring Boot)
EXPOSE 8080
# Define o comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]