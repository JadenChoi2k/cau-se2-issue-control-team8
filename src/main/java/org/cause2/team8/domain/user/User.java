package org.cause2.team8.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cause2.team8.common.utils.exceptions.ErrorBase;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.project.Issue;
import org.cause2.team8.domain.project.IssueComment;
import org.cause2.team8.domain.project.IssuePriority;
import org.cause2.team8.domain.project.Project;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usr")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User {
    @Id
    @GeneratedValue
    @Getter
    @Column(name = "user_id")
    protected Long id;

    @Getter
    @Column(length = 100, unique = true, nullable = false, name = "login_id")
    protected String loginId;

    @Getter
    @Setter
    @Column(length = 20, nullable = false)
    protected String name;

    @Setter
    @Column(nullable = false)
    protected String password;

    @Getter
    @ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
    private List<Project> participatedIn = new ArrayList<>();

    public abstract UserRole getUserRole();

    public boolean passwordMatches(String password) {
        return this.password.equals(password);
    }

    /**
     * userId: 4 ~ 100자의 영숫자
     * name: 2 ~ 20자의 영어 또는 한글 또는 숫자 또는 _(특수문자), 띄어쓰기는 단어 중간만 허용
     * password: 8 ~ 255자의 영숫자, 특수문자는 최소 1개 이상 포함
     * role: not null
     * @throws ErrorBase 유효성 검사 실패 시 발생
     */
    public void validate() throws ErrorBase {
        if (!loginId.matches("[a-zA-Z0-9]{4,100}")) {
            throw new SimpleError(ErrorCode.BAD_REQUEST, "로그인 아이디는 4 ~ 100자의 영숫자로 입력해주세요");
        }
        if (!name.matches("^(?=.{2,20}$)(?!.*\\s\\s)([a-zA-Z가-힣0-9_]+(\\s[a-zA-Z가-힣0-9_]+)?)$")) {
            throw new SimpleError(ErrorCode.BAD_REQUEST, "이름은 2 ~ 20자의 영어, 한글, _(특수문자), 띄어쓰기는 단어 중간에 한 글자만 허용해주세요");
        }
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,255}$")) {
            throw new SimpleError(ErrorCode.BAD_REQUEST, "비밀번호는 8 ~ 255자의 영숫자, 특수문자는 최소 1개 이상 포함해주세요");
        }
    }

    public Issue reportIssue(Project project, IssuePriority priority, String title, String description) {
        return new Issue(priority, title, description, project, this);
    }

    public IssueComment commentToIssue(Issue issue, String content) {
        IssueComment issueComment = new IssueComment(this, issue, content);
        issue.getComments().add(issueComment);
        return issueComment;
    }
}
