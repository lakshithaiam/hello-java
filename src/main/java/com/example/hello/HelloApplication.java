package com.example.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RestController
public class HelloApplication {

    // In-memory visitor counter
    private AtomicInteger visitorCount = new AtomicInteger(0);

    @GetMapping("/")
    public String hello(@RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor,
                        @RequestHeader(value = "Host", required = false) String host) {

        int count = visitorCount.incrementAndGet();

        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "Unknown";
        }

        String clientIP = xForwardedFor != null ? xForwardedFor : "Unknown";

        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");

        return "<h1>Hello from Java Spring Boot 🚀</h1>" +
                "<p>Server Hostname: " + hostname + "</p>" +
                "<p>Client IP: " + clientIP + "</p>" +
                "<p>Visitor number: " + count + "</p>" +
                "<p>Java Version: " + javaVersion + "</p>" +
                "<p>OS: " + osName + " (" + osArch + ")</p>";
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }
}
