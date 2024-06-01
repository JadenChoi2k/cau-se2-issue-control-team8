package org.cause2.team8.service;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cause2.team8.common.utils.Utils;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.project.*;
import org.cause2.team8.domain.user.Developer;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.domain.user.UserRole;
import org.cause2.team8.dto.project.IssueCommentDTO;
import org.cause2.team8.dto.project.IssueDTO;
import org.cause2.team8.dto.project.ProjectDTO;
import org.cause2.team8.dto.user.UserDTO;
import org.cause2.team8.repository.project.IssueRepository;
import org.cause2.team8.repository.project.ProjectRepository;
import org.cause2.team8.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
        if (createRequest.getProjectId().equals("all")) {
            throw new SimpleError(ErrorCode.BAD_REQUEST);
        }
        if (projectRepository.existsById(createRequest.getProjectId())) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        // userIds 중 숫자로 변환 가능한 것만 추출
        List<Long> userIdsLong = createRequest.getUserIds().stream()
            .map((idStr) -> {
                try {
                    return Long.parseLong(idStr);
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
        // userIds 중 숫자로 변환 가능한 것만 DB에서 조회
        List<User> users = new ArrayList<>();
        if (!userIdsLong.isEmpty()) {
            users = userRepository.findAllById(userIdsLong);
        }
        // 프로젝트 생성
        Project project = createRequest.create(Utils.getAdmin(session), users);
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

    @Transactional(readOnly = true)
    public List<UserDTO.Info> recommendDevelopers(String issueId) {
        Issue issue = issueRepository.findByIssueIdOpt(Long.parseLong(issueId))
            .orElseThrow(() -> new SimpleError(ErrorCode.NOT_FOUND));
        // 이미 할당된 경우
        if (issue.getAssignee() != null) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }

        // 추천 방식
        // 1. 해당 프로젝트에 참여 중인 개발자 중 처리 중인 이슈가 적은 순
        // 2. 이전의 처리한 이슈와 현재 이슈의 유사도가 높은 순
        // 3. 이전의 이슈 처리 수가 높은 순
        // 위의 순서대로 차례차례 추천

        List<Developer> developers
            = projectRepository.getDevelopersOrderByAssignedCountAsc(issue.getProject());
        developers.sort((d1, d2) -> {
            List<Issue> i1 = d1.getAssignedIssues();
            List<Issue> i2 = d2.getAssignedIssues();

            if (i1.isEmpty() && i2.isEmpty()) {
                return 0;
            } else if (i1.isEmpty()) {
                return 1;
            } else if (i2.isEmpty()) {
                return -1;
            }

            double d1Score = i1.stream()
                .mapToDouble(i -> i.calculateSimilarity(issue))
                .sum() / i1.size();
            double d2Score = i2.stream()
                .mapToDouble(i -> i.calculateSimilarity(issue))
                .sum() / i2.size();

            if (d1Score > d2Score) {
                return -1;
            } else if (d1Score < d2Score) {
                return 1;
            } else {
                return -Integer.compare(i1.size(), i2.size());
            }
        });

        return developers.stream()
            .map(UserDTO.Info::from)
            .toList();
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
