package com.ProcureGov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TenderBid {
    private int bid_id,
    tender_id,
    supplier_id,
    delivery_days;
    private String compliance_statement,
    document_file_path;
    private Timestamp submitted_at;
    private double price;
}
