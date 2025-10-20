// ProjectResponse.java
package OrangeCloud.UserRepo.dto.project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProjectResponse {
    private Long projectId;
    private Long teamId;
    private String teamName;
    private UUID userId;
    private String createdByName;
    private String updatedByName;
    private String status;
    private List<ProjectMemberResponse> teamMembers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기본 생성자
    public ProjectResponse() {}

    // 모든 필드 생성자
    public ProjectResponse(Long projectId, Long teamId, String teamName, UUID userId,
                           String createdByName, String updatedByName, String status,
                           List<ProjectMemberResponse> teamMembers, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.projectId = projectId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.userId = userId;
        this.createdByName = createdByName;
        this.updatedByName = updatedByName;
        this.status = status;
        this.teamMembers = teamMembers;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter, Setter 메서드들
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getUpdatedByName() { return updatedByName; }
    public void setUpdatedByName(String updatedByName) { this.updatedByName = updatedByName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<ProjectMemberResponse> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<ProjectMemberResponse> teamMembers) { this.teamMembers = teamMembers; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}