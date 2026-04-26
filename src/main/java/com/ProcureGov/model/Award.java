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
public class Award{
    private int award_id,
    tender_id,
    bid_id,
    awarded_by;
    private double awarded_value;
    private String officer_justification;
    private Timestamp award_date;
}
