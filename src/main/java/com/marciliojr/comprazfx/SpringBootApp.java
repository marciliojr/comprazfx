package com.marciliojr.comprazfx;

import com.marciliojr.comprazfx.infra.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringBootApp {
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        DatabaseInitializer.init();
        context = SpringApplication.run(SpringBootApp.class, args);
        ApplicationFX.launch(ApplicationFX.class, args);
    }
}