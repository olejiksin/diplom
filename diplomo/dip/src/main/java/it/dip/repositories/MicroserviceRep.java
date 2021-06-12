package it.dip.repositories;

import it.dip.models.Microservice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MicroserviceRep extends MongoRepository<Microservice, String> {

    List<Microservice> getAllByClientId(String clientId);
    Optional<Microservice> getByNameAndClientId(String name,String clientId);
}
