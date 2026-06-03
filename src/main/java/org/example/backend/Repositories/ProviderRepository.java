package org.example.backend.Repositories;

import org.example.backend.Entities.Provider;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProviderRepository extends MongoRepository<Provider, String> {

    Provider getProviderByName(String provider);

    Optional<Provider> getProviderById(String id);
}
