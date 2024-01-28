package mk.ukim.finki.wp.kol2022.g1.model;

import org.springframework.security.core.GrantedAuthority;

public enum EmployeeType implements GrantedAuthority {
    ADMIN,
    REGULAR,
    CONSULTANT;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
