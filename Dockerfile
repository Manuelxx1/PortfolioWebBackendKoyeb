# Etapa de build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos pom.xml primero para aprovechar cache de dependencias
COPY pom.xml .

# Limpiamos dependencias viejas y descargamos las nuevas
RUN mvn dependency:purge-local-repository -DmanualInclude="com.mercadopago:sdk-java" \
    && mvn dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Compilamos y empaquetamos
RUN mvn clean package -DskipTests

# Etapa de runtime con JDK limpio
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copiamos el JAR generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Ejecutamos la aplicación
ENTRYPOINT ["java","-jar","app.jar"]
