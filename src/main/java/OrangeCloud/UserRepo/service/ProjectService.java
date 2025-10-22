//package OrangeCloud.UserRepo.service;
//
//import OrangeCloud.UserRepo.entity.Project;
//import OrangeCloud.UserRepo.entity.ProjectStatus;
//import OrangeCloud.UserRepo.repository.ProjectRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigInteger;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//@Slf4j
//public class ProjectService {
//
//    private final ProjectRepository projectRepository;
//    private final TeamService teamService;
//    // 프로젝트 생성
//    public Project createProject(UUID teamId, UUID createdBy, ProjectStatus status) {
//        log.info("Creating new project with teamId: {}, createdBy: {}, status: {}", teamId, createdBy, status);
//
//        Project project = Project.builder()
//                .teamId(teamId)
//                .createdBy(createdBy)
//                .status(status)
//                .isActive(true)
//                .build();
//
//        Project savedProject = projectRepository.save(project);
//        log.info("Created project with ID: {}", savedProject.getProjectId());
//        return savedProject;
//    }
//
//    // 소프트 삭제
//    public boolean softDeleteProject(UUID projectId) {
//        log.info("Soft deleting project with ID: {}", projectId);
//        int updatedRows = projectRepository.softDeleteById(projectId);
//        boolean success = updatedRows > 0;
//        log.info("Project soft delete result: {}", success);
//        return success;
//    }
//
//    // 프로젝트 재활성화
//    public boolean reactivateProject(UUID projectId) {
//        log.info("Reactivating project with ID: {}", projectId);
//        int updatedRows = projectRepository.reactivateById(projectId);
//        boolean success = updatedRows > 0;
//        log.info("Project reactivation result: {}", success);
//        return success;
//    }
//
//    // 활성화된 모든 프로젝트 조회
//    @Transactional(readOnly = true)
//    public List<Project> getAllActiveProjects() {
//        log.debug("Fetching all active projects");
//        return projectRepository.findAllActiveProjects();
//    }
//
//    // ID로 활성화된 프로젝트 조회
//    @Transactional(readOnly = true)
//    public Optional<Project> getActiveProjectById(UUID projectId) {
//        log.debug("Fetching active project by ID: {}", projectId);
//        return projectRepository.findByProjectIdAndIsActiveTrue(projectId);
//    }
//
//    // 팀별 활성화된 프로젝트 조회
//    @Transactional(readOnly = true)
//    public List<Project> getActiveProjectsByTeamId(UUID teamId) {
//        log.debug("Fetching active projects by teamId: {}", teamId);
//        return projectRepository.findActiveByTeamId(teamId);
//    }
//
//    // 상태별 활성화된 프로젝트 조회
//    @Transactional(readOnly = true)
//    public List<Project> getActiveProjectsByStatus(ProjectStatus status) {
//        log.debug("Fetching active projects by status: {}", status);
//        return projectRepository.findActiveByStatus(status);
//    }
//
//    // 생성자별 활성화된 프로젝트 조회
//    @Transactional(readOnly = true)
//    public List<Project> getActiveProjectsByCreatedBy(UUID createdBy) {
//        log.debug("Fetching active projects by createdBy: {}", createdBy);
//        return projectRepository.findActiveByCreatedBy(createdBy);
//    }
//
//    // 프로젝트 정보 수정
//    public Optional<Project> updateProject(UUID projectId, UUID teamId, String status, UUID updatedBy) {
//        log.info("Updating project {} by user {} with teamId: {}, status: {}",
//                projectId, updatedBy, teamId, status);
//
//        // 1. 프로젝트 존재 확인
//        Optional<Project> projectOpt = projectRepository.findById(projectId);
//        if (projectOpt.isEmpty()) {
//            throw new IllegalArgumentException("프로젝트를 찾을 수 없습니다. Project ID: " + projectId);
//        }
//
//        Project project = projectOpt.get();
//
//        // 2. 수정자가 해당 프로젝트의 팀에 속해있는지 확인
//        if (!isUserInProjectTeam(project, updatedBy)) {
//            throw new IllegalArgumentException("프로젝트 수정 권한이 없습니다. 해당 프로젝트의 팀 멤버만 수정할 수 있습니다.");
//        }
//
//        // 3. 프로젝트 정보 업데이트
//        boolean updated = false;
//
//        if (teamId != null && !teamId.equals(project.getTeamId())) {
//            // 새로운 팀으로 변경하는 경우, 수정자가 새 팀에도 속해있는지 확인
//            if (!teamService.isTeamMember(teamId, updatedBy)) {
//                throw new IllegalArgumentException("새로운 팀에 대한 권한이 없습니다. 해당 팀의 멤버만 프로젝트를 할당할 수 있습니다.");
//            }
//            project.setTeamId(teamId);
//            updated = true;
//        }
//
//        if (status != null && !status.equals(project.getStatus())) {
//            project.setStatus(ProjectStatus.valueOf(status));
//            updated = true;
//        }
//
//        if (updated) {
//            project.setUpdatedBy(updatedBy);
//            project.setUpdatedAt(LocalDateTime.now());
//            Project savedProject = projectRepository.save(project);
//            log.info("Project {} successfully updated by user {}", projectId, updatedBy);
//            return Optional.of(savedProject);
//        }
//
//        log.info("No changes made to project {}", projectId);
//        return Optional.of(project);
//    }
//
//    // 프로젝트 상태만 업데이트
//    public boolean updateProjectStatus(UUID projectId, ProjectStatus status, UUID updatedBy) {
//        log.info("Updating project status: {} to status: {}", projectId, status);
//        int updatedRows = projectRepository.updateStatus(projectId, status, updatedBy);
//        boolean success = updatedRows > 0;
//        log.info("Project status update result: {}", success);
//        return success;
//    }
//
//    // 팀의 모든 프로젝트 비활성화
//    public boolean softDeleteProjectsByTeamId(UUID teamId) {
//        log.info("Soft deleting all projects for team: {}", teamId);
//        int updatedRows = projectRepository.softDeleteByTeamId(teamId);
//        boolean success = updatedRows > 0;
//        log.info("Team projects soft delete result: {} projects affected", updatedRows);
//        return success;
//    }
//
//    // 활성화된 프로젝트 수 조회
//    @Transactional(readOnly = true)
//    public long getActiveProjectCount() {
//        return projectRepository.countByIsActiveTrue();
//    }
//
//    // 팀별 활성화된 프로젝트 수 조회
//    @Transactional(readOnly = true)
//    public long getActiveProjectCountByTeamId(UUID teamId) {
//        return projectRepository.countActiveByTeamId(teamId);
//    }
//
//    // 상태별 활성화된 프로젝트 수 조회
//    @Transactional(readOnly = true)
//    public long getActiveProjectCountByStatus(ProjectStatus status) {
//        return projectRepository.countActiveByStatus(status);
//    }
//
//    // 비활성화된 프로젝트 조회 (관리자용)
//    @Transactional(readOnly = true)
//    public List<Project> getInactiveProjects() {
//        return projectRepository.findInactiveProjects();
//    }
//
//    // 최근 생성된 활성화된 프로젝트 조회
//    @Transactional(readOnly = true)
//    public List<Project> getRecentActiveProjects(int days) {
//        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
//        return projectRepository.findActiveProjectsCreatedAfter(startDate);
//    }
//    private boolean isUserInProjectTeam(Project project, UUID userId) {
//        if (project.getTeamId() == null) {
//            // 팀이 할당되지 않은 프로젝트는 누구나 수정 가능하게 할지 결정
//            return true; // 또는 false로 설정하여 팀이 있는 프로젝트만 수정 가능하게 할 수 있음
//        }
//
//        // TeamService를 통해 사용자가 해당 팀의 멤버인지 확인
//        return teamService.isTeamMember(project.getTeamId(), userId);
//    }
//}