package com.example.spring.bzauthservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImgService {
    public void uploadImg(String name, MultipartFile file);
}
