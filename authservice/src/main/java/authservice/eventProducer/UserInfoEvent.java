package authservice.eventProducer;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserInfoEvent {
    private String userID;
    private String name;
    private Long income;
    private Long phoneNumber;
    private String email;

}
