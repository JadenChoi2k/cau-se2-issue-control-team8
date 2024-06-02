package org.cause2.team8.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cause2.team8.domain.project.Project;
import org.cause2.team8.domain.user.Admin;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.dto.user.UserDTO;

import java.time.LocalDate;
import java.util.List;

public abstract class ProjectDTO {
    @Getter
    @RequiredArgsConstructor
    @Schema(name = "ProjectInfo")
    public static class Info {
        private final String projectId;
        private final String title;
        private final String description;
        private final LocalDate startDate;
        private final LocalDate dueDate;

        public static Info from(Project project) {
            return new Info(project.getProjectId(), project.getTitle(), project.getDescription(), project.getStartDate(), project.getDueDate());
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(name = "ProjectDetail")
    public static class Detail {
        private final String projectId;
        private final String title;
        private final String description;
        private final LocalDate startDate;
        private final LocalDate dueDate;
        private final List<UserDTO.Info> participants;

        public static Detail from(Project project) {
            return new Detail(
                project.getProjectId(),
                project.getTitle(),
                project.getDescription(),
                project.getStartDate(),
                project.getDueDate(),
                project.getParticipants().stream()
                    .map(UserDTO.Info::from)
                    .toList()
            );
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(name = "ProjectCreateRequest")
    public static class CreateRequest {
        @NotEmpty
        private final String projectId;
        @NotEmpty
        private final String title;
        @NotNull
        private final String description;
        @NotNull
        private final LocalDate startDate;
        @NotNull
        private final LocalDate dueDate;
        @NotNull
        private final List<String> userIds;

        public Project create(Admin admin, List<User> users) {
            Project project = admin.createProject(projectId, title, description, startDate, dueDate);
            for (User user : users) {
                admin.participate(project, user);
            }
            return project;
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(name = "ProjectEditRequest")
    public static class EditRequest {
        private final String title;
        private final String description;

        public Project edit(Project project) {
            project.edit(title, description);
            return project;
        }
    }
}
