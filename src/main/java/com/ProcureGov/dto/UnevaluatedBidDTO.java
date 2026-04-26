package com.ProcureGov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnevaluatedBidDTO {
    private int bidId;
    private String supplierName;
    private double bidAmount;
    private int deliveryDays;
    private Date submittedAt;
    private int evaluationsCompleted;
    private int totalEvaluators;
}