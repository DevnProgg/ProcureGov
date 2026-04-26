package com.ProcureGov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private int account_id, role_id, user_id;
    private String username, password_hash;
    private boolean active_status;
}
