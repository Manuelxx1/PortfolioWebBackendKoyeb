# Etapa de build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos solo el pom para preparar dependencias
COPY pom.xml .

# 1) Borramos cualquier rastro previo del SDK en el repo local
RUN rm -rf /root/.m2/repository/com/mercadopago

# 2) Forzamos actualización de metadatos y dependencias
RUN mvn -U dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# 3) Antes de compilar, mostramos el arbol de dependencias para verificar versión
RUN mvn -U dependency:tree && \
    mvn -U clean package -DskipTests

# Etapa de runtime con JDK limpio
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
