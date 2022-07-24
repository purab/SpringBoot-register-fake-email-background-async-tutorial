FROM adoptopenjdk:8-jre-hotspot

EXPOSE 8080

ADD target/register-fake-mail-async-0.0.1-SNAPSHOT.jar register-fake-mail-async-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","register-fake-mail-async-0.0.1-SNAPSHOT.jar"]