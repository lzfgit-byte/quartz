#FROM openjdk:8u212-jre
FROM myopenjdk:8u212-jre-chinese-font
MAINTAINER jxyzwww@163.com
COPY ./target/*.jar /app.jar
ENV TZ="Asia/Shanghai"
#ENTRYPOINT ["java","-Xmx2048m","-Xms2048m","-javaagent:/skywalking/skywalking-agent/skywalking-agent.jar=agent.service_name=power-job,agent.service_group=power-framework,collector.backend_service=192.168.0.220:11800","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]