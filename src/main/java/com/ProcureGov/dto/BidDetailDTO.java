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
public class BidDetailDTO {
    private int bidId;
    private int tenderId;
    private int supplierId;
    private int deliveryDays;
    private String complianceStatement;
    private String documentFilePath;
    private Timestamp submittedAt;
    private String businessName;
    private String regNumber;
    private String email;
    private String phoneNumber;
    private Double evaluationScore;
    private Double priceScore;
    private Double technicalScore;
    private Double deliveryScore;
    private Double price;
    private boolean isAwarded;
}