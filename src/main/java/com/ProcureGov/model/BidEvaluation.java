package com.ProcureGov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidEvaluation {
    private int evaluationId;
    private int bidId;
    private int tenderId;
    private int evaluatorId;
    private double priceScore;
    private double technicalScore;
    private double deliveryScore;
    private double weightedTotal;
    private Date evaluatedAt;

    // Additional display fields
    private String evaluatorName;
    private boolean hasEvaluated;
}
