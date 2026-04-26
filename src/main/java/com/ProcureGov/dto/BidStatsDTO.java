package com.ProcureGov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidStatsDTO {
    private int totalBids;
    private int uniqueTenders;
    private int wonBids;
    private int evaluatedBids;
    private int pendingBids;
    private Double averageScore;

    public double getSuccessRate() {
        if (totalBids > 0) {
            return ((double) wonBids / totalBids) * 100;
        }
        return 0;
    }

    public double getEvaluationRate() {
        if (totalBids > 0) {
            return ((double) evaluatedBids / totalBids) * 100;
        }
        return 0;
    }
}