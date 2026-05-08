<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Dashboard" scope="request" />
<c:set var="activePage" value="dashboard" scope="request" />
<c:set var="pageSection" value="Supplier Portal" scope="request" />

<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/WEB-INF/jsp/includes/head.jsp" %></head>
<body>

<div class="pg-layout">
    <%@ include file="/WEB-INF/jsp/includes/sidebar.jsp" %>

    <div class="pg-main">
        <%@ include file="/WEB-INF/jsp/includes/topbar.jsp" %>

        <main class="pg-content">
            <c:if test="${not empty successMessage}">
                <div class="pg-alert pg-alert--success" style="margin-bottom:1.5rem;">
                    <span class="material-symbols-outlined">check_circle</span>
                    <span>${successMessage}</span>
                </div>
            </c:if>

            <c:if test="${not empty infoMessage}">
                <div class="pg-alert pg-alert--info" style="margin-bottom:1.5rem;">
                    <span class="material-symbols-outlined">info</span>
                    <span>${infoMessage}</span>
                </div>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="pg-alert pg-alert--error" style="margin-bottom:1.5rem;">
                    <span class="material-symbols-outlined">error</span>
                    <span>${errorMessage}</span>
                </div>
            </c:if>
            <!-- Page Header -->
            <div class="pg-page-header">
                <div class="pg-page-header__kicker">Supplier Overview</div>
                <h1 class="pg-page-header__title">
                    Strategic Opportunities
                </h1>
                <p class="pg-page-header__subtitle">Browse open tenders and track your active submissions.</p>
            </div>

            <!-- Two-column -->
            <div style="display:grid; grid-template-columns:1fr 300px; gap:1.5rem; align-items:start;">

                <!-- Open Tenders Feed -->
                <div>
                    <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:1rem;">
                        <h2 class="text-headline-sm">Open Tenders</h2>
                        <a href="${pageContext.request.contextPath}/app/tenders" class="btn btn-ghost btn-sm">
                            Browse All
                            <span class="material-symbols-outlined">arrow_forward</span>
                        </a>
                    </div>

                    <div style="display:flex; flex-direction:column; gap:0.75rem;">
                        <c:choose>
                            <c:when test="${not empty openTenders}">
                                <c:forEach var="tender" items="${openTenders}">
                                    <div class="pg-news-card">
                                        <div class="pg-news-card__kicker">
                                            <span class="material-symbols-outlined" style="font-size:0.75rem;">circle</span>
                                            Open Tender
                                            <span style="margin-left:auto; color:var(--color-outline);">REF: ${tender.reference_number}</span>
                                        </div>
                                        <div class="pg-news-card__title">${tender.title}</div>
                                        <p style="font-size:0.8125rem; color:var(--color-on-surface-variant); margin:0 0 0.75rem; line-height:1.5;">
                                            ${tender.description}
                                        </p>
                                        <div class="pg-news-card__meta">
                                            <span>
                                                <span class="material-symbols-outlined">folder</span>
                                                ${tender.category}
                                            </span>
                                            <span>
                                                <span class="material-symbols-outlined">schedule</span>
                                                Closes <fmt:formatDate value="${tender.expiry_datetime}" pattern="dd MMM yyyy, HH:mm"/>
                                            </span>
                                            <span>
                                                <span class="material-symbols-outlined">payments</span>
                                                LSL <fmt:formatNumber value="${tender.estimated_value}" type="number" groupingUsed="true"/>
                                            </span>
                                        </div>
                                        <div style="margin-top:1rem; display:flex; gap:0.5rem;">
                                            <a href="${pageContext.request.contextPath}/app/supplier/tenders/${tender.tender_id}" class="btn btn-ghost btn-sm">View Details</a>
                                            <c:if test="${not hasActiveBid}">
                                                <a href="${pageContext.request.contextPath}/app/bids/submit?tenderId=${tender.tender_id}" class="btn btn-primary btn-sm">
                                                    <span class="material-symbols-outlined">send</span>
                                                    Submit Bid
                                                </a>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="pg-card" style="text-align:center; padding:3rem 2rem;">
                                    <span class="material-symbols-outlined" style="font-size:2.5rem; color:var(--color-outline); display:block; margin:0 auto 0.75rem;">gavel</span>
                                    <div style="font-family:var(--font-headline); font-size:1.125rem; color:var(--color-on-surface-variant);">No open tenders at this time</div>
                                    <div style="font-size:0.875rem; color:var(--color-outline); margin-top:0.375rem;">Check back soon or browse the full registry.</div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Right: My Bids + Insight -->
                <div style="display:flex; flex-direction:column; gap:1rem;">

                    <!-- My Activity -->
                    <div class="pg-card">
                        <div class="pg-card__header">
                            <h3 class="text-title">My Submissions</h3>
                            <a href="${pageContext.request.contextPath}/app/supplier/bids" class="btn btn-ghost btn-sm">All</a>
                        </div>
                        <div class="pg-card__body" style="padding-top:0;">
                            <c:choose>
                                <c:when test="${not empty myBids}">
                                    <div class="pg-feed">
                                        <c:forEach var="bid" items="${myBids}" begin="0" end="4">
                                            <div class="pg-feed__item">
                                                <div class="pg-feed__content">
                                                    <div class="pg-feed__title">${bid.tenderTitle}</div>
                                                    <div style="margin-top:0.25rem;">
                                                        <c:choose>
                                                            <c:when test="${bid.tenderStatus eq 'OPEN'}">
                                                                <span class="badge badge--open">Open</span>
                                                            </c:when>
                                                            <c:when test="${bid.tenderStatus eq 'UNDER_EVALUATION'}">
                                                                <span class="badge badge--evaluation">Under Evaluation</span>
                                                            </c:when>
                                                            <c:when test="${bid.tenderStatus eq 'AWARDED'}">
                                                                <span class="badge badge--awarded">Awarded</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge badge--closed">${bid.tenderStatus}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                                <div class="pg-feed__time">
                                                    <fmt:formatDate value="${bid.submittedAt}" pattern="dd MMM"/>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p style="font-size:0.8125rem; color:var(--color-outline); padding:0.5rem 0 0.75rem;">No bids submitted yet.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Procurement Insight pull-quote -->
                    <div class="pg-pullquote">
                        <span class="material-symbols-outlined" style="font-size:1rem; display:block; margin-bottom:0.375rem; color:var(--color-primary-fixed-dim);">lightbulb</span>
                        Proposals that clearly address the technical specifications and include supporting certifications see stronger evaluation scores.
                        <footer style="margin-top:0.5rem; font-family:var(--font-body); font-style:normal; font-size:0.6875rem; font-weight:700; letter-spacing:0.06em; text-transform:uppercase; color:var(--color-outline);">— Procurement Advisory</footer>
                    </div>

                    <!-- Registration number -->
                    <div class="pg-card" style="padding:1rem 1.25rem;">
                        <div style="font-size:0.6875rem; font-weight:700; text-transform:uppercase; letter-spacing:0.08em; color:var(--color-outline); margin-bottom:0.25rem;">Supplier Reg. Number</div>
                        <div style="font-family:var(--font-headline); font-size:1.25rem; font-weight:700; color:var(--color-primary);">
                            <c:out value="${sessionScope.user.reg_number}" default="Not Available" />
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