package org.cause2.team8.service;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cause2.team8.common.utils.Utils;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.project.Issue;
import org.cause2.team8.domain.project.IssueComment;
import org.cause2.team8.domain.project.IssueStatus;
import org.cause2.team8.domain.project.Project;
import org.cause2.team8.domain.user.Developer;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.domain.user.UserRole;
import org.cause2.team8.dto.project.IssueCommentDTO;
import org.cause2.team8.dto.project.IssueDTO;
import org.cause2.team8.dto.project.ProjectDTO;
import org.cause2.team8.repository.project.IssueRepository;
import org.cause2.team8.repository.project.ProjectRepository;
import org.cause2.team8.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public boolean hasProjectAuth(HttpSession session, String projectId) {
        User user = Utils.getUserAuth(session);
        if (user.getUserRole().hasRole(UserRole.ADMIN)) {
            return true;
        }
        return projectRepository.participating(projectId, user.getId());
    }

    private Issue findOneIssueAuth(Long issueId, HttpSession session) {
        User user = Utils.getUserAuth(session);
        Issue issue = issueRepository.findByIssueIdOpt(issueId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        if (!user.getUserRole().hasRole(UserRole.PL) && !issue.getReporter().getId().equals(user.getId())) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
        return issue;
    }

    public ProjectDTO.Info createProject(ProjectDTO.CreateRequest createRequest, HttpSession session) {
        Project project = createRequest.create(Utils.getAdmin(session));
        projectRepository.save(project);
        return ProjectDTO.Info.from(project);
    }

    public ProjectDTO.Info editProject(String projectId, ProjectDTO.EditRequest editRequest) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        project = editRequest.edit(project);
        return ProjectDTO.Info.from(project);
    }

    public void deleteProject(String projectId) {
        projectRepository.deleteById(projectId);
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO.Info> findAllProjects() {
        return projectRepository.findAll().stream()
            .map(ProjectDTO.Info::from)
            .toList();
    }

    public ProjectDTO.Detail addOneParticipant(String projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        if (project.getParticipants().contains(user)) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        project.getParticipants().add(user);
        return ProjectDTO.Detail.from(project);
    }

    public List<ProjectDTO.Info> findAllParticipatingProjects(HttpSession session) {
        User user = Utils.getUserAuth(session);
        entityManager.persist(user);
        return user.getParticipatedIn().stream()
            .map(ProjectDTO.Info::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDTO.Detail findOneProject(String projectId, HttpSession session) {
        User user = Utils.getUserAuth(session);
        Project project = projectRepository.findByIdOpt(projectId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        if (!user.getUserRole().hasRole(UserRole.ADMIN)) {
            user = userRepository.findById(user.getId())
                .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
            if (!project.getParticipants().contains(user)) {
                throw new SimpleError(ErrorCode.FORBIDDEN);
            }
        }
        return ProjectDTO.Detail.from(project);
    }

    public IssueDTO.Detail reportOneIssue(String projectId, IssueDTO.CreateRequest createRequest, HttpSession session) {
        if (!hasProjectAuth(session, projectId)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        User user = Utils.getUserAuth(session);
        entityManager.persist(user);
        Issue issue = createRequest.create(project, user);
        issueRepository.save(issue);
        return IssueDTO.Detail.from(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueDTO.PageItem> paginateIssues(String projectId, int page, int size) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        Page<Issue> issues = issueRepository.findAllByProject(project, PageRequest.of(page, size));
        return issues.stream()
            .map(IssueDTO.PageItem::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public IssueDTO.Detail findOneIssue(Long issueId) {
        return IssueDTO.Detail.from(issueRepository.findById(issueId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND)));
    }

    public IssueDTO.Detail editIssue(Long issueId, IssueDTO.EditRequest editRequest, HttpSession session) {
        Issue issue = findOneIssueAuth(issueId, session);
        issue = editRequest.edit(issue);
        return IssueDTO.Detail.from(issue);
    }

//    public IssueDTO.Detail changeIssueStatus(Long issueId, IssueStatus status, HttpSession session) {
//        Issue issue = findOneIssueAuth(issueId, session);
//        if (!Utils.getUserAuth(session).getUserRole().hasRole(UserRole.PL)) {
//            throw new SimpleError(ErrorCode.FORBIDDEN);
//        }
//        issue.setStatus(status);
//        return IssueDTO.Detail.from(issue);
//    }

    public IssueDTO.Detail assignDeveloperToIssue(Long issueId, Long userId) {
        Issue issue = issueRepository.findByIssueIdOpt(issueId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        if (issue.getStatus() != IssueStatus.NEW) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        if (!projectRepository.participating(issue.getProject().getProjectId(), userId)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
        Developer dev = userRepository.findDeveloperById(userId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        issue.setAssignee(dev);
        return IssueDTO.Detail.from(issue);
    }

    public IssueDTO.Detail fixIssue(Long issueId, HttpSession session) {
        Issue issue = findOneIssueAuth(issueId, session);
        if (issue.getStatus() != IssueStatus.ASSIGNED && issue.getStatus() != IssueStatus.REOPENED) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        User user = Utils.getUserAuth(session);
        if (user instanceof Developer) {
            entityManager.persist(user);
            issue.setFixer((Developer) user);
            return IssueDTO.Detail.from(issue);
        } else {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
    }

    public IssueDTO.Detail resolveIssue(Long issueId, HttpSession session) {
        Issue issue = findOneIssueAuth(issueId, session);
        if (issue.getStatus() != IssueStatus.FIXED) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        issue.resolve();
        return IssueDTO.Detail.from(issue);
    }

    public IssueDTO.Detail closeIssue(Long issueId, HttpSession session) {
        Issue issue = findOneIssueAuth(issueId, session);
        if (issue.getStatus() != IssueStatus.RESOLVED) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        issue.close();
        return IssueDTO.Detail.from(issue);
    }

    public IssueDTO.Detail reopenIssue(Long issueId, HttpSession session) {
        Issue issue = findOneIssueAuth(issueId, session);
        if (issue.getStatus() != IssueStatus.RESOLVED) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        issue.reopen();
        return IssueDTO.Detail.from(issue);
    }

    public void deleteIssue(Long issueId, HttpSession session) {
        Issue issue = findOneIssueAuth(issueId, session);
        issueRepository.delete(issue);
    }

    public IssueCommentDTO.Main commentToIssue(String projectId, Long issueId, IssueCommentDTO.Request createRequest, HttpSession session) {
        Issue issue = issueRepository.findByIssueIdOpt(issueId)
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        if (!issue.getProject().getProjectId().equals(projectId)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
        User user = Utils.getUserAuth(session);
        IssueComment issueComment = createRequest.create(user, issue);
        entityManager.persist(issueComment);
        return IssueCommentDTO.Main.from(issueComment);
    }

    public IssueCommentDTO.Main editIssueComment(String projectId, Long issueId, Long commentId, IssueCommentDTO.Request editRequest, HttpSession session) {
        User user = Utils.getUserAuth(session);
        IssueComment comment = entityManager
            .createQuery("select ic from IssueComment ic" +
                " where ic.commentId = :commentId" +
                " and ic.issue.issueId = :issueId" +
                " and ic.issue.project.projectId = :projectId" +
                " and ic.user.id = :userId", IssueComment.class)
            .setParameter("commentId", commentId)
            .setParameter("issueId", issueId)
            .setParameter("projectId", projectId)
            .setParameter("userId", user.getId())
            .getSingleResult();
        if (comment == null) {
            throw new SimpleError(ErrorCode.NOT_FOUND);
        }
        return IssueCommentDTO.Main.from(editRequest.edit(comment));
    }

    public void deleteIssueComment(String projectId, Long issueId, Long commentId, HttpSession session) {
        IssueComment comment = entityManager
            .createQuery("select ic from IssueComment ic" +
                " where ic.commentId = :commentId" +
                " and ic.issue.issueId = :issueId" +
                " and ic.issue.project.projectId = :projectId", IssueComment.class)
            .setParameter("commentId", commentId)
            .setParameter("issueId", issueId)
            .setParameter("projectId", projectId)
            .getSingleResult();
        if (comment == null) {
            throw new SimpleError(ErrorCode.NOT_FOUND);
        }
        User user = Utils.getUserAuth(session);
        if (user.getUserRole().hasRole(UserRole.PL) || comment.getUser().getId().equals(user.getId())) {
            entityManager.remove(comment);
        } else {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
    }
}
