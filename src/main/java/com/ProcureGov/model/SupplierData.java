package com.ProcureGov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SupplierData {
    private int account_id,
    user_id,
    supplier_id;
    private String username,
    role_name,
    business_name,
    email,
    address,
    phone_number,
    reg_number;
}
