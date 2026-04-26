package com.ProcureGov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {
    private int supplier_id;
    private String business_name,
    email,
    address,
    phone_number,
    reg_number;
}
