package OrangeCloud.UserRepo.dto.project;

import OrangeCloud.UserRepo.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {
    private BigInteger projectId;
    private UUID teamId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
    private ProjectStatus status;
    private Boolean isActive;
    private String teamName;
    private String createdByName;
    private String updatedByName;
}
