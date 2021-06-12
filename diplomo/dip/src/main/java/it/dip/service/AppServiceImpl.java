package it.dip.service;

import it.dip.dto.MicroserviceAppDTO;
import it.dip.models.*;
import it.dip.repositories.MicroserviceRep;
import it.dip.utils.AES;
import it.dip.utils.DockerControl;
import it.dip.utils.KubernetesControl;
import it.dip.utils.SshMaker;
import it.dip.dto.AppDTO;
import it.dip.dto.FromFrontAppDTO;
import it.dip.repositories.AppRep;
import it.dip.repositories.ClientRep;
import it.dip.repositories.ServerRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class AppServiceImpl implements AppService {
    @Autowired
    private ServerRep serverRep;
    @Autowired
    private AppRep appRep;
    @Autowired
    private MicroserviceRep microserviceRep;
    @Autowired
    private ClientRep clientRep;
    @Autowired
    private GitServiceImpl gitService;
    @Autowired
    private DockerControl dockerControl;
    @Autowired
    private KubernetesControl kubernetesControl;

    @Override
    public List<AppDTO> getAllAppsByLoginOrEmail(String loginOrEmail) {
        Optional<Client> client = clientRep.getClientByLogin(loginOrEmail);
        List<AppDTO> list = new ArrayList<>();
        if (client.isPresent()) {
            for (App app : appRep.getAllByClientsId(client.get().get_id())) {
                list.add(AppDTO.builder()
                        .name(app.getName())
                        .status(app.getStatus())
                        .link(app.getLink())
                        .ip(app.getIp())
                        .port(app.getPort())
                        .id(app.get_id())
                        .appType(app.getAppType())
                        .appLang(app.getAppLang())
                        .build());
            }
        }
        return list;
    }

    @Override
    public List<Microservice> getAllMicroservicesByLogin(String login) {
        Optional<Client> client = clientRep.getClientByLogin(login);
        List<Microservice> list = new ArrayList<>();
        if (client.isPresent()) {
            return microserviceRep.getAllByClientId(client.get().get_id());
        }
        return list;
    }


    @Override
    public String addNewAppMono(FromFrontAppDTO appDTO, String loginOrEmail) {
        Optional<Client> client = clientRep.getClientByLogin(loginOrEmail);
        Optional<App> checkAp=appRep.getByNameAndClientsId(appDTO.getName(), client.get().get_id());
        if (checkAp.isPresent()) {
            System.out.println(checkAp.get().getName());
            return "Rename app pls";
        }
        if (client.isPresent()) {
            if (appDTO.getServerType().equals("own")) {
                SshMaker sshMaker = new SshMaker(appDTO.getLogin(), appDTO.getPassword(), appDTO.getIp());
                dockerControl.setSshMakerAndServerType(sshMaker, "own");
            } else {
                dockerControl.setSshMakerAndServerType(null, "company");
            }
            App appp = App.builder()
                    .appType("mono")
                    .status("online")
                    .link(appDTO.getLink())
                    .name(appDTO.getName())
                    .clientsId(client.get().get_id())
                    .serverType(appDTO.getServerType())
                    .appLang(appDTO.getAppLang())
                    .build();
            int port = 0;
            if (appDTO.getServerType().equals("own")) {
                appp.setIp(appDTO.getIp());
                if (!appDTO.getPort().equals("default")) {
                    appp.setPort(appDTO.getPort());
                }
            } else {
                appp.setIp("169.254.202.98");
                if (!appDTO.getPort().equals("default")) {
                    try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(appDTO.getPort()))) {
                        serverSocket.setReuseAddress(true);
                        if (!serverSocket.isClosed()) {
                            appp.setPort(appDTO.getPort());
                            port = Integer.parseInt(appp.getPort());
                            serverSocket.close();
                        } else {
                            return "Port is closed";
                        }
                    } catch (IOException e) {
                        System.out.println("Port problem: " + e);
                        e.printStackTrace();
                    }
                } else {
                    try (ServerSocket socket = new ServerSocket(0)) {
                        socket.setReuseAddress(true);
                        port = socket.getLocalPort();
                        socket.close();
                    } catch (IOException ignored) {
                        System.out.println("Port problem: " + ignored);
                    }
                    if (port > 0) {
                        appp.setPort(port + "");
                    }
                }
            }
            appRep.save(appp);
            Optional<App> ap = appRep.getByNameAndClientsId(appp.getName(), client.get().get_id());
            dockerControl.setAppName(appp.getName(), ap.get().get_id(), "app");
            Server server = Server.builder()
                    .ip(appDTO.getIp())
                    .login(appDTO.getLogin())
                    .password(appDTO.getPassword())
                    .appsId(ap.get().get_id())
                    .build();

            boolean flag = false;
            if (gitService.clone(appp, server)) {
                if (dockerControl.checkDockerInstance()) {
                    if (dockerControl.checkDockerfile()) {
                        flag = dockerControl.createAndRunContainer(port + "", appp.getAppLang());
                    } else {
                        dockerControl.createDockerfile(appDTO.getAppLang());
                        flag = dockerControl.createAndRunContainer(port + "", appp.getAppLang());
                    }
                } else {
                    if (dockerControl.installDocker()) {
                        if (dockerControl.checkDockerfile()) {
                            flag = dockerControl.createAndRunContainer(port + "", appp.getAppLang());
                        } else {
                            dockerControl.createDockerfile(appDTO.getAppLang());
                            flag = dockerControl.createAndRunContainer(port + "", appp.getAppLang());
                        }
                    } else {
                        return "Docker install problem";
                    }
                }
            } else {
                return "Clone problem";
            }
            if (flag) {
                if (server.getLogin() != null) {
                    server.setPassword(AES.encrypt(appDTO.getPassword(), appDTO.getLogin() + "_" + appDTO.getName()));
                    serverRep.save(server);
                }
                return "ok" + ap.get().get_id();
            } else return "fail";
        }
        return "fail";
    }

    private void setSshForControl(Optional<App> app) {
        Optional<Server> server = serverRep.getServerByAppsId(app.get().get_id());
        SshMaker sshMaker = new SshMaker(server.get().getLogin(),
                AES.decrypt(server.get().getPassword(), server.get().getLogin() + "_" + app.get().getName()),
                server.get().getIp());
        dockerControl.setSshMakerAndServerType(sshMaker, "own");
    }


    @Override
    public boolean deleteApp(String id) {
        Optional<App> app = appRep.getBy_id(id);
        if (app.isPresent() && app.get().getAppType().equals("mono")) {
            if (app.get().getServerType().equals("own")) {
                setSshForControl(app);
            } else {
                dockerControl.setSshMakerAndServerType(null, "company");
            }
            dockerControl.setAppName(app.get().getName(), app.get().get_id(), "app");
            appRep.delete(app.get());
            dockerControl.deleteContainer();
            return true;
        }
        return false;
    }

    @Override
    public boolean startOrTurnOff(String id) {
        Optional<App> app = appRep.getBy_id(id);
        if (app.isPresent() && app.get().getAppType().equals("mono")) {
            if (app.get().getServerType().equals("own")) {
                setSshForControl(app);
            } else {
                dockerControl.setSshMakerAndServerType(null, "company");
            }
            if (app.get().getStatus().equals("offline")) {
                dockerControl.setAppName(app.get().getName(), app.get().get_id(), "app");
                app.get().setStatus("online");
                appRep.save(app.get());
                dockerControl.startContainer();
                return true;
            } else if (app.get().getStatus().equals("online")) {
                dockerControl.setAppName(app.get().getName(), app.get().get_id(), "app");
                app.get().setStatus("offline");
                appRep.save(app.get());
                dockerControl.stopContainer();
                return true;
            }
        }
        return false;
    }

    private boolean installDockerMicro() {
        if (!dockerControl.checkDockerInstance()) {
            return dockerControl.installDocker();
        } else {
            return true;
        }
    }

    @Override
    public Object addNewAppMicro(MicroserviceAppDTO appDTO, String loginOrEmail) {
        Optional<Client> client = clientRep.getClientByLogin(loginOrEmail);
        if (client.isPresent()) {
            String appName = (appDTO.getName() + "_" + loginOrEmail).toLowerCase();
            kubernetesControl.setMasterAndWorkerNodesAndAppName(appDTO.getLogin(), appDTO.getPassword(), appDTO.getIp(),
                    appDTO.getWorkerNodes(), appName);
            SshMaker ssh = new SshMaker(appDTO.getLogin(), appDTO.getPassword(), appDTO.getIp());
            dockerControl.setSshMakerAndServerType(ssh, "own");
            kubernetesControl.setSshMaker(ssh);
            List<SubMicroApp> listSubMicros = new ArrayList<>();
            if (kubernetesControl.installKubernetes()) {
                if (installDockerMicro() && kubernetesControl.checkAndInstallGit()) {
                    if (gitService.cloneMicro(appDTO, loginOrEmail)) {
                        for (WorkerNode workerNode : appDTO.getWorkerNodes()) {
                            ssh = new SshMaker(workerNode.getUser(), workerNode.getPas(), workerNode.getHost());
                            dockerControl.setSshMakerAndServerType(ssh, "own");
                            kubernetesControl.setSshMaker(ssh);
                            if (!kubernetesControl.installKubernetes()) {
                                return "Kubernetes install problem." + workerNode.getHost();
                            }
                            if (!installDockerMicro()) {
                                return "Docker install problem." + workerNode.getHost();
                            }
                            if (!kubernetesControl.checkAndInstallGit()) {
                                return "Git install problem." + workerNode.getHost();
                            }
                            kubernetesControl.addWorkerNodeToCluster(workerNode);
                        }

                        Map<String, String> map = gitService.getMapDirectoriesAndAppLang(appDTO, loginOrEmail);
                        dockerControl.setSshMakerAndServerType(new SshMaker(appDTO.getLogin(), appDTO.getPassword(), appDTO.getIp()), "own");
                        for (String project : map.keySet()) {
                            dockerControl.checkAndCreateDockerFileForMicro(map.get(project), appName, project);
                            dockerControl.createImage(appName, appName + "_" + project);
                            kubernetesControl.createDeployment(appName, project, appName + "_" + project, appDTO.getProjectPort().get(project));
                            kubernetesControl.createService(appName, project, appDTO.getProjectPort().get(project));
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            listSubMicros.add(SubMicroApp.builder()
                                    .appLang(map.get(project))
                                    .name(project)
                                    .status("online")
                                    .port(appDTO.getProjectPort().get(project))
                                    .ip(kubernetesControl.getIpProject(project))
                                    .build());
                        }
                    } else return "Clone problem to master";
                } else {
                    return "Docker or Git install problem on master node";
                }
                microserviceRep.save(
                        Microservice.builder()
                                .appType("micro")
                                .link(appDTO.getLink())
                                .name(appDTO.getName())
                                .status("online")
                                .subNodes(listSubMicros)
                                .clientId(client.get().get_id())
                                .build());

                Optional<Microservice> ap = microserviceRep.getByNameAndClientId(appDTO.getName(), client.get().get_id());

                serverRep.save(Server.builder()
                        .ip(appDTO.getIp())
                        .login(appDTO.getLogin())
                        .password(appDTO.getPassword())
                        .appsId(ap.get().get_id())
                        .build());
                return ap.get();
            } else return "Kubernetes install problem on master node";
        }
        return "fail";
    }
}
