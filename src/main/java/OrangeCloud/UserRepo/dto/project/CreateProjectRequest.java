// CreateProjectRequest.java
package OrangeCloud.UserRepo.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {
    @NotNull(message = "팀 ID는 필수입니다.")
    private Long teamId;

    private String description; // 추가 필드

    public Long getTeamId() {
        return teamId;
    }

}