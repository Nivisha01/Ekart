FROM openjdk:17-jdk-alpine

EXPOSE 8080

ENV APP_HOME=/usr/src/app

COPY target/shopping-cart-0.0.1-SNAPSHOT.jar $APP_HOME/app.jar

WORKDIR $APP_HOME

ENTRYPOINT ["java", "-jar", "app.jar"]
