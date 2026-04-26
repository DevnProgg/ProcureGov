package com.ProcureGov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidScoreSummary {
    private int bidId;
    private String supplierName;
    private double bidAmount;
    private int deliveryDays;
    private double lowestBidAmount;
    private int shortestDeliveryDays;
    private double priceScore;
    private double deliveryScore;
    private Double technicalScore;
    private Double weightedTotal;
    private Double finalScore;
    private boolean hasCurrentUserEvaluated;
    private int evaluationsCompleted;
    private int totalEvaluators;
}
