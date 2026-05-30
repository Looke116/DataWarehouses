package org.example.backend;

import org.example.backend.Entities.Provider;
import org.example.backend.Repositories.ProviderRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Random;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }


//    @Bean
//    ApplicationRunner runner(ProviderRepository repository) {
//        return args -> {
//            Provider AlphaVantage = new Provider("AlphaVantage", "", List.of("open", "high", "low", "close", "volume"));
//            repository.save(AlphaVantage);
//
//            Provider TwelveData = new Provider("TwelveData", "", List.of("open", "high", "low", "close", "volume"));
//            repository.save(TwelveData);
//        };
//    }

//    @Bean
//    ApplicationRunner runner(AssetRepository repository) {
//        return args -> {
//
//            repository.deleteAll();
//
//            // save a couple of customers
//            repository.save(new Asset(0, "a", "desc", new Date(), new HashMap<>()));
//            repository.save(new Asset(1, "b", "desc", new Date(), new HashMap<>()));
//
//            // fetch all customers
//            System.out.println("Customers found with findAll():");
//            System.out.println("-------------------------------");
//            for (Asset asset : repository.findAll()) {
//                System.out.println(asset);
//            }
//            System.out.println();
//
//            // fetch an individual customer
//            System.out.println("Customer found with findByFirstName('Alice'):");
//            System.out.println("--------------------------------");
//            System.out.println(repository.findById(1));
//
//            System.out.println("Customers found with findByLastName('Smith'):");
//            System.out.println("--------------------------------");
//            for (Asset customer : repository.findAll()) {
//                System.out.println(customer);
//            }
//        };
//    }
}
