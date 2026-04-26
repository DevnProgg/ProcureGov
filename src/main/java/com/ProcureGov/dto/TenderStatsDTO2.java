package com.ProcureGov.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenderStatsDTO2 {
    private int totalTenders;
    private int openTenders;
    private int closedTenders;
    private int underEvaluationTenders;
    private int awardedTenders;
    private int draftTenders;
    private double totalEstimatedValue;
    private double awardedValue;
    private int totalBidsSubmitted;
    private int activeSuppliers;
}
