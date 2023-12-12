package com.auth.service.controller;

import com.auth.service.config.FileUploadUtil;
import com.auth.service.dto.AuthRequest;
import com.auth.service.entity.Users;
import com.auth.service.service.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthServiceImpl service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> addNewUser(@RequestBody Users user) {
        if(!service.checkUserEmail(user.getEmail())){
            service.saveUser(user);
            String token = service.generateToken(user.getEmail());
            Optional<Users> users = service.getUserById(user.getEmail());
            int user_id = users.get().getId();
            String email = users.get().getEmail();

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user_id", user_id);
            response.put("email", email);
            response.put("error", 0);
            response.put("text", "OK");

            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        else{
            Map response = new HashMap();
            response.put("token", "");
            response.put("user_id", "");
            response.put("email", "");
            response.put("error", 1);
            response.put("text", "user is aleady exists");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/token")
    public ResponseEntity<Map<String, Object>> getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            try {
                String token = service.generateToken(authRequest.getEmail());
                Optional<Users> user = service.getUserById(authRequest.getEmail());
                int user_id = user.get().getId();
                String email = user.get().getEmail();

                String phone_number = user.get().getPhone_number();
                String avatar_image = user.get().getAvatar_image();

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user_id", user_id);
                response.put("email", email);
                response.put("error", 0);
                response.put("text", "ok");
                response.put("phone_number", phone_number);
                response.put("avatar_image", avatar_image);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch(Exception e){

                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Users users) {
            try {
                String user_response = service.updateUser(users);

                Map<String, Object> response = new HashMap<>();
                response.put("status", "OK");

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch(Exception e){
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return "Token is valid";
    }

    @PostMapping("/upload")
    public ResponseEntity saveUser(@RequestParam("image") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        String uploadDir = "./upload";

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return ResponseEntity.ok().build();
    }
}