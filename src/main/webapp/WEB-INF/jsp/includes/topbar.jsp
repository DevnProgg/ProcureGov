
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="pg-topbar">

    <nav class="pg-topbar__breadcrumb" aria-label="Breadcrumb">
        <span class="material-symbols-outlined" style="font-size:1rem; color:var(--color-outline)">home</span>

        <c:if test="${not empty pageSection}">
            <span><c:out value="${pageSection}"/></span>
            <span class="material-symbols-outlined" style="font-size:0.875rem;">chevron_right</span>
        </c:if>

        <span class="crumb-current">
            <c:out value="${not empty pageTitle ? pageTitle : 'Dashboard'}"/>
        </span>
    </nav>

    <div class="pg-topbar__actions">
        <div class="pg-topbar__search" role="search" aria-label="Search tenders">
            <span class="material-symbols-outlined" style="font-size:1rem;">search</span>
            <span>Search tenders…</span>
        </div>

        <button class="pg-topbar__icon-btn" aria-label="Notifications" title="Notifications">
            <span class="material-symbols-outlined">notifications</span>
            <c:if test="${not empty unreadNotifCount and unreadNotifCount gt 0}">
                <span class="notif-dot" aria-hidden="true"></span>
            </c:if>
        </button>

        <c:if test="${sessionScope.user.role_name eq 'PROCUREMENT_OFFICER'}">
            <button class="pg-topbar__icon-btn" aria-label="Settings" title="Settings">
                <span class="material-symbols-outlined">settings</span>
            </button>
        </c:if>
    </div>

</header>