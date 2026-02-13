package authservice.Repository;

import authservice.entities.RefreshToken;
import authservice.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends CrudRepository<RefreshToken,Integer> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserInfo(UserInfo user);
    void deleteByUserInfo(UserInfo user); // Optional, you can also use .ifPresent().delete()



}
