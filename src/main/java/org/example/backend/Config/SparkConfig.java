package org.example.backend.Config;

import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/test}")
    private String mongoUri;


    @Bean
    public SparkSession sparkSession() {
        return SparkSession.builder()
                .appName("FinancialDataWarehouseAnalytics")
                .master("local[*]")
                .config("spark.mongodb.read.connection.uri", mongoUri)
                .config("spark.mongodb.write.connection.uri", mongoUri)
                .getOrCreate();
    }
}