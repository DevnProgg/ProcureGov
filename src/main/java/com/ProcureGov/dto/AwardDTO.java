package com.ProcureGov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AwardDTO {

    private int awardId;
    private int tenderId;
    private int bidId;
    private BigDecimal awardedValue;
    private String officerJustification;
    private Timestamp awardDate;
    private int awardedBy;
    private String awardedByName;

    private String tenderReference;
    private String tenderTitle;
    private String tenderCategory;

    private int supplierId;
    private String supplierBusinessName;
    private String supplierEmail;
    private String supplierPhone;

    private BigDecimal bidPrice;
    private Integer deliveryDays;
    private Double finalScore;

    private String awardNoticeNumber;
    private String contractNumber;
}