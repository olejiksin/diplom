import React from 'react';

export const DockerList: React.FC = () =>{
    return(
        <div className="help-main-div" style={{marginBottom:'50px'}}>
            <h1>Dockerfiles for applications</h1>
            <div className="help-main-div" id="java"><h1>Java</h1>
                <div className="docker-code">
                    <p> FROM maven:3.6.3-jdk-8-slim AS build</p>
                    <p> COPY src /home/app/src</p>
                    <p> COPY pom.xml /home/app</p>
                    <p>RUN mvn -f /home/app/pom.xml clean test package</p>
                    <p># Package stage</p>
                    <p>FROM openjdk:8-jdk-alpine</p>
                    <p>COPY --from=build /home/app/target/*.jar app.jar</p>
                    <p>ENTRYPOINT ["java","-jar","app.jar"]</p>
                </div>
            </div>
            <div className="help-main-div" id="python"><h1>Python</h1>
                <div className="docker-code">
                    <p> FROM maven:3.6.3-jdk-8-slim AS build</p>
                    <p> COPY src /home/app/src</p>
                    <p> COPY pom.xml /home/app</p>
                    <p>RUN mvn -f /home/app/pom.xml clean test package</p>
                    <p># Package stage</p>
                    <p>FROM openjdk:8-jdk-alpine</p>
                    <p>COPY --from=build /home/app/target/*.jar app.jar</p>
                    <p>ENTRYPOINT ["java","-jar","app.jar"]</p>
                </div>
            </div>
            <div className="help-main-div" id="nodejs"><h1>Node.JS</h1>
                <div className="docker-code">
                    <p> FROM maven:3.6.3-jdk-8-slim AS build</p>
                    <p> COPY src /home/app/src</p>
                    <p> COPY pom.xml /home/app</p>
                    <p>RUN mvn -f /home/app/pom.xml clean test package</p>
                    <p># Package stage</p>
                    <p>FROM openjdk:8-jdk-alpine</p>
                    <p>COPY --from=build /home/app/target/*.jar app.jar</p>
                    <p>ENTRYPOINT ["java","-jar","app.jar"]</p></div>
            </div>
            <div className="help-main-div" id="c#"><h1>C#</h1>
                <div className="docker-code">
                    <p> FROM maven:3.6.3-jdk-8-slim AS build</p>
                    <p> COPY src /home/app/src</p>
                    <p> COPY pom.xml /home/app</p>
                    <p>RUN mvn -f /home/app/pom.xml clean test package</p>
                    <p># Package stage</p>
                    <p>FROM openjdk:8-jdk-alpine</p>
                    <p>COPY --from=build /home/app/target/*.jar app.jar</p>
                    <p>ENTRYPOINT ["java","-jar","app.jar"]</p>
                </div>
            </div>
        </div>
    )
}
