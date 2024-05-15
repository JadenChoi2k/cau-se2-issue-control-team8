package org.cause2.team8.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.project.Project;
import org.cause2.team8.dto.project.ProjectDTO;
import org.cause2.team8.repository.project.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectDTO.Info createProject(ProjectDTO.CreateRequest createRequest) {
        Project project = createRequest.create();
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
}
