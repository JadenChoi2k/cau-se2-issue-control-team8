package org.cause2.team8.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.project.IssueStatus;
import org.cause2.team8.domain.user.UserRole;
import org.cause2.team8.dto.project.IssueCommentDTO;
import org.cause2.team8.dto.project.IssueDTO;
import org.cause2.team8.dto.project.ProjectDTO;
import org.cause2.team8.dto.user.UserDTO;
import org.cause2.team8.service.ProjectService;
import org.cause2.team8.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final UserService userService;
    private final ProjectService projectService;

    private void checkAdmin(HttpSession session) {
        if (!userService.hasRole(session, UserRole.ADMIN)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
    }

    private void checkPL(HttpSession session) {
        if (!userService.hasRole(session, UserRole.PL)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
    }

    private void checkProjectAuth(HttpSession session, String projectId) {
        if (!projectService.hasProjectAuth(session, projectId)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
    }

    /* ADMIN only endpoints START */

    @PostMapping
    @Operation(summary = "새로운 프로젝트 생성. 어드민만 접근할 수 있습니다.")
    public ResponseEntity<ProjectDTO.Info> createNewProject(
        @Validated @RequestBody ProjectDTO.CreateRequest createRequest, HttpSession session) {
        checkAdmin(session);
        return ResponseEntity.ok(projectService.createProject(createRequest, session));
    }

    @PatchMapping("/{projectId}")
    @Operation(summary = "프로젝트 정보 수정")
    public ResponseEntity<ProjectDTO.Info> editProject(
        @PathVariable String projectId, @Validated @RequestBody ProjectDTO.EditRequest editRequest, HttpSession session) {
        checkAdmin(session);
        return ResponseEntity.ok(projectService.editProject(projectId, editRequest));
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "프로젝트 삭제")
    public ResponseEntity<?> deleteProject(@PathVariable String projectId, HttpSession session) {
        checkAdmin(session);
        projectService.deleteProject(projectId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @Operation(summary = "모든 프로젝트 조회. ADMIN만 접근 가능합니다.")
    public ResponseEntity<List<ProjectDTO.Info>> findAllProjects(HttpSession session) {
        checkAdmin(session);
        return ResponseEntity.ok(projectService.findAllProjects());
    }

    @PostMapping("/{projectId}/participant/{userId}")
    @Operation(summary = "프로젝트 참여자 추가. ADMIN만 접근 가능합니다.")
    public ResponseEntity<ProjectDTO.Detail> addParticipant(@PathVariable String projectId, @PathVariable Long userId, HttpSession session) {
        checkAdmin(session);
        return ResponseEntity.ok(projectService.addOneParticipant(projectId, userId));
    }

    /* ADMIN only endpoints END */

    @GetMapping
    @Operation(summary = "참여 중인 프로젝트 조회")
    public ResponseEntity<List<ProjectDTO.Info>> findAllParticipatingProjects(HttpSession session) {
        return ResponseEntity.ok(projectService.findAllParticipatingProjects(session));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 상세 조회. 권한이 필요함. 참여 중이거나, 어드민 권한.")
    public ResponseEntity<ProjectDTO.Detail> findOneProject(@PathVariable String projectId, HttpSession session) {
        return ResponseEntity.ok(projectService.findOneProject(projectId, session));
    }

    @PostMapping("/{projectId}/issue")
    @Operation(summary = "이슈 생성")
    public ResponseEntity<IssueDTO.Detail> reportOneIssue(
        @PathVariable String projectId, @Validated @RequestBody IssueDTO.CreateRequest createRequest, HttpSession session) {
        return ResponseEntity.ok(projectService.reportOneIssue(projectId, createRequest, session));
    }

    @GetMapping("/{projectId}/issue")
    @Operation(summary = "이슈 목록 조회")
    public ResponseEntity<List<IssueDTO.PageItem>> paginateIssues(
        @PathVariable String projectId, @RequestParam Integer page, @RequestParam Integer size, HttpSession session) {
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.paginateIssues(projectId, page, size));
    }

    @GetMapping("/{projectId}/issue/{issueId}")
    @Operation(summary = "이슈 상세 조회")
    public ResponseEntity<IssueDTO.Detail> findOneIssue(@PathVariable String projectId, @PathVariable Long issueId, HttpSession session) {
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.findOneIssue(issueId));
    }

    @PatchMapping("/{projectId}/issue/{issueId}")
    @Operation(summary = "이슈 내용 변경")
    public ResponseEntity<IssueDTO.Detail> editOneIssue(@PathVariable String projectId, @PathVariable Long issueId,
                                                        @RequestBody IssueDTO.EditRequest editRequest, HttpSession session) {
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.editIssue(issueId, editRequest, session));
    }

    @PostMapping("/{projectId}/issue/{issueId}/assign/{userId}")
    @Operation(summary = "이슈에 개발자 할당. project leader만 가능.")
    public ResponseEntity<IssueDTO.Detail> assignToIssue(
        @PathVariable String projectId, @PathVariable Long issueId, @PathVariable Long userId, HttpSession session) {
        checkPL(session);
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.assignDeveloperToIssue(issueId, userId));
    }

    @GetMapping("/{projectId}/issue/{issueId}/recommend")
    @Operation(summary = "이슈에 할당할 개발자 추천")
    public ResponseEntity<List<UserDTO.Info>> recommendDevelopers(
        @PathVariable String projectId, @PathVariable String issueId, HttpSession session) {
        checkPL(session);
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.recommendDevelopers(issueId));
    }

    @PostMapping("/{projectId}/issue/{issueId}/fix")
    @Operation(summary = "이슈 fixed 처리. developer이면서 assignee만 가능.")
    public ResponseEntity<IssueDTO.Detail> fixIssue(@PathVariable String projectId, @PathVariable Long issueId, HttpSession session) {
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.fixIssue(issueId, session));
    }

    @PostMapping("/{projectId}/issue/{issueId}/resolve")
    @Operation(summary = "이슈 resolved 처리. project leader만 가능.")
    public ResponseEntity<IssueDTO.Detail> resolveIssue(@PathVariable String projectId, @PathVariable Long issueId, HttpSession session) {
        checkPL(session);
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.resolveIssue(issueId, session));
    }

    @PostMapping("/{projectId}/issue/{issueId}/close")
    @Operation(summary = "이슈 closed 처리. project leader만 가능.")
    public ResponseEntity<IssueDTO.Detail> closeIssue(@PathVariable String projectId, @PathVariable Long issueId, HttpSession session) {
        checkPL(session);
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.closeIssue(issueId, session));
    }

    @PostMapping("/{projectId}/issue/{issueId}/reopen")
    @Operation(summary = "이슈 reopen 처리. project leader만 가능. resolve여야만 가능.")
    public ResponseEntity<IssueDTO.Detail> reopenIssue(@PathVariable String projectId, @PathVariable Long issueId, HttpSession session) {
        checkPL(session);
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.reopenIssue(issueId, session));
    }

    @DeleteMapping("/{projectId}/issue/{issueId}")
    @Operation(summary = "이슈 삭제")
    public ResponseEntity<?> deleteIssue(@PathVariable String projectId, @PathVariable Long issueId, HttpSession session) {
        checkProjectAuth(session, projectId);
        projectService.deleteIssue(issueId, session);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{projectId}/issue/{issueId}/comment")
    @Operation(summary = "이슈에 댓글 달기")
    public ResponseEntity<IssueCommentDTO.Main> commentToIssue(
        @PathVariable String projectId, @PathVariable Long issueId,
        @Validated @RequestBody IssueCommentDTO.Request commentRequest, HttpSession session) {
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.commentToIssue(projectId, issueId, commentRequest, session));
    }

    @PatchMapping("/{projectId}/issue/{issueId}/comment/{commentId}")
    @Operation(summary = "이슈 댓글 수정")
    public ResponseEntity<IssueCommentDTO.Main> editIssueComment(
        @PathVariable String projectId, @PathVariable Long issueId, @PathVariable Long commentId,
        @Validated @RequestBody IssueCommentDTO.Request commentRequest, HttpSession session) {
        checkProjectAuth(session, projectId);
        return ResponseEntity.ok(projectService.editIssueComment(projectId, issueId, commentId, commentRequest, session));
    }

    @DeleteMapping("/{projectId}/issue/{issueId}/comment/{commentId}")
    @Operation(summary = "이슈 댓글 삭제")
    public ResponseEntity<?> deleteIssueComment(
        @PathVariable String projectId, @PathVariable Long issueId, @PathVariable Long commentId, HttpSession session) {
        checkProjectAuth(session, projectId);
        projectService.deleteIssueComment(projectId, issueId, commentId, session);
        return ResponseEntity.ok().build();
    }
}
