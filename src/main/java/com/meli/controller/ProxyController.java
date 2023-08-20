package com.meli.controller;

import com.meli.service.ProxyService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
public class ProxyController {
    private final static Logger log = LogManager.getLogger(ProxyController.class);
    private final Bucket bucketCategories;
    private final Bucket bucketSells;


    @Autowired
    ProxyService service;

    public ProxyController() {
        Bandwidth limitCategories = Bandwidth.classic(1000, Refill.greedy(1, Duration.ofMinutes(3)));
        this.bucketCategories = Bucket4j.builder()
                .addLimit(limitCategories)
                .build();
        Bandwidth limitSells = Bandwidth.classic(10, Refill.greedy(1, Duration.ofMinutes(1)));
        this.bucketSells = Bucket4j.builder()
                .addLimit(limitSells)
                .build();
    }
    // For all methods and path

    @GetMapping("/**")
    public ResponseEntity<String> sendRequest(@RequestBody(required = false) String body, HttpMethod method, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
        return service.processProxyRequest(body, method, request, response, UUID.randomUUID().toString());
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/sells/**")
    public ResponseEntity<String> sendSells(@RequestBody(required = false) String body, HttpMethod method, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
        if (bucketSells.tryConsume(1)) {
            return service.processProxyRequest(body, method, request, response, UUID.randomUUID().toString());
        }
        log.info("Too Many Requests");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too Many Requests");
    }

    @GetMapping("/categories/**")
    public ResponseEntity<String> sendCategories(@RequestBody(required = false) String body, HttpMethod method, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
        if (bucketCategories.tryConsume(1)) {
            return service.processProxyRequest(body, method, request, response, UUID.randomUUID().toString());
        }
        log.info("Too Many Requests");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too Many Requests");
    }
}

