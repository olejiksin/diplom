package it.dip.repositories;

import it.dip.models.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRep extends MongoRepository<Client, String> {

    Optional<Client> getClientByLogin(String login);

    Optional<Client> getClientBy_id(String id);

//    @Query(value = "{$or:[{email:?0}, {login:?0}]}")
//    Optional<Client> getClientByEmailOrLogin(String login);
}
