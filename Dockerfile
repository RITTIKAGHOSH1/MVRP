FROM maven AS build
LABEL authors="ritti"

WORKDIR /usr/src/app
COPY . /usr/src/app

RUN mvn clean package

FROM openjdk
WORKDIR /usr/src/app
COPY --from=build /usr/src/app/target/JspritFirstP-1.0-SNAPSHOT.jar /usr/src/app
CMD ["java","-jar","/usr/src/app/JspritFirstP-1.0-SNAPSHOT.jar"]
