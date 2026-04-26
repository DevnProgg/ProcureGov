package com.ProcureGov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeData {
    private int account_id,
    user_id,
    employee_id;
    private String username,
    role_name,
    privilege_level,
    full_names,
    phone_number,
    gender;
    private boolean active_status;
    private Timestamp created_at;
}

