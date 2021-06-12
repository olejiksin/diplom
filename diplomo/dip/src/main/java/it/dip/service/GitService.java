package it.dip.service;

import it.dip.dto.FromFrontAppDTO;
import it.dip.dto.MicroserviceAppDTO;
import it.dip.models.App;
import it.dip.models.Server;

import java.util.Map;

public interface GitService {
    boolean checkGit(FromFrontAppDTO server);


    boolean cloneMicro(MicroserviceAppDTO appDTO, String login);

    Map<String, String> getMapDirectoriesAndAppLang(MicroserviceAppDTO appDTO, String login);

    boolean clone(App app, Server server);
}
