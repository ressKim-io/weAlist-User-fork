//package OrangeCloud.UserRepo.repository;
//
//import OrangeCloud.UserRepo.entity.Project;
//import OrangeCloud.UserRepo.entity.ProjectStatus;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public interface ProjectRepository extends JpaRepository<Project, UUID> {  // BigInteger → UUID
//
//    // 소프트 삭제
//    @Modifying
//    @Transactional
//    @Query("UPDATE Project p SET p.isActive = false, p.deletedAt = CURRENT_TIMESTAMP WHERE p.projectId = :projectId")
//    int softDeleteById(@Param("projectId") UUID projectId);  // BigInteger → UUID
//
//    // 프로젝트 재활성화
//    @Modifying
//    @Transactional
//    @Query("UPDATE Project p SET p.isActive = true, p.deletedAt = null WHERE p.projectId = :projectId")
//    int reactivateById(@Param("projectId") UUID projectId);  // BigInteger → UUID
//
//    // 활성화된 프로젝트만 조회
//    @Query("SELECT p FROM Project p WHERE p.isActive = true")
//    List<Project> findAllActiveProjects();
//
//    // ID로 활성화된 프로젝트 조회
//    Optional<Project> findByProjectIdAndIsActiveTrue(UUID projectId);  // BigInteger → UUID
//
//    // 팀별 활성화된 프로젝트 조회
//    @Query("SELECT p FROM Project p WHERE p.teamId = :teamId AND p.isActive = true")
//    List<Project> findActiveByTeamId(@Param("teamId") UUID teamId);
//
//    // 상태별 활성화된 프로젝트 조회
//    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.isActive = true")
//    List<Project> findActiveByStatus(@Param("status") ProjectStatus status);
//
//    // 생성자별 활성화된 프로젝트 조회
//    @Query("SELECT p FROM Project p WHERE p.createdBy = :createdBy AND p.isActive = true")
//    List<Project> findActiveByCreatedBy(@Param("createdBy") UUID createdBy);
//
//    // 활성화된 프로젝트 수 조회
//    long countByIsActiveTrue();
//
//    // 팀별 활성화된 프로젝트 수 조회
//    @Query("SELECT COUNT(p) FROM Project p WHERE p.teamId = :teamId AND p.isActive = true")
//    long countActiveByTeamId(@Param("teamId") UUID teamId);
//
//    // 상태별 활성화된 프로젝트 수 조회
//    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status AND p.isActive = true")
//    long countActiveByStatus(@Param("status") ProjectStatus status);
//
//    // 팀의 모든 프로젝트 비활성화
//    @Modifying
//    @Transactional
//    @Query("UPDATE Project p SET p.isActive = false, p.deletedAt = CURRENT_TIMESTAMP WHERE p.teamId = :teamId")
//    int softDeleteByTeamId(@Param("teamId") UUID teamId);
//
//    // 비활성화된 프로젝트 조회
//    @Query("SELECT p FROM Project p WHERE p.isActive = false ORDER BY p.deletedAt DESC")
//    List<Project> findInactiveProjects();
//
//    // 생성일 기준 활성화된 프로젝트 조회
//    @Query("SELECT p FROM Project p WHERE p.createdAt >= :startDate AND p.isActive = true ORDER BY p.createdAt DESC")
//    List<Project> findActiveProjectsCreatedAfter(@Param("startDate") LocalDateTime startDate);
//
//    // 프로젝트 상태 업데이트
//    @Modifying
//    @Transactional
//    @Query("UPDATE Project p SET p.status = :status, p.updatedBy = :updatedBy WHERE p.projectId = :projectId AND p.isActive = true")
//    int updateStatus(@Param("projectId") UUID projectId, @Param("status") ProjectStatus status, @Param("updatedBy") UUID updatedBy);
//}