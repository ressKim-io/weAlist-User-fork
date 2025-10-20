package OrangeCloud.UserRepo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;
    
    @Column(name = "team_id")
    private Long teamId;
    
//    @Column(name = "member_id")
//    private Long memberId;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private UUID createdBy;
    
    @Column(name = "updated_by")
    private UUID updatedBy;
    @Column(name = "user_id")
    private UUID userId;
    
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;
    
    // 기본 생성자
    public Project() {}
    
    // 생성자
    public Project(Long teamId,  UUID createdBy) {
        this.teamId = teamId;
//        this.memberId = memberId;
        this.createdBy = createdBy;
        this.status = ProjectStatus.INCOMPLETE;
    }
    
    // Getter, Setter
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    
    public Long getTeamId() { return teamId; }

    public void setTeamId(Long teamId) { this.teamId = teamId; }
    

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    
    public UUID getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }
    
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public void setUserId(UUID userId){
        this.userId = userId;
    }
    public UUID getUserId() {
        return userId;
    }

}

