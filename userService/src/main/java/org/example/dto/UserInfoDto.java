package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.example.entities.UserInfo;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDto {

    private String userID;

    private String name;

    private Long income;

    private Long phoneNumber;

    private String email;

    private String profilePic;

    public UserInfo transformToUserInfo(){
        return UserInfo.builder()
                .profilePic(profilePic)
                .userID(userID)
                .name(name)
                .income(income)
                .phoneNumber(phoneNumber)
                .email(email).build();
    }


}
