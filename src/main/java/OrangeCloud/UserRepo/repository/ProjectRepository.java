

import OrangeCloud.UserRepo.entity.Project;  // 이 import 문이 필요
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByTeamId(Long teamId);
    List<Project> findByMemberId(Long memberId);
}