package it.dip.repositories;

import it.dip.models.App;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppRep extends MongoRepository<App, String> {
    List<App> getAllByClientsId(String id);

    Optional<App> getByNameAndClientsId(String appName, String id);

    Optional<App> getBy_id(String id);

}
