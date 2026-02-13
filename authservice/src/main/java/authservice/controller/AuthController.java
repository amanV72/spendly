package authservice.controller;

import lombok.AllArgsConstructor;
import authservice.Model.UserInfoDto;
import authservice.Service.JwtService;
import authservice.Service.RefreshTokenService;
import authservice.Service.UserDetailServiceImplementation;
import authservice.entities.RefreshToken;
import authservice.response.JwtResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@AllArgsConstructor
@RestController
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailServiceImplementation userDetailServiceImplementation;

    @PostMapping("auth/v1/signup")
    public ResponseEntity signup(@RequestBody UserInfoDto userInfoDto){
        try{
            String userId= userDetailServiceImplementation.signupUser(userInfoDto);
            if(Boolean.FALSE.equals(userId)){
                return new ResponseEntity<>("Already exist", HttpStatus.BAD_REQUEST);
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());
            String jwtToken = jwtService.generateToken(userInfoDto.getUsername());
            return new ResponseEntity<>(JwtResponseDto
                    .builder()
                    .accessToken(jwtToken)
                    .token(refreshToken.getToken()).build(),
                    HttpStatus.OK
            );
        }catch (Exception ex){
            log.error("Exception occurred during signup process: {}", ex.getMessage(), ex);
            return new ResponseEntity<>("Exception in User Service",HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/auth/v1/ping")
    public ResponseEntity<String> ping(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null && authentication.isAuthenticated()){
            String userId= userDetailServiceImplementation.getUserByUsername(authentication.getName());
            if(Objects.nonNull(userId)){
                return ResponseEntity.ok(userId);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }


}
