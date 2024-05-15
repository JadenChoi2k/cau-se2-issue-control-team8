package org.cause2.team8.dto.project;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cause2.team8.domain.project.Project;
import org.cause2.team8.dto.user.UserDTO;

import java.util.List;

public abstract class ProjectDTO {
    @Getter
    @RequiredArgsConstructor
    public static class Info {
        private final String projectId;
        private final String title;
        private final String description;

        public static Info from(Project project) {
            return new Info(project.getProjectId(), project.getTitle(), project.getDescription());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Detail {
        private final String projectId;
        private final String title;
        private final String description;
        private final List<UserDTO.Info> participants;

        public static Detail from(Project project) {
            return new Detail(
                project.getProjectId(),
                project.getTitle(),
                project.getDescription(),
                project.getParticipants().stream().map(UserDTO.Info::from).toList()
            );
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class CreateRequest {
        @NotEmpty
        private final String projectId;
        @NotEmpty
        private final String title;
        @NotNull
        private final String description;

        public Project create() {
            return new Project(projectId, title, description);
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
