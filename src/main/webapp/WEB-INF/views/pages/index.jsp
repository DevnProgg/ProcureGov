<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">

<head><%@ include file="/WEB-INF/jsp/includes/head.jsp" %></head>

<body>


<!-- HEADER NAV -->

<header style="
display:flex;
justify-content:space-between;
align-items:center;
padding:24px 48px;
border-bottom:1px solid var(--color-outline-variant);
">

    <div class="serif-italic text-primary" style="font-size:20px;">
        <a href="${pageContext.request.contextPath}/home" style="text-decoration:none; color:inherit;">ProcureGov</a>
    </div>

    <nav style="display:flex; gap:28px; align-items:center;">

        <a href="${pageContext.request.contextPath}/app/tenders">Tender Registry</a>
        <a href="${pageContext.request.contextPath}/#">Statistics</a>
        <a href="${pageContext.request.contextPath}/#">Information</a>

        <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-primary">
            Sign In
        </a>

    </nav>

</header>



<!-- MAIN CONTENT WRAPPER -->

<main style="
max-width:1200px;
margin:auto;
padding:40px 48px;
display:grid;
grid-template-columns: 520px 1fr;
gap:80px;
">




    <!-- HERO PANEL -->

    <section class="pg-card pg-card--raised" style="padding:48px; background:var(--color-primary-fixed);">

        <div class="text-kicker text-secondary mb-2">
            OFFICIAL PROCUREMENT PORTAL
        </div>

        <h1 class="text-headline-lg text-primary mb-3">
            Modernizing Lesotho's Public Tenders
        </h1>

        <p class="text-body text-muted mb-4">
            Access real-time listings, transparent bidding, and authoritative
            documentation for all government infrastructure projects.
        </p>

        <a href="${pageContext.request.contextPath}/app/tenders" class="btn btn-primary">
            Browse Active Tenders →
        </a>

        <!-- Quick Stats -->
        <c:if test="${not empty tenderStats}">
            <div style="margin-top:32px; display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                <div>
                    <div style="font-size:0.75rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--color-on-surface-variant);">Open Tenders</div>
                    <div style="font-size:2rem; font-weight:700; color:var(--color-primary);">${tenderStats.openTenders}</div>
                </div>
                <div>
                    <div style="font-size:0.75rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--color-on-surface-variant);">Active Suppliers</div>
                    <div style="font-size:2rem; font-weight:700; color:var(--color-primary);">${tenderStats.activeSuppliers}</div>
                </div>
            </div>
        </c:if>

    </section>





    <!-- BULLETIN PANEL -->

    <section>

        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1.5rem;">
            <h2 class="text-headline-sm mb-0">
                Latest Opportunity Bulletins
            </h2>

            <a href="${pageContext.request.contextPath}/app/tenders" class="text-primary text-label">
                View All Registry →
            </a>
        </div>

        <c:choose>
            <c:when test="${not empty featuredTenders}">
                <div style="display:flex; flex-direction:column; gap:1rem;">
                    <c:forEach var="tender" items="${featuredTenders}">
                        <div class="pg-card" style="padding:1.5rem;">
                            <div style="display:flex; align-items:flex-start; gap:0.75rem; margin-bottom:0.75rem;">
                                <span class="material-symbols-outlined" style="color:var(--color-primary);">gavel</span>
                                <div style="flex:1;">
                                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.25rem;">
                                        <span style="font-size:0.75rem; color:var(--color-outline); font-weight:500;">REF: ${tender.reference_number}</span>
                                        <span class="badge badge--open">OPEN</span>
                                    </div>
                                    <h3 style="font-family:var(--font-headline); font-size:1.125rem; margin:0 0 0.5rem 0; color:var(--color-on-surface);">
                                        ${tender.title}
                                    </h3>
                                    <p style="font-size:0.8125rem; color:var(--color-on-surface-variant); margin:0 0 1rem 0; line-height:1.5;">
                                        ${tender.description}
                                    </p>
                                    <div style="display:flex; gap:1.5rem; font-size:0.75rem; color:var(--color-outline);">
                                        <span style="display:flex; align-items:center; gap:0.25rem;">
                                            <span class="material-symbols-outlined" style="font-size:1rem;">category</span>
                                            ${tender.category}
                                        </span>
                                        <span style="display:flex; align-items:center; gap:0.25rem;">
                                            <span class="material-symbols-outlined" style="font-size:1rem;">schedule</span>
                                            Closes <fmt:formatDate value="${tender.expiry_datetime}" pattern="dd MMM yyyy"/>
                                        </span>
                                        <span style="display:flex; align-items:center; gap:0.25rem;">
                                            <span class="material-symbols-outlined" style="font-size:1rem;">payments</span>
                                            LSL <fmt:formatNumber value="${tender.estimated_value}" type="number" groupingUsed="true"/>
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div style="margin-top:1rem; padding-top:1rem; border-top:1px solid var(--color-outline-variant);">
                                <a href="${pageContext.request.contextPath}/app/tenders/${tender.tender_id}" class="btn btn-ghost btn-sm" style="width:100%; text-align:center;">
                                    View Tender Details →
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="pg-card" style="padding:3rem 2rem; text-align:center;">
                    <span class="material-symbols-outlined" style="font-size:3rem; color:var(--color-outline); margin-bottom:1rem; display:block;">inbox</span>
                    <p class="text-muted" style="margin:0;">
                        No active bulletins at this time.
                    </p>
                    <p style="font-size:0.875rem; color:var(--color-outline); margin-top:0.5rem;">
                        Check back soon for new opportunities.
                    </p>
                    <a href="${pageContext.request.contextPath}/app/tenders" class="btn btn-primary btn-sm" style="margin-top:1.5rem;">
                        Browse All Tenders
                    </a>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Additional Stats Section -->
        <c:if test="${not empty tenderStats}">
            <div style="margin-top:2.5rem;">
                <h3 class="text-title mb-3">Procurement Overview</h3>
                <div style="display:grid; grid-template-columns:1fr 1fr; gap:1rem;">
                    <div class="pg-card" style="padding:1rem;">
                        <div style="font-size:0.6875rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--color-outline);">Total Tenders</div>
                        <div style="font-size:1.5rem; font-weight:700; color:var(--color-primary);">${tenderStats.totalTenders}</div>
                        <div style="font-size:0.75rem; color:var(--color-outline); margin-top:0.25rem;">
                            ${tenderStats.awardedTenders} awarded · ${tenderStats.underEvaluationTenders} under evaluation
                        </div>
                    </div>
                    <div class="pg-card" style="padding:1rem;">
                        <div style="font-size:0.6875rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--color-outline);">Total Value</div>
                        <div style="font-size:1.5rem; font-weight:700; color:var(--color-primary);">
                            LSL <fmt:formatNumber value="${tenderStats.totalEstimatedValue}" type="number" maxFractionDigits="0" groupingUsed="true"/>
                        </div>
                        <div style="font-size:0.75rem; color:var(--color-outline); margin-top:0.25rem;">
                            ${tenderStats.totalBidsSubmitted} bids submitted
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

    </section>



