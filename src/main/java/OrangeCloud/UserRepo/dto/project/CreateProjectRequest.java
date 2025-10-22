// CreateProjectRequest.java
package OrangeCloud.UserRepo.dto.project;

import OrangeCloud.UserRepo.entity.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectRequest {

    @NotNull(message = "팀 ID는 필수입니다.")
    private UUID teamId;

    @NotNull(message = "생성자 ID는 필수입니다.")
    private UUID createdBy;

    @NotNull(message = "프로젝트 상태는 필수입니다.")
    private ProjectStatus status;
}