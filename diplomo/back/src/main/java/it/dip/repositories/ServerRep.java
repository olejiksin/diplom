package it.dip.repositories;


import it.dip.models.Server;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerRep extends MongoRepository<Server, String> {
    Optional<Server> getServerByAppsId(String appId);
}
