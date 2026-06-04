package org.example.backend.Config;

import jakarta.annotation.PreDestroy;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;

@Component
public class SparkShutdownHook {

    private final SparkSession sparkSession;

    public SparkShutdownHook(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    @PreDestroy
    public void cleanUp() {
        if (sparkSession != null) {
            sparkSession.stop();
        }
    }
}