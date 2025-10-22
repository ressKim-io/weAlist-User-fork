//package OrangeCloud.UserRepo.controller;
//
//import OrangeCloud.UserRepo.dto.MessageApiResponse;
//import OrangeCloud.UserRepo.dto.project.*;
//import OrangeCloud.UserRepo.entity.Project;
//import OrangeCloud.UserRepo.entity.ProjectStatus;
//import OrangeCloud.UserRepo.service.ProjectService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigInteger;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/projects")
//@RequiredArgsConstructor
//@Slf4j
//public class ProjectController {
//
//    private final ProjectService projectService;
//
//    // 프로젝트 생성
//    @PostMapping
//    public ResponseEntity<MessageApiResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
//        log.info("Request to create project: {}", request);
//
//        try {
//            Project project = projectService.createProject(request.getTeamId(), request.getCreatedBy(), request.getStatus());
//            return ResponseEntity.ok(MessageApiResponse.success("프로젝트가 성공적으로 생성되었습니다.", project));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(MessageApiResponse.failure("프로젝트 생성에 실패했습니다: " + e.getMessage()));
//        }
//    }
//
//    // 프로젝트 소프트 삭제
//    @DeleteMapping("/{projectId}")
//    public ResponseEntity<MessageApiResponse> deleteProject(@PathVariable UUID projectId) {
//        log.info("Request to delete project: {}", projectId);
//
//        boolean deleted = projectService.softDeleteProject(projectId);
//        if (deleted) {
//            return ResponseEntity.ok(MessageApiResponse.success("프로젝트가 성공적으로 삭제되었습니다."));
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    // 활성화된 프로젝트 목록 조회
//    @GetMapping
//    public ResponseEntity<MessageApiResponse> getAllActiveProjects() {
//        log.info("Request to get all active projects");
//        List<Project> projects = projectService.getAllActiveProjects();
//        return ResponseEntity.ok(MessageApiResponse.success("활성화된 프로젝트 목록을 성공적으로 조회했습니다.", projects));
//    }
//
//    // 특정 프로젝트 조회
//    @GetMapping("/{projectId}")
//    public ResponseEntity<MessageApiResponse> getActiveProject(@PathVariable UUID projectId) {
//        log.info("Request to get project: {}", projectId);
//
//        Optional<Project> project = projectService.getActiveProjectById(projectId);
//        if (project.isPresent()) {
//            return ResponseEntity.ok(MessageApiResponse.success("프로젝트 정보를 성공적으로 조회했습니다.", project.get()));
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    // 팀별 활성화된 프로젝트 조회
//    @GetMapping("/team/{teamId}")
//    public ResponseEntity<MessageApiResponse> getActiveProjectsByTeam(@PathVariable UUID teamId) {
//        log.info("Request to get active projects by team: {}", teamId);
//        List<Project> projects = projectService.getActiveProjectsByTeamId(teamId);
//        return ResponseEntity.ok(MessageApiResponse.success("팀의 프로젝트 목록을 성공적으로 조회했습니다.", projects));
//    }
//
//    // 상태별 활성화된 프로젝트 조회
//    @GetMapping("/status/{status}")
//    public ResponseEntity<MessageApiResponse> getActiveProjectsByStatus(@PathVariable ProjectStatus status) {
//        log.info("Request to get active projects by status: {}", status);
//        List<Project> projects = projectService.getActiveProjectsByStatus(status);
//        return ResponseEntity.ok(MessageApiResponse.success("상태별 프로젝트 목록을 성공적으로 조회했습니다.", projects));
//    }
//
//    // 생성자별 활성화된 프로젝트 조회
//    @GetMapping("/creator/{createdBy}")
//    public ResponseEntity<MessageApiResponse> getActiveProjectsByCreator(@PathVariable UUID createdBy) {
//        log.info("Request to get active projects by creator: {}", createdBy);
//        List<Project> projects = projectService.getActiveProjectsByCreatedBy(createdBy);
//        return ResponseEntity.ok(MessageApiResponse.success("생성자별 프로젝트 목록을 성공적으로 조회했습니다.", projects));
//    }
//
//    // 프로젝트 정보 수정
//    @PutMapping("/{projectId}")
//    public ResponseEntity<MessageApiResponse> updateProject(@PathVariable UUID projectId,
//                                                            @Valid @RequestBody UpdateProjectRequest request) {
//        log.info("Request to update project: {} with data: {}", projectId, request);
//
//        try {
//            // 권한 검사를 포함한 프로젝트 업데이트
//            Optional<Project> updatedProject = projectService.updateProject(
//                    projectId,
//                    request.getTeamId(),
//                    String.valueOf(request.getStatus()),
//                    request.getUpdatedBy()
//            );
//
//            if (updatedProject.isPresent()) {
//                return ResponseEntity.ok(MessageApiResponse.success("프로젝트 정보가 성공적으로 수정되었습니다.", updatedProject.get()));
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest()
//                    .body(MessageApiResponse.failure(e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(MessageApiResponse.failure("프로젝트 수정에 실패했습니다: " + e.getMessage()));
//        }
//    }
//
//    // 프로젝트 상태만 수정
//    @PatchMapping("/{projectId}/status")
//    public ResponseEntity<MessageApiResponse> updateProjectStatus(@PathVariable UUID projectId,
//                                                                  @Valid @RequestBody UpdateProjectStatusRequest request) {
//        log.info("Request to update project status: {} to status: {}", projectId, request.getStatus());
//
//        boolean updated = projectService.updateProjectStatus(projectId, request.getStatus(), request.getUpdatedBy());
//        if (updated) {
//            return ResponseEntity.ok(MessageApiResponse.success("프로젝트 상태가 성공적으로 수정되었습니다."));
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    // 프로젝트 재활성화
//    @PutMapping("/{projectId}/reactivate")
//    public ResponseEntity<MessageApiResponse> reactivateProject(@PathVariable UUID projectId) {
//        log.info("Request to reactivate project: {}", projectId);
//
//        boolean reactivated = projectService.reactivateProject(projectId);
//        if (reactivated) {
//            return ResponseEntity.ok(MessageApiResponse.success("프로젝트가 성공적으로 재활성화되었습니다."));
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    // 팀의 모든 프로젝트 삭제
//    @DeleteMapping("/team/{teamId}")
//    public ResponseEntity<MessageApiResponse> deleteProjectsByTeam(@PathVariable UUID teamId) {
//        log.info("Request to delete all projects for team: {}", teamId);
//
//        boolean deleted = projectService.softDeleteProjectsByTeamId(teamId);
//        if (deleted) {
//            return ResponseEntity.ok(MessageApiResponse.success("팀의 모든 프로젝트가 성공적으로 삭제되었습니다."));
//        } else {
//            return ResponseEntity.ok(MessageApiResponse.success("삭제할 프로젝트가 없습니다."));
//        }
//    }
//
//    // 활성화된 프로젝트 수 조회
//    @GetMapping("/count")
//    public ResponseEntity<MessageApiResponse> getActiveProjectCount() {
//        long count = projectService.getActiveProjectCount();
//        return ResponseEntity.ok(MessageApiResponse.success("활성화된 프로젝트 수를 성공적으로 조회했습니다.", count));
//    }
//
//    // 팀별 활성화된 프로젝트 수 조회
//    @GetMapping("/team/{teamId}/count")
//    public ResponseEntity<MessageApiResponse> getActiveProjectCountByTeam(@PathVariable UUID teamId) {
//        long count = projectService.getActiveProjectCountByTeamId(teamId);
//        return ResponseEntity.ok(MessageApiResponse.success("팀의 프로젝트 수를 성공적으로 조회했습니다.", count));
//    }
//
//    // 상태별 활성화된 프로젝트 수 조회
//    @GetMapping("/status/{status}/count")
//    public ResponseEntity<MessageApiResponse> getActiveProjectCountByStatus(@PathVariable ProjectStatus status) {
//        long count = projectService.getActiveProjectCountByStatus(status);
//        return ResponseEntity.ok(MessageApiResponse.success("상태별 프로젝트 수를 성공적으로 조회했습니다.", count));
//    }
//
//    // 비활성화된 프로젝트 조회 (관리자용)
//    @GetMapping("/inactive")
//    public ResponseEntity<MessageApiResponse> getInactiveProjects() {
//        log.info("Request to get inactive projects");
//        List<Project> projects = projectService.getInactiveProjects();
//        return ResponseEntity.ok(MessageApiResponse.success("비활성화된 프로젝트 목록을 성공적으로 조회했습니다.", projects));
//    }
//
//    // 최근 생성된 프로젝트 조회
//    @GetMapping("/recent")
//    public ResponseEntity<MessageApiResponse> getRecentProjects(@RequestParam(defaultValue = "7") int days) {
//        log.info("Request to get recent projects for {} days", days);
//        List<Project> projects = projectService.getRecentActiveProjects(days);
//        return ResponseEntity.ok(MessageApiResponse.success("최근 생성된 프로젝트 목록을 성공적으로 조회했습니다.", projects));
//    }
//}