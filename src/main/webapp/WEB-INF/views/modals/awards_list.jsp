<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en"/>

<c:set var="pageTitle" value="${not empty pageTitle ? pageTitle : 'Award Notices'}" scope="request"/>
<c:set var="pageSection" value="${not empty pageSection ? pageSection : 'Public Notices'}" scope="request"/>
<c:set var="activePage" value="notices" scope="request"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
    <style>
        /* Award Card Styles */
        .award-card {
            transition: all 0.2s ease;
            border-left: 4px solid var(--color-tertiary-fixed-dim);
            margin-bottom: 1.5rem;
        }
        .award-card:hover {
            transform: translateY(-2px);
            box-shadow: var(--shadow-float);
        }

        /* Badge Styles */
        .contract-badge {
            background: linear-gradient(135deg, var(--color-tertiary-fixed) 0%, var(--color-tertiary-fixed-dim) 100%);
            padding: 0.25rem 0.75rem;
            border-radius: var(--radius-full);
            font-family: var(--font-label);
            font-size: 0.625rem;
            font-weight: 700;
            letter-spacing: 0.05em;
            display: inline-flex;
            align-items: center;
            gap: 0.375rem;
            white-space: nowrap;
        }

        .contract-badge--ref {
            background: var(--color-primary-fixed);
            color: var(--color-primary);
        }

        /* Gazette Stamp */
        .gazette-stamp {
            position: relative;
            overflow: hidden;
        }

        .gazette-stamp::after {
            content: "AWARDED";
            position: absolute;
            top: 15px;
            right: -35px;
            background: var(--color-tertiary);
            color: var(--color-on-tertiary);
            padding: 0.2rem 2.5rem;
            transform: rotate(45deg);
            font-size: 0.625rem;
            font-weight: 700;
            letter-spacing: 0.12em;
            opacity: 0.08;
            pointer-events: none;
            z-index: 0;
        }

        /* Score Pill */
        .score-pill {
            background: var(--color-primary-fixed);
            color: var(--color-primary);
            padding: 0.2rem 0.625rem;
            border-radius: var(--radius-full);
            font-size: 0.75rem;
            font-weight: 600;
            white-space: nowrap;
        }

        /* Details Box */
        .details-box {
            background: var(--color-surface-container-low);
            padding: 1.25rem;
            border-radius: var(--radius-lg);
            height: 100%;
        }

        .details-box__title {
            font-family: var(--font-label);
            font-size: 0.625rem;
            font-weight: 700;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            color: var(--color-outline);
            margin-bottom: 0.75rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        /* Detail Row */
        .detail-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.5rem 0;
            border-bottom: 1px solid var(--color-surface-container-high);
        }

        .detail-row:last-child {
            border-bottom: none;
        }

        .detail-row__label {
            font-size: 0.8125rem;
            color: var(--color-on-surface-variant);
        }

        .detail-row__value {
            font-size: 0.875rem;
            font-weight: 600;
            color: var(--color-on-surface);
            text-align: right;
        }

        .detail-row__value--highlight {
            color: var(--color-tertiary);
            font-family: var(--font-headline);
            font-size: 1rem;
        }

        /* Justification Block */
        .justification-block {
            margin-top: 1.25rem;
            padding: 1.25rem;
            background: var(--color-surface-container-highest);
            border-radius: var(--radius-lg);
            border-left: 3px solid var(--color-primary-fixed-dim);
        }

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 4rem 2rem;
            background: var(--color-surface-container-lowest);
            border-radius: var(--radius-card);
            box-shadow: var(--shadow-card);
        }

        .empty-state__icon {
            font-size: 4rem;
            color: var(--color-outline-variant);
            margin-bottom: 1rem;
        }

        /* Action Bar */
        .action-bar {
            display: flex;
            gap: 0.75rem;
            justify-content: flex-end;
            margin-bottom: 1.5rem;
            flex-wrap: wrap;
        }

        /* Animation */
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .award-card {
            animation: fadeInUp 0.4s ease-out;
            animation-fill-mode: both;
        }

        .award-card:nth-child(1) { animation-delay: 0.05s; }
        .award-card:nth-child(2) { animation-delay: 0.1s; }
        .award-card:nth-child(3) { animation-delay: 0.15s; }
        .award-card:nth-child(4) { animation-delay: 0.2s; }
        .award-card:nth-child(5) { animation-delay: 0.25s; }

        /* Print Styles */
        .print-only { display: none; }

        @media print {
            .pg-sidebar, .pg-topbar, .pg-app-footer, .no-print {
                display: none !important;
            }
            .pg-main { margin-left: 0 !important; }
            .pg-content { padding: 0 !important; }
            .print-only { display: block !important; }
            .award-card {
                break-inside: avoid;
                page-break-inside: avoid;
                border: 1px solid #ddd;
                box-shadow: none;
                border-left: 2px solid #999;
            }
            body { background: white; }
        }
    </style>
