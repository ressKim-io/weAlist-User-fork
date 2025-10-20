// TeamResponse.java
package OrangeCloud.UserRepo.dto.team; // 또는 package OrangeCloud.UserRepo.dto.team;

import OrangeCloud.UserRepo.dto.team.TeamMemberResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TeamResponse {
    private UUID teamId;
    private String name;
    private UUID ownerId;
    private String ownerName;
    private List<TeamMemberResponse> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기본 생성자
    public TeamResponse() {}

    // 모든 필드 생성자
    public TeamResponse(UUID teamId, String name, UUID ownerId, String ownerName,
                        List<TeamMemberResponse> members, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.teamId = teamId;
        this.name = name;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.members = members;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter 메서드들 (Jackson 직렬화를 위해 필수)
    public UUID getTeamId() { return teamId; }
    public String getName() { return name; }
    public UUID getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public List<TeamMemberResponse> getMembers() { return members; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setter 메서드들
    public void setTeamId(UUID teamId) { this.teamId = teamId; }
    public void setName(String name) { this.name = name; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void setMembers(List<TeamMemberResponse> members) { this.members = members; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "TeamResponse{" +
                "teamId=" + teamId +
                ", name='" + name + '\'' +
                ", ownerId=" + ownerId +
                ", ownerName='" + ownerName + '\'' +
                ", members=" + members +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}