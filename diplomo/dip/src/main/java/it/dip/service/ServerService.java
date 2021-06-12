package it.dip.service;


import it.dip.models.Server;

import java.util.Optional;

public interface ServerService {
    Optional<Server> getServerByAppId(String appId);

    void addNewServer(Server server);
}
