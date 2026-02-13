package authservice.controller;

import authservice.Service.JwtService;
import authservice.Service.RefreshTokenService;
import authservice.entities.RefreshToken;
import authservice.request.AuthRequestDto;
import authservice.request.RefreshTokenDto;
import authservice.response.JwtResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("auth/v1/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequestDto authRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword())
            );

            if (authentication.isAuthenticated()) {
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDto.getUsername());

                return ResponseEntity.ok(JwtResponseDto.builder()
                        .accessToken(jwtService.generateToken(authRequestDto.getUsername()))
                        .token(refreshToken.getToken())
                        .build());
            } else {
                System.out.println("*******************************Not found");
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Authentication failed");
            }
        } catch (Exception e) {
            System.out.println("the error is :    "+e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }


    @PostMapping("auth/v1/refreshToken")
    public JwtResponseDto refreshToken(@RequestBody RefreshTokenDto refreshTokenDto){

        return refreshTokenService.findByToken(refreshTokenDto.getToken())
                //.map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken= jwtService.generateToken(userInfo.getUsername());
                    return JwtResponseDto.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenDto.getToken()).build();
                }).orElseThrow(()->new RuntimeException("Refresh token is not in db.."));

    }
}
