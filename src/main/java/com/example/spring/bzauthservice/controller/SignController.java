package com.example.spring.bzauthservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SignController {

    @GetMapping("/loginSuccess")
    public String home(){
        return "home";
    }

    @GetMapping("/join")
    public String join(@RequestParam String email, @RequestParam String provider, @RequestParam String role, Model model){
        model.addAttribute("email", email);
        model.addAttribute("provider", provider);
        model.addAttribute("role", role);

        return "join";
    }

    @GetMapping("/fail")
    public String fail(){
        return "fail";
    }
}
