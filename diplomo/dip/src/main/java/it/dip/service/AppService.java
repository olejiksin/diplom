package it.dip.service;

import it.dip.dto.AppDTO;
import it.dip.dto.DataBaseDTOtoFt;
import it.dip.dto.FromFrontAppDTO;
import it.dip.dto.MicroserviceAppDTO;
import it.dip.models.Microservice;

import java.util.List;

public interface AppService {
    List<AppDTO> getAllAppsByLoginOrEmail(String loginOrEmail);

    List<Microservice> getAllMicroservicesByLogin(String login);

    String addNewAppMono(FromFrontAppDTO appDTO, String loginOrEmail);

    boolean deleteApp(String id);

//    boolean updateAppProp(AppDTO appDTO);

    boolean startOrTurnOff(String id);


    Object addNewAppMicro(MicroserviceAppDTO appDTO, String loginOrEmail);
}
