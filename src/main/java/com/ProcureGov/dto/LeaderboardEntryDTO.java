package com.ProcureGov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDTO {
    private int bidId;
    private String supplierName;
    private String regNumber;
    private double bidAmount;
    private double avgTechnicalScore;
    private double finalScore;
    private boolean isAwarded;
}