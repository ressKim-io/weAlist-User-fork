//// DataLoader.java
//package OrangeCloud.UserRepo.config;
//
//import OrangeCloud.UserRepo.entity.*;
//import OrangeCloud.UserRepo.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//public class DataLoader implements CommandLineRunner {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private GroupRepository groupRepository;
//
//    @Autowired
//    private TeamRepository teamRepository;
//
//    @Autowired
//    private ProjectRepository projectRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 기존 데이터가 있으면 스킵
//        if (userRepository.count() > 0) {
//            return;
//        }
//
//        System.out.println("Creating dummy data...");
//
//        // 1. 사용자 생성
//        User user1 = createUser("김철수", "user1@test.com", "password123");
//        User user2 = createUser("이영희", "user2@test.com", "password123");
//        User user3 = createUser("박민수", "user3@test.com", "password123");
//        User user4 = createUser("최지현", "user4@test.com", "password123");
//
//        // 2. 그룹 생성 (회사)
//        Group company1 = createGroup("테크컴퍼니", UUID.fromString("12212213-0000-0000-0000-0333333000"));;
//        Group company2 = createGroup("스타트업코리아", UUID.fromString("12212213-0000-0000-0000-00012112"));;
//
//        // 3. 팀 생성 (여러 회사 사람들이 모인 프로젝트 팀)
//        UUID teamGroupId1 = UUID.randomUUID();
//        UUID teamGroupId2 = UUID.randomUUID();
//
//        // 팀1: user1(소유자), user2 참여
//        createTeamMembership(1L, user1.getUserId(), teamGroupId1);
//        createTeamMembership(2L, user2.getUserId(), teamGroupId1);
//
//        // 팀2: user3(소유자), user4 참여
//        createTeamMembership(3L, user3.getUserId(), teamGroupId2);
//        createTeamMembership(4L, user4.getUserId(), teamGroupId2);
//
//        // 4. 프로젝트 생성
//        createProject(1L, user1.getUserId(), ProjectStatus.INCOMPLETE);
//        createProject(1L, user2.getUserId(), ProjectStatus.COMPLETE);
//        createProject(2L, user3.getUserId(), ProjectStatus.INCOMPLETE);
//
//        System.out.println("Dummy data created successfully!");
//        System.out.println("Users created: " + userRepository.count());
//        System.out.println("Teams created: " + teamRepository.count());
//        System.out.println("Projects created: " + projectRepository.count());
//    }
//
//    private User createUser(String name, String email, String password) {
//        User user = new User();
//        user.setName(name);
//        user.setEmail(email);
//        user.setPasswordHash(passwordEncoder.encode(password));
//        return userRepository.save(user);
//    }
//
//    private Group createGroup(String name, UUID uuid) {
//        Group group = new Group();
//        group.setName(name);
//        group.setGroupId(uuid);
//        return groupRepository.save(group);
//    }
//
//    private Team createTeamMembership(Long teamId, UUID userId, UUID groupId) {
//        Team team = new Team();
//        team.setTeamId(teamId);
//        team.setUserId(userId);
//        team.setGroupId(groupId);
//        return teamRepository.save(team);
//    }
//
//    private Project createProject(Long teamId, UUID createdBy, ProjectStatus status) {
//        Project project = new Project();
//        project.setTeamId(teamId);
//        project.setCreatedBy(createdBy);
//        project.setUserId(createdBy);
//        project.setStatus(status);
//        return projectRepository.save(project);
//    }
//}