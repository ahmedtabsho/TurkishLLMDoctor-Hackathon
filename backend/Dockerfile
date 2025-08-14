FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Maven bağımlılıklarını önce kopyalayıp önbelleğe alın
COPY pom.xml .
RUN mvn dependency:go-offline

# Kaynak kodları kopyalayın ve build edin
COPY src ./src
COPY system.properties .
RUN mvn clean package -DskipTests

# Çalışma zamanı imajı
FROM openjdk:17-jdk-slim
WORKDIR /app

# Uygulama JAR dosyasını kopyalayın
COPY --from=build /app/target/*.jar app.jar

# Port'u açın
EXPOSE 8080

# Çevresel değişkenleri tanımlayın
ENV PORT=8080

# Uygulamayı başlatın
CMD ["java", "-Dserver.port=8080", "-jar", "app.jar"]
