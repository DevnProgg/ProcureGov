package com.ProcureGov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidSummaryDTO {
    private int bidId;
    private int tenderId;
    private String tenderTitle;
    private String tenderReference;
    private String tenderStatus;
    private Timestamp submittedAt;
    private String evaluationStatus;
    private Double totalScore;
}