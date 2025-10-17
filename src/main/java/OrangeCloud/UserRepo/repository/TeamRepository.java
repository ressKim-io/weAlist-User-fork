package OrangeCloud.UserRepo.repository;

import OrangeCloud.UserRepo.entity.Team;  // 이 import 문이 필요
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByUserUuid(UUID userUuid);
    List<Team> findByGroupId(UUID groupId);
}