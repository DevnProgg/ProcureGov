<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en"/>

<c:set var="pageTitle" value="Evaluate Bid" scope="request"/>
<c:set var="pageSection" value="Bid Evaluation" scope="request"/>
<c:set var="activePage" value="evaluations" scope="request"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
    <style>
        /* Evaluation-specific styling */
        .eval-metric-card {
            background: var(--color-surface-container-lowest);
            border: 1px solid var(--color-outline-variant);
            border-radius: var(--radius-lg);
            padding: 1.5rem;
            transition: all 0.2s ease;
        }

        .eval-metric-card:hover {
            box-shadow: var(--shadow-card);
            border-color: var(--color-primary);
        }

        .eval-metric-label {
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.08em;
            color: var(--color-outline);
            margin-bottom: 0.5rem;
        }

        .eval-metric-value {
            font-family: var(--font-headline);
            font-size: 2.5rem;
            font-weight: 700;
            color: var(--color-primary);
            line-height: 1;
        }

        .eval-metric-detail {
            font-size: 0.8125rem;
            color: var(--color-on-surface-variant);
            margin-top: 0.5rem;
        }

        .auto-calculated {
            background: linear-gradient(135deg, var(--color-primary-fixed) 0%, var(--color-secondary-fixed-dim) 100%);
            padding: 0.25rem 0.75rem;
            border-radius: var(--radius-full);
            font-size: 0.6875rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            color: var(--color-on-primary-fixed-variant);
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
        }

        .weighted-badge {
            background: var(--color-surface-container-high);
            padding: 0.25rem 0.625rem;
            border-radius: var(--radius-full);
            font-size: 0.6875rem;
            font-weight: 600;
            color: var(--color-primary);
        }

        .score-input-large {
            width: 100%;
            padding: 1rem;
            font-family: var(--font-headline);
            font-size: 2rem;
            font-weight: 700;
            text-align: center;
            background: var(--color-surface-container);
            border: 2px solid var(--color-outline-variant);
            border-radius: var(--radius-lg);
            color: var(--color-primary);
            transition: all 0.2s ease;
        }

        .score-input-large:focus {
            outline: none;
            border-color: var(--color-primary);
            background: var(--color-surface-container-lowest);
            box-shadow: 0 0 0 3px rgba(0, 63, 135, 0.1);
        }

        .score-input-large::placeholder {
            color: var(--color-outline);
            opacity: 0.5;
        }

        .evaluation-locked {
            opacity: 0.6;
            pointer-events: none;
            position: relative;
        }

        .evaluation-locked::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: repeating-linear-gradient(
                    45deg,
                    transparent,
                    transparent 10px,
                    rgba(0, 0, 0, 0.02) 10px,
                    rgba(0, 0, 0, 0.02) 20px
            );
            border-radius: var(--radius-lg);
            pointer-events: none;
        }

        .bid-detail-row {
            display: flex;
            justify-content: space-between;
            padding: 0.75rem 0;
            border-bottom: 1px solid var(--color-outline-variant);
        }

        .bid-detail-row:last-child {
            border-bottom: none;
        }

        .bid-detail-label {
            font-weight: 600;
            color: var(--color-on-surface-variant);
        }

        .bid-detail-value {
            font-weight: 700;
            color: var(--color-on-surface);
        }

        .evaluator-status-list {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
        }

        .evaluator-status-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0.75rem;
            background: var(--color-surface-container-low);
            border-radius: var(--radius-md);
        }

        .evaluator-name {
            font-weight: 600;
            color: var(--color-on-surface);
        }

        .status-indicator {
            display: inline-flex;
            align-items: center;
            gap: 0.375rem;
            padding: 0.25rem 0.75rem;
            border-radius: var(--radius-full);
            font-size: 0.75rem;
            font-weight: 600;
        }

        .status-indicator.completed {
            background: var(--color-secondary-container);
            color: var(--color-on-secondary);
        }

        .status-indicator.pending {
            background: var(--color-surface-container-high);
            color: var(--color-outline);
        }

        .notification-banner {
            background: linear-gradient(135deg, var(--color-tertiary-fixed) 0%, var(--color-secondary-fixed) 100%);
            border-left: 4px solid var(--color-secondary);
            padding: 1rem 1.5rem;
            border-radius: var(--radius-lg);
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .notification-banner .material-symbols-outlined {
            font-size: 2rem;
            color: var(--color-secondary);
        }

        .notification-content h3 {
            margin: 0 0 0.25rem 0;
            font-size: 1rem;
            font-weight: 700;
            color: var(--color-on-secondary-fixed);
        }

        .notification-content p {
            margin: 0;
            font-size: 0.8125rem;
            color: var(--color-on-secondary-fixed-variant);
        }

        .award-action-card {
            background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
            border: 2px solid #4CAF50;
            border-radius: var(--radius-lg);
            padding: 1.5rem;
            margin-top: 1.5rem;
        }

        .leaderboard-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
            padding: 0.125rem 0.5rem;
            border-radius: var(--radius-full);
            font-size: 0.75rem;
            font-weight: 700;
        }

        .leaderboard-badge.first {
            background: #FFD700;
            color: #5D4037;
        }

        .leaderboard-badge.second {
            background: #C0C0C0;
            color: #424242;
        }

        .leaderboard-badge.third {
            background: #CD7F32;
            color: #FFFFFF;
        }
    </style>
