
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="activePage" value="tenders" />
<c:set var="pageTitle" value="Tenders Registry" />
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
            <c:if test="${sessionScope.user.role_name eq 'PROCUREMENT_OFFICER'}">
                <!-- Create Button -->
                    <div class="no-print" style="display: flex; justify-content: flex-end; margin-bottom: 1.5rem;">
                        <a href="${pageContext.request.contextPath}/officer/create-tender">
                        <button class="btn btn-primary">
                            <span class="material-symbols-outlined" style="font-size: 1rem;">print</span>
                            Create New Tender Offer
                        </button>
                        </a>
                    </div>
            </c:if>

            <!-- Header + Filters in one row -->
            <div style="display:flex; align-items:flex-end; justify-content:space-between; margin-bottom:1.5rem; flex-wrap:wrap; gap:1rem;">
                <div>
                    <div class="pg-page-header__kicker">Live Registry</div>
                    <h1 class="pg-page-header__title" style="margin:0;">Procurement Tenders</h1>
                    <p class="pg-page-header__subtitle" style="margin:0.25rem 0 0;">
                        ${not empty totalCount ? totalCount : '0'} authorised listings for current fiscal year.
                    </p>
                </div>

                <!-- Filter Form -->
                <form method="get" action="${pageContext.request.contextPath}/app/tenders"
                      data-validate="true"
                      style="display:flex; gap:0.5rem; align-items:center; flex-wrap:wrap;">
                    <select name="status" class="form-select" style="width:auto; padding:0.5rem 2rem 0.5rem 0.75rem; font-size:0.8125rem;">
                        <option value="" ${empty statusFilter ? 'selected' : ''}>All Statuses</option>
                        <option value="OPEN"            ${statusFilter eq 'OPEN'             ? 'selected' : ''}>Open</option>
                        <option value="CLOSED"          ${statusFilter eq 'CLOSED'           ? 'selected' : ''}>Closed</option>
                        <option value="UNDER_EVALUATION"${statusFilter eq 'UNDER_EVALUATION' ? 'selected' : ''}>Under Evaluation</option>
                        <option value="AWARDED"         ${statusFilter eq 'AWARDED'          ? 'selected' : ''}>Awarded</option>
                    </select>
                    <select name="category" class="form-select" style="width:auto; padding:0.5rem 2rem 0.5rem 0.75rem; font-size:0.8125rem;">
                        <option value="" ${empty catFilter ? 'selected' : ''}>All Categories</option>
                        <option value="Construction"      ${catFilter eq 'Construction'      ? 'selected' : ''}>Construction</option>
                        <option value="Roads"             ${catFilter eq 'Roads'             ? 'selected' : ''}>Roads</option>
                        <option value="Electrical"        ${catFilter eq 'Electrical'        ? 'selected' : ''}>Electrical</option>
                        <option value="Plumbing"          ${catFilter eq 'Plumbing'          ? 'selected' : ''}>Plumbing</option>
                        <option value="General Services"  ${catFilter eq 'General Services'  ? 'selected' : ''}>General Services</option>
                    </select>
                    <button type="submit" class="btn btn-tonal btn-sm">
                        <span class="material-symbols-outlined">filter_list</span>
                        Filter
                    </button>
                    <c:if test="${not empty statusFilter or not empty catFilter}">
                        <a href="${pageContext.request.contextPath}/app/tenders" class="btn btn-ghost btn-sm">Clear</a>
                    </c:if>
                </form>
            </div>

            <!-- Tender Cards -->
            <div style="display:flex; flex-direction:column; gap:0.75rem;">
                <c:choose>
                    <c:when test="${not empty tenders}">
                        <c:forEach var="tender" items="${tenders}">
                            <div class="pg-news-card" style="cursor:default;">
                                <div style="display:flex; justify-content:space-between; align-items:flex-start; gap:1rem; flex-wrap:wrap;">
                                    <div style="flex:1; min-width:0;">
                                        <!-- Kicker: status + reference -->
                                        <div class="pg-news-card__kicker">
                                            <c:choose>
                                                <c:when test="${tender.status eq 'OPEN'}"><span class="badge badge--open" style="font-size:0.5625rem;">Open Tender</span></c:when>
                                                <c:when test="${tender.status eq 'DRAFT'}"><span class="badge badge--draft" style="font-size:0.5625rem;">Draft</span></c:when>
                                                <c:when test="${tender.status eq 'CLOSED'}"><span class="badge badge--closed" style="font-size:0.5625rem;">Closed</span></c:when>
                                                <c:when test="${tender.status eq 'UNDER_EVALUATION'}"><span class="badge badge--evaluation" style="font-size:0.5625rem;">Under Evaluation</span></c:when>
                                                <c:when test="${tender.status eq 'AWARDED'}"><span class="badge badge--awarded" style="font-size:0.5625rem;">Awarded</span></c:when>
                                                <c:otherwise><span class="badge badge--draft" style="font-size:0.5625rem;">${tender.status}</span></c:otherwise>
                                            </c:choose>
                                            <span style="margin-left:0.5rem; color:var(--color-outline);">REF: ${tender.reference_number}</span>
                                        </div>

                                        <div class="pg-news-card__title">${tender.title}</div>
                                        <p style="font-size:0.8125rem; color:var(--color-on-surface-variant); margin:0.375rem 0 0.75rem; line-height:1.5;">${tender.description}</p>

                                        <div class="pg-news-card__meta">
                                            <span><span class="material-symbols-outlined">folder</span>${tender.category}</span>
                                            <span><span class="material-symbols-outlined">payments</span>
                                                Est. LSL <fmt:formatNumber value="${tender.estimated_value}" type="number" groupingUsed="true"/>
                                            </span>
                                            <span><span class="material-symbols-outlined">schedule</span>
                                                Closes <fmt:formatDate value="${tender.expiry_datetime}" pattern="dd MMM yyyy HH:mm"/>
                                            </span>
                                        </div>
                                    </div>

                                    <!-- Actions column -->
                                    <div style="display:flex; flex-direction:column; gap:0.5rem; align-items:flex-end; flex-shrink:0;">
                                        <a href="${pageContext.request.contextPath}/app/tenders/${tender.tender_id}" class="btn btn-ghost btn-sm">
                                            View Details
                                        </a>
                                        <%-- Show Submit Bid only if OPEN and user is a SUPPLIER --%>
                                        <c:if test="${tender.status eq 'OPEN' and sessionScope.user.role_name eq 'SUPPLIER' and not hasActiveBid}">
                                            <a href="${pageContext.request.contextPath}/app/bids/submit?tenderId=${tender.tender_id}" class="btn btn-primary btn-sm">
                                                <span class="material-symbols-outlined">send</span>
                                                Submit Bid
                                            </a>
                                        </c:if>
                                        <%-- Officer: edit draft --%>
                                        <c:if test="${tender.status eq 'DRAFT' and sessionScope.user.role_name eq 'PROCUREMENT_OFFICER'}">
                                            <a href="${pageContext.request.contextPath}/officer/edit-tender?id=${tender.tender_id}" class="btn btn-tonal btn-sm">
                                                <span class="material-symbols-outlined">edit</span>
                                                Edit Draft
                                            </a>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="pg-card" style="text-align:center; padding:3rem;">
                            <span class="material-symbols-outlined" style="font-size:2.5rem; color:var(--color-outline); display:block; margin:0 auto 0.75rem;">search_off</span>
                            <div style="font-family:var(--font-headline); font-size:1.125rem; color:var(--color-on-surface-variant);">No tenders match your filters</div>
                            <a href="${pageContext.request.contextPath}/app/tenders" class="btn btn-ghost btn-sm" style="margin-top:1rem; display:inline-flex;">Clear Filters</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </main>
        <%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
    </div>
</div>

</body>
</html>
