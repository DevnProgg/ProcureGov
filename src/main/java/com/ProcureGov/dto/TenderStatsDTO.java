package com.ProcureGov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenderStatsDTO {
    private int totalTenders;
    private int openTenders;
    private int closedTenders;
    private int underEvaluationTenders;
    private int awardedTenders;
    private double totalEstimatedValue;
    private double awardedValue;
    private int totalBidsSubmitted;
    private int activeSuppliers;

    // Additional calculated fields
    public double getAverageBidsPerTender() {
        if (totalTenders > 0) {
            return (double) totalBidsSubmitted / totalTenders;
        }
        return 0;
    }

    public double getAwardedPercentage() {
        if (totalTenders > 0) {
            return ((double) awardedTenders / totalTenders) * 100;
        }
        return 0;
    }

    public double getCompetitionRatio() {
        if (openTenders > 0 && activeSuppliers > 0) {
            return (double) activeSuppliers / openTenders;
        }
        return 0;
    }
}

