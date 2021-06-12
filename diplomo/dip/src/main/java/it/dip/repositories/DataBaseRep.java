package it.dip.repositories;

import it.dip.models.DataBase;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataBaseRep extends MongoRepository<DataBase,String> {
    List<DataBase> getAllByClientsId(String login);

    Optional<DataBase> getByNameAndClientsId(String name,String clientId);
    Optional<DataBase> getBy_id(String id);
}