</main>




<!-- FOOTER -->

<footer class="pg-footer">

    <div>

        <div class="pg-footer__brand">
            ProcureGov
        </div>

        <div class="pg-footer__copy">
            © 2026 PROCUREMENT AUTHORITY. MINISTRY OF PUBLIC WORKS, LESOTHO.
            PRECISE. TRANSPARENT. AUTHORITATIVE.
        </div>

    </div>


    <ul class="pg-footer__links">

        <li><a href="${pageContext.request.contextPath}/#">Terms of Service</a></li>
        <li><a href="${pageContext.request.contextPath}/#">Privacy Policy</a></li>
        <li><a href="${pageContext.request.contextPath}/#">Accessibility</a></li>
        <li><a href="${pageContext.request.contextPath}/#">Support</a></li>
        <li><a href="${pageContext.request.contextPath}/#">Freedom of Information</a></li>

    </ul>


</footer>

<!-- Error Alert (if any) -->
<c:if test="${not empty error}">
    <div style="position:fixed; bottom:20px; right:20px; background:var(--color-error); color:white; padding:1rem 1.5rem; border-radius:8px; box-shadow:0 4px 12px rgba(0,0,0,0.15);">
        <span class="material-symbols-outlined" style="vertical-align:middle; margin-right:0.5rem;">error</span>
        ${error}
    </div>
</c:if>

</body>
</html>