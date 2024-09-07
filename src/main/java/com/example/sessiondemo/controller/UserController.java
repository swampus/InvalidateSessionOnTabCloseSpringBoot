package com.example.sessiondemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/invalidateSession")
    public String invalidateSession(HttpSession session) {
        System.out.println("INVALIDATE SESION CAME GET");
        session.invalidate();
        return "session-invalidated";
    }

    @PostMapping("/invalidateSession")
    public String invalidateSessionTabsClosed(HttpSession session) {
        System.out.println("INVALIDATE SESION CAME");
        session.invalidate();
        return "session-invalidated";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logged-out";
    }
}

