package org.example.backend;

import org.example.backend.Entities.Source;
import org.example.backend.Repositories.SourceRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
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
//    public static void main(String[] args) throws IOException {
//        Random rand = new Random();
//        FileWriter fw = new FileWriter("tickets.sql");
//        for (int i = 0; i < 5000; i++) {
//            StringBuilder builder = new StringBuilder();
//            builder.append("INSERT INTO INCIDENT_TICKETS (Ticket_Number, Status, Priority, Company, Project, Team, Assigned_Person, Description) VALUES (");
//            builder.append(i).append(", ");
//
//            int status = rand.nextInt(5);
//            switch (status) {
//                case 0 -> builder.append("'Open'");
//                case 1 -> builder.append("'In Progress'");
//                case 2 -> builder.append("'Resolved'");
//                case 3 -> builder.append("'Closed'");
//                case 4 -> builder.append("'Waiting for Customer'");
//            }
//            builder.append(", ");
//
//            int priority = rand.nextInt(4);
//            switch (priority) {
//                case 0 -> builder.append("'Low'");
//                case 1 -> builder.append("'Medium'");
//                case 2 -> builder.append("'High'");
//                case 3 -> builder.append("'Critical'");
//            }
//            builder.append(", ");
//
//            int company = rand.nextInt(4);
//            switch (company) {
//                case 0 -> builder.append("'IBM'");
//                case 1 -> builder.append("'Google'");
//                case 2 -> builder.append("'Amazon'");
//                case 3 -> builder.append("'Microsoft'");
//            }
//            builder.append(", ");
//
//            builder.append("'Project").append(rand.nextInt(100)).append("', ");
//            builder.append(rand.nextInt(3) + 1).append(", ");
//            builder.append(rand.nextInt(6) + 1).append(", ");
//            builder.append("'Unique Description ").append(i).append("')\n");
//
//            fw.write(builder.toString());
//        }
//        fw.close();
//    }


//    @Bean
//    ApplicationRunner runner(SourceRepository repository) {
//        return args -> {
//            List<String> attributes = List.of("open", "high", "low", "close", "volume");
//            Source source = new Source("AlphaVantage", "", Date.from(Instant.now()), attributes);
//            repository.save(source);
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
