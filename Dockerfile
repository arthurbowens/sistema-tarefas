# Use uma imagem base do OpenJDK 17
FROM openjdk:17-jdk-slim

# Definir diretório de trabalho
WORKDIR /app

# Copiar o arquivo JAR da aplicação
COPY target/tarefas-1.0-SNAPSHOT.jar app.jar

# Expor a porta 8080
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