</head>
<body>

<div class="pg-layout">
    <jsp:include page="/WEB-INF/jsp/includes/sidebar.jsp" />

    <main class="pg-main">
        <jsp:include page="/WEB-INF/jsp/includes/topbar.jsp" />

        <div class="pg-content">

            <%-- Action Buttons Bar --%>
            <div class="action-bar no-print">
                <button onclick="downloadGazette()" class="btn btn-primary">
                    <span class="material-symbols-outlined" style="font-size:1.125rem;">picture_as_pdf</span>
                    Download Full Gazette
                </button>
                <button onclick="window.print()" class="btn btn-ghost">
                    <span class="material-symbols-outlined" style="font-size:1.125rem;">print</span>
                    Print Gazette
                </button>
            </div>

            <%-- Statistical Summary --%>
            <c:set var="totalValue" value="0"/>
            <c:forEach var="award" items="${awards}">
                <c:set var="totalValue" value="${totalValue + award.awardedValue}"/>
            </c:forEach>

            <%-- Count unique suppliers --%>
            <c:set var="supplierIds" value=""/>
            <c:set var="uniqueSupplierCount" value="0"/>
            <c:forEach var="award" items="${awards}">
                <c:if test="${!supplierIds.contains(String.valueOf(award.supplierId))}">
                    <c:set var="uniqueSupplierCount" value="${uniqueSupplierCount + 1}"/>
                    <c:set var="supplierIds" value="${supplierIds},${award.supplierId}"/>
                </c:if>
            </c:forEach>
            <c:if test="${sessionScope.user.role_name eq 'PROCUREMENT_OFFICER'}">
            <div style="display:grid; grid-template-columns:repeat(3, 1fr); gap:1rem; margin-top:2rem;">
                <div class="pg-stat-card">
                    <div class="pg-stat-card__label">Total Awards</div>
                    <div class="pg-stat-card__value">${awards.size()}</div>
                    <div class="pg-stat-card__sub">Contracts issued</div>
                </div>
                <div class="pg-stat-card">
                    <div class="pg-stat-card__label">Total Value</div>
                    <div class="pg-stat-card__value">
                        M<fmt:formatNumber value="${totalValue}" maxFractionDigits="0" groupingUsed="true"/>
                    </div>
                    <div class="pg-stat-card__sub">Combined contract value</div>
                </div>
                <div class="pg-stat-card">
                    <div class="pg-stat-card__label">Unique Suppliers</div>
                    <div class="pg-stat-card__value">${uniqueSupplierCount}</div>
                    <div class="pg-stat-card__sub">Businesses awarded</div>
                </div>
            </div>
            </c:if>

            <%-- Gazette Header --%>
            <div style="text-align:center; padding:2.5rem 2rem; margin-bottom:2rem;
                        background:linear-gradient(135deg, var(--color-primary-fixed) 0%, var(--color-surface-container-lowest) 100%);
                        border-radius:var(--radius-card);">
                <div class="gazette-seal" style="display:inline-block; font-size:0.5625rem; font-weight:700;
                            letter-spacing:0.3em; text-transform:uppercase; color:var(--color-primary);
                            border:1px solid rgba(0,63,135,0.15); padding:0.25rem 0.875rem;
                            border-radius:var(--radius-full); margin-bottom:1rem;">
                    <span class="material-symbols-outlined" style="font-size:0.75rem; vertical-align:middle;">verified</span>
                    OFFICIAL GAZETTE
                </div>
                <h1 class="text-headline-lg" style="color:var(--color-primary); margin:0 0 0.5rem;">
                    Award Notices
                </h1>
                <p style="color:var(--color-on-surface-variant); max-width:550px; margin:0 auto; font-size:0.875rem;">
                    Official publication of contract awards issued by the ProcureGov Authority.
                    All awards are final and binding upon publication.
                </p>
            </div>

            <%-- Messages --%>
            <c:if test="${not empty param.success}">
                <div class="pg-alert pg-alert--success mb-6" role="alert">
                    <span class="material-symbols-outlined">check_circle</span>
                    <span>${param.success}</span>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="pg-alert pg-alert--error mb-6" role="alert">
                    <span class="material-symbols-outlined">error</span>
                    <span>${error}</span>
                </div>
            </c:if>

            <%-- Awards List --%>
            <c:choose>
                <c:when test="${empty awards}">
                    <div class="empty-state">
                        <span class="material-symbols-outlined empty-state__icon">gavel</span>
                        <h2 class="text-headline-md mb-2">No Award Notices Published</h2>
                        <p style="color:var(--color-on-surface-variant); margin:0;">
                            There are currently no awarded contracts to display. Check back later for updates.
                        </p>
                    </div>
                </c:when>

                <c:otherwise>
                    <%-- Award Cards --%>
                    <c:forEach var="award" items="${awards}">
                        <div class="award-card pg-card gazette-stamp">
                            <div class="pg-card__body" style="padding:1.5rem;">

                                <%-- Top Section: Badges + Date --%>
                                <div style="display:flex; justify-content:space-between; align-items:flex-start;
                                            flex-wrap:wrap; gap:1rem; margin-bottom:1rem;">

                                    <%-- Badges --%>
                                    <div style="display:flex; align-items:center; gap:0.5rem; flex-wrap:wrap;">
                                        <span class="contract-badge">
                                            <span class="material-symbols-outlined" style="font-size:0.875rem;">description</span>
                                            ${award.awardNoticeNumber}
                                        </span>
                                        <span class="contract-badge contract-badge--ref">
                                            <span class="material-symbols-outlined" style="font-size:0.875rem;">contract</span>
                                            ${award.contractNumber}
                                        </span>
                                    </div>

                                    <%-- Award Date --%>
                                    <div style="text-align:right;">
                                        <div style="font-family:var(--font-label); font-size:0.625rem; font-weight:700;
                                                    letter-spacing:0.08em; text-transform:uppercase; color:var(--color-outline);
                                                    margin-bottom:0.125rem;">
                                            Award Date
                                        </div>
                                        <div style="font-weight:600; font-size:0.9375rem;">
                                            <fmt:formatDate value="${award.awardDate}" pattern="dd MMMM yyyy"/>
                                        </div>
                                    </div>
                                </div>

                                <%-- Tender Title & Reference --%>
                                <h2 class="text-headline-md mb-2" style="margin:0 0 0.5rem;">
                                    ${award.tenderTitle}
                                </h2>
                                <div style="display:flex; align-items:center; gap:1rem; font-size:0.8125rem;
                                            color:var(--color-on-surface-variant); margin-bottom:1.25rem;">
                                    <span style="display:flex; align-items:center; gap:0.25rem;">
                                        <span class="material-symbols-outlined" style="font-size:0.875rem;">inventory_2</span>
                                        Tender Ref: ${award.tenderReference}
                                    </span>
                                    <span style="display:flex; align-items:center; gap:0.25rem;">
                                        <span class="material-symbols-outlined" style="font-size:0.875rem;">category</span>
                                        ${award.tenderCategory}
                                    </span>
                                </div>

                                <%-- Two Column Details Grid --%>
                                <div style="display:grid; grid-template-columns:1fr 1fr; gap:1.25rem; margin-bottom:1.25rem;">

                                    <%-- Left: Awarded To --%>
                                    <div class="details-box">
                                        <div class="details-box__title">
                                            <span class="material-symbols-outlined" style="font-size:1rem;">business</span>
                                            AWARDED TO
                                        </div>
                                        <div style="font-weight:600; font-size:1rem; margin-bottom:0.5rem; color:var(--color-on-surface);">
                                            ${award.supplierBusinessName}
                                        </div>
                                        <div style="font-size:0.8125rem; color:var(--color-on-surface-variant);">
                                            <div style="margin-bottom:0.25rem;">${award.supplierEmail}</div>
                                            <div>${award.supplierPhone}</div>
                                        </div>
                                    </div>

                                    <%-- Right: Contract Details --%>
                                    <div class="details-box">
                                        <div class="details-box__title">
                                            <span class="material-symbols-outlined" style="font-size:1rem;">payments</span>
                                            CONTRACT DETAILS
                                        </div>
                                        <div class="detail-row">
                                            <span class="detail-row__label">Awarded Value</span>
                                            <span class="detail-row__value detail-row__value--highlight">
                                                M<fmt:formatNumber value="${award.awardedValue}" maxFractionDigits="0" groupingUsed="true"/>
                                            </span>
                                        </div>
                                        <div class="detail-row">
                                            <span class="detail-row__label">Bid Price</span>
                                            <span class="detail-row__value">
                                                M<fmt:formatNumber value="${award.bidPrice}" maxFractionDigits="0" groupingUsed="true"/>
                                            </span>
                                        </div>
                                        <div class="detail-row">
                                            <span class="detail-row__label">Delivery</span>
                                            <span class="detail-row__value">${award.deliveryDays} days</span>
                                        </div>
                                        <c:if test="${not empty award.finalScore}">
                                            <div class="detail-row">
                                                <span class="detail-row__label">Eval. Score</span>
                                                <span class="score-pill">
                                                    <fmt:formatNumber value="${award.finalScore}" maxFractionDigits="1"/>%
                                                </span>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>

                                <%-- Justification --%>
                                <div class="justification-block">
                                    <div style="font-family:var(--font-label); font-size:0.625rem; font-weight:700;
                                                letter-spacing:0.08em; text-transform:uppercase; color:var(--color-outline);
                                                margin-bottom:0.5rem; display:flex; align-items:center; gap:0.5rem;">
                                        <span class="material-symbols-outlined" style="font-size:0.875rem;">gavel</span>
                                        OFFICER'S JUSTIFICATION
                                    </div>
                                    <p style="font-style:italic; margin:0 0 0.5rem; color:var(--color-on-surface);
                                              font-size:0.875rem; line-height:1.6;">
                                        "${award.officerJustification}"
                                    </p>
                                    <div style="font-size:0.75rem; color:var(--color-outline);
                                                border-top:1px solid var(--color-surface-container-high);
                                                padding-top:0.5rem;">
                                        Issued by: <strong>${award.awardedByName}</strong>
                                    </div>
                                </div>

                                <%-- Card Actions --%>
                                <div style="display:flex; gap:0.75rem; margin-top:1.25rem; padding-top:1rem;
                                            border-top:1px solid var(--color-surface-container-high);">
                                    <button onclick="downloadAward(${award.awardId})" class="btn btn-tonal btn-sm">
                                        <span class="material-symbols-outlined" style="font-size:1rem;">picture_as_pdf</span>
                                        Download PDF
                                    </button>
                                    <a href="${pageContext.request.contextPath}/app/awards/view?id=${award.awardId}"
                                       class="btn btn-ghost btn-sm" style="text-decoration:none;">
                                        <span class="material-symbols-outlined" style="font-size:1rem;">visibility</span>
                                        View Details
                                    </a>
                                </div>

                                <%-- Print-only footer --%>
                                <div class="print-only" style="margin-top:1rem; text-align:center; font-size:0.625rem;
                                            color:var(--color-outline);">
                                    This is a computer-generated document. No signature is required.
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />
    </main>
</div>

<script>
    function downloadAward(awardId) {
        window.location.href = '${pageContext.request.contextPath}/app/awards/download?id=' + awardId;
    }

    function downloadGazette() {
        window.location.href = '${pageContext.request.contextPath}/app/awards/download-gazette';
    }

    function viewPDFInline(awardId) {
        window.open('${pageContext.request.contextPath}/app/awards/download?id=' + awardId + '&inline=true', '_blank');
    }
</script>

</body>
</html>