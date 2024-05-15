package org.cause2.team8.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.user.UserRole;
import org.cause2.team8.dto.project.ProjectDTO;
import org.cause2.team8.service.ProjectService;
import org.cause2.team8.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final UserService userService;
    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "새로운 프로젝트 생성. 어드민만 접근할 수 있습니다.")
    public ResponseEntity<ProjectDTO.Info> createNewProject(
        @Validated @RequestBody ProjectDTO.CreateRequest createRequest, HttpSession session) {
        if (!userService.hasRole(session, UserRole.ADMIN)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
        return ResponseEntity.ok(projectService.createProject(createRequest));
    }

    @PatchMapping("/{projectId}")
    @Operation(summary = "프로젝트 정보 수정")
    public ResponseEntity<ProjectDTO.Info> editProject(
        @PathVariable String projectId, @Validated @RequestBody ProjectDTO.EditRequest editRequest, HttpSession session) {
        if (!userService.hasRole(session, UserRole.ADMIN)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
        return ResponseEntity.ok(projectService.editProject(projectId, editRequest));
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "프로젝트 삭제")
    public ResponseEntity<?> deleteProject(@PathVariable String projectId, HttpSession session) {
        if (!userService.hasRole(session, UserRole.ADMIN)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
        projectService.deleteProject(projectId);
        return ResponseEntity.ok().build();
    }
}
