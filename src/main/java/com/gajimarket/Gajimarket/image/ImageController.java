package com.gajimarket.Gajimarket.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@CrossOrigin
public class ImageController {

    //프론트에서 이미지 파일을 요청하는 경로
    @GetMapping("/image")
    public ResponseEntity<?> returnImage(@RequestParam String image){
        String path="src/main/resources/static";
        System.out.println("/image request: "+path+image);
        Resource resource=new FileSystemResource(path+image);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }


}
