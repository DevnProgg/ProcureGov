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
public class ActivityItem {
    private int id;
    private String title;
    private String description;
    private String type;
    private String category;
    private String icon;
    private Date timestamp;
    private int userId;
}
