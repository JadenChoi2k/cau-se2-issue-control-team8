package org.cause2.team8.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usr")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(length = 100, unique = true, nullable = false, name = "login_id")
    private String loginId;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Setter
    private UserRole role;

    /**
     * userId: 6 ~ 100자의 영숫자
     * name: 2 ~ 20자의 영어, 한글, _(특수문자), 띄어쓰기는 단어 중간에 한 글자만 허용
     * password: 8 ~ 20자의 영숫자, 특수문자는 최소 1개 이상 포함
     * role: not null
     * @return validation result
     */
    public boolean validate() {
        return loginId.matches("[a-zA-Z0-9]{6,100}")
            && name.matches("^[a-zA-Z가-힣]+[a-zA-Z가-힣_ ]{0,18}[a-zA-Z가-힣_]$")
            && password.matches("^(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,20}$")
            && role != null;
    }

    private User(String userId, String name, String password, UserRole role) {
        this.loginId = userId;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public static User create(String userId, String name, String password, UserRole role) {
        User user = new User(userId, name, password, role);
        if (!user.validate()) {
            throw new RuntimeException("Invalid user information");
        }
        return user;
    }
}
