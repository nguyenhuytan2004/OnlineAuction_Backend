# Bước 1: Tải thư viện
FROM maven:3.9.6-eclipse-temurin-21 AS deps
WORKDIR /app
COPY pom.xml .
# Lệnh này chỉ tải các dependencies mà không build ứng dụng, giúp tăng tốc độ build sau này nếu code thay đổi nhưng dependencies không thay đổi
RUN mvn dependency:go-offline -B

# Bước 2: Build ứng dụng
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Bước 3: Chạy ứng dụng
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]