package it.dip.service;

import it.dip.models.Server;
import it.dip.repositories.ServerRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    private ServerRep serverRep;

    @Override
    public Optional<Server> getServerByAppId(String appId) {
        return serverRep.getServerByAppsId(appId);
    }

    @Override
    public void addNewServer(Server server) {
        serverRep.save(server);
    }

}
