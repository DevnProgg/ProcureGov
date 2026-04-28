<%--
award_detail.jsp — Single award notice detail page
Displays comprehensive award information for a specific contract.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en"/>

<c:set var="pageTitle" value="${pageTitle}" scope="request"/>
<c:set var="pageSection" value="${pageSection}" scope="request"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
    <style>
        .official-seal {
            text-align: center;
            padding: 2rem;
            background: linear-gradient(135deg, var(--color-primary-fixed) 0%, var(--color-surface-container-lowest) 100%);
            border-radius: var(--radius-card) var(--radius-card) 0 0;
        }
        @media print {
            .no-print { display: none; }
            .official-seal { background: none; border-bottom: 2px solid #000; }
        }
    </style>
</head>
<body>

<div class="pg-layout">
    <jsp:include page="/WEB-INF/jsp/includes/sidebar.jsp" />

    <main class="pg-main">
        <jsp:include page="/WEB-INF/jsp/includes/topbar.jsp" />

        <div class="pg-content">
            <c:if test="${not empty award}">
                <div class="official-seal">
                    <div class="gazette-seal">
                        <span class="material-symbols-outlined">verified</span>
                        PROCUREMENT GAZETTE
                    </div>
                    <h1 class="text-headline-lg">Award Notice</h1>
                    <p class="text-body">${award.awardNoticeNumber}</p>

                    <div class="no-print" style="display: flex; gap: 0.75rem; justify-content: flex-end; margin-bottom: 1rem;">
                        <button onclick="downloadAward(${award.awardId})" class="btn btn-primary">
                            <span class="material-symbols-outlined">picture_as_pdf</span>
                            Download PDF
                        </button>
                        <button onclick="window.print()" class="btn btn-ghost">
                            <span class="material-symbols-outlined">print</span>
                            Print
                        </button>
                        <a href="${pageContext.request.contextPath}/app/awards" class="btn btn-ghost">
                            <span class="material-symbols-outlined">arrow_back</span>
                            Back to Awards
                        </a>
                    </div>
                </div>

                <div class="pg-card" style="margin-top: 1rem;">
                    <div class="pg-card__body" style="padding: 2rem;">
                        <!-- Award details content similar to the card in awards_list.jsp -->
                        <h2>${award.tenderTitle}</h2>
                        <p><strong>Tender Reference:</strong> ${award.tenderReference}</p>
                        <p><strong>Supplier:</strong> ${award.supplierBusinessName}</p>
                        <p><strong>Awarded Value:</strong>
                            <fmt:formatNumber value="${award.awardedValue}" type="currency" currencySymbol="M"/>
                        </p>
                        <p><strong>Award Date:</strong>
                            <fmt:formatDate value="${award.awardDate}" pattern="dd MMMM yyyy"/>
                        </p>
                        <p><strong>Justification:</strong> ${award.officerJustification}</p>
                        <p><strong>Issued By:</strong> ${award.awardedByName}</p>
                    </div>
                </div>

                <div class="no-print" style="margin-top: 1rem; text-align: center;">
                    <button onclick="window.print()" class="btn btn-primary">Print Award Notice</button>
                    <a href="${pageContext.request.contextPath}/app/awards" class="btn btn-ghost">Back to Awards</a>
                </div>
            </c:if>
        </div>

        <jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />
    </main>
</div>

<script>
    function downloadAward(awardId) {
        window.location.href = '${pageContext.request.contextPath}/app/awards/download?id=' + awardId;
    }
</script>

</body>
</html>