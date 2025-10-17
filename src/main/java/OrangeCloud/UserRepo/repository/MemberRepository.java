package OrangeCloud.UserRepo.repository;

import OrangeCloud.UserRepo.entity.Member;  // 이 import 문이 필요
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    List<Member> findByUserId(UUID userId);
    List<Member> findByGroupId(UUID groupId);
    List<Member> findByUserIdAndGroupId(UUID userId, UUID groupId);
}