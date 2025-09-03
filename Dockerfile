# Usa una imagen base liviana con Java 17
FROM openjdk:17-jdk-slim

# Crea un directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR al contenedor
COPY target/abmlcontroller-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto que Render asigna dinámicamente
EXPOSE 8080

# Render usa la variable de entorno PORT, así que Spring Boot debe respetarla
ENV PORT=8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
