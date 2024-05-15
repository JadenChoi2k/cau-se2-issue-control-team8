package org.cause2.team8.domain.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("TESTER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tester extends User {
    @Override
    public UserRole getUserRole() {
        return UserRole.TESTER;
    }

    public Tester(String loginId, String name, String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        super.validate();
    }
}
