// TeamMemberResponse.java
package OrangeCloud.UserRepo.dto.team;

import java.time.LocalDateTime;
import java.util.UUID;

public class TeamMemberResponse {
    private UUID userId;
    private String name;
    private String email;
    private String role;
    private LocalDateTime joinedAt;

    // 기본 생성자
    public TeamMemberResponse() {}

    // 모든 필드 생성자
    public TeamMemberResponse(UUID userId, String name, String email, String role, LocalDateTime joinedAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    // Getter 메서드들 (Jackson 직렬화를 위해 필수)
    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    // Setter 메서드들
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public String toString() {
        return "TeamMemberResponse{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", joinedAt=" + joinedAt +
                '}';
    }
}