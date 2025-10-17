//package OrangeCloud.UserRepo.entity;
//
//import jakarta.persistence.Embeddable;
//import java.io.Serializable;
//import java.util.Objects;
//import java.util.UUID;
//
//@Embeddable
//public class MemberId implements Serializable {
//    private UUID userId;
//    private UUID groupId;
//
//    public MemberId() {}
//
//    public MemberId(UUID userId, UUID groupId) {
//        this.userId = userId;
//        this.groupId = groupId;
//    }
//
//    public UUID getUserId() { return userId; }
//    public void setUserId(UUID userId) { this.userId = userId; }
//
//    public UUID getGroupId() { return groupId; }
//    public void setGroupId(UUID groupId) { this.groupId = groupId; }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        MemberId memberId = (MemberId) o;
//        return Objects.equals(userId, memberId.userId) && Objects.equals(groupId, memberId.groupId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(userId, groupId);
//    }
//}