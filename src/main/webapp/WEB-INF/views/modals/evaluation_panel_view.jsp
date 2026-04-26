<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en"/>

<c:set var="pageTitle" value="Bid Evaluation" scope="request"/>
<c:set var="pageSection" value="Bid Evaluation" scope="request"/>
<c:set var="activePage" value="evaluations" scope="request"/>

<!DOCTYPE html>
<html lang="en">
<head>
  <jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
  <style>
    /* Tender List Styles */
    .tender-card {
      background: var(--color-surface-container-lowest);
      border-radius: var(--radius-card);
      box-shadow: var(--shadow-card);
      padding: 1.5rem;
      cursor: pointer;
      transition: all 0.2s ease;
      border-left: 4px solid transparent;
      margin-bottom: 1rem;
    }

    .tender-card:hover {
      box-shadow: var(--shadow-float);
      transform: translateY(-2px);
    }

    .tender-card.status-under-evaluation {
      border-left-color: var(--color-primary);
    }

    .tender-card.status-closed {
      border-left-color: var(--color-tertiary);
    }

    .tender-card.status-evaluated {
      border-left-color: var(--color-secondary);
    }

    .tender-card.status-awarded {
      border-left-color: #4CAF50;
    }

    .tender-card-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 1rem;
    }

    .tender-reference {
      font-family: var(--font-label);
      font-size: 0.6875rem;
      font-weight: 700;
      letter-spacing: 0.1em;
      text-transform: uppercase;
      color: var(--color-outline);
    }

    .tender-title {
      font-family: var(--font-headline);
      font-size: 1.25rem;
      font-weight: 600;
      color: var(--color-on-surface);
      margin: 0.25rem 0;
    }

    .tender-meta {
      display: flex;
      gap: 1.5rem;
      flex-wrap: wrap;
      margin-top: 0.75rem;
    }

    .tender-meta-item {
      display: flex;
      align-items: center;
      gap: 0.375rem;
      font-size: 0.8125rem;
      color: var(--color-on-surface-variant);
    }

    .tender-meta-item .material-symbols-outlined {
      font-size: 1rem;
      color: var(--color-outline);
    }

    .status-badge {
      display: inline-flex;
      align-items: center;
      gap: 0.25rem;
      padding: 0.375rem 0.75rem;
      border-radius: var(--radius-full);
      font-size: 0.6875rem;
      font-weight: 700;
      letter-spacing: 0.06em;
      text-transform: uppercase;
    }

    .status-under-evaluation {
      background: var(--color-primary-fixed);
      color: var(--color-primary);
    }

    .status-closed {
      background: var(--color-tertiary-fixed);
      color: var(--color-on-tertiary-fixed-variant);
    }

    .status-evaluated {
      background: var(--color-secondary-fixed);
      color: var(--color-on-secondary-fixed-variant);
    }

    .status-awarded {
      background: #E8F5E9;
      color: #2E7D32;
    }

    .status-section {
      margin-bottom: 2rem;
    }

    .status-section-title {
      font-family: var(--font-headline);
      font-size: 1.25rem;
      font-weight: 600;
      color: var(--color-on-surface);
      margin-bottom: 1rem;
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .empty-state {
      text-align: center;
      padding: 3rem 2rem;
      color: var(--color-outline);
      background: var(--color-surface-container-lowest);
      border-radius: var(--radius-card);
    }

    .empty-state .material-symbols-outlined {
      font-size: 3rem;
      margin-bottom: 1rem;
      color: var(--color-outline-variant);
    }

    /* Bid Detail Styles (reused from previous) */
    .tab-nav {
      display: flex;
      gap: 0.25rem;
      background: var(--color-surface-container-high);
      border-radius: var(--radius-lg);
      padding: 0.25rem;
      margin-bottom: 1.5rem;
    }

    .tab-btn {
      flex: 1;
      padding: 0.75rem 1.5rem;
      border-radius: var(--radius-md);
      font-size: 0.875rem;
      font-weight: 600;
      border: none;
      cursor: pointer;
      transition: all 0.2s;
      background: transparent;
      color: var(--color-on-surface-variant);
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
    }

    .tab-btn.active {
      background: var(--color-surface-container-lowest);
      color: var(--color-primary);
      box-shadow: var(--shadow-card);
    }

    .tab-btn .badge-count {
      background: var(--color-primary);
      color: var(--color-on-primary);
      font-size: 0.75rem;
      padding: 0.125rem 0.5rem;
      border-radius: var(--radius-full);
    }

    .bid-list-card {
      background: var(--color-surface-container-lowest);
      border-radius: var(--radius-card);
      box-shadow: var(--shadow-card);
      overflow: hidden;
      margin-bottom: 1rem;
      transition: all 0.2s;
    }

    .bid-list-card:hover {
      box-shadow: var(--shadow-float);
    }

    .bid-list-header {
      padding: 1.25rem 1.5rem;
      border-bottom: 1px solid var(--color-surface-container-high);
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .bid-list-body {
      padding: 1.25rem 1.5rem;
    }

    .bid-info-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
      margin-bottom: 1rem;
    }

    .bid-info-item {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .bid-info-label {
      font-size: 0.6875rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.08em;
      color: var(--color-outline);
    }

    .bid-info-value {
      font-size: 0.9375rem;
      font-weight: 600;
      color: var(--color-on-surface);
    }

    .bid-actions {
      display: flex;
      gap: 0.75rem;
      padding-top: 1rem;
      border-top: 1px solid var(--color-surface-container-high);
    }

    /* Leaderboard Styles */
    .leaderboard-table {
      width: 100%;
      border-collapse: collapse;
    }

    .leaderboard-table th {
      padding: 0.75rem 1rem;
      font-family: var(--font-label);
      font-size: 0.6875rem;
      font-weight: 700;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: var(--color-outline);
      text-align: left;
      background: var(--color-surface-container-low);
      border-bottom: 2px solid var(--color-outline-variant);
    }

    .leaderboard-table td {
      padding: 1rem;
      border-bottom: 1px solid var(--color-surface-container-high);
      vertical-align: middle;
    }

    .leaderboard-table tbody tr:hover {
      background: var(--color-surface-container-low);
    }

    .rank-cell {
      font-family: var(--font-headline);
      font-size: 1.5rem;
      font-weight: 700;
      text-align: center;
      width: 60px;
    }

    .rank-1 { color: #FFD700; }
    .rank-2 { color: #C0C0C0; }
    .rank-3 { color: #CD7F32; }

    .score-bar {
      height: 8px;
      background: var(--color-surface-container-high);
      border-radius: var(--radius-full);
      overflow: hidden;
      margin-top: 0.25rem;
    }

    .score-bar-fill {
      height: 100%;
      background: linear-gradient(90deg, var(--color-primary), var(--color-primary-container));
      border-radius: var(--radius-full);
      transition: width 0.6s ease;
    }

    .award-btn {
      background: linear-gradient(135deg, #2E7D32, #4CAF50);
      color: white;
      padding: 0.5rem 1rem;
      border-radius: var(--radius-lg);
      font-weight: 600;
      font-size: 0.8125rem;
      border: none;
      cursor: pointer;
      transition: all 0.2s;
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      text-decoration: none;
    }

    .award-btn:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(46, 125, 50, 0.3);
    }

    .progress-indicator {
      background: var(--color-primary-fixed);
      padding: 1rem 1.5rem;
      border-radius: var(--radius-lg);
      margin-bottom: 1.5rem;
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

      <!-- Page Header -->
      <div class="pg-page-header">
        <div class="pg-page-header__kicker">
          <span class="material-symbols-outlined" style="font-size: 0.875rem;">gavel</span>
          Bid Evaluation
        </div>
        <h1 class="pg-page-header__title">
          <c:choose>
            <c:when test="${viewMode eq 'bidDetail'}">
              ${tender.title}
            </c:when>
            <c:otherwise>
              Evaluation Panel
            </c:otherwise>
          </c:choose>
        </h1>
        <c:if test="${viewMode eq 'bidDetail'}">
          <p class="pg-page-header__subtitle">
            ${tender.reference_number} —
            <span class="badge badge--${tender.status eq 'UNDER_EVALUATION' ? 'evaluation' : tender.status eq 'CLOSED' ? 'closed' : 'evaluated'}">
                            ${tender.status}
                        </span>
          </p>
        </c:if>
        <c:if test="${viewMode eq 'bidDetail'}">
          <div class="pg-page-header__actions">
            <a href="${pageContext.request.contextPath}/app/evaluations/panel"
               class="btn btn-ghost btn-sm">
              <span class="material-symbols-outlined" style="font-size: 1rem;">arrow_back</span>
              Back to All Tenders
            </a>
          </div>
        </c:if>
      </div>

      <c:choose>
        <%-- TENDER LIST VIEW --%>
        <c:when test="${viewMode eq 'tenderList'}">

          <%-- Active Evaluations --%>
          <c:if test="${not empty underEvaluationTenders}">
            <div class="status-section">
              <h2 class="status-section-title">
                <span class="material-symbols-outlined" style="color: var(--color-primary);">hourglass_top</span>
                Under Evaluation
                <span class="badge-count">${underEvaluationTenders.size()}</span>
              </h2>
              <c:forEach var="tender" items="${underEvaluationTenders}">
                <a href="${pageContext.request.contextPath}/app/evaluations/panel?tenderId=${tender.tender_id}"
                   style="text-decoration: none;">
                  <div class="tender-card status-under-evaluation">
                    <div class="tender-card-header">
                      <div>
                        <div class="tender-reference">${tender.reference_number}</div>
                        <h3 class="tender-title">${tender.title}</h3>
                      </div>
                      <span class="status-badge status-under-evaluation">
                                                <span class="material-symbols-outlined" style="font-size: 0.875rem;">rate_review</span>
                                                Under Evaluation
                                            </span>
                    </div>
                    <div class="tender-meta">
                                            <span class="tender-meta-item">
                                                <span class="material-symbols-outlined">category</span>
                                                ${tender.category}
                                            </span>
                      <span class="tender-meta-item">
                                                <span class="material-symbols-outlined">event</span>
                                                Published: <fmt:formatDate value="${tender.publish_datetime}" pattern="dd MMM yyyy"/>
                                            </span>
                      <span class="tender-meta-item">
                                                <span class="material-symbols-outlined">attach_money</span>
                                                LSL <fmt:formatNumber value="${tender.estimated_value}" type="number" groupingUsed="true" maxFractionDigits="0"/>
                                            </span>
                    </div>
                  </div>
                </a>
              </c:forEach>
            </div>
          </c:if>

          <%-- Closed Tenders (Ready for Evaluation) --%>
          <c:if test="${not empty closedTenders}">
            <div class="status-section">
              <h2 class="status-section-title">
                <span class="material-symbols-outlined" style="color: var(--color-tertiary);">lock</span>
                Closed — Ready for Evaluation
                <span class="badge-count">${closedTenders.size()}</span>
              </h2>
              <c:forEach var="tender" items="${closedTenders}">
                <a href="${pageContext.request.contextPath}/app/evaluations/panel?tenderId=${tender.tender_id}"
                   style="text-decoration: none;">
                  <div class="tender-card status-closed">
                    <div class="tender-card-header">
                      <div>
                        <div class="tender-reference">${tender.reference_number}</div>
                        <h3 class="tender-title">${tender.title}</h3>
                      </div>
                      <span class="status-badge status-closed">
                                                <span class="material-symbols-outlined" style="font-size: 0.875rem;">how_to_vote</span>
                                                Ready to Evaluate
                                            </span>
                    </div>
                    <div class="tender-meta">
                                            <span class="tender-meta-item">
                                                <span class="material-symbols-outlined">category</span>
                                                ${tender.category}
                                            </span>
                      <span class="tender-meta-item">
                                                <span class="material-symbols-outlined">event_busy</span>
                                                Closed: <fmt:formatDate value="${tender.expiry_datetime}" pattern="dd MMM yyyy"/>
                                            </span>
                      <span class="tender-meta-item">
                                                <span class="material-symbols-outlined">attach_money</span>
                                                LSL <fmt:formatNumber value="${tender.estimated_value}" type="number" groupingUsed="true" maxFractionDigits="0"/>
                                            </span>
                    </div>
                  </div>
                </a>
              </c:forEach>
            </div>
          </c:if>

          <%-- Evaluated Tenders --%>
          <c:if test="${not empty evaluatedTenders}">
            <div class="status-section">
              <h2 class="status-section-title">
                <span class="material-symbols-outlined" style="color: var(--color-secondary);">task_alt</span>
                Evaluated
                <span class="badge-count">${evaluatedTenders.size()}</span>
              </h2>
              <c:forEach var="tender" items="${evaluatedTenders}">
                <a href="${pageContext.request.contextPath}/app/evaluations/panel?tenderId=${tender.tender_id}"
                   style="text-decoration: none;">
                  <div class="tender-card status-evaluated">
                    <div class="tender-card-header">
                      <div>
                        <div class="tender-reference">${tender.reference_number}</div>
                        <h3 class="tender-title">${tender.title}</h3>
                      </div>
                      <span class="status-badge status-evaluated">
                                                <span class="material-symbols-outlined" style="font-size: 0.875rem;">check_circle</span>
                                                Evaluated
                                            </span>
                    </div>
                    <div class="tender-meta">
                                            <span class="tender-meta-item">
                                                <span class="material-symbols-outlined">category</span>
                                                ${tender.category}
                                            </span>
                      <span class="tender-meta-item">
                                                <span class="material-symbols-outlined">attach_money</span>
                                                LSL <fmt:formatNumber value="${tender.estimated_value}" type="number" groupingUsed="true" maxFractionDigits="0"/>
                                            </span>
                    </div>
                  </div>
                </a>
              </c:forEach>
            </div>
          </c:if>

          <%-- Empty State --%>
          <c:if test="${empty underEvaluationTenders && empty closedTenders && empty evaluatedTenders}">
            <div class="empty-state">
              <span class="material-symbols-outlined">inbox</span>
              <h2 class="text-headline-sm" style="color: var(--color-on-surface);">No Tenders Available</h2>
              <p style="margin-top: 0.5rem;">
                There are currently no tenders requiring evaluation.
              </p>
            </div>
          </c:if>

        </c:when>

        <%-- BID DETAIL VIEW --%>
        <c:otherwise>

          <%-- Evaluation Progress --%>
          <c:if test="${not empty evaluationProgress}">
            <div class="progress-indicator">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                                    <span style="font-weight: 600; color: var(--color-primary);">
                                        Evaluation Progress
                                    </span>
                  <span style="font-size: 0.875rem; color: var(--color-on-surface-variant); margin-left: 0.5rem;">
                                        ${evaluationProgress.myEvaluatedCount} / ${evaluationProgress.totalBids} bids evaluated by you
                                    </span>
                </div>
                <span style="font-weight: 700; color: var(--color-primary);">
                                    ${evaluationProgress.completedEvaluations} / ${evaluationProgress.totalEvaluationsNeeded} total evaluations
                                </span>
              </div>
              <div class="score-bar" style="margin-top: 0.75rem;">
                <div class="score-bar-fill"
                     style="width: ${evaluationProgress.progressPercentage}%;"></div>
              </div>
            </div>
          </c:if>

          <!-- Tab Navigation -->
          <div class="tab-nav">
            <button class="tab-btn active" onclick="switchTab('pending')">
              <span class="material-symbols-outlined">pending_actions</span>
              Pending
              <c:if test="${not empty unevaluatedBids}">
                <span class="badge-count">${unevaluatedBids.size()}</span>
              </c:if>
            </button>
            <button class="tab-btn" onclick="switchTab('leaderboard')">
              <span class="material-symbols-outlined">leaderboard</span>
              Leaderboard
            </button>
            <c:if test="${tender.status eq 'EVALUATED' || tender.status eq 'AWARDED'}">
              <button class="tab-btn" onclick="switchTab('evaluated')">
                <span class="material-symbols-outlined">check_circle</span>
                All Evaluations
              </button>
            </c:if>
          </div>

          <!-- Tab Content: Pending Evaluations -->
          <div id="tab-pending" class="tab-content">
            <c:choose>
              <c:when test="${empty unevaluatedBids}">
                <div class="empty-state">
                  <span class="material-symbols-outlined">task_alt</span>
                  <h2 class="text-headline-sm" style="color: var(--color-on-surface);">
                    All Bids Evaluated
                  </h2>
                  <p style="margin-top: 0.5rem;">
                    You have evaluated all bids for this tender.
                  </p>
                </div>
              </c:when>
              <c:otherwise>
                <c:forEach var="bid" items="${unevaluatedBids}">
                  <div class="bid-list-card">
                    <div class="bid-list-header">
                      <div>
                        <span class="text-title">${bid.supplierName}</span>
                        <span class="status-badge status-closed" style="margin-left: 0.75rem;">
                                                    <span class="material-symbols-outlined" style="font-size: 0.875rem;">schedule</span>
                                                    Pending Your Evaluation
                                                </span>
                      </div>
                      <span class="text-body" style="color: var(--color-outline);">
                                                Bid #${bid.bidId}
                                            </span>
                    </div>
                    <div class="bid-list-body">
                      <div class="bid-info-grid">
                        <div class="bid-info-item">
                          <span class="bid-info-label">Bid Amount</span>
                          <span class="bid-info-value">
                                                        LSL <fmt:formatNumber value="${bid.bidAmount}" type="number" groupingUsed="true" maxFractionDigits="2"/>
                                                    </span>
                        </div>
                        <div class="bid-info-item">
                          <span class="bid-info-label">Delivery Timeline</span>
                          <span class="bid-info-value">${bid.deliveryDays} days</span>
                        </div>
                        <div class="bid-info-item">
                          <span class="bid-info-label">Submitted</span>
                          <span class="bid-info-value">
                                                        <fmt:formatDate value="${bid.submittedAt}" pattern="dd MMM yyyy"/>
                                                    </span>
                        </div>
                        <div class="bid-info-item">
                          <span class="bid-info-label">Evaluators Completed</span>
                          <span class="bid-info-value">
                                                        ${bid.evaluationsCompleted} / ${bid.totalEvaluators}
                                                    </span>
                        </div>
                      </div>
                      <div class="bid-actions">
                        <a href="${pageContext.request.contextPath}/app/evaluations/evaluate?bidId=${bid.bidId}&tenderId=${tender.tender_id}"
                           class="btn btn-primary">
                          <span class="material-symbols-outlined">rate_review</span>
                          Evaluate This Bid
                        </a>
                      </div>
                    </div>
                  </div>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>

          <!-- Tab Content: Leaderboard -->
          <div id="tab-leaderboard" class="tab-content" style="display: none;">
            <c:choose>
              <c:when test="${empty leaderboard}">
                <div class="empty-state">
                  <span class="material-symbols-outlined">leaderboard</span>
                  <h2 class="text-headline-sm" style="color: var(--color-on-surface);">
                    No Rankings Available
                  </h2>
                  <p style="margin-top: 0.5rem;">
                    Rankings will appear once all evaluators have completed their assessments.
                  </p>
                </div>
              </c:when>
              <c:otherwise>
                <div class="pg-table-wrapper">
                  <table class="leaderboard-table">
                    <thead>
                    <tr>
                      <th>Rank</th>
                      <th>Supplier</th>
                      <th>Bid Amount</th>
                      <th>Avg. Technical</th>
                      <th>Final Score</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="entry" items="${leaderboard}" varStatus="loop">
                      <tr>
                        <td class="rank-cell">
                          <c:choose>
                            <c:when test="${loop.index == 0}">
                              <span class="rank-1">🥇</span>
                            </c:when>
                            <c:when test="${loop.index == 1}">
                              <span class="rank-2">🥈</span>
                            </c:when>
                            <c:when test="${loop.index == 2}">
                              <span class="rank-3">🥉</span>
                            </c:when>
                            <c:otherwise>
                              <span style="color: var(--color-outline);">#${loop.index + 1}</span>
                            </c:otherwise>
                          </c:choose>
                        </td>
                        <td>
                          <div class="supplier-name">${entry.supplierName}</div>
                          <div style="font-size: 0.75rem; color: var(--color-outline);">
                            ${entry.regNumber}
                          </div>
                        </td>
                        <td>
                          <div style="font-weight: 600;">
                            LSL <fmt:formatNumber value="${entry.bidAmount}" type="number" groupingUsed="true" maxFractionDigits="2"/>
                          </div>
                        </td>
                        <td>
                          <fmt:formatNumber value="${entry.avgTechnicalScore}" maxFractionDigits="1"/>%
                        </td>
                        <td>
                          <div style="font-weight: 700; color: var(--color-primary); font-size: 1.125rem;">
                            <fmt:formatNumber value="${entry.finalScore}" maxFractionDigits="2"/>%
                          </div>
                          <div class="score-bar">
                            <div class="score-bar-fill"
                                 style="width: ${entry.finalScore}%;"></div>
                          </div>
                        </td>
                        <td>
                          <c:choose>
                            <c:when test="${entry.awarded}">
                                                                <span class="status-badge status-awarded">
                                                                    <span class="material-symbols-outlined" style="font-size: 0.875rem;">emoji_events</span>
                                                                    Awarded
                                                                </span>
                            </c:when>
                            <c:otherwise>
                                                                <span class="status-badge status-evaluated">
                                                                    <span class="material-symbols-outlined" style="font-size: 0.875rem;">check_circle</span>
                                                                    Evaluated
                                                                </span>
                            </c:otherwise>
                          </c:choose>
                        </td>
                        <td>
                          <c:choose>
                            <c:when test="${entry.awarded}">
                              <a href="${pageContext.request.contextPath}/app/awards/view?bidId=${entry.bidId}"
                                 class="btn btn-ghost btn-sm">
                                <span class="material-symbols-outlined">description</span>
                                View Award
                              </a>
                            </c:when>
                            <c:when test="${userRole eq 'PROCUREMENT_OFFICER' && (tender.status eq 'EVALUATED' || tender.status eq 'AWARDED')}">
                              <a href="${pageContext.request.contextPath}/app/awards/create?tenderId=${tender.tender_id}&bidId=${entry.bidId}"
                                 class="award-btn">
                                <span class="material-symbols-outlined">verified</span>
                                Award Contract
                              </a>
                            </c:when>
                            <c:otherwise>
                              <a href="${pageContext.request.contextPath}/app/evaluations/evaluate?bidId=${entry.bidId}&tenderId=${tender.tender_id}"
                                 class="btn btn-ghost btn-sm">
                                <span class="material-symbols-outlined">visibility</span>
                                View
                              </a>
                            </c:otherwise>
                          </c:choose>
                        </td>
                      </tr>
                    </c:forEach>
                    </tbody>
                  </table>
                </div>
              </c:otherwise>
            </c:choose>
          </div>

          <!-- Tab Content: All Evaluations -->
          <c:if test="${tender.status eq 'EVALUATED' || tender.status eq 'AWARDED'}">
            <div id="tab-evaluated" class="tab-content" style="display: none;">
              <div class="pg-table-wrapper">
                <table class="pg-table">
                  <thead>
                  <tr>
                    <th>Supplier</th>
                    <th>Bid Amount</th>
                    <th>Avg. Price Score</th>
                    <th>Avg. Technical</th>
                    <th>Avg. Delivery</th>
                    <th>Final Score</th>
                    <th>Evaluators</th>
                    <th>Actions</th>
                  </tr>
                  </thead>
                  <tbody>
                  <c:forEach var="summary" items="${allBidSummaries}">
                    <tr>
                      <td>
                        <div class="col-headline">${summary.supplierName}</div>
                      </td>
                      <td>
                        LSL <fmt:formatNumber value="${summary.bidAmount}" type="number" groupingUsed="true" maxFractionDigits="2"/>
                      </td>
                      <td>
                        <fmt:formatNumber value="${summary.priceScore}" maxFractionDigits="1"/>%
                      </td>
                      <td>
                        <fmt:formatNumber value="${summary.technicalScore}" maxFractionDigits="1"/>%
                      </td>
                      <td>
                        <fmt:formatNumber value="${summary.deliveryScore}" maxFractionDigits="1"/>%
                      </td>
                      <td>
                        <strong style="color: var(--color-primary);">
                          <fmt:formatNumber value="${summary.finalScore}" maxFractionDigits="2"/>%
                        </strong>
                      </td>
                      <td>
                        ${summary.evaluationsCompleted} / ${summary.totalEvaluators}
                      </td>
                      <td>
                        <a href="${pageContext.request.contextPath}/app/evaluations/evaluate?bidId=${summary.bidId}&tenderId=${tender.tender_id}"
                           class="btn btn-ghost btn-sm">
                          <span class="material-symbols-outlined">visibility</span>
                          View
                        </a>
                      </td>
                    </tr>
                  </c:forEach>
                  </tbody>
                </table>
              </div>
            </div>
          </c:if>

        </c:otherwise>
      </c:choose>
    </div>

    <jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />
  </main>
</div>

<script>
  function switchTab(tabName) {
    document.querySelectorAll('.tab-content').forEach(tab => {
      tab.style.display = 'none';
    });

    document.querySelectorAll('.tab-btn').forEach(btn => {
      btn.classList.remove('active');
    });

    document.getElementById('tab-' + tabName).style.display = 'block';
    event.target.closest('.tab-btn').classList.add('active');
  }
</script>

</body>
</html>