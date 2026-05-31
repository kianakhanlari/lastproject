package ir.maktab.shortcut.fianlproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing

public class FianlprojectApplication {

    public static void main(String[] args) {
     /*   AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(Config.class);*/

        SpringApplication.run(FianlprojectApplication.class, args);
    }

}
