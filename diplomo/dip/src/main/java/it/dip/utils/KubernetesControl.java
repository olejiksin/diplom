package it.dip.utils;

import it.dip.models.WorkerNode;

import java.util.HashMap;
import java.util.List;

public class KubernetesControl {
    private SshMaker sshMaker;
    private static final String dirName = "projects/";
    private String masterUser;
    private String masterPas;
    private String masterHost;
    private List<WorkerNode> workerNodes;
    private String appName;

    public void setSshMaker(SshMaker sshMaker) {
        this.sshMaker = sshMaker;
    }

    public void setMasterAndWorkerNodesAndAppName(String user, String pas, String host, List<WorkerNode> nodes, String appName) {
        this.masterUser = user;
        this.masterHost = host;
        this.masterPas = pas;
        this.workerNodes = nodes;
        this.appName = appName;
    }

    private void install() {
        sshMaker.doCommand("sudo apt-get update ; " +
                " sudo apt-get install -y apt-transport-https ca-certificates curl ; " +
                " sudo curl -fsSLo /usr/share/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg ; " +
                " echo \"deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main\" | sudo tee /etc/apt/sources.list.d/kubernetes.list ;" +
                " sudo apt-get update ; " +
                " sudo apt-get install -y kubelet kubeadm kubectl ; " +
                " sudo apt-mark hold kubelet kubeadm kubectl");

    }

    public boolean checkAndInstallGit() {
        sshMaker.doCommand(" sudo git");
        if (sshMaker.getOutLog().substring(3, 13).equals("usage: git")) {
            return true;
        } else {
            sshMaker.doCommand(" sudo apt update ; sudo apt install git");
            return sshMaker.getStatus();
        }
    }

    public boolean installKubernetes() {
        sshMaker = new SshMaker(this.masterUser, this.masterPas, this.masterHost);
        install();
        if (sshMaker.getStatus()) {
            for (WorkerNode workerNode : workerNodes) {
                sshMaker = new SshMaker(workerNode.getUser(), workerNode.getPas(), workerNode.getHost());
                install();
                if (!sshMaker.getStatus()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void addWorkerNodeToCluster(WorkerNode workerNode) {
        this.setSshMaker(new SshMaker(masterUser, masterPas, masterHost));
        sshMaker.doCommand("sudo kubeadm token create --print-join-command");
        String command = sshMaker.getOutLog();
        SshMaker sshMakerWorkerNode = new SshMaker(workerNode.getUser(), workerNode.getPas(), workerNode.getHost());
        sshMakerWorkerNode.doCommand("sudo apt-get update && sudo apt-get install -y apt-transport-https curl");
        sshMakerWorkerNode.doCommand("sudo curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -");
        sshMakerWorkerNode.doCommand("sudo apt-add-repository \"deb http://apt.kubernetes.io/ kubernetes-xenial main\"");
        sshMakerWorkerNode.doCommand("sudo apt-get update && sudo apt-get install -y kubelet kubeadm kubectl docker.io");
        sshMakerWorkerNode.doCommand("sudo systemctl enable docker");
        sshMakerWorkerNode.doCommand("sudo " + command);
    }

    public void createDeployment(String dirName, String project, String imageName, String port) {
        this.setSshMaker(new SshMaker(masterUser, masterPas, masterHost));
        String deployment =
                "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  labels:\n" +
                        "    app: " + project + "\n" +
                        "  name: " + project + "\n" +
                        "spec:\n" +
                        "  minReadySeconds: 15\n" +
                        "  replicas: 2\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: " + project + "\n" +
                        "  strategy:\n" +
                        "    rollingUpdate:\n" +
                        "      maxSurge: 1\n" +
                        "      maxUnavailable: 1\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: " + project + "\n" +
                        "    spec:\n" +
                        "      containers: \n" +
                        "      - image: " + imageName + "\n" +
                        "        imagePullPolicy: Never\n" +
                        "        name: " + project + "\n" +
                        "        ports: \n" +
                        "         - name: http\n" +
                        "           containerPort: " + port + "\n" +
                        "      restartPolicy: Always\n";
        sshMaker.forKubernetesFiles(dirName, "deployment", project, deployment);
        sshMaker.doCommand("cd "+dirName+" ; sudo kubectl create -f deployment" + project + ".yaml");
    }

    public void createService(String dirName, String project, String port) {
        this.setSshMaker(new SshMaker(masterUser, masterPas, masterHost));
        String service =
                "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        " name: " + project + "\n" +
                        "spec:\n" +
                        " type: LoadBalancer\n" +
                        " ports:\n" +
                        " - port: " + port + "\n" +
                        "   protocol: TCP\n" +
                        "   targetPort: " + port + "\n" +
                        " selector:\n" +
                        "  app: " + project + "\n";
        sshMaker.forKubernetesFiles(dirName, "service", project, service);
        sshMaker.doCommand("cd "+dirName+" ; sudo ; kubectl create -f service" + project + ".yaml");
    }

    public String getIpProject(String project) {
        sshMaker.doCommand(" sudo kubectl get svc | grep "+project);
        return sshMaker.getOutLog().split(" ")[11];
    }
}
