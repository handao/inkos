package com.inkos.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class UserContext {
    private Long userId;
    private String email;
    private String role;

    public boolean isAdmin() {
        return "admin".equals(role);
    }
}
