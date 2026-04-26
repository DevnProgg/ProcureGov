<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="activePage" value="tenders" />
<c:set var="pageTitle" value="${tender.title}" />
<c:set var="pageSection" value="Tenders" />

<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/WEB-INF/jsp/includes/head.jsp" %></head>
<body>

<div class="pg-layout">
    <%@ include file="/WEB-INF/jsp/includes/sidebar.jsp" %>

    <div class="pg-main">
        <%@ include file="/WEB-INF/jsp/includes/topbar.jsp" %>

        <main class="pg-content">
            <!-- Page Header -->
            <div class="pg-page-header">
                <div class="pg-page-header__kicker">
                    <span class="material-symbols-outlined" style="font-size: 0.875rem;">gavel</span>
                    Tender Detail
                </div>
                <div style="display: flex; align-items: flex-start; justify-content: space-between; gap: 1rem; flex-wrap: wrap;">
                    <div>
                        <h1 class="pg-page-header__title">${tender.title}</h1>
                        <p class="pg-page-header__subtitle">REF: ${tender.reference_number}</p>
                    </div>
                    <div class="pg-page-header__actions no-print">
                        <%-- Officer Actions --%>
                        <c:if test="${canEdit and userRole eq 'PROCUREMENT_OFFICER'}">
                            <a href="${pageContext.request.contextPath}/officer/edit-tender?id=${tender.tender_id}" class="btn btn-tonal">
                                <span class="material-symbols-outlined">edit</span>
                                Edit Draft
                            </a>
                        </c:if>
                        <%-- Supplier Bid Action --%>
                        <c:if test="${tender.status eq 'OPEN' and userRole eq 'SUPPLIER'}">
                            <c:choose>
                                <c:when test="${hasBid}">
                                    <span class="badge badge--info" style="padding: 0.625rem 1.25rem;">
                                        <span class="material-symbols-outlined">check_circle</span>
                                        Bid Submitted
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/app/bids/submit?tenderId=${tender.tender_id}" class="btn btn-primary">
                                        <span class="material-symbols-outlined">send</span>
                                        Submit Bid
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/app/tenders" class="btn btn-ghost">
                            <span class="material-symbols-outlined">arrow_back</span>
                            Back to Registry
                        </a>
                    </div>
                </div>
            </div>

            <!-- Status Banner -->
            <div style="margin-bottom: 2rem;">
                <c:choose>
                    <c:when test="${tender.status eq 'OPEN'}">
                        <div class="pg-alert pg-alert--success">
                            <span class="material-symbols-outlined">hourglass_top</span>
                            <div>
                                <strong>Open for Bidding</strong> — Closes on <fmt:formatDate value="${tender.expiry_datetime}" pattern="dd MMMM yyyy 'at' HH:mm"/>.
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${tender.status eq 'DRAFT'}">
                        <div class="pg-alert pg-alert--warning">
                            <span class="material-symbols-outlined">edit_note</span>
                            <div><strong>Draft Tender</strong> — This listing is not yet public. Complete and publish to receive bids.</div>
                        </div>
                    </c:when>
                    <c:when test="${tender.status eq 'CLOSED'}">
                        <div class="pg-alert pg-alert--info">
                            <span class="material-symbols-outlined">lock</span>
                            <div><strong>Tender Closed</strong> — Bidding period ended on <fmt:formatDate value="${tender.expiry_datetime}" pattern="dd MMMM yyyy"/>.</div>
                        </div>
                    </c:when>
                    <c:when test="${tender.status eq 'UNDER_EVALUATION'}">
                        <div class="pg-alert pg-alert--warning">
                            <span class="material-symbols-outlined">rate_review</span>
                            <div><strong>Under Evaluation</strong> — Bids are being reviewed by the procurement committee.</div>
                        </div>
                    </c:when>
                    <c:when test="${tender.status eq 'AWARDED'}">
                        <div class="pg-alert pg-alert--success">
                            <span class="material-symbols-outlined">award_star</span>
                            <div><strong>Tender Awarded</strong> — Contract has been issued for this procurement.</div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="pg-alert">
                            <span class="material-symbols-outlined">info</span>
                            <div>Status: ${tender.status}</div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Main Content Grid -->
            <div style="display: grid; grid-template-columns: 1fr 320px; gap: 1.5rem; align-items: start;">

                <!-- Left Column: Description & Details -->
                <div style="display: flex; flex-direction: column; gap: 1.5rem;">

                    <!-- Tender Description -->
                    <div class="pg-card">
                        <div class="pg-card__header">
                            <h2 class="text-headline-sm" style="margin: 0; color: var(--color-primary);">Tender Description</h2>
                        </div>
                        <div class="pg-card__body">
                            <p style="font-size: 0.9375rem; line-height: 1.7; color: var(--color-on-surface); white-space: pre-wrap;">${tender.description}</p>

                            <c:if test="${not empty tender.notice_file_path}">
                                <div style="margin-top: 1.5rem;">
                                    <span class="form-label">Attached Notice Document</span>
                                    <a href="${pageContext.request.contextPath}/${tender.notice_file_path}" class="btn btn-ghost btn-sm" target="_blank">
                                        <span class="material-symbols-outlined">description</span>
                                        View Official Notice
                                    </a>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- Evaluation Criteria Section (Placeholder for now) -->
                    <div class="pg-card">
                        <div class="pg-card__header">
                            <h2 class="text-headline-sm" style="margin: 0; color: var(--color-primary);">Evaluation Criteria</h2>
                            <span class="badge badge--draft">Coming Soon</span>
                        </div>
                        <div class="pg-card__body">
                            <p class="text-muted" style="font-size: 0.8125rem;">Detailed evaluation criteria and scoring breakdowns will be published here once the tender moves into the evaluation phase.</p>
                        </div>
                    </div>
                </div>

                <!-- Right Column: Sidebar Stats & Meta -->
                <div style="display: flex; flex-direction: column; gap: 1.5rem;">

                    <!-- Key Info Card -->
                    <div class="pg-card">
                        <div class="pg-card__header">
                            <h2 class="text-label" style="margin:0; color: var(--color-outline); text-transform: uppercase;">Procurement Summary</h2>
                        </div>
                        <div class="pg-card__body" style="display: flex; flex-direction: column; gap: 1.25rem;">

                            <!-- Estimated Value -->
                            <div>
                                <span class="form-label">Estimated Value (LSL)</span>
                                <div class="text-headline-sm" style="margin: 0.25rem 0 0; color: var(--color-primary);">
                                    <fmt:formatNumber value="${tender.estimated_value}" type="currency" currencySymbol="M " groupingUsed="true" maxFractionDigits="0"/>
                                </div>
                            </div>

                            <!-- Category -->
                            <div>
                                <span class="form-label">Category</span>
                                <div style="display: flex; align-items: center; gap: 0.5rem; margin-top: 0.25rem;">
                                    <span class="material-symbols-outlined" style="color: var(--color-outline);">folder</span>
                                    <span class="text-body" style="font-weight: 500;">${tender.category}</span>
                                </div>
                            </div>

                            <!-- Timeline -->
                            <div>
                                <span class="form-label">Timeline</span>
                                <div style="display: flex; flex-direction: column; gap: 0.75rem; margin-top: 0.5rem;">
                                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                                        <span class="material-symbols-outlined" style="color: var(--color-outline); font-size: 1.25rem;">calendar_today</span>
                                        <div>
                                            <div style="font-size: 0.8125rem; font-weight: 600;">Published</div>
                                            <div style="font-size: 0.75rem; color: var(--color-outline);">
                                                <fmt:formatDate value="${tender.publish_datetime}" pattern="dd MMM yyyy"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                                        <span class="material-symbols-outlined" style="color: var(--color-tertiary); font-size: 1.25rem;">schedule</span>
                                        <div>
                                            <div style="font-size: 0.8125rem; font-weight: 600;">Closing Date</div>
                                            <div style="font-size: 0.75rem; color: var(--color-outline);">
                                                <fmt:formatDate value="${tender.expiry_datetime}" pattern="dd MMM yyyy 'at' HH:mm"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Bidding Activity -->
                            <div>
                                <span class="form-label">Bidding Activity</span>
                                <div style="display: flex; align-items: center; gap: 0.5rem; margin-top: 0.25rem;">
                                    <span class="material-symbols-outlined" style="color: var(--color-primary);">groups</span>
                                    <span class="text-body" style="font-weight: 600;">
                                        ${bidCount} <span style="font-weight: 400; color: var(--color-outline);">Bid${bidCount != 1 ? 's' : ''} Received</span>
                                    </span>
                                </div>
                            </div>

                            <!-- Status Badge -->
                            <div>
                                <span class="form-label">Status</span>
                                <div style="margin-top: 0.25rem;">
                                    <c:choose>
                                        <c:when test="${tender.status eq 'OPEN'}"><span class="badge badge--open">Open</span></c:when>
                                        <c:when test="${tender.status eq 'DRAFT'}"><span class="badge badge--draft">Draft</span></c:when>
                                        <c:when test="${tender.status eq 'CLOSED'}"><span class="badge badge--closed">Closed</span></c:when>
                                        <c:when test="${tender.status eq 'UNDER_EVALUATION'}"><span class="badge badge--evaluation">Under Evaluation</span></c:when>
                                        <c:when test="${tender.status eq 'AWARDED'}"><span class="badge badge--awarded">Awarded</span></c:when>
                                        <c:otherwise><span class="badge badge--draft">${tender.status}</span></c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Quick Actions Card -->
                    <div class="pg-card" style="background: var(--color-surface-container-low);">
                        <div class="pg-card__body" style="display: flex; flex-direction: column; gap: 0.75rem;">
                            <button class="btn btn-ghost btn-full" onclick="window.print()">
                                <span class="material-symbols-outlined">print</span>
                                Print Notice
                            </button>
                            <button class="btn btn-ghost btn-full">
                                <span class="material-symbols-outlined">share</span>
                                Share Tender
                            </button>
                            <c:if test="${tender.status eq 'OPEN'}">
                                <button class="btn btn-ghost btn-full">
                                    <span class="material-symbols-outlined">flag</span>
                                    Report Issue
                                </button>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>

        </main>
        <%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
    </div>
</div>

</body>
</html>