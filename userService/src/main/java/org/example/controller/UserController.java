package org.example.controller;

import org.apache.kafka.common.protocol.types.Field;
import org.example.Services.UserService;
import org.example.dto.UserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UserController {


    @Autowired
    private  UserService userService;



    @PostMapping("user/v1/createUpdate")
    public ResponseEntity<UserInfoDto> createUpdate(@RequestHeader("user-id") String userId,@RequestBody UserInfoDto userInfoDto){
        try{
            userInfoDto.setUserID(userId);
            UserInfoDto user= userService.createOrUpdateUser(userInfoDto);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("user/v1/uploadProfilePic")
    public ResponseEntity<String> uploadProfilePic(@RequestParam("file")MultipartFile file,@RequestHeader("user-id") String userId){
        try{
            String profilePicURL= userService.setProfilePic(file,userId);
            return new ResponseEntity<>(profilePicURL,HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload image",HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("user/v1/getUser")
    public ResponseEntity<UserInfoDto> getUser(@RequestHeader("user-id") String userID){
        try{
            UserInfoDto user=userService.getUser(userID);
            return new ResponseEntity<>(user,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
