FROM maven:3-jdk-11

COPY ./ /app/

WORKDIR /app

RUN mvn package -Dmaven.test.skip=true

EXPOSE 8080

CMD  [ "mvn", "spring-boot:run" ]