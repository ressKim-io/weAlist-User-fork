package OrangeCloud.UserRepo.repository;

import OrangeCloud.UserRepo.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
import java.util.*;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    // 기존 메서드들
    List<Team> findByGroupId(UUID groupId);
    List<Team> findByUserId(UUID userId);
    List<Team> findByGroupIdIn(Set<UUID> groupIds);
    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);
    void deleteByGroupIdAndUserId(UUID groupId, UUID userId);

    // 프로젝트 관련 추가 메서드들

    // 특정 팀 ID와 사용자 ID로 멤버십 확인 (프로젝트 권한 체크용)
    boolean existsByTeamIdAndUserId(Long teamId, UUID userId);

    // 특정 팀 ID의 모든 멤버 조회 (프로젝트 팀원 확인용)
    List<Team> findByTeamId(Long teamId);

    // 사용자가 속한 모든 팀의 teamId 목록 조회 (내 프로젝트 조회용)
    @Query("SELECT t.teamId FROM Team t WHERE t.userId = :userId")
    List<Long> findTeamIdsByUserId(@Param("userId") UUID userId);

    // 특정 팀 ID들에 속한 모든 멤버십 조회
    List<Team> findByTeamIdIn(List<Long> teamIds);

    // 특정 사용자가 특정 팀에 속하는지 확인 (권한 체크용)
    @Query("SELECT COUNT(t) > 0 FROM Team t WHERE t.teamId = :teamId AND t.userId = :userId")
    boolean isUserMemberOfTeam(@Param("teamId") Long teamId, @Param("userId") UUID userId);

    // 팀의 첫 번째 멤버(소유자) 조회 (팀 소유자 확인용)
    @Query("SELECT t FROM Team t WHERE t.teamId = :teamId ORDER BY t.teamId ASC")
    List<Team> findByTeamIdOrderByTeamId(@Param("teamId") Long teamId);

    // 특정 팀의 소유자(첫 번째 멤버) 조회
    @Query("SELECT t FROM Team t WHERE t.teamId = :teamId ORDER BY t.teamId ASC LIMIT 1")
    Optional<Team> findTeamOwner(@Param("teamId") Long teamId);

    // 사용자가 소유한 팀들 조회 (첫 번째 멤버인 팀들)
    @Query("SELECT t FROM Team t WHERE t.userId = :userId AND t.teamId IN " +
            "(SELECT MIN(t2.teamId) FROM Team t2 GROUP BY t2.groupId)")
    List<Team> findTeamsOwnedByUser(@Param("userId") UUID userId);
}