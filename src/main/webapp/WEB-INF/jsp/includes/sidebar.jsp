<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<aside class="pg-sidebar" role="navigation" aria-label="Main navigation" xmlns:c="http://www.w3.org/1999/XSL/Transform">

    <div class="pg-sidebar__brand">
        Procurement<span style="font-weight:400; font-style:normal;">Gov</span>
        <span class="badge">Alpha</span>
    </div>

    <c:if test="${sessionScope.user.role_name == 'PROCUREMENT_OFFICER' || sessionScope.user.role_name == 'BOARD_MEMBER'}">
        <span class="pg-sidebar__section-label">Operations</span>
        <nav class="pg-sidebar__nav">
            <a href="${pageContext.request.contextPath}/app/officer/dashboard"
               class="pg-sidebar__nav-item ${activePage eq 'dashboard' ? 'active' : ''}">
                <span class="material-symbols-outlined">newspaper</span>
                Dashboard
            </a>

            <a href="${pageContext.request.contextPath}/app/tenders"
               class="pg-sidebar__nav-item ${activePage eq 'tenders' ? 'active' : ''}">
                <span class="material-symbols-outlined">gavel</span>
                Tenders
                    <span class="badge-count">+</span>
            </a>

            <a href="${pageContext.request.contextPath}/app/evaluations/panel"
               class="pg-sidebar__nav-item ${activePage eq 'evaluation' ? 'active' : ''}">
                <span class="material-symbols-outlined">fact_check</span>
                Evaluations
                    <span class="badge-count">+</span>
            </a>

<!--            <a href="${pageContext.request.contextPath}/app/evaluations/results"-->
<!--               class="pg-sidebar__nav-item ${activePage eq 'leaderboard' ? 'active' : ''}">-->
<!--                <span class="material-symbols-outlined">military_tech</span>-->
<!--                Results-->
<!--            </a>-->

            <c:if test="${sessionScope.user.role_name eq 'PROCUREMENT_OFFICER'}">
                <a href="${pageContext.request.contextPath}/app/officer/award"
                   class="pg-sidebar__nav-item ${activePage eq 'award' ? 'active' : ''}">
                    <span class="material-symbols-outlined">workspace_premium</span>
                    Award Contract
                </a>
            </c:if>
        </nav>

        <span class="pg-sidebar__section-label">Archive</span>
        <nav class="pg-sidebar__nav">
            <a href="${pageContext.request.contextPath}/app/awards"
               class="pg-sidebar__nav-item ${activePage eq 'notices' ? 'active' : ''}">
                <span class="material-symbols-outlined">description</span>
                Award Notices
            </a>
        </nav>
    </c:if>

    <c:if test="${sessionScope.user.role_name eq 'SUPPLIER'}">
        <span class="pg-sidebar__section-label">My Portal</span>
        <nav class="pg-sidebar__nav">
            <a href="${pageContext.request.contextPath}/app/supplier/dashboard"
               class="pg-sidebar__nav-item ${activePage eq 'dashboard' ? 'active' : ''}">
                <span class="material-symbols-outlined">home</span>
                Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/app/tenders"
               class="pg-sidebar__nav-item ${activePage eq 'tenders' ? 'active' : ''}">
                <span class="material-symbols-outlined">gavel</span>
                Browse Tenders
            </a>
            <a href="${pageContext.request.contextPath}/app/supplier/bids"
               class="pg-sidebar__nav-item ${activePage eq 'my-bids' ? 'active' : ''}">
                <span class="material-symbols-outlined">assignment</span>
                My Bids
            </a>
            <a href="${pageContext.request.contextPath}/app/awards/"
               class="pg-sidebar__nav-item ${activePage eq 'notices' ? 'active' : ''}">
                <span class="material-symbols-outlined">military_tech</span>
                Award Notices
            </a>
        </nav>
    </c:if>

    <div class="pg-sidebar__footer" style="margin-top: auto;">
        <div class="pg-sidebar__user-card">
            <div class="pg-sidebar__avatar">
                <c:out value="${sessionScope.user.role_name == 'SUPPLIER' ? sessionScope.user.business_name.substring(0,1) : sessionScope.user.full_names.substring(0,1)}" />
            </div>
            <div class="pg-sidebar__user-info">
                <div class="pg-sidebar__user-name">
                    <c:out value="${sessionScope.user.role_name == 'SUPPLIER' ? sessionScope.user.business_name : sessionScope.user.full_names}" />
                </div>
                <div class="pg-sidebar__user-role">
                    <c:choose>
                        <c:when test="${sessionScope.user.role_name eq 'PROCUREMENT_OFFICER'}">Procurement Officer</c:when>
                        <c:when test="${sessionScope.user.role_name eq 'BOARD_MEMBER'}">Eval. Committee</c:when>
                        <c:when test="${sessionScope.user.role_name eq 'SUPPLIER'}">Supplier</c:when>
                        <c:otherwise>${sessionScope.user.role_name}</c:otherwise>
                    </c:choose>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/auth/logout"
               title="Sign out"
               class="pg-topbar__icon-btn"
               style="flex-shrink:0; text-decoration:none;">
                <span class="material-symbols-outlined" style="font-size:1.125rem;">logout</span>
            </a>
        </div>
    </div>
</aside>