package com.ProcureGov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsDTO {
    private String category;
    private int tenderCount;
    private int openCount;
    private double totalValue;
    private double avgValue;
}