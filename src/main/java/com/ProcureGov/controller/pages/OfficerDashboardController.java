package com.ProcureGov.controller.pages;

import com.ProcureGov.dto.*;
import com.ProcureGov.model.*;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/app/officer/dashboard")
public class OfficerDashboardController extends HttpServlet {

    private TenderService tenderService;
    private BidService bidService;
    private EvaluationService evaluationService;

    @Override
    public void init() throws ServletException {
        this.tenderService = new TenderService();
        this.bidService = new BidService();
        this.evaluationService = new EvaluationService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);

            // Get user information
            if (session != null && session.getAttribute("user") != null) {
                Object user = session.getAttribute("user");
                if (user instanceof EmployeeData employee) {
                    req.setAttribute("userRole", employee.getPrivilege_level());
                    req.setAttribute("userName", employee.getFull_names());
                }
            }

            // Set greeting based on time of day
            setGreeting(req);

            // Set current date
            setCurrentDate(req);

            // Get tender statistics
            setTenderStats(req);

            // Get recent activity
            setSampleActivity(req);

            // Get category distribution
            setCategoryDistribution(req);

            // Get recent tenders
            setRecentTenders(req);

            // Get bid statistics
            setBidStatistics(req);

            // Get compliance alerts
            setComplianceAlerts(req);

            // Get evaluation statistics
            setEvaluationStats(req);

