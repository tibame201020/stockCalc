FROM maven:3.6.3-jdk-8 as builder
COPY . /buildDir
WORKDIR /buildDir
RUN mvn clean package
FROM openjdk:8-jdk-alpine
COPY --from=builder /buildDir/target/stockCalc-0.0.1-SNAPSHOT.jar app/springboot.jar
COPY ./twsessl.cer /app/twsessl.cer
RUN keytool -importcert -file /app/twsessl.cer -keystore cacerts -alias server -noprompt -storepass changeit
WORKDIR /app
ENTRYPOINT ["java", "-jar", "springboot.jar"]
