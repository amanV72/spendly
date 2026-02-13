package authservice.Service;

import authservice.Repository.RefreshTokenRepo;
import authservice.Repository.UserRepo;
import authservice.entities.RefreshToken;
import authservice.entities.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenRepo refreshTokenRepo;

    @Autowired
    UserRepo userRepo;


    public RefreshToken createRefreshToken(String username){
        UserInfo userInfoExtracted= userRepo.findByUsername(username);
        refreshTokenRepo.findByUserInfo(userInfoExtracted).ifPresent(refreshTokenRepo::delete);

        RefreshToken refreshToken= RefreshToken
                .builder()
                .userInfo(userInfoExtracted)
                .token(UUID.randomUUID().toString())
               // .expiryDate(Instant.now().plusMillis(600000))
                .build();
        return refreshTokenRepo.save(refreshToken);

    }

//    public RefreshToken verifyExpiration(RefreshToken token){
//        if(token.getExpiryDate().compareTo(Instant.now())<0){
//            refreshTokenRepo.delete(token);
//            throw new RuntimeException(token.getToken()+"Refresh token is expired!");
//        }
//        return token;
//    }
    public Optional<RefreshToken> findByToken(String token){
       return refreshTokenRepo.findByToken(token);
    }

}
