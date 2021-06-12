package it.dip.service;

import it.dip.dto.MicroserviceAppDTO;
import it.dip.utils.SshMaker;
import it.dip.dto.FromFrontAppDTO;
import it.dip.models.App;
import it.dip.models.Server;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class GitServiceImpl implements GitService {

    private static final String dir = "projects/";


    @Override
    public boolean checkGit(FromFrontAppDTO server) {
        if (server.getServerType().equals("company")) {
            return true;
        } else if (server.getServerType().equals("own")) {
            SshMaker sshMaker = new SshMaker(server.getLogin(), server.getPassword(), server.getIp());
            sshMaker.doCommand(" sudo git");
            if (sshMaker.getOutLog().substring(3, 13).equals("usage: git")) {
                return true;
            } else {
                sshMaker.doCommand(" sudo apt update ; sudo apt install git");
            }
        }
        return false;
    }

    @Override
    public boolean cloneMicro(MicroserviceAppDTO appDTO, String login) {
        String gitDir = appDTO.getLink().split("/")[4];
        String renameDir = (appDTO.getName() + "_" + login).toLowerCase();
        SshMaker sshMaker = new SshMaker(appDTO.getLogin(), appDTO.getPassword(), appDTO.getIp());
        sshMaker.doCommand(" sudo mkdir " + dir);
        if (sshMaker.getOutLog().substring(3, 8).equals("mkdir")) {
            sshMaker.doCommand("cd " + dir + " ; git clone " + appDTO.getLink() + " ; sudo vm " + gitDir + " " + renameDir);
        } else {
            sshMaker.doCommand(" sudo mkdir " + dir + " ; cd " + dir + " ; git clone " + appDTO.getLink() + " ; sudo vm " + gitDir + " " + renameDir);
        }
        return sshMaker.getStatus();
    }

    @Override
    public Map<String, String> getMapDirectoriesAndAppLang(MicroserviceAppDTO appDTO, String login) {
        String gitDir = appDTO.getLink().split("/")[4];
        String renameDir = (appDTO.getName() + "_" + login).toLowerCase();
        Map<String, String> map = new HashMap<>();
        Path path = Paths.get(dir);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (System.getProperty("os.name").substring(0, 7).equals("Windows")) {
            processBuilder.command(Arrays.asList("cmd.exe", "/c", "cd " + dir + " && git clone " + appDTO.getLink() + " && rename " + gitDir + " " + renameDir));
            return smallMethod(renameDir, processBuilder);
        } else {
            processBuilder.command(Arrays.asList("exec", "cd " + dir + " ; git clone " + appDTO.getLink() + " ; sudo vm " + gitDir + " " + renameDir));
            return smallMethod(renameDir, processBuilder);
        }
    }

    private Map<String, String> smallMethod(String renameDir, ProcessBuilder processBuilder) {
        Map<String, String> map = new HashMap<>();
        try {
            Process process = processBuilder.start();
            while (true) {
                if (!process.isAlive()) {
                    Path pat = Paths.get("projects/" + renameDir).toAbsolutePath();
                    String[] dirs = new File(String.valueOf(pat)).list();
                    for (String project : dirs) {
                        map.put(project, getProjectName(project));
                    }
                    return map;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private String getProjectName(String dir) {
        String result = "";
        if (new File(dir + "/package.json").isFile()) {
            return "Node.JS";
        } else if (new File(dir + "/pom.xml").isFile()) {
            return "Java(SB)";
        } else if (new File(dir + "/" + dir.split("/")[1] + ".sln").isFile()) {
            return "C#";
        } else if (new File(dir + "/_init_.py").isFile()) {
            return "Python";
        }
        return result;
    }

    @Override
    public boolean clone(App app, Server server) {
        String gitDir = app.getLink().split("/")[4];
        System.out.println(gitDir);
        String renameDir = (app.getName() + "_" + app.get_id()).toLowerCase();
        System.out.println(renameDir);
        if (app.getServerType().equals("own")) {
            SshMaker sshMaker = new SshMaker(server.getLogin(), server.getPassword(), server.getIp());
            String command1 = " sudo mkdir " + dir;
            sshMaker.doCommand(command1);
            if (sshMaker.getOutLog().substring(3, 8).equals("mkdir")) {
                String command = " cd " + dir + " ; git clone " + app.getLink() + " ; sudo vm " + gitDir + " " + renameDir;
                sshMaker.doCommand(command);
            } else {
                String command = " sudo mkdir " + dir + " ; cd " + dir + " ; git clone " + app.getLink() + " ; sudo vm " + gitDir + " " + renameDir;
                sshMaker.doCommand(command);
            }
            return sshMaker.getStatus();
        } else {
            Path path = Paths.get(dir);
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (System.getProperty("os.name").substring(0, 7).equals("Windows")) {
                processBuilder.command(Arrays.asList("cmd.exe", "/c", "cd " + dir + " && git clone " + app.getLink() + " && rename " + gitDir + " " + renameDir));
                try {
                    Process process = processBuilder.start();
                    Thread.sleep(30000);
//                    while (true) {
                        if (!process.isAlive()) {
                            return true;
                        }
//                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                processBuilder.command(Arrays.asList("exec", "cd " + dir + " ; git clone " + app.getLink() + " ; sudo vm " + gitDir + " " + renameDir));
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
        }
        return false;
    }
}
