#Amazon Corretto 21 (Java 21)

FROM maven:3.9.5-eclipse-temurin-21

WORKDIR /app

COPY backend/pom.xml .

RUN mvn dependency:go-offline

COPY backend/src ./src

COPY .git ./git

RUN mvn clean package -DskipTests

CMD ["sh", "-c", "java -jar target/*.jar"]

