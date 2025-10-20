package OrangeCloud.UserRepo.service;

import OrangeCloud.UserRepo.dto.project.*;
import OrangeCloud.UserRepo.entity.Project;
import OrangeCloud.UserRepo.entity.ProjectStatus;
import OrangeCloud.UserRepo.entity.Team;
import OrangeCloud.UserRepo.entity.User;
import OrangeCloud.UserRepo.repository.ProjectRepository;
import OrangeCloud.UserRepo.repository.TeamRepository;
import OrangeCloud.UserRepo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          TeamRepository teamRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    // 프로젝트 목록 조회 (필터링, 페이징)
    public Page<ProjectResponse> getProjects(Long teamId, String status, boolean myProjects,
                                             Authentication authentication, Pageable pageable) {
        Page<Project> projects;

        if (myProjects) {
            User currentUser = getCurrentUser(authentication);
            List<Long> teamIds = teamRepository.findTeamIdsByUserId(currentUser.getUserId());

            if (teamIds.isEmpty()) {
                return Page.empty(pageable);
            }

            projects = projectRepository.findByTeamIdInAndFilters(teamIds, teamId, status, pageable);
        } else {
            projects = projectRepository.findByFilters(teamId, status, pageable);
        }

        return projects.map(this::convertToProjectResponse);
    }

    // 프로젝트 생성
    public ProjectResponse createProject(CreateProjectRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        List<Team> teamMembers = teamRepository.findByTeamId(request.getTeamId());
        if (teamMembers.isEmpty()) {
            throw new RuntimeException("팀을 찾을 수 없습니다.");
        }

        boolean isMember = teamRepository.existsByTeamIdAndUserId(request.getTeamId(), currentUser.getUserId());
        if (!isMember) {
            throw new RuntimeException("해당 팀의 멤버만 프로젝트를 생성할 수 있습니다.");
        }

        Project project = new Project();
        project.setTeamId(request.getTeamId());
        project.setCreatedBy(currentUser.getUserId()); // UUID 직접 사용
        project.setUserId(currentUser.getUserId());    // UUID 직접 사용
        project.setStatus(ProjectStatus.INCOMPLETE);

        Project savedProject = projectRepository.save(project);
        return convertToProjectResponse(savedProject);
    }

    // 프로젝트 상세 조회
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        return convertToProjectResponse(project);
    }

    // 프로젝트 수정
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request, Authentication authentication) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        User currentUser = getCurrentUser(authentication);

        // 권한 확인 (UUID 직접 비교)
        boolean hasPermission = project.getCreatedBy().equals(currentUser.getUserId()) ||
                teamRepository.isUserMemberOfTeam(project.getTeamId(), currentUser.getUserId());

        if (!hasPermission) {
            throw new RuntimeException("프로젝트를 수정할 권한이 없습니다.");
        }

        // 상태 업데이트
        if (request.getStatus() != null) {
            try {
                ProjectStatus status = ProjectStatus.valueOf(request.getStatus().toUpperCase());
                project.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("유효하지 않은 상태값입니다.");
            }
        }

        project.setUpdatedBy(currentUser.getUserId()); // UUID 직접 사용
        Project savedProject = projectRepository.save(project);

        return convertToProjectResponse(savedProject);
    }

    // 프로젝트 삭제
    public void deleteProject(Long id, Authentication authentication) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        User currentUser = getCurrentUser(authentication);

        // 권한 확인 (UUID 직접 비교)
        if (!project.getCreatedBy().equals(currentUser.getUserId())) {
            throw new RuntimeException("프로젝트를 삭제할 권한이 없습니다.");
        }

        projectRepository.delete(project);
    }

    // 프로젝트 타임라인 조회
    public ProjectTimelineResponse getProjectTimeline(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        List<ProjectTimelineResponse.TimelineEvent> events = new ArrayList<>();

        // 생성 이벤트 - UUID로 사용자 조회
        User creator = getUserByUUID(project.getCreatedBy());
        events.add(new ProjectTimelineResponse.TimelineEvent(
                "CREATED",
                "프로젝트가 생성되었습니다.",
                creator != null ? creator.getName() : "Unknown",
                project.getCreatedAt()
        ));

        // 수정 이벤트 (수정자가 있는 경우)
        if (project.getUpdatedBy() != null && project.getUpdatedAt() != null) {
            User updater = getUserByUUID(project.getUpdatedBy());
            events.add(new ProjectTimelineResponse.TimelineEvent(
                    "UPDATED",
                    "프로젝트가 수정되었습니다.",
                    updater != null ? updater.getName() : "Unknown",
                    project.getUpdatedAt()
            ));
        }

        // 상태 변경 이벤트
        if (project.getStatus() == ProjectStatus.COMPLETE) {
            User updater = project.getUpdatedBy() != null ?
                    getUserByUUID(project.getUpdatedBy()) : null;
            events.add(new ProjectTimelineResponse.TimelineEvent(
                    "STATUS_CHANGED",
                    "프로젝트가 완료 상태로 변경되었습니다.",
                    updater != null ? updater.getName() : "Unknown",
                    project.getUpdatedAt() != null ? project.getUpdatedAt() : project.getCreatedAt()
            ));
        }

        return new ProjectTimelineResponse(id, events);
    }

    // 현재 사용자 조회
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    // UUID로 사용자 조회
    private User getUserByUUID(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // Project를 ProjectResponse로 변환
    private ProjectResponse convertToProjectResponse(Project project) {
        // 팀 정보 조회
        List<Team> teamMembers = teamRepository.findByTeamId(project.getTeamId());
        String teamName = teamMembers.isEmpty() ? "Unknown Team" : "Team " + project.getTeamId();

        // 팀 소유자 확인 (첫 번째 멤버)
        UUID teamOwnerId;
        if (!teamMembers.isEmpty()) {
            teamOwnerId = teamMembers.get(0).getUserId();
        } else {
            teamOwnerId = null;
        }

        // 모든 팀 멤버의 UUID 수집
        List<UUID> memberIds = teamMembers.stream()
                .map(Team::getUserId)
                .collect(Collectors.toList());

        // 한 번의 쿼리로 모든 사용자 조회
        List<User> users = userRepository.findAllById(memberIds);
        Map<UUID, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));

        // 팀 멤버들의 상세 정보 생성
        List<ProjectMemberResponse> memberResponses = teamMembers.stream()
                .map(teamMember -> {
                    User user = userMap.get(teamMember.getUserId());
                    boolean isOwner = teamMember.getUserId().equals(teamOwnerId);
                    return new ProjectMemberResponse(
                            teamMember.getUserId(),
                            user != null ? user.getName() : "Unknown",
                            user != null ? user.getEmail() : "Unknown",
                            isOwner
                    );
                })
                .collect(Collectors.toList());

        // 프로젝트 생성자와 수정자 정보 조회
        User creator = getUserByUUID(project.getCreatedBy());
        User updater = project.getUpdatedBy() != null ?
                getUserByUUID(project.getUpdatedBy()) : null;

        return new ProjectResponse(
                project.getProjectId(),
                project.getTeamId(),
                teamName,
                project.getUserId(),
                creator != null ? creator.getName() : "Unknown",
                updater != null ? updater.getName() : null,
                project.getStatus().name(),
                memberResponses, // 팀 멤버 목록
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}