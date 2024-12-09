package com.example.spring.bzauthservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auths")
public class SignController {
    @GetMapping("/fail")
    public String fail(){
        return "fail";
    }
}
