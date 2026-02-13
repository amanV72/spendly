package org.example.Services;

import org.example.dto.UserInfoDto;
import org.example.entities.UserInfo;
import org.example.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public UserInfoDto createOrUpdateUser(UserInfoDto userInfoDto) {
        Function<UserInfo, UserInfo> updateUser = user -> {
            user.setName(userInfoDto.getName());
            user.setIncome(userInfoDto.getIncome());
            user.setPhoneNumber(user.getPhoneNumber());
            user.setEmail(user.getEmail());
            user.setProfilePic(user.getProfilePic());
            return userRepo.save(user);
        };
        Supplier<UserInfo> createUser = () -> {
            return userRepo.save(userInfoDto.transformToUserInfo());
        };
        UserInfo userInfo = userRepo.findByUserID(userInfoDto.getUserID())
                .map(updateUser)
                .orElseGet(createUser);
        return new UserInfoDto(
                userInfo.getUserID(),
                userInfo.getName(),
                userInfo.getIncome(),
                userInfo.getPhoneNumber(),
                userInfo.getEmail(),
                userInfo.getProfilePic()
        );
    }

    public String setProfilePic(MultipartFile multipartFile, String userId) throws IOException {
        String filename = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        String uploadDir = "uploads/profile-pics/";

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.write(filePath, multipartFile.getBytes());

        // Save public URL instead of internal file path
       // String publicUrl = "http://10.150.152.72:9810/profile-pics/" + filename;

        UserInfo user = userRepo.findByUserID(userId).orElseThrow();
        user.setProfilePic(filename);  // Store full URL
        userRepo.save(user);

        return filename;
    }


    public UserInfoDto getUser(String userID) throws Exception {
        Optional<UserInfo> userInfoOptional = userRepo.findByUserID(userID);


        if (userInfoOptional.isEmpty()) {
            throw new Exception("user not found");
        }
        UserInfo userInfo = userInfoOptional.get();

        return UserInfoDto.builder()
                .userID(userInfo.getUserID())
                .name(userInfo.getName())
                .income(userInfo.getIncome())
                .phoneNumber(userInfo.getPhoneNumber())
                .profilePic(userInfo.getProfilePic())
                .email(userInfo.getEmail()).build();
    }


}
