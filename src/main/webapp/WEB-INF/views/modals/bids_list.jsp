<%--
bids_list.jsp — Supplier bids listing page
Displays all bids submitted by the logged-in supplier with status and scores.
Usage: Accessed via /app/supplier/bids
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en"/>

<c:set var="pageTitle" value="My Bids" scope="request"/>
<c:set var="pageSection" value="Supplier Portal" scope="request"/>
<c:set var="activePage" value="my-bids" scope="request"/>

<!DOCTYPE html>
<html lang="en">
<head>
  <jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
  <style>
    /* Additional inline styles for bid-specific components */
    .bid-card {
      transition: all 0.2s ease;
    }
    .bid-card:hover {
      transform: translateY(-2px);
    }
    .score-highlight {
      background: linear-gradient(135deg, var(--color-primary-fixed) 0%, var(--color-primary-fixed-dim) 100%);
      border-radius: var(--radius-full);
      padding: 0.25rem 0.75rem;
      font-weight: 700;
      font-size: 0.8125rem;
      display: inline-flex;
      align-items: center;
      gap: 0.375rem;
    }
    .status-timeline {
      position: relative;
      padding-left: 1.5rem;
    }
    .status-timeline::before {
      content: '';
      position: absolute;
      left: 0.5rem;
      top: 0.5rem;
      bottom: 0.5rem;
      width: 2px;
      background: var(--color-surface-container-high);
    }
    .status-dot {
      position: absolute;
      left: -0.125rem;
      width: 0.625rem;
      height: 0.625rem;
      border-radius: 50%;
      background: var(--color-outline);
    }
    .status-dot.completed {
      background: var(--color-secondary);
    }
    .status-dot.active {
      background: var(--color-primary);
      box-shadow: 0 0 0 3px rgba(0, 63, 135, 0.2);
    }
    .filter-chip {
      padding: 0.375rem 1rem;
      border-radius: var(--radius-full);
      font-size: 0.75rem;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.15s;
      background: var(--color-surface-container-high);
      color: var(--color-on-surface-variant);
    }
    .filter-chip.active {
      background: var(--color-primary);
      color: var(--color-on-primary);
    }
    .filter-chip:hover {
      background: var(--color-primary-fixed);
      color: var(--color-primary);
    }
    @keyframes slideIn {
      from {
        opacity: 0;
        transform: translateY(10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
    .bid-card {
      animation: slideIn 0.3s ease-out;
    }
  </style>
</head>
<body>

<div class="pg-layout">
  <jsp:include page="/WEB-INF/jsp/includes/sidebar.jsp" />

  <main class="pg-main">
    <jsp:include page="/WEB-INF/jsp/includes/topbar.jsp" />

    <div class="pg-content">
      <!-- Page Header -->
      <div class="pg-page-header">
        <div class="pg-page-header__kicker">
          <span class="material-symbols-outlined" style="font-size: 0.875rem;">assignment</span>
          Supplier Portal
        </div>
        <h1 class="pg-page-header__title">My Bids</h1>
        <p class="pg-page-header__subtitle">
          Track and manage all your submitted tender bids. View evaluation statuses and scores.
        </p>
        <div class="pg-page-header__actions">
          <a href="${pageContext.request.contextPath}/app/tenders" class="btn btn-tonal btn-sm">
            <span class="material-symbols-outlined" style="font-size: 1rem;">gavel</span>
            Browse New Tenders
          </a>
        </div>
      </div>

      <!-- Success/Error Messages -->
      <c:if test="${not empty param.success}">
        <div class="pg-alert pg-alert--success" style="margin-bottom: 1.5rem;" role="alert">
          <span class="material-symbols-outlined">check_circle</span>
          <span>${param.success}</span>
        </div>
      </c:if>
      <c:if test="${not empty error}">
        <div class="pg-alert pg-alert--error" style="margin-bottom: 1.5rem;" role="alert">
          <span class="material-symbols-outlined">error</span>
          <span>${error}</span>
        </div>
      </c:if>

      <!-- Filter Bar -->
      <div class="flex items-center justify-between" style="margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
        <div class="flex" style="gap: 0.5rem; flex-wrap: wrap;">
          <button class="filter-chip active" data-filter="all">All Bids</button>
          <button class="filter-chip" data-filter="PENDING">Pending Review</button>
          <button class="filter-chip" data-filter="EVALUATING">Under Evaluation</button>
          <button class="filter-chip" data-filter="AWARDED">Awarded</button>
          <button class="filter-chip" data-filter="REJECTED">Not Awarded</button>
        </div>
        <div class="text-body-sm text-muted">
          <span class="material-symbols-outlined" style="font-size: 1rem; vertical-align: middle;">info</span>
          Showing all bids sorted by submission date
        </div>
      </div>

      <!-- Bids List -->
      <c:choose>
        <c:when test="${empty bids}">
          <div class="pg-card" style="text-align: center; padding: 3rem 2rem;">
            <span class="material-symbols-outlined" style="font-size: 3rem; color: var(--color-outline); margin-bottom: 1rem;">inbox</span>
            <h3 class="text-headline-sm" style="margin-bottom: 0.5rem;">No bids submitted yet</h3>
            <p class="text-body" style="color: var(--color-outline); margin-bottom: 1.5rem;">
              You haven't submitted any bids. Browse available tenders and submit your first bid.
            </p>
            <a href="${pageContext.request.contextPath}/app/tenders" class="btn btn-primary">
              <span class="material-symbols-outlined" style="font-size: 1rem;">search</span>
              Browse Tenders
            </a>
          </div>
        </c:when>
        <c:otherwise>
          <div class="grid-1" style="display: flex; flex-direction: column; gap: 1rem;">
            <c:forEach var="bid" items="${bids}" varStatus="status">
              <div class="bid-card pg-card" data-bid-status="${bid.evaluationStatus}">
                <div class="pg-card__body" style="padding: 1.25rem 1.5rem;">
                  <div class="flex justify-between items-start" style="flex-wrap: wrap; gap: 1rem;">
                    <!-- Left Column: Tender Info -->
                    <div style="flex: 2; min-width: 200px;">
                      <div class="flex items-center gap-2" style="margin-bottom: 0.5rem; flex-wrap: wrap;">
                                                <span class="text-kicker" style="background: var(--color-surface-container-high); padding: 0.125rem 0.5rem; border-radius: var(--radius-full);">
                                                    Ref: ${bid.tenderReference}
                                                </span>
                        <c:choose>
                          <c:when test="${bid.evaluationStatus == 'AWARDED'}">
                                                        <span class="badge badge--awarded">
                                                            <span class="material-symbols-outlined" style="font-size: 0.75rem;">workspace_premium</span>
                                                            Awarded
                                                        </span>
                          </c:when>
                          <c:when test="${bid.evaluationStatus == 'REJECTED'}">
                                                        <span class="badge badge--error">
                                                            <span class="material-symbols-outlined" style="font-size: 0.75rem;">close</span>
                                                            Not Awarded
                                                        </span>
                          </c:when>
                          <c:when test="${bid.evaluationStatus == 'EVALUATING'}">
                                                        <span class="badge badge--evaluation">
                                                            <span class="material-symbols-outlined" style="font-size: 0.75rem;">pending</span>
                                                            Under Evaluation
                                                        </span>
                          </c:when>
                          <c:when test="${bid.evaluationStatus == 'SHORTLISTED'}">
                                                        <span class="badge badge--info">
                                                            <span class="material-symbols-outlined" style="font-size: 0.75rem;">star</span>
                                                            Shortlisted
                                                        </span>
                          </c:when>
                          <c:otherwise>
                                                        <span class="badge badge--draft">
                                                            <span class="material-symbols-outlined" style="font-size: 0.75rem;">schedule</span>
                                                            Pending Review
                                                        </span>
                          </c:otherwise>
                        </c:choose>
                      </div>

                      <h3 class="text-title" style="margin-bottom: 0.5rem; font-size: 1.125rem;">
                        <a href="${pageContext.request.contextPath}/app/tenders/view?tenderId=${bid.tenderId}"
                           style="text-decoration: none; color: inherit;">
                          ${bid.tenderTitle}
                        </a>
                      </h3>

                      <div class="flex" style="gap: 1rem; flex-wrap: wrap; margin-top: 0.75rem;">
                        <div class="flex items-center gap-1 text-body-sm text-muted">
                          <span class="material-symbols-outlined" style="font-size: 0.875rem;">event</span>
                          Submitted:
                          <fmt:formatDate value="${bid.submittedAt}" pattern="dd MMM yyyy, hh:mm a"/>
                        </div>
                        <c:if test="${not empty bid.totalScore}">
                          <div class="flex items-center gap-1">
                                                        <span class="score-highlight">
                                                            <span class="material-symbols-outlined" style="font-size: 0.875rem;">trending_up</span>
                                                            Score: ${bid.totalScore}%
                                                        </span>
                          </div>
                        </c:if>
                      </div>
                    </div>

                    <!-- Right Column: Actions -->
                    <div style="flex-shrink: 0;">
                      <div class="flex" style="gap: 0.5rem;">
                        <a href="${pageContext.request.contextPath}/app/tenders/view?tenderId=${bid.tenderId}"
                           class="btn btn-ghost btn-sm">
                          <span class="material-symbols-outlined" style="font-size: 1rem;">visibility</span>
                          View Tender
                        </a>
                        <c:if test="${bid.evaluationStatus == 'AWARDED'}">
                          <a href="${pageContext.request.contextPath}/app/notices?tenderId=${bid.tenderId}"
                             class="btn btn-tonal btn-sm">
                            <span class="material-symbols-outlined" style="font-size: 1rem;">description</span>
                            Award Notice
                          </a>
                        </c:if>
                      </div>
                    </div>
                  </div>

                  <!-- Evaluation Timeline (if applicable) -->
                  <c:if test="${not empty bid.evaluationStatus && bid.evaluationStatus != 'PENDING'}">
                    <div class="status-timeline" style="margin-top: 1rem; padding-top: 0.5rem;">
                      <div style="display: flex; justify-content: space-between; flex-wrap: wrap; gap: 1rem;">
                        <div style="position: relative; flex: 1; min-width: 120px;">
                          <div class="status-dot ${bid.evaluationStatus != 'PENDING' ? 'completed' : ''}"
                               style="top: 0;"></div>
                          <div class="text-label" style="margin-top: 0.5rem;">Submission</div>
                          <div class="text-body-sm text-muted">
                            <fmt:formatDate value="${bid.submittedAt}" pattern="dd MMM"/>
                          </div>
                        </div>
                        <div style="position: relative; flex: 1; min-width: 120px;">
                          <div class="status-dot ${bid.evaluationStatus == 'EVALUATING' || bid.evaluationStatus == 'SHORTLISTED' || bid.evaluationStatus == 'AWARDED' || bid.evaluationStatus == 'REJECTED' ? 'completed' : ''}"
                               style="top: 0;"></div>
                          <div class="text-label" style="margin-top: 0.5rem;">Technical Review</div>
                        </div>
                        <div style="position: relative; flex: 1; min-width: 120px;">
                          <div class="status-dot ${bid.evaluationStatus == 'SHORTLISTED' || bid.evaluationStatus == 'AWARDED' || bid.evaluationStatus == 'REJECTED' ? 'completed' : ''}"
                               style="top: 0;"></div>
                          <div class="text-label" style="margin-top: 0.5rem;">Financial Eval</div>
                        </div>
                        <div style="position: relative; flex: 1; min-width: 120px;">
                          <div class="status-dot ${bid.evaluationStatus == 'AWARDED' || bid.evaluationStatus == 'REJECTED' ? 'completed' : ''}"
                               style="top: 0;"></div>
                          <div class="text-label" style="margin-top: 0.5rem;">Final Decision</div>
                        </div>
                      </div>
                    </div>
                  </c:if>
                </div>
              </div>
            </c:forEach>
          </div>

          <!-- Summary Statistics -->
          <div class="grid-3" style="margin-top: 2rem;">
            <div class="pg-stat-card">
              <div class="pg-stat-card__label">Total Bids</div>
              <div class="pg-stat-card__value">${bids.size()}</div>
              <div class="pg-stat-card__sub">Across all tenders</div>
            </div>
            <div class="pg-stat-card">
              <div class="pg-stat-card__label">Under Evaluation</div>
              <div class="pg-stat-card__value">
                ${bids.stream().filter(b -> b.evaluationStatus == 'EVALUATING').count()}
              </div>
              <div class="pg-stat-card__sub">Pending decision</div>
            </div>
            <div class="pg-stat-card">
              <div class="pg-stat-card__label">Successful Bids</div>
              <div class="pg-stat-card__value">
                ${bids.stream().filter(b -> b.evaluationStatus == 'AWARDED').count()}
              </div>
              <div class="pg-stat-card__sub">Contracts awarded</div>
            </div>
          </div>
        </c:otherwise>
      </c:choose>
    </div>

    <jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />
  </main>
</div>

<script>
  // Filter functionality
  document.querySelectorAll('.filter-chip').forEach(chip => {
    chip.addEventListener('click', function() {
      // Update active state
      document.querySelectorAll('.filter-chip').forEach(c => c.classList.remove('active'));
      this.classList.add('active');

      const filterValue = this.dataset.filter;
      const bidCards = document.querySelectorAll('.bid-card');

      bidCards.forEach(card => {
        if (filterValue === 'all') {
          card.style.display = '';
        } else {
          const status = card.dataset.bidStatus;
          if (status === filterValue) {
            card.style.display = '';
          } else {
            card.style.display = 'none';
          }
        }
      });
    });
  });
</script>

</body>
</html>