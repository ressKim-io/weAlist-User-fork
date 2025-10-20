package OrangeCloud.UserRepo.repository;

import OrangeCloud.UserRepo.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByTeamId(Long teamId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE " +
            "(:teamId IS NULL OR p.teamId = :teamId) AND " +
            "(:status IS NULL OR p.status = :status)")
    Page<Project> findByFilters(@Param("teamId") Long teamId,
                                @Param("status") String status,
                                Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.teamId IN :teamIds AND " +
            "(:teamId IS NULL OR p.teamId = :teamId) AND " +
            "(:status IS NULL OR p.status = :status)")
    Page<Project> findByTeamIdInAndFilters(@Param("teamIds") List<Long> teamIds,
                                           @Param("teamId") Long teamId,
                                           @Param("status") String status,
                                           Pageable pageable);
}