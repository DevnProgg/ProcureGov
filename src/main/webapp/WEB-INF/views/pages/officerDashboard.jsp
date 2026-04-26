
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="activePage" value="dashboard" />
<c:set var="pageTitle" value="Officer Dashboard | ProcureGov" />
<c:set var="pageSection" value="Operations" />


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
                    <span class="material-symbols-outlined" style="font-size:0.75rem;">calendar_today</span>
                    Morning Edition
                </div>
                <h1 class="pg-page-header__title">
                    The Briefing <em style="font-weight:400; font-style:italic;">— Today's Ledger</em>
                </h1>
                <p class="pg-page-header__subtitle">Active procurement activity across all categories and stages.</p>
                <div class="pg-page-header__actions">
                    <c:if test="${sessionScope.user.role_name eq 'PROCUREMENT_OFFICER'}">
                        <a href="${pageContext.request.contextPath}/officer/create-tender" class="btn btn-primary">
                            <span class="material-symbols-outlined">add_circle</span>
                            New Tender
                        </a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/app/tenders" class="btn btn-ghost">
                        <span class="material-symbols-outlined">list</span>
                        View All Tenders
                    </a>
                </div>
            </div>

            <!-- ── KPI Stats Row ── -->
            <div class="grid-4" style="margin-bottom:2rem;">
                <div class="pg-stat-card">
                    <div class="pg-stat-card__label">Open Tenders</div>
                    <div class="pg-stat-card__value">${not empty openCount ? openCount : '0'}</div>
                    <div class="pg-stat-card__sub">Accepting bid submissions</div>
                </div>
                <div class="pg-stat-card">
                    <div class="pg-stat-card__label">Under Evaluation</div>
                    <div class="pg-stat-card__value">${not empty evalCount ? evalCount : '0'}</div>
                    <div class="pg-stat-card__sub">Bids being scored</div>
                </div>
                <div class="pg-stat-card">
                    <div class="pg-stat-card__value" style="color:var(--color-secondary);">${not empty awardedCount ? awardedCount : '0'}</div>
                    <div class="pg-stat-card__label">Awarded YTD</div>
                    <div class="pg-stat-card__sub">Contracts finalised</div>
                </div>
                <div class="pg-stat-card">
                    <div class="pg-stat-card__label">Drafts Pending</div>
                    <div class="pg-stat-card__value" style="color:var(--color-outline);">${not empty draftCount ? draftCount : '0'}</div>
                    <div class="pg-stat-card__sub">Not yet published</div>
                </div>
            </div>

            <!-- ── Two-column layout: Feed + Quick Actions ── -->
            <div style="display:grid; grid-template-columns:1fr 320px; gap:1.5rem; align-items:start;">

                <!-- Activity Feed -->
                <div class="pg-card">
                    <div class="pg-card__header">
                        <div>
                            <div class="text-kicker" style="color:var(--color-secondary); margin-bottom:0.25rem;">Live Feed</div>
                            <h2 class="text-headline-sm">Latest Actions</h2>
                        </div>
                        <a href="#" class="btn btn-ghost btn-sm">View All</a>
                    </div>
                    <div class="pg-card__body">
                        <div class="pg-feed">
                            <%-- Rendered from recentActivity list set by OfficerDashboardServlet --%>
                            <c:choose>
                                <c:when test="${not empty recentActivity}">
                                    <c:forEach var="item" items="${recentActivity}">
                                        <div class="pg-feed__item">
                                            <div class="pg-feed__icon pg-feed__icon--${item.type}">
                                                <span class="material-symbols-outlined" style="font-size:1rem;">${item.icon}</span>
                                            </div>
                                            <div class="pg-feed__content">
                                                <div class="pg-feed__title">${item.title}</div>
                                                <div class="pg-feed__desc">${item.description}</div>
                                            </div>
                                            <div class="pg-feed__time">${item.relativeTime}</div>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <%-- Placeholder when no activity yet --%>
                                    <div class="pg-feed__item">
                                        <div class="pg-feed__icon pg-feed__icon--info">
                                            <span class="material-symbols-outlined" style="font-size:1rem;">add_circle</span>
                                        </div>
                                        <div class="pg-feed__content">
                                            <div class="pg-feed__title">No recent activity</div>
                                            <div class="pg-feed__desc">Create your first tender to get started.</div>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- Right Column: Quick Access + Compliance Alert -->
                <div style="display:flex; flex-direction:column; gap:1rem;">

                    <!-- Quick Access Card -->
                    <div class="pg-card">
                        <div class="pg-card__header">
                            <h3 class="text-title">Quick Access</h3>
                        </div>
                        <div class="pg-card__body" style="display:flex; flex-direction:column; gap:0.5rem; padding-top:0;">
                            <c:if test="${sessionScope.user.role_name eq 'PROCUREMENT_OFFICER'}">
                                <a href="${pageContext.request.contextPath}/officer/create-tender"
                                   class="btn btn-tonal btn-full" style="justify-content:flex-start;">
                                    <span class="material-symbols-outlined">add_circle</span>
                                    Create New Tender
                                </a>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/app/tenders"
                               class="btn btn-ghost btn-full" style="justify-content:flex-start;">
                                <span class="material-symbols-outlined">inventory_2</span>
                                Tender Registry
                            </a>

                            <a href="${pageContext.request.contextPath}/app/evaluations/panel"
                               class="btn btn-ghost btn-full" style="justify-content:flex-start;">
                                <span class="material-symbols-outlined">fact_check</span>
                                Evaluation Panel
                            </a>
                            <a href="${pageContext.request.contextPath}/app/notices"
                               class="btn btn-ghost btn-full" style="justify-content:flex-start;">
                                <span class="material-symbols-outlined">description</span>
                                Award Notices
                            </a>
                        </div>
                    </div>

                    <!-- Tender Lifecycle Reference -->
                    <div class="pg-card">
                        <div class="pg-card__header" style="padding-bottom:0.5rem;">
                            <h3 class="text-title">Lifecycle Stages</h3>
                        </div>
                        <div class="pg-card__body" style="padding-top:0.5rem;">
                            <div style="display:flex; flex-direction:column; gap:0.375rem; font-size:0.8125rem;">
                                <div style="display:flex; align-items:center; gap:0.5rem;">
                                    <span class="badge badge--draft">Draft</span>
                                    <span style="color:var(--color-on-surface-variant);">Not visible to suppliers</span>
                                </div>
                                <div style="display:flex; align-items:center; gap:0.5rem;">
                                    <span class="badge badge--open">Open</span>
                                    <span style="color:var(--color-on-surface-variant);">Accepting bids</span>
                                </div>
                                <div style="display:flex; align-items:center; gap:0.5rem;">
                                    <span class="badge badge--closed">Closed</span>
                                    <span style="color:var(--color-on-surface-variant);">Bids locked</span>
                                </div>
                                <div style="display:flex; align-items:center; gap:0.5rem;">
                                    <span class="badge badge--evaluation">Under Eval.</span>
                                    <span style="color:var(--color-on-surface-variant);">Scoring active</span>
                                </div>
                                <div style="display:flex; align-items:center; gap:0.5rem;">
                                    <span class="badge badge--awarded">Awarded</span>
                                    <span style="color:var(--color-on-surface-variant);">Contract finalised</span>
                                </div>
                            </div>
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
