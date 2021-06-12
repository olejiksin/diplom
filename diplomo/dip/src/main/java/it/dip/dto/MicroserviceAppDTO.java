package it.dip.dto;

import it.dip.models.SubMicroApp;
import it.dip.models.WorkerNode;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
@Builder
public class MicroserviceAppDTO {
    private List<WorkerNode> workerNodes;
    private String ip; //master ip
    private String login;
    private String password;

    private String name;
    private String id;
    private String link;
    private String appType;
    private String status;
    private List<SubMicroApp> subMicroApps;
    private HashMap<String,String> projectPort;
}
