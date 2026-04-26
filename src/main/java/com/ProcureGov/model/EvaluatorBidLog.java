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
public class EvaluatorBidLog {
    private int log_id,
    bid_id,
    employee_id;
    private double price_score,
    technical_compliance_score,
    delivery_timeline_score,
    weighted_total;
    private Timestamp evaluated_at;
}
