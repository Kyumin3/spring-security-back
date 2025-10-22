package com.example.lkm.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MainController {

    @GetMapping("/api1")
    public ResponseEntity<?>  api1(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return new ResponseEntity<>("api1", HttpStatus.OK);
    }

    @GetMapping("/api2")
    public ResponseEntity<?>  api2() {

        return new ResponseEntity<>("api2", HttpStatus.OK);
    }

}
