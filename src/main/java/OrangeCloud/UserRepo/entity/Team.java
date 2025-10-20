package OrangeCloud.UserRepo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "user_id")
    private UUID userId;

//    @Column(name = "member_id")
//    private Long memberId;

//    @Column(name = "user_uuid", columnDefinition = "UUID")
//    private UUID userUuid;

    @Column(name = "group_id", columnDefinition = "UUID")
    private UUID groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private Group group;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Project> projects;

    // 기본 생성자
    public Team() {}

    // 생성자
    public Team(UUID userId, Long teamId, UUID groupId) {
        this.userId = userId;
        this.teamId = teamId;
        this.groupId = groupId;
    }

//    // Getter, Setter
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

//    public Long getMemberId() { return memberId; }
//    public void setMemberId(Long memberId) { this.memberId = memberId; }

//    public UUID getUserUuid() { return userUuid; }
//    public void setUserUuid(UUID userUuid) { this.userUuid = userUuid; }

    public UUID getGroupId() { return groupId; }
    public void setGroupId(UUID groupId) { this.groupId = groupId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
}