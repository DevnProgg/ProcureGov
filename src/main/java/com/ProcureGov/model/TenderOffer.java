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
public class TenderOffer {
    private int tender_id, created_by;
    private String reference_number,
            title,
            description,
            status,
            category,
            notice_file_path;
    private Date publish_datetime, expiry_datetime;
    private double estimated_value;
}
