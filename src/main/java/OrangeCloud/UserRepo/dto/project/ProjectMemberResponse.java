// ProjectMemberResponse.java
package OrangeCloud.UserRepo.dto.project;

import java.util.UUID;

public class ProjectMemberResponse {
    private UUID userId;
    private String name;
    private String email;
    private boolean isOwner;

    // 기본 생성자
    public ProjectMemberResponse() {}

    // 모든 필드 생성자 - 명시적으로 추가
    public ProjectMemberResponse(UUID userId, String name, String email, boolean isOwner) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.isOwner = isOwner;
    }

    // Getter, Setter
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isOwner() { return isOwner; }
    public void setOwner(boolean owner) { isOwner = owner; }

    @Override
    public String toString() {
        return "ProjectMemberResponse{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isOwner=" + isOwner +
                '}';
    }
}