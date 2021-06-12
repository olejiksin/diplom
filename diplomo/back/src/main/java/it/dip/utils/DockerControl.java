package it.dip.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;


//монолиты
public class DockerControl {

    private String appName;
    private SshMaker sshMaker;
    private static final String dirName = "projects/";
    private String serverType;
    private FileWriter fileWriter;
    private final Map<String, String> dockerfileByLang;
    private final Map<String, String> defaultPorts;
    private final Map<String, String> dbDockerfile;

    public DockerControl() {
        serverType = "company";
        dockerfileByLang = new HashMap<>();
        dockerfileByLang.put("Java(SB)",
                "FROM maven:3.6.3-jdk-8-slim AS build\n" +
                        "COPY src /home/app/src\n" +
                        "COPY pom.xml /home/app\n" +
                        "RUN mvn -f /home/app/pom.xml clean test package\n" +
                        "\n" +
                        "# Package stage\n" +
                        "FROM openjdk:8-jdk-alpine\n" +
                        "COPY --from=build /home/app/target/*.jar app.jar\n" +
                        "ENTRYPOINT [\"java\",\"-jar\",\"app.jar\"]");
        dockerfileByLang.put("Java(Tomcat)",
                "");
        dockerfileByLang.put("Node.JS",
                "FROM node:current-alpine\n " +
                        "WORKDIR /usr/src/app\n" +
                        "COPY . /usr/src/app\n" +
                        "RUN npm install\n" +
                        "CMD [\"npm\", \"start\"]"
        );
        dockerfileByLang.put("Python",
                "FROM python:latest\n" +
                        "\n" +
                        "RUN apt-get update \\\n" +
                        "    && apt-get install -y --no-install-recommends \\\n" +
                        "        postgresql-client \\\n" +
                        "    && rm -rf /var/lib/apt/lists/*\n" +
                        "\n" +
                        "WORKDIR /usr/src/app\n" +
                        "COPY requirements.txt ./\n" +
                        "RUN pip install -r requirements.txt\n" +
                        "\n" +
                        "CMD [\"python\", \"manage.py\", \"runserver\", \"0.0.0.0:8000\"]");
        dockerfileByLang.put("C#",
                "FROM mcr.microsoft.com/dotnet/sdk:5.0 AS build-env\n" +
                        "WORKDIR /app\n" +
                        "\n" +
                        "COPY *.csproj ./\n" +
                        "RUN dotnet restore\n" +
                        "\n" +
                        "COPY ../engine/examples ./\n" +
                        "RUN dotnet publish -c Release -o out\n" +
                        "\n" +
                        "FROM mcr.microsoft.com/dotnet/aspnet:3.1\n" +
                        "WORKDIR /app\n" +
                        "COPY --from=build-env /app/out .\n" +
                        "ENTRYPOINT [\"dotnet\", \"aspnetapp.dll\"]");

        defaultPorts = new HashMap<>();
        defaultPorts.put("Java(SB)", "8080");
        defaultPorts.put("Java(Tomcat)", "8080");
        defaultPorts.put("Node.JS", "3000");
        defaultPorts.put("Python", "8000");
        defaultPorts.put("C#", "8080");
        defaultPorts.put("postgresql", "5432");
        defaultPorts.put("mongodb", "27017");
        defaultPorts.put("mysql", "3306");
        defaultPorts.put("redis", "6379");

        dbDockerfile = new HashMap<>();
        dbDockerfile.put("postgresql",
                "FROM postgres:10\n" +
                        "ADD init.sql /docker-entrypoint-initdb.d/\n");
        dbDockerfile.put("mongodb",
                "FROM mongo:latest\n" +
                        "ADD init.js /docker-entrypoint-initdb.d/\n");
//        dbDockerfile.put("redis",
//                "FROM ");
        dbDockerfile.put("mysql",
                "FROM mysql:latest\n" +
                        "ADD init.sql /docker-entrypoint-initdb.d/\n");
    }

    public void setAppName(String appName, String appId, String type) {
        if (type.equals("app")) {
            this.appName = (appName + "_" + appId).toLowerCase();
        } else {
            this.appName = appName + "_" + appId;
        }
    }

    public void setSshMakerAndServerType(SshMaker sshMaker, String serverType) {
        this.sshMaker = sshMaker;
        this.serverType = serverType;
    }


    public boolean checkDockerfile() {
        if (serverType.equals("own")) {
            sshMaker.doCommand(" cd " + dirName + " ; cd " + appName + " ; cat Dockerfile");
            if (sshMaker.getOutLog().substring(3, 10).equals("cat: Do")) return true;
        } else {
            Path path = Paths.get(dirName + appName).toAbsolutePath();
            File file = new File(path + "/Dockerfile");
            if (file.exists()) {
                return true;
            } else return false;
        }
        return false;
    }

    public void createDockerfile(String appLang) {
        String dockerfileData = dockerfileByLang.get(appLang);
        if (serverType.equals("own")) {
            String directory = dirName + appName;
            sshMaker.forExternalDockerfile(directory, dockerfileData);
        } else if (serverType.equals("company")) {
            Path path = Paths.get(dirName + appName);
            File file = new File(path + "/Dockerfile");
            try {
                file.createNewFile();
                fileWriter = new FileWriter(file);
                fileWriter.write(dockerfileData);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkDockerInstance() {
        if (serverType.equals("own")) {
            sshMaker.doCommand(" sudo docker");
            if (sshMaker.getOutLog().substring(6, 10).equals("sage")) return true;
        } else return true;
        return false;
    }

    public boolean checkAndCreateDockerFileForMicro(String appLang,String appNameMicro, String subAppName){
        sshMaker.doCommand(" cd "+dirName+appNameMicro+" ; cd "+subAppName+" ; cat Dockerfile");
        if(!sshMaker.getOutLog().substring(3,10).equals("cat: Do")){
            String dockerfileData=dockerfileByLang.get(appLang);
            String dir=dirName+appNameMicro+"/"+subAppName;
            sshMaker.forExternalDockerfile(dir,dockerfileData);
            return true;
        }
        return false;
    }

    public boolean createImage(String microAppName,String subMicroApp) {
        sshMaker.doCommand(" cd " + dirName+"/"+microAppName + " ; sudo docker build  -t " + subMicroApp + " " + subMicroApp);
        return sshMaker.getStatus();
    }

    public boolean installDB(String dbType, String port, String dbName, String login) {
        switch (dbType) {
            case "postgresql": {
                return installPostgres(port, dbName, login);
            }
            case "mongodb": {
                return installMongoDB(port, dbName, login);
            }
            case "mysql": {
                return installMySQL(port, dbName, login);
            }
            case "redis": {
                return installRedis(port, dbName, login);
            }
        }
        return false;
    }

    private boolean installRedis(String port, String dbName, String login) {
        if (serverType.equals("own")) {
        } else {
            ProcessBuilder builder = new ProcessBuilder();
            if (System.getProperty("os.name").substring(0, 7).equals("Windows")) {

            } else {
            }
        }
        return false;
    }

    private boolean installMySQL(String port, String dbName, String login) {
        String path = dirName + "/newDb_" + login;
        String containerName = dbName + "_" + login;
        if (serverType.equals("own")) {
            sshMaker.forExternalDb(path, dbDockerfile.get("mysql"), "sql");
            sshMaker.doCommand(" cd " + dirName + "/newDb_" + login + " ; sudo docker build -t " + path + " " + containerName +
                    " ; sudo docker run -d --name " + containerName + " -p " + port + ":" + defaultPorts.get("mysql") + " " + containerName);
            return sshMaker.getStatus();
        } else {
            return companyServerInstallDB(path, containerName, port, "mysql");
        }
    }

    private boolean installMongoDB(String port, String dbName, String login) {
        String path = dirName + "/newDb_" + login;
        String containerName = dbName + "_" + login;
        if (serverType.equals("own")) {
            sshMaker.forExternalDb(path, dbDockerfile.get("mongodb"), "js");
            sshMaker.doCommand(" cd " + dirName + "/newDb_" + login + " ; sudo docker build -t " + path + " " + containerName +
                    " ; sudo docker run -d --name " + containerName + " -p " + port + ":" + defaultPorts.get("mongodb") + " " + containerName);
            return sshMaker.getStatus();
        } else {
            return companyServerInstallDB(path, containerName, port, "mongoDB");
        }
    }


    private boolean installPostgres(String port, String dbName, String login) {
        String path = dirName + "/newDb_" + login;
        String containerName = dbName + "_" + login;
        if (serverType.equals("own")) {
            sshMaker.forExternalDb(path, dbDockerfile.get("postgresql"), "sql");
            sshMaker.doCommand(" cd " + dirName + "/newDb_" + login + " ; sudo docker build -t " + path + " " + containerName +
                    " ; sudo docker run -d --name " + containerName + " -p " + port + ":" + defaultPorts.get("postgresql") + " " + containerName);
            return sshMaker.getStatus();
        } else {
            return companyServerInstallDB(path, containerName, port, "postgresql");
        }
    }

    private boolean companyServerInstallDB(String path, String containerName, String port, String dbType) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (System.getProperty("os.name").substring(0, 7).equals("Windows")) {
            processBuilder.command(asList("cmd.exe", "/c", "cd " + Paths.get(path).toAbsolutePath().toString() + " && docker build -t " + containerName + " " + containerName
                    + " && docker run -d --name " + containerName + " -p " + port + ":" + defaultPorts.get(dbType) + " " + containerName));
            try {
                Process process = processBuilder.start();
                while (true) {
                    if (!process.isAlive()) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            processBuilder.command(asList("exec", "cd " + Paths.get(path).toAbsolutePath().toString() + " ; docker build -t " + containerName + " " + containerName
                    + " ; docker run -d --name " + containerName + " -p " + port + ":" + defaultPorts.get(dbType) + " " + containerName));
            try {
                Process process = processBuilder.start();
                while (true) {
                    if (!process.isAlive()) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean installDocker() {
        if (serverType.equals("own")) {
            sshMaker.doCommand(" sudo apt update");
            sshMaker.doCommand(" sudo apt install apt-transport-https ca-certificates curl software-properties-common");
            sshMaker.doCommand(" sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add ");
            sshMaker.doCommand(" sudo add-apt-repository \"deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable\" ");
            sshMaker.doCommand(" sudo apt update");
            sshMaker.doCommand(" sudo apt-cache policy docker-ce");
            sshMaker.doCommand(" sudo apt install docker-ce");
        }
        return checkDockerInstance();
    }

    public boolean createAndRunContainer(String port, String appLang) {
        if (serverType.equals("own")) {
            sshMaker.doCommand(" cd " + dirName + " ; sudo docker build  -t " + appName + " " + appName);
            sshMaker.doCommand(" cd " + dirName + " ; sudo docker run -d --name " + appName + " -p " + port + ":" + port + " " + appName);
            return sshMaker.getStatus();
        } else if (serverType.equals("company")) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            Path dockerfilePath = Paths.get(dirName).toAbsolutePath();
            if (System.getProperty("os.name").substring(0, 7).equals("Windows")) {
                processBuilder.command(asList("cmd.exe", "/c", "cd " + dockerfilePath.toString() + " && docker build -t " + appName + " " + appName
                        + " && docker run -d --name " + appName + " -p " + port + ":" + defaultPorts.get(appLang) + " " + appName));
                try {
                    Process process = processBuilder.start();
                    while (true) {
                        if (!process.isAlive()) {
                            return true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                processBuilder.command(asList("exec", "cd " + dockerfilePath.toString() + " ; docker build -t " + appName + " " + appName
                        + " ; docker run -d --name " + appName + " -p " + port + ":" + defaultPorts.get(appLang) + " " + appName));
                try {
                    Process process = processBuilder.start();
                    while (true) {
                        if (!process.isAlive()) {
                            return true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public void deleteContainer() {
        stopContainer();
        if (serverType.equals("company")) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (System.getProperty("os.name").substring(0, 7).equals("Windows")) {
                processBuilder.command("cmd.exe", "/c", "docker rm -f " + appName + " && docker rmi " + appName + " -f");
                try {
                    processBuilder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                processBuilder.command("exec", " sudo docker rm -f " + appName + " ; sudo docker rmi " + appName + " -f");
                try {
                    processBuilder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            sshMaker.doCommand(" sudo docker rm -f " + appName + " ; sudo docker rmi " + appName + " -f");
        }
    }

    public void stopContainer() {
        if (serverType.equals("company")) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (System.getProperty("os.name").substring(0, 7).equals("Windows")) {
                processBuilder.command("cmd.exe", "/c", "docker stop " + appName);
                try {
                    processBuilder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                processBuilder.command("exec", " sudo docker stop " + appName);
                try {
                    processBuilder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            sshMaker.doCommand(" sudo docker stop " + appName);
        }
    }

    public void startContainer() {
        if (serverType.equals("company")) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (System.getProperty("os.name").substring(0, 7).equals("Windows")) {
                processBuilder.command("cmd.exe", "/c", "docker start " + appName);
                try {
                    processBuilder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                processBuilder.command("exec", " sudo docker start " + appName);
                try {
                    processBuilder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            sshMaker.doCommand(" sudo docker start " + appName);
        }
    }

}
