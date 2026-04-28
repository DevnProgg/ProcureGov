
package com.ProcureGov.service;

import com.ProcureGov.model.ActivityItem;
import com.ProcureGov.repository.ActivityRepository;

import java.util.List;

public class ActivityService {
    private final ActivityRepository activityRepository;

    public ActivityService() {
        this.activityRepository = new ActivityRepository();
    }

    public List<ActivityItem> getRecentActivity(int limit) throws   Exception {
        return activityRepository.findRecentActivity(limit);
    }
}