            // Forward to dashboard
            req.getRequestDispatcher("/WEB-INF/views/pages/officerDashboard.jsp")
                    .forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Unable to load dashboard data: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/pages/officerDashboard.jsp")
                    .forward(req, resp);
        }
    }

    private void setGreeting(HttpServletRequest req) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Morning";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Afternoon";
        } else if (hour >= 17 && hour < 22) {
            greeting = "Evening";
        } else {
            greeting = "Night";
        }

        req.setAttribute("greeting", greeting);
    }

    private void setCurrentDate(HttpServletRequest req) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH);
        req.setAttribute("currentDate", sdf.format(new Date()));
    }

    private void setTenderStats(HttpServletRequest req) {
        try {
            TenderStatsDTO stats = tenderService.getTenderStats();

            req.setAttribute("totalTenders", stats.getTotalTenders());
            req.setAttribute("openCount", stats.getOpenTenders());
            req.setAttribute("evalCount", stats.getUnderEvaluationTenders());
            req.setAttribute("awardedCount", stats.getAwardedTenders());
            req.setAttribute("draftCount", tenderService.getDraftTenders().size() );
            req.setAttribute("closedCount", stats.getClosedTenders());
            req.setAttribute("totalEstimatedValue", stats.getTotalEstimatedValue());
            req.setAttribute("totalValue", stats.getTotalEstimatedValue());

        } catch (Exception e) {
            setDefaultStats(req);
        }
    }

    private void setDefaultStats(HttpServletRequest req) {
        req.setAttribute("totalTenders", 0);
        req.setAttribute("openCount", 0);
        req.setAttribute("evalCount", 0);
        req.setAttribute("awardedCount", 0);
        req.setAttribute("draftCount", 0);
        req.setAttribute("closedCount", 0);
        req.setAttribute("totalEstimatedValue", 0.0);
        req.setAttribute("totalValue", 0.0);
    }


    private void setSampleActivity(HttpServletRequest req) {
        List<Map<String, Object>> activities = new ArrayList<>();

        // Sample activity items
        addSampleActivity(activities, "New Tender Created",
                "Tender for Road Construction Materials - Northern District has been published",
                "add_circle", "info", "Construction");

        addSampleActivity(activities, "Bid Received",
                "BuildCorp Ltd submitted a bid for MPW-2024-1234",
                "send", "bid", "Roads");

        addSampleActivity(activities, "Evaluation Started",
                "Evaluation panel has begun scoring bids for Electrical Supplies Tender",
                "rate_review", "evaluation", "Electrical");

        req.setAttribute("recentActivity", activities);
    }

    private void addSampleActivity(List<Map<String, Object>> activities,
                                   String title, String description,
                                   String icon, String type, String category) {
        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("description", description);
        item.put("icon", icon);
        item.put("type", type);
        item.put("relativeTime", "Just now");

        if (category != null) {
            List<Map<String, String>> tags = new ArrayList<>();
            Map<String, String> tag = new HashMap<>();
            tag.put("label", category);
            tag.put("type", getCategoryType(category));
            tags.add(tag);
            item.put("metadata", tags);
        }

        activities.add(item);
    }

    private void setCategoryDistribution(HttpServletRequest req) throws Exception{
            List<CategoryStatsDTO> categoryStats = tenderService.getCategoryStats();

            if (categoryStats != null && !categoryStats.isEmpty()) {
                int totalCount = categoryStats.stream()
                        .mapToInt(CategoryStatsDTO::getTenderCount)
                        .sum();

                List<Map<String, Object>> categories = new ArrayList<>();

                for (CategoryStatsDTO stat : categoryStats) {
                    Map<String, Object> category = getStringObjectMap(stat, totalCount);

                    categories.add(category);
                }

                req.setAttribute("categoryStats", categories);
            }
    }

    @Nonnull
    private Map<String, Object> getStringObjectMap(CategoryStatsDTO stat, int totalCount) {
        Map<String, Object> category = new HashMap<>();
        category.put("name", stat.getCategory());
        category.put("count", stat.getTenderCount());
        category.put("openCount", stat.getOpenCount());
        category.put("totalValue", stat.getTotalValue());
        category.put("avgValue", stat.getAvgValue());
        category.put("type", getCategoryType(stat.getCategory()));

        // Calculate percentage
        double percentage = totalCount > 0 ?
                (stat.getTenderCount() * 100.0 / totalCount) : 0;
        category.put("percentage", Math.round(percentage));
        return category;
    }

    private void setRecentTenders(HttpServletRequest req) throws Exception {
            List<TenderOffer> recentTenders = tenderService.getOpenTendersExcludingDrafts(5);

            if (recentTenders != null && !recentTenders.isEmpty()) {
                List<Map<String, Object>> tenderList = new ArrayList<>();

                for (TenderOffer tender : recentTenders) {
                    Map<String, Object> tenderMap = new HashMap<>();
                    tenderMap.put("referenceNumber", tender.getReference_number());
                    tenderMap.put("title", tender.getTitle());
                    tenderMap.put("category", tender.getCategory());
                    tenderMap.put("categoryType", getCategoryType(tender.getCategory()));
                    tenderMap.put("estimatedValue", tender.getEstimated_value());
                    tenderMap.put("status", tender.getStatus());
                    tenderMap.put("tenderId", tender.getTender_id());

                    if (tender.getExpiry_datetime() != null) {
                        tenderMap.put("closingDate", tender.getExpiry_datetime());
                    }

                    tenderList.add(tenderMap);
                }

                req.setAttribute("recentTenders", tenderList);
            }
    }

    private void setBidStatistics(HttpServletRequest req) {
        try {
            int totalBids = bidService.getTotalBidCount();
            req.setAttribute("bidCount", totalBids);

            // Calculate bid trend (this month vs last month)
            int thisMonthBids = bidService.getBidCountForCurrentMonth();
            int lastMonthBids = bidService.getBidCountForLastMonth();

            if (lastMonthBids > 0) {
                double trend = ((double)(thisMonthBids - lastMonthBids) / lastMonthBids) * 100;
                req.setAttribute("bidTrend", Math.round(trend));
            } else if (thisMonthBids > 0) {
                req.setAttribute("bidTrend", 100);
            } else {
                req.setAttribute("bidTrend", 0);
            }

        } catch (Exception e) {
            req.setAttribute("bidCount", 0);
            req.setAttribute("bidTrend", 0);
        }
    }

    private void setComplianceAlerts(HttpServletRequest req) throws  Exception {
            List<String> alerts = new ArrayList<>();

            // Check for tenders closing soon (within 48 hours)
            List<TenderOffer> closingSoon = tenderService.getTendersClosingWithin(48);
            if (closingSoon != null && !closingSoon.isEmpty()) {
                alerts.add(closingSoon.size() + " tender(s) closing within 48 hours. Ensure evaluation panels are ready.");
            }

            // Check for tenders pending evaluation
            int pendingEval = tenderService.getPendingEvaluationCount();
            if (pendingEval > 5) {
                alerts.add(pendingEval + " tenders pending evaluation. Consider scheduling evaluation panels.");
            }

            // Check for draft tenders older than 7 days
            int staleDrafts = tenderService.getStaleDraftCount(7);
            if (staleDrafts > 0) {
                alerts.add(staleDrafts + " draft tender(s) older than 7 days. Please complete or remove them.");
            }

            // Check bid-to-tender ratio
            TenderStatsDTO stats = tenderService.getTenderStats();
            int totalBids = bidService.getTotalBidCount();
            if (stats.getOpenTenders() > 0 && totalBids == 0) {
                alerts.add("No bids received for current open tenders. Consider extending deadlines or increasing visibility.");
            }

            if (!alerts.isEmpty()) {
                req.setAttribute("complianceAlerts", alerts);
                req.setAttribute("complianceAlert", alerts.getFirst()); // Primary alert
            }
    }

    private void setEvaluationStats(HttpServletRequest req) {
        try {
            int activeEvaluations = evaluationService.getActiveEvaluationCount();
            int completedEvaluations = evaluationService.getCompletedEvaluationCount();
            double averageScore = evaluationService.getAverageEvaluationScore();

            req.setAttribute("activeEvaluations", activeEvaluations);
            req.setAttribute("completedEvaluations", completedEvaluations);
            req.setAttribute("averageScore", averageScore);
            req.setAttribute("evalCount", activeEvaluations);

        } catch (Exception e) {
            req.setAttribute("activeEvaluations", 0);
            req.setAttribute("completedEvaluations", 0);
            req.setAttribute("averageScore", 0.0);
        }
    }

    // Utility methods

    private String getCategoryType(String category) {
        if (category == null) return "general";

        return switch (category.toLowerCase()) {
            case "construction" -> "construction";
            case "roads" -> "roads";
            case "electrical" -> "electrical";
            case "plumbing" -> "plumbing";
            default -> "general";
        };
    }
}