</head>
<body>

<div class="pg-layout">
    <jsp:include page="/WEB-INF/jsp/includes/sidebar.jsp" />

    <main class="pg-main">
        <jsp:include page="/WEB-INF/jsp/includes/topbar.jsp" />

        <div class="pg-content">
            <!-- Success/Error Messages -->
            <c:if test="${not empty param.success}">
                <div class="pg-alert pg-alert--success" role="alert">
                    <span class="material-symbols-outlined">check_circle</span>
                    <span>${param.success}</span>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="pg-alert pg-alert--error" role="alert">
                    <span class="material-symbols-outlined">error</span>
                    <span>${error}</span>
                </div>
            </c:if>

            <!-- Notification Banner for Procurement Officer if all committee members done -->
            <c:if test="${showOfficerNotification && userRole eq 'PROCUREMENT_OFFICER'}">
                <div class="notification-banner">
                    <span class="material-symbols-outlined">campaign</span>
                    <div class="notification-content">
                        <h3>All Committee Members Have Completed Their Evaluations</h3>
                        <p>You are the last evaluator. After you submit your scores, the system will automatically calculate final rankings and transition this tender to EVALUATED status.</p>
                    </div>
                </div>
            </c:if>

            <!-- Page Header -->
            <div class="pg-page-header">
                <div class="pg-page-header__kicker">
                    <span class="material-symbols-outlined" style="font-size: 0.875rem;">assignment_turned_in</span>
                    Tender ${tender.reference_number}
                </div>
                <h1 class="pg-page-header__title">Evaluate Bid #${bid.bid_id}</h1>
                <p class="pg-page-header__subtitle">
                    ${tender.title} — Supplier: ${supplier.business_name}
                </p>
                <div class="pg-page-header__actions">
                    <a href="${pageContext.request.contextPath}/app/evaluations/panel?tenderId=${tender.tender_id}"
                       class="btn btn-ghost btn-sm">
                        <span class="material-symbols-outlined" style="font-size: 1rem;">arrow_back</span>
                        Back to All Bids
                    </a>
                </div>
            </div>

            <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1.5rem; align-items: start;">

                <!-- Left Column: Bid Details & Evaluation Form -->
                <div style="display: flex; flex-direction: column; gap: 1.5rem;">

                    <!-- Bid Information Card -->
                    <div class="pg-card">
                        <div class="pg-card__header">
                            <h2 class="text-headline-sm">Bid Details</h2>
                        </div>
                        <div class="pg-card__body">
                            <div class="bid-detail-row">
                                <span class="bid-detail-label">Supplier</span>
                                <span class="bid-detail-value">${supplier.business_name}</span>
                            </div>
                            <div class="bid-detail-row">
                                <span class="bid-detail-label">Registration Number</span>
                                <span class="bid-detail-value">${supplier.reg_number}</span>
                            </div>
                            <div class="bid-detail-row">
                                <span class="bid-detail-label">Bid Amount</span>
                                <span class="bid-detail-value">
                                    LSL <fmt:formatNumber value="${bid.price}" type="number" groupingUsed="true" maxFractionDigits="2"/>
                                </span>
                            </div>
                            <div class="bid-detail-row">
                                <span class="bid-detail-label">Delivery Timeline</span>
                                <span class="bid-detail-value">${bid.delivery_days} days</span>
                            </div>
                            <div class="bid-detail-row">
                                <span class="bid-detail-label">Submitted</span>
                                <span class="bid-detail-value">
                                    <fmt:formatDate value="${bid.submitted_at}" pattern="dd MMM yyyy, hh:mm a"/>
                                </span>
                            </div>
                            <div class="bid-detail-row">
                                <span class="bid-detail-label">Compliance Statement</span>
                                <span class="bid-detail-value" style="max-width: 400px; text-align: right;">
                                    ${bid.compliance_statement}
                                </span>
                            </div>
                            <c:if test="${not empty bid.document_file_path}">
                                <div class="bid-detail-row">
                                    <span class="bid-detail-label">Supporting Document</span>
                                    <a href="${pageContext.request.contextPath}/${bid.document_file_path}"
                                       class="btn btn-tonal btn-sm" style="margin-left: auto;">
                                        <span class="material-symbols-outlined" style="font-size: 1rem;">download</span>
                                        Download
                                    </a>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- Scoring Metrics Grid -->
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem;">

                        <!-- Price Score - Auto-calculated -->
                        <div class="eval-metric-card">
                            <div class="eval-metric-label">
                                <span>Price Score</span>
                                <span class="weighted-badge">Weight: 40%</span>
                            </div>
                            <div class="eval-metric-value">
                                <fmt:formatNumber value="${priceScore}" maxFractionDigits="2"/>
                                <span style="font-size: 1rem; font-weight: 400; color: var(--color-outline);">/100</span>
                            </div>
                            <div class="eval-metric-detail">
                                <div class="auto-calculated">
                                    <span class="material-symbols-outlined" style="font-size: 0.75rem;">calculate</span>
                                    Auto-calculated
                                </div>
                                <div style="margin-top: 0.5rem;">
                                    Formula: (Lowest Bid ÷ This Bid) × 100
                                </div>
                            </div>
                        </div>

                        <!-- Delivery Timeline Score - Auto-calculated -->
                        <div class="eval-metric-card">
                            <div class="eval-metric-label">
                                <span>Delivery Score</span>
                                <span class="weighted-badge">Weight: 25%</span>
                            </div>
                            <div class="eval-metric-value">
                                <fmt:formatNumber value="${deliveryScore}" maxFractionDigits="2"/>
                                <span style="font-size: 1rem; font-weight: 400; color: var(--color-outline);">/100</span>
                            </div>
                            <div class="eval-metric-detail">
                                <div class="auto-calculated">
                                    <span class="material-symbols-outlined" style="font-size: 0.75rem;">calculate</span>
                                    Auto-calculated
                                </div>
                                <div style="margin-top: 0.5rem;">
                                    Formula: (Shortest Timeline ÷ This Timeline) × 100
                                </div>
                            </div>
                        </div>

                    </div>

                    <!-- Technical Compliance Scoring Form -->
                    <div class="pg-card ${hasEvaluated ? 'evaluation-locked' : ''}">
                        <div class="pg-card__header">
                            <h2 class="text-headline-sm">Technical Compliance Score</h2>
                            <span class="weighted-badge">Weight: 35%</span>
                        </div>
                        <div class="pg-card__body">
                            <c:choose>
                                <c:when test="${hasEvaluated}">
                                    <!-- Display existing score -->
                                    <div style="text-align: center; padding: 2rem 0;">
                                        <div class="eval-metric-label">Your Technical Score</div>
                                        <div class="eval-metric-value" style="color: var(--color-secondary);">
                                            <fmt:formatNumber value="${myTechnicalScore}" maxFractionDigits="2"/>
                                            <span style="font-size: 1rem; font-weight: 400; color: var(--color-outline);">/100</span>
                                        </div>
                                        <div style="margin-top: 1rem;">
                                            <span class="badge badge--open">
                                                <span class="material-symbols-outlined" style="font-size: 0.75rem;">check_circle</span>
                                                Already Evaluated
                                            </span>
                                        </div>
                                        <div class="eval-metric-detail" style="margin-top: 1rem;">
                                            Evaluated on <fmt:formatDate value="${myEvaluationDate}" pattern="dd MMM yyyy, hh:mm a"/>
                                        </div>
                                        <div class="eval-metric-detail" style="margin-top: 0.5rem;">
                                            Your Weighted Total: <strong><fmt:formatNumber value="${myWeightedTotal}" maxFractionDigits="2"/>%</strong>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <!-- Evaluation Form -->
                                    <form action="${pageContext.request.contextPath}/app/evaluations/submit-score"
                                          method="POST"
                                          data-validate="true"
                                          onsubmit="return confirmEvaluation()">

                                        <input type="hidden" name="bidId" value="${bid.bid_id}"/>
                                        <input type="hidden" name="tenderId" value="${tender.tender_id}"/>
                                        <input type="hidden" name="priceScore" value="${priceScore}"/>
                                        <input type="hidden" name="deliveryScore" value="${deliveryScore}"/>
                                        <input type="hidden" name="bidAmount" value="${bid.price}"/>
                                        <input type="hidden" name="deliveryDays" value="${bid.delivery_days}"/>

                                        <div style="margin-bottom: 1.5rem;">
                                            <label for="technicalScore" class="form-label" style="display: block; margin-bottom: 0.5rem;">
                                                Enter Technical Compliance Score (0-100)
                                            </label>
                                            <input type="number"
                                                   id="technicalScore"
                                                   name="technicalScore"
                                                   class="score-input-large"
                                                   min="0"
                                                   max="100"
                                                   step="0.01"
                                                   placeholder="0.00"
                                                   required
                                                   oninput="calculateWeightedTotal(this.value)"/>
                                            <div style="font-size: 0.8125rem; color: var(--color-on-surface-variant); margin-top: 0.5rem;">
                                                Score based on: Past performance, safety certifications, logistics feasibility, and technical capacity
                                            </div>
                                        </div>

                                        <!-- Live Weighted Total Preview -->
                                        <div class="eval-metric-card" style="background: var(--color-primary-fixed); margin-bottom: 1.5rem;">
                                            <div class="eval-metric-label" style="color: var(--color-on-primary-fixed-variant);">
                                                Weighted Total (Preview)
                                            </div>
                                            <div class="eval-metric-value" id="weightedTotalPreview" style="color: var(--color-primary);">
                                                0.00
                                                <span style="font-size: 1rem; font-weight: 400; color: var(--color-outline);">%</span>
                                            </div>
                                            <div class="eval-metric-detail" style="color: var(--color-on-primary-fixed-variant);">
                                                Formula: (Price × 0.40) + (Technical × 0.35) + (Delivery × 0.25)
                                            </div>
                                        </div>

                                        <!-- Conflict of Interest Declaration -->
                                        <div style="background: var(--color-surface-container-high); padding: 1rem; border-radius: var(--radius-md); margin-bottom: 1.5rem;">
                                            <label style="display: flex; align-items: start; gap: 0.75rem; cursor: pointer;">
                                                <input type="checkbox"
                                                       name="conflictDeclaration"
                                                       required
                                                       style="margin-top: 0.25rem; width: 1.125rem; height: 1.125rem; cursor: pointer;"/>
                                                <span style="flex: 1; font-size: 0.8125rem; color: var(--color-on-surface);">
                                                    <strong>Conflict of Interest Declaration:</strong> I declare that I have no financial, personal, or professional relationship with this supplier that would compromise my ability to evaluate this bid objectively and fairly. This declaration is mandatory as per Procurement Directive 14.b.
                                                </span>
                                            </label>
                                        </div>

                                        <!-- Submit Button -->
                                        <button type="submit" class="btn btn-primary btn-full">
                                            <span class="material-symbols-outlined">save</span>
                                            Submit Evaluation Score
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Award Action Section (only visible when tender is EVALUATED and user is PROCUREMENT_OFFICER) -->
                    <c:if test="${tender.status eq 'EVALUATED' && userRole eq 'PROCUREMENT_OFFICER'}">
                        <div class="award-action-card">
                            <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                                <span class="material-symbols-outlined" style="font-size: 2rem; color: #4CAF50;">emoji_events</span>
                                <div>
                                    <h3 style="margin: 0; color: #2E7D32;">Ready for Award</h3>
                                    <p style="margin: 0.25rem 0 0; font-size: 0.8125rem; color: #558B2F;">
                                        All evaluations are complete. You can now award this tender.
                                    </p>
                                </div>
                            </div>
                            <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
                                <a href="${pageContext.request.contextPath}/app/evaluations/leaderboard?tenderId=${tender.tender_id}"
                                   class="btn btn-secondary">
                                    <span class="material-symbols-outlined">leaderboard</span>
                                    View Leaderboard
                                </a>
                                <a href="${pageContext.request.contextPath}/app/awards/create?tenderId=${tender.tender_id}&bidId=${bid.bid_id}"
                                   class="btn btn-primary">
                                    <span class="material-symbols-outlined">verified</span>
                                    Award This Bid
                                </a>
                            </div>
                        </div>
                    </c:if>
                </div>

                <!-- Right Column: Evaluation Progress & Context -->
                <div style="display: flex; flex-direction: column; gap: 1.5rem; position: sticky; top: calc(var(--topbar-height) + 1.5rem);">

                    <!-- Evaluation Progress -->
                    <div class="pg-card">
                        <div class="pg-card__header">
                            <h3 class="text-title">Evaluation Progress</h3>
                        </div>
                        <div class="pg-card__body">
                            <div class="evaluator-status-list">
                                <c:forEach var="evaluator" items="${evaluatorsList}">
                                    <div class="evaluator-status-item">
                                        <span class="evaluator-name">${evaluator.name}</span>
                                        <c:choose>
                                            <c:when test="${evaluator.hasEvaluated}">
                                                <span class="status-indicator completed">
                                                    <span class="material-symbols-outlined" style="font-size: 0.875rem;">check_circle</span>
                                                    Completed
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-indicator pending">
                                                    <span class="material-symbols-outlined" style="font-size: 0.875rem;">pending</span>
                                                    Pending
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:forEach>
                            </div>

                            <div style="margin-top: 1rem; padding-top: 1rem; border-top: 1px solid var(--color-outline-variant);">
                                <div style="display: flex; justify-content: space-between; font-size: 0.8125rem;">
                                    <span style="color: var(--color-on-surface-variant);">Progress</span>
                                    <span style="font-weight: 700; color: var(--color-primary);">
                                        ${evaluationsCompleted} / ${totalEvaluators} Evaluators
                                    </span>
                                </div>
                                <div style="background: var(--color-surface-container-high); height: 6px; border-radius: 3px; margin-top: 0.5rem; overflow: hidden;">
                                    <div style="background: var(--color-secondary); height: 100%; width: ${totalEvaluators > 0 ? (evaluationsCompleted / totalEvaluators) * 100 : 0}%; transition: width 0.3s ease;"></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Quick Links based on tender status -->
                    <div class="pg-card">
                        <div class="pg-card__header">
                            <h3 class="text-title">Quick Actions</h3>
                        </div>
                        <div class="pg-card__body" style="display: flex; flex-direction: column; gap: 0.5rem; padding-top: 0;">

                            <!-- Tender Notice Link -->
                            <c:if test="${not empty tender.notice_file_path}">
                                <a href="${pageContext.request.contextPath}/${bid.document_file_path}"
                                   class="btn btn-ghost btn-full" style="justify-content: flex-start;">
                                    <span class="material-symbols-outlined">description</span>
                                    View Tender Notice
                                </a>
                            </c:if>

                            <!-- Back to Evaluation Panel -->
                            <a href="${pageContext.request.contextPath}/app/evaluations/panel?tenderId=${tender.tender_id}"
                               class="btn btn-ghost btn-full" style="justify-content: flex-start;">
                                <span class="material-symbols-outlined">list_alt</span>
                                Evaluation Panel
                            </a>

                            <!-- Award Registry -->
                            <a href="${pageContext.request.contextPath}/app/awards/"
                               class="btn btn-ghost btn-full" style="justify-content: flex-start;">
                                <span class="material-symbols-outlined">newspaper</span>
                                Award Registry
                            </a>
                        </div>
                    </div>

                    <!-- Score Display (if already evaluated and all evaluations complete) -->
                    <c:if test="${hasEvaluated && tender.status eq 'EVALUATED'}">
                        <div class="pg-card">
                            <div class="pg-card__header">
                                <h3 class="text-title">Final Scores</h3>
                            </div>
                            <div class="pg-card__body">
                                <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                                    <div style="display: flex; justify-content: space-between;">
                                        <span style="color: var(--color-on-surface-variant);">Average Price Score</span>
                                        <span style="font-weight: 700;">${avgPriceScore}</span>
                                    </div>
                                    <div style="display: flex; justify-content: space-between;">
                                        <span style="color: var(--color-on-surface-variant);">Average Technical Score</span>
                                        <span style="font-weight: 700;">${avgTechnicalScore}</span>
                                    </div>
                                    <div style="display: flex; justify-content: space-between;">
                                        <span style="color: var(--color-on-surface-variant);">Average Delivery Score</span>
                                        <span style="font-weight: 700;">${avgDeliveryScore}</span>
                                    </div>
                                    <div style="border-top: 1px solid var(--color-outline-variant); padding-top: 0.75rem; display: flex; justify-content: space-between;">
                                        <span style="font-weight: 700; color: var(--color-primary);">Final Score</span>
                                        <span style="font-weight: 700; color: var(--color-primary); font-size: 1.25rem;">
                                            <fmt:formatNumber value="${finalScore}" maxFractionDigits="2"/>%
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <!-- Evaluation Criteria -->
                    <div class="pg-card">
                        <div class="pg-card__header">
                            <h3 class="text-title">Evaluation Criteria</h3>
                        </div>
                        <div class="pg-card__body" style="font-size: 0.8125rem; color: var(--color-on-surface-variant); display: flex; flex-direction: column; gap: 0.75rem;">
                            <div>
                                <div style="font-weight: 700; color: var(--color-primary); margin-bottom: 0.25rem;">
                                    Price Score (40%)
                                </div>
                                <div>Auto-calculated based on competitive pricing. Lower bids receive higher scores.</div>
                            </div>
                            <div>
                                <div style="font-weight: 700; color: var(--color-primary); margin-bottom: 0.25rem;">
                                    Technical Compliance (35%)
                                </div>
                                <div>Manual assessment of past performance, certifications, and logistics capacity.</div>
                            </div>
                            <div>
                                <div style="font-weight: 700; color: var(--color-primary); margin-bottom: 0.25rem;">
                                    Delivery Timeline (25%)
                                </div>
                                <div>Auto-calculated based on proposed delivery speed. Faster deliveries score higher.</div>
                            </div>
                        </div>
                    </div>

                    <!-- Important Notice -->
                    <div class="pg-pullquote" style="font-size: 0.8125rem;">
                        Evaluators cannot see other evaluators' individual scores until they have submitted their own scores for this bid.
                        <footer style="margin-top: 0.5rem; font-family: var(--font-body); font-style: normal; font-size: 0.6875rem; font-weight: 700; letter-spacing: 0.06em; text-transform: uppercase; color: var(--color-outline);">
                            — Assessment Requirement
                        </footer>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />
    </main>
</div>

<script>
    // Calculate weighted total in real-time as user enters technical score
    function calculateWeightedTotal(technicalScore) {
        const priceScore = parseFloat('${priceScore}') || 0;
        const deliveryScore = parseFloat('${deliveryScore}') || 0;
        const technical = parseFloat(technicalScore) || 0;

        // Weighted formula: (Price × 0.40) + (Technical × 0.35) + (Delivery × 0.25)
        const weightedTotal = (priceScore * 0.40) + (technical * 0.35) + (deliveryScore * 0.25);

        document.getElementById('weightedTotalPreview').innerHTML =
            weightedTotal.toFixed(2) + '<span style="font-size: 1rem; font-weight: 400; color: var(--color-outline);">%</span>';
    }

    // Confirmation dialog before submission
    function confirmEvaluation() {
        const technicalScore = document.getElementById('technicalScore').value;
        const conflictCheck = document.querySelector('input[name="conflictDeclaration"]').checked;

        if (!conflictCheck) {
            alert('You must declare no conflict of interest before submitting your evaluation.');
            return false;
        }

        const message = 'Are you sure you want to submit this evaluation?\n\n' +
            'Technical Score: ' + technicalScore + '/100\n\n' +
            'Once submitted, you cannot change your scores.';

        return confirm(message);
    }

    // Auto-focus on technical score input if not yet evaluated
    <c:if test="${!hasEvaluated}">
        window.addEventListener('DOMContentLoaded', function() {
        document.getElementById('technicalScore').focus();
    });
    </c:if>
</script>

</body>
</html>