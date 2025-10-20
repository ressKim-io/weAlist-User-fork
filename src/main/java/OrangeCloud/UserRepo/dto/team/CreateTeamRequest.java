// CreateTeamRequest.java
package OrangeCloud.UserRepo.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTeamRequest {
    @NotBlank(message = "팀 이름은 필수입니다.")
    @Size(min = 2, max = 100, message = "팀 이름은 2자 이상 100자 이하여야 합니다.")
    private String name;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;
}