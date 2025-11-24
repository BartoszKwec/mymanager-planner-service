package pl.kwec.mymanagerplannerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MymanagerPlannerServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MymanagerPlannerServiceApplication.class, args);
    }
}
