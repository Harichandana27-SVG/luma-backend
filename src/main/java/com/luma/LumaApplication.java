package com.luma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LumaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LumaApplication.class, args);
        System.out.println("=================================================");
        System.out.println("✨ LUMA Web Platform is running!");
        System.out.println("🌐 Open in browser: http://localhost:8080");
        System.out.println("🔐 LUMA Vault active with end-to-end security");
        System.out.println("💾 H2 Console: http://localhost:8080/h2-console");
        System.out.println("=================================================");
    }
}
