//package OrangeCloud.UserRepo.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Entity
//@Table(name = "project")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@ToString
//@EqualsAndHashCode(of = "projectId")
//public class Project {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)  // IDENTITY → UUID로 변경
//    @Column(name = "project_id", updatable = false, nullable = false, columnDefinition = "UUID")
//    private UUID projectId;  // BigInteger → UUID로 변경
//
//    @Column(name = "team_id", nullable = false, columnDefinition = "UUID")
//    private UUID teamId;
//
//    @CreationTimestamp
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @Column(name = "created_by", nullable = false, columnDefinition = "UUID")
//    private UUID createdBy;
//
//    @Column(name = "updated_by", columnDefinition = "UUID")
//    private UUID updatedBy;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ProjectStatus status;
//
//    // 소프트 삭제를 위한 필드들
//    @Column(name = "is_active", nullable = false)
//    @Builder.Default
//    private Boolean isActive = true;
//
//        @Column(name = "deleted_at")
//
//        private LocalDateTime deletedAt;
//
//    }
//
//