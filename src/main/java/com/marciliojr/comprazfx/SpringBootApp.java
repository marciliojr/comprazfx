package com.marciliojr.comprazfx;

import com.marciliojr.comprazfx.infra.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SpringBootApp {
    public static void main(String[] args) {
        DatabaseInitializer.init();
        SpringApplication.run(SpringBootApp.class, args);
        MainApplication.launch(MainApplication.class, args);
    }
}

