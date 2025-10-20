package OrangeCloud.UserRepo.service;

import OrangeCloud.UserRepo.dto.team.TeamResponse;
import OrangeCloud.UserRepo.dto.team.*;
import OrangeCloud.UserRepo.entity.Team;
import OrangeCloud.UserRepo.entity.User;
import OrangeCloud.UserRepo.entity.Group;
import OrangeCloud.UserRepo.repository.TeamRepository;
import OrangeCloud.UserRepo.repository.UserRepository;
import OrangeCloud.UserRepo.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository,
                       UserRepository userRepository,
                       GroupRepository groupRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    // 모든 팀 조회 (팀별로 그룹핑해서 반환)
    public Page<TeamResponse> getAllTeams(Pageable pageable) {
        List<Team> allTeamMemberships = teamRepository.findAll();

        // groupId별로 팀 멤버십들을 그룹핑 (같은 groupId = 같은 팀)
        Map<UUID, List<Team>> teamsByGroupId = allTeamMemberships.stream()
                .collect(Collectors.groupingBy(Team::getGroupId));

        // 각 그룹을 TeamResponse로 변환
        List<TeamResponse> teamResponses = teamsByGroupId.entrySet().stream()
                .map(entry -> convertToTeamResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), teamResponses.size());
        List<TeamResponse> paginatedTeams = teamResponses.subList(start, end);

        return new PageImpl<>(paginatedTeams, pageable, teamResponses.size());
    }

    // 내가 속한 팀 조회
    public Page<TeamResponse> getMyTeams(Authentication authentication, Pageable pageable) {
        User currentUser = getCurrentUser(authentication);

        // 현재 사용자가 속한 팀 멤버십들 조회 (userId로 검색)
        List<Team> myMemberships = teamRepository.findByUserId(currentUser.getUserId());

        if (myMemberships.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        // 내가 속한 팀들의 groupId 수집
        Set<UUID> myTeamGroupIds = myMemberships.stream()
                .map(Team::getGroupId)
                .collect(Collectors.toSet());

        // 해당 그룹들의 모든 멤버십 조회
        List<Team> allMembershipsInMyTeams = teamRepository.findByGroupIdIn(myTeamGroupIds);

        // groupId별로 그룹핑
        Map<UUID, List<Team>> teamsByGroupId = allMembershipsInMyTeams.stream()
                .collect(Collectors.groupingBy(Team::getGroupId));

        // TeamResponse로 변환
        List<TeamResponse> teamResponses = teamsByGroupId.entrySet().stream()
                .map(entry -> convertToTeamResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), teamResponses.size());
        List<TeamResponse> paginatedTeams = teamResponses.subList(start, end);

        return new PageImpl<>(paginatedTeams, pageable, teamResponses.size());
    }

    // 팀 생성 (새로운 그룹 ID로 팀 생성)
    public TeamResponse createTeam(CreateTeamRequest request, Authentication authentication) {
        User owner = getCurrentUser(authentication);
        UUID newTeamGroupId = UUID.randomUUID(); // 새로운 팀을 위한 그룹 ID 생성

        // 팀 생성자를 첫 번째 멤버로 추가
        Team ownerMembership = new Team();
        ownerMembership.setUserId(owner.getUserId()); // 소유자의 UUID
        ownerMembership.setGroupId(newTeamGroupId);   // 팀 그룹 ID

        Team savedMembership = teamRepository.save(ownerMembership);

        return convertToTeamResponse(newTeamGroupId, Arrays.asList(savedMembership));
    }

    // 특정 팀 조회
    public TeamResponse getTeamById(UUID teamGroupId) {
        List<Team> teamMemberships = teamRepository.findByGroupId(teamGroupId);

        if (teamMemberships.isEmpty()) {
            throw new RuntimeException("팀을 찾을 수 없습니다.");
        }

        return convertToTeamResponse(teamGroupId, teamMemberships);
    }

    // 팀 정보 수정
    public TeamResponse updateTeam(UUID teamGroupId, UpdateTeamRequest request, Authentication authentication) {
        List<Team> teamMemberships = teamRepository.findByGroupId(teamGroupId);

        if (teamMemberships.isEmpty()) {
            throw new RuntimeException("팀을 찾을 수 없습니다.");
        }

        User currentUser = getCurrentUser(authentication);

        // 팀 소유자 확인 (첫 번째 멤버를 소유자로 간주하거나, 별도 로직 필요)
        Team ownerMembership = teamMemberships.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("팀 소유자를 찾을 수 없습니다."));

        if (!ownerMembership.getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("팀 소유자만 팀 정보를 수정할 수 있습니다.");
        }

        // 실제 팀 정보 수정 로직 (Group 엔티티 업데이트 등)
        return convertToTeamResponse(teamGroupId, teamMemberships);
    }

    // 팀원 추가
    public void addTeamMember(UUID teamGroupId, AddTeamMemberRequest request, Authentication authentication) {
        List<Team> teamMemberships = teamRepository.findByGroupId(teamGroupId);

        if (teamMemberships.isEmpty()) {
            throw new RuntimeException("팀을 찾을 수 없습니다.");
        }

        User currentUser = getCurrentUser(authentication);

        // 권한 확인 (팀에 속한 사용자만 다른 사용자를 추가할 수 있다고 가정)
        boolean hasPermission = teamMemberships.stream()
                .anyMatch(membership -> membership.getUserId().equals(currentUser.getUserId()));

        if (!hasPermission) {
            throw new RuntimeException("팀원을 추가할 권한이 없습니다.");
        }

        User userToAdd = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 이미 팀원인지 확인
        boolean alreadyMember = teamMemberships.stream()
                .anyMatch(membership -> membership.getUserId().equals(request.getUserId()));

        if (alreadyMember) {
            throw new RuntimeException("이미 팀에 속한 사용자입니다.");
        }

        // 새 멤버십 생성
        Team newMembership = new Team();
        newMembership.setUserId(request.getUserId());
        newMembership.setGroupId(teamGroupId);

        teamRepository.save(newMembership);
    }

    // 팀원 제거
    public void removeTeamMember(UUID teamGroupId, UUID userId, Authentication authentication) {
        List<Team> teamMemberships = teamRepository.findByGroupId(teamGroupId);

        if (teamMemberships.isEmpty()) {
            throw new RuntimeException("팀을 찾을 수 없습니다.");
        }

        User currentUser = getCurrentUser(authentication);

        // 제거할 멤버십 찾기
        Team membershipToRemove = teamMemberships.stream()
                .filter(membership -> membership.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 사용자는 팀에 속하지 않습니다."));

        // 권한 확인: 팀의 첫 번째 멤버(소유자)이거나 자기 자신을 제거하는 경우
        boolean isOwner = teamMemberships.get(0).getUserId().equals(currentUser.getUserId());
        boolean isSelf = currentUser.getUserId().equals(userId);

        if (!isOwner && !isSelf) {
            throw new RuntimeException("팀원을 제거할 권한이 없습니다.");
        }

        // 팀 소유자(첫 번째 멤버)는 제거할 수 없음
        if (teamMemberships.get(0).getUserId().equals(userId)) {
            throw new RuntimeException("팀 소유자는 제거할 수 없습니다.");
        }

        teamRepository.delete(membershipToRemove);
    }

    // 현재 사용자 조회
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    // 팀 멤버십들을 TeamResponse로 변환
    private TeamResponse convertToTeamResponse(UUID teamGroupId, List<Team> memberships) {
        // 팀 소유자 찾기 (첫 번째 멤버를 소유자로 간주)
        Team ownerMembership = memberships.stream()
                .findFirst()
                .orElse(null);

        if (ownerMembership == null) {
            throw new RuntimeException("팀 멤버를 찾을 수 없습니다.");
        }

        User owner = userRepository.findById(ownerMembership.getUserId()).orElse(null);

        // 팀 멤버들 정보 생성
        List<TeamMemberResponse> members = memberships.stream()
                .map(this::convertToTeamMemberResponse)
                .collect(Collectors.toList());

        // Group 정보에서 팀 이름 가져오기
        Group group = groupRepository.findById(teamGroupId).orElse(null);
        String teamName = group != null ? group.getName() : "Team " + teamGroupId.toString().substring(0, 8);

        return new TeamResponse(
                teamGroupId,
                teamName,
                ownerMembership.getUserId(),
                owner != null ? owner.getName() : "Unknown",
                members,
                LocalDateTime.now(), // 생성일시
                LocalDateTime.now()  // 수정일시
        );
    }

    // Team 멤버십을 TeamMemberResponse로 변환
    private TeamMemberResponse convertToTeamMemberResponse(Team membership) {
        User user = userRepository.findById(membership.getUserId()).orElse(null);
        String role = "MEMBER"; // 기본값 (역할 구분이 없으므로)

        return new TeamMemberResponse(
                membership.getUserId(),
                user != null ? user.getName() : "Unknown",
                user != null ? user.getEmail() : "Unknown",
                role,
                LocalDateTime.now() // 가입일시
        );
    }
}