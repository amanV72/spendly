package authservice.Service;

import authservice.eventProducer.UserInfoEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import authservice.Model.UserInfoDto;
import authservice.Repository.UserRepo;
import authservice.entities.UserInfo;
import authservice.eventProducer.UserInfoProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailServiceImplementation implements UserDetailsService {
    @Autowired
    private final UserRepo userRepo;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserInfoProducer userInfoProducer;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user = userRepo.findByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("User not found!");
        }
        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExists(UserInfoDto userInfoDto){
        return userRepo.findByUsername(userInfoDto.getUsername());
    }
    public String signupUser(UserInfoDto userInfoDto){
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(Objects.nonNull(checkIfUserAlreadyExists(userInfoDto))){
            return null;
        }
        String userId= UUID.randomUUID().toString();
        userRepo.save(new UserInfo(
                userId,
                userInfoDto.getUsername(),
                userInfoDto.getPassword(),
                new HashSet<>()
                )
        );
        //push event to queue(kafka)
       userInfoProducer.sendEventToKafka(userInfoEvent(userInfoDto,userId));

        return userId;
    }

    public String getUserByUsername(String username){
        return Optional.of(userRepo.findByUsername(username)).map(UserInfo::getUserID).orElse(null);

    }
    private UserInfoEvent userInfoEvent(UserInfoDto userInfoDto,String userID){
        return UserInfoEvent.builder()
                .userID(userID)
                .name(userInfoDto.getName())
                .income(userInfoDto.getIncome())
                .phoneNumber(userInfoDto.getPhoneNumber())
                .email(userInfoDto.getEmail()).build();
    }
}
