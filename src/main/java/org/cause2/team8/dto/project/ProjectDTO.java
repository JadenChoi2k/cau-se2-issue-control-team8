package org.cause2.team8.dto.project;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cause2.team8.domain.project.Project;

public class ProjectDTO {
    @Getter
    @RequiredArgsConstructor
    public static class Info {
        private final String projectId;
        private final String title;

        public static Info from(Project project) {
            return new Info(project.getProjectId(), project.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class CreateRequest {
        @NotEmpty
        private final String projectId;
        @NotEmpty
        private final String title;

        public Project create() {
            return new Project(projectId, title);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class EditRequest {
        private final String title;
        private final String description;

        public Project edit(Project project) {
            project.edit(title, description);
            return project;
        }
    }
}
