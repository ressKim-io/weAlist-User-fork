// AddTeamMemberRequest.java
package OrangeCloud.UserRepo.dto.team;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddTeamMemberRequest {
    @NotNull(message = "사용자 ID는 필수입니다.")
    private UUID userId;

    private String role = "MEMBER"; // OWNER, ADMIN, MEMBER

    public UUID getUserId(){return userId;}
    public String getUserRole(){return role;}
}