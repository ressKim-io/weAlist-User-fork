package OrangeCloud.UserRepo.controller;

import OrangeCloud.UserRepo.dto.*;
import OrangeCloud.UserRepo.dto.project.CreateProjectRequest;
import OrangeCloud.UserRepo.dto.project.ProjectResponse;
import OrangeCloud.UserRepo.dto.project.ProjectTimelineResponse;
import OrangeCloud.UserRepo.dto.project.UpdateProjectRequest;
import OrangeCloud.UserRepo.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Projects", description = "프로젝트 관리 API")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 프로젝트 목록 조회 (필터링, 페이징)
    @GetMapping
    @Operation(summary = "프로젝트 목록 조회", description = "프로젝트 목록을 조회합니다. 필터링과 페이징을 지원합니다.")
    public ResponseEntity<MessageApiResponse> getProjects(
            @Parameter(description = "팀 ID로 필터링")
            @RequestParam(required = false) Long teamId,
            @Parameter(description = "상태로 필터링 (COMPLETE, INCOMPLETE)")
            @RequestParam(required = false) String status,
            @Parameter(description = "내가 참여한 프로젝트만 조회")
            @RequestParam(defaultValue = "false") boolean myProjects,
            @Parameter(description = "페이징 정보")
            Pageable pageable,
            Authentication authentication) {
        try {
            Page<ProjectResponse> projects = projectService.getProjects(teamId, status, myProjects, authentication, pageable);
            return ResponseEntity.ok(new MessageApiResponse(true, "프로젝트 목록 조회 완료", projects));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageApiResponse(false, e.getMessage()));
        }
    }

    // 프로젝트 생성
    @PostMapping
    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    public ResponseEntity<MessageApiResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication) {
        try {
            ProjectResponse project = projectService.createProject(request, authentication);
            return ResponseEntity.ok(new MessageApiResponse(true, "프로젝트가 생성되었습니다.", project));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageApiResponse(false, e.getMessage()));
        }
    }

    // 프로젝트 상세 조회
    @GetMapping("/{id}")
    @Operation(summary = "프로젝트 상세 조회", description = "특정 프로젝트의 상세 정보를 조회합니다.")
    public ResponseEntity<MessageApiResponse> getProjectById(@PathVariable Long id) {
        try {
            ProjectResponse project = projectService.getProjectById(id);
            return ResponseEntity.ok(new MessageApiResponse(true, "프로젝트 조회 완료", project));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 프로젝트 수정
    @PatchMapping("/{id}")
    @Operation(summary = "프로젝트 수정", description = "프로젝트 정보를 수정합니다.")
    public ResponseEntity<MessageApiResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request,
            Authentication authentication) {
        try {
            ProjectResponse project = projectService.updateProject(id, request, authentication);
            return ResponseEntity.ok(new MessageApiResponse(true, "프로젝트가 수정되었습니다.", project));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("권한")) {
                return ResponseEntity.status(403)
                        .body(new MessageApiResponse(false, e.getMessage()));
            } else if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageApiResponse(false, e.getMessage()));
            }
        }
    }

    // 프로젝트 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    public ResponseEntity<MessageApiResponse> deleteProject(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            projectService.deleteProject(id, authentication);
            return ResponseEntity.ok(new MessageApiResponse(true, "프로젝트가 삭제되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("권한")) {
                return ResponseEntity.status(403)
                        .body(new MessageApiResponse(false, e.getMessage()));
            } else if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageApiResponse(false, e.getMessage()));
            }
        }
    }

    // 프로젝트 타임라인 조회
    @GetMapping("/{id}/timeline")
    @Operation(summary = "프로젝트 타임라인 조회", description = "프로젝트의 타임라인 데이터를 조회합니다.")
    public ResponseEntity<MessageApiResponse> getProjectTimeline(@PathVariable Long id) {
        try {
            ProjectTimelineResponse timeline = projectService.getProjectTimeline(id);
            return ResponseEntity.ok(new MessageApiResponse(true, "프로젝트 타임라인 조회 완료", timeline));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}