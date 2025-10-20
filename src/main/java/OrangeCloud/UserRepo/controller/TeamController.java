package OrangeCloud.UserRepo.controller;

import OrangeCloud.UserRepo.dto.*;
import OrangeCloud.UserRepo.dto.team.AddTeamMemberRequest;
import OrangeCloud.UserRepo.dto.team.CreateTeamRequest;
import OrangeCloud.UserRepo.dto.team.TeamResponse;
import OrangeCloud.UserRepo.dto.team.UpdateTeamRequest;
import OrangeCloud.UserRepo.service.TeamService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Teams", description = "팀 관리 API")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // 팀 목록 조회
    @GetMapping
    @Operation(summary = "팀 목록 조회", description = "사용자가 속한 팀 또는 모든 팀 목록을 조회합니다.")
    public ResponseEntity<MessageApiResponse> getTeams(
            @Parameter(description = "내가 속한 팀만 조회")
            @RequestParam(defaultValue = "false") boolean myTeams,
            @Parameter(description = "페이징 정보")
            Pageable pageable,
            Authentication authentication) {
        try {
            Page<TeamResponse> teams;
            if (myTeams) {
                teams = teamService.getMyTeams(authentication, pageable);
            } else {
                teams = teamService.getAllTeams(pageable);
            }
            return ResponseEntity.ok(new MessageApiResponse(true, "팀 목록 조회 완료", teams));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageApiResponse(false, e.getMessage()));
        }
    }

    // 팀 생성
    @PostMapping
    @Operation(summary = "팀 생성", description = "새로운 팀을 생성합니다.")
    public ResponseEntity<MessageApiResponse> createTeam(
            @Valid @RequestBody CreateTeamRequest request,
            Authentication authentication) {
        try {
            TeamResponse team = teamService.createTeam(request, authentication);
            return ResponseEntity.ok(new MessageApiResponse(true, "팀이 생성되었습니다.", team));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageApiResponse(false, e.getMessage()));
        }
    }

    // 팀 상세 조회
    @GetMapping("/{id}")
    @Operation(summary = "팀 상세 조회", description = "특정 팀의 상세 정보를 조회합니다.")
    public ResponseEntity<MessageApiResponse> getTeamById(@PathVariable UUID id) {
        try {
            TeamResponse team = teamService.getTeamById(id);
            return ResponseEntity.ok(new MessageApiResponse(true, "팀 조회 완료", team));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 팀 정보 수정
    @PatchMapping("/{id}")
    @Operation(summary = "팀 정보 수정", description = "팀 정보를 수정합니다. 팀 소유자만 수정 가능합니다.")
    public ResponseEntity<MessageApiResponse> updateTeam(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeamRequest request,
            Authentication authentication) {
        try {
            TeamResponse team = teamService.updateTeam(id, request, authentication);
            return ResponseEntity.ok(new MessageApiResponse(true, "팀 정보가 수정되었습니다.", team));
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

    // 팀원 추가
    @PostMapping("/{id}/members")
    @Operation(summary = "팀원 추가", description = "팀에 새로운 멤버를 추가합니다.")
    public ResponseEntity<MessageApiResponse> addTeamMember(
            @PathVariable UUID id,
            @Valid @RequestBody AddTeamMemberRequest request,
            Authentication authentication) {
        try {
            teamService.addTeamMember(id, request, authentication);
            return ResponseEntity.ok(new MessageApiResponse(true, "팀원이 추가되었습니다."));
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

    // 팀원 제거
    @DeleteMapping("/{id}/members/{userId}")
    @Operation(summary = "팀원 제거", description = "팀에서 멤버를 제거합니다.")
    public ResponseEntity<MessageApiResponse> removeTeamMember(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            Authentication authentication) {
        try {
            teamService.removeTeamMember(id, userId, authentication);
            return ResponseEntity.ok(new MessageApiResponse(true, "팀원이 제거되었습니다."));
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
}