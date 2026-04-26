
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
        .award-card {
            transition: all 0.2s ease;
            border-left: 3px solid transparent;
        }
        .award-card:hover {
            transform: translateY(-2px);
            border-left-color: var(--color-tertiary);
        }
        .contract-badge {
            background: linear-gradient(135deg, var(--color-tertiary-fixed) 0%, var(--color-tertiary-fixed-dim) 100%);
            padding: 0.25rem 0.75rem;
            border-radius: var(--radius-full);
            font-family: var(--font-label);
            font-size: 0.6875rem;
            font-weight: 700;
            letter-spacing: 0.05em;
            display: inline-flex;
            align-items: center;
            gap: 0.375rem;
        }
        .gazette-stamp {
            position: relative;
            overflow: hidden;
        }
        .gazette-stamp::after {
            content: "AWARDED";
            position: absolute;
            top: 20px;
            right: -30px;
            background: var(--color-tertiary);
            color: var(--color-on-tertiary);
            padding: 0.25rem 2rem;
            transform: rotate(45deg);
            font-size: 0.6875rem;
            font-weight: 700;
            letter-spacing: 0.1em;
            opacity: 0.15;
            pointer-events: none;
        }
        .score-pill {
            background: var(--color-primary-fixed);
            color: var(--color-primary);
            padding: 0.25rem 0.625rem;
            border-radius: var(--radius-full);
            font-size: 0.75rem;
            font-weight: 600;
        }
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
        .award-card:nth-child(6) { animation-delay: 0.3s; }

        .print-only {
            display: none;
        }
        @media print {
            .pg-sidebar, .pg-topbar, .pg-app-footer, .no-print {
                display: none !important;
            }
            .pg-main {
                margin-left: 0 !important;
            }
            .pg-content {
                padding: 0 !important;
            }
            .print-only {
                display: block !important;
            }
            .award-card {
                break-inside: avoid;
                page-break-inside: avoid;
                border: 1px solid #ddd;
                margin-bottom: 1rem;
            }
        }
    </style>
</head>
<body>

<div class="pg-layout">
    <jsp:include page="/WEB-INF/jsp/includes/sidebar.jsp" />

    <main class="pg-main">
        <jsp:include page="/WEB-INF/jsp/includes/topbar.jsp" />
        <div class="no-print" style="display: flex; gap: 0.75rem; margin-bottom: 1.5rem; justify-content: flex-end;">
            <button onclick="downloadGazette()" class="btn btn-primary">
                <span class="material-symbols-outlined" style="font-size: 1rem;">picture_as_pdf</span>
                Download Full Gazette
            </button>

            <!-- Print Button -->
            <div class="no-print" style="display: flex; justify-content: flex-end; margin-bottom: 1.5rem;">
                <button onclick="window.print()" class="btn btn-ghost btn-sm">
                    <span class="material-symbols-outlined" style="font-size: 1rem;">print</span>
                    Print Gazette
                </button>
            </div>
        <div class="pg-content">
            <!-- Gazette Header -->
            <div class="gazette-header" style="background: linear-gradient(135deg, var(--color-primary-fixed) 0%, var(--color-surface-container-lowest) 100%); margin-bottom: 2rem; border-radius: var(--radius-card);">
                <div class="gazette-seal" style="margin-bottom: 1rem;">
                    <span class="material-symbols-outlined" style="font-size: 0.75rem;">verified</span>
                    OFFICIAL GAZETTE
                </div>
                <h1 class="text-headline-lg" style="color: var(--color-primary); margin-bottom: 0.5rem;">
                    Award Notices
                </h1>
                <p class="text-body" style="color: var(--color-on-surface-variant); max-width: 600px; margin: 0 auto;">
                    Official publication of contract awards issued by the ProcureGov Authority.
                    All awards are final and binding upon publication.
                </p>
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

            <!-- Awards List -->
            <c:choose>
                <c:when test="${empty awards}">
                    <div class="pg-card" style="text-align: center; padding: 3rem 2rem;">
                        <span class="material-symbols-outlined" style="font-size: 3rem; color: var(--color-outline); margin-bottom: 1rem;">gavel</span>
                        <h3 class="text-headline-sm" style="margin-bottom: 0.5rem;">No Award Notices Published</h3>
                        <p class="text-body" style="color: var(--color-outline); margin-bottom: 1.5rem;">
                            There are currently no awarded contracts to display. Check back later for updates.
                        </p>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Awards Grid -->
                    <div class="grid-1" style="display: flex; flex-direction: column; gap: 1.5rem;">
                        <c:forEach var="award" items="${awards}">
                            <div class="award-card pg-card gazette-stamp">
                                <div class="pg-card__body" style="padding: 1.5rem;">
                                    <!-- Header with Reference Numbers -->
                                    <div class="flex justify-between items-start" style="flex-wrap: wrap; gap: 1rem; margin-bottom: 1rem;">
                                        <div>
                                            <div class="flex items-center gap-2" style="margin-bottom: 0.5rem; flex-wrap: wrap;">
                                                <span class="contract-badge">
                                                    <span class="material-symbols-outlined" style="font-size: 0.875rem;">description</span>
                                                    ${award.awardNoticeNumber}
                                                </span>
                                                <span class="contract-badge" style="background: var(--color-primary-fixed); color: var(--color-primary);">
                                                    <span class="material-symbols-outlined" style="font-size: 0.875rem;">contract</span>
                                                    ${award.contractNumber}
                                                </span>
                                            </div>
                                            <h2 class="text-headline-md" style="margin-bottom: 0.25rem;">
                                                ${award.tenderTitle}
                                            </h2>
                                            <div class="flex items-center gap-2 text-body-sm text-muted">
                                                <span class="material-symbols-outlined" style="font-size: 0.875rem;">inventory_2</span>
                                                Tender Ref: ${award.tenderReference}
                                                <span class="material-symbols-outlined" style="font-size: 0.875rem; margin-left: 0.5rem;">category</span>
                                                ${award.tenderCategory}
                                            </div>
                                        </div>
                                        <div class="text-right">
                                            <div class="text-kicker" style="color: var(--color-outline); margin-bottom: 0.25rem;">Award Date</div>
                                            <div class="text-title">
                                                <fmt:formatDate value="${award.awardDate}" pattern="dd MMMM yyyy"/>
                                            </div>
                                        </div>

                                        <%-- Add download button to each award card --%>
                                        <div class="flex" style="gap: 0.5rem; margin-top: 1rem;">
                                            <button onclick="downloadAward(${award.awardId})" class="btn btn-tonal btn-sm">
                                                <span class="material-symbols-outlined" style="font-size: 1rem;">picture_as_pdf</span>
                                                Download PDF
                                            </button>
                                            <a href="${pageContext.request.contextPath}/app/awards/view?id=${award.awardId}"
                                               class="btn btn-ghost btn-sm">
                                                <span class="material-symbols-outlined" style="font-size: 1rem;">visibility</span>
                                                View Details
                                            </a>
                                        </div>
                                    </div>

                                    <!-- Award Details Grid -->
                                    <div class="grid-2" style="margin: 1.5rem 0; gap: 1.5rem;">
                                        <!-- Supplier Information -->
                                        <div style="background: var(--color-surface-container-low); padding: 1rem; border-radius: var(--radius-lg);">
                                            <div class="text-kicker" style="margin-bottom: 0.75rem;">
                                                <span class="material-symbols-outlined" style="font-size: 0.875rem;">business</span>
                                                AWARDED TO
                                            </div>
                                            <div class="text-title" style="margin-bottom: 0.25rem;">${award.supplierBusinessName}</div>
                                            <div class="text-body-sm text-muted">
                                                <div>${award.supplierEmail}</div>
                                                <div>${award.supplierPhone}</div>
                                            </div>
                                        </div>

                                        <!-- Contract Value & Performance -->
                                        <div style="background: var(--color-surface-container-low); padding: 1rem; border-radius: var(--radius-lg);">
                                            <div class="text-kicker" style="margin-bottom: 0.75rem;">
                                                <span class="material-symbols-outlined" style="font-size: 0.875rem;">payments</span>
                                                CONTRACT DETAILS
                                            </div>
                                            <div class="flex justify-between" style="margin-bottom: 0.5rem;">
                                                <span class="text-body-sm text-muted">Awarded Value:</span>
                                                <span class="text-title" style="color: var(--color-tertiary);">
                                                    <fmt:formatNumber value="${award.awardedValue}" type="currency" currencySymbol="M" maxFractionDigits="0"/>
                                                </span>
                                            </div>
                                            <div class="flex justify-between" style="margin-bottom: 0.5rem;">
                                                <span class="text-body-sm text-muted">Bid Price:</span>
                                                <span class="text-body">
                                                    <fmt:formatNumber value="${award.bidPrice}" type="currency" currencySymbol="M" maxFractionDigits="0"/>
                                                </span>
                                            </div>
                                            <div class="flex justify-between">
                                                <span class="text-body-sm text-muted">Delivery Timeline:</span>
                                                <span class="text-body">${award.deliveryDays} days</span>
                                            </div>
                                            <c:if test="${not empty award.finalScore}">
                                                <div class="flex justify-between" style="margin-top: 0.5rem; padding-top: 0.5rem; border-top: 1px solid var(--color-surface-container-high);">
                                                    <span class="text-body-sm text-muted">Evaluation Score:</span>
                                                    <span class="score-pill">
                                                        <fmt:formatNumber value="${award.finalScore}" maxFractionDigits="1"/>%
                                                    </span>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>

                                    <!-- Justification -->
                                    <div style="margin-top: 1rem; padding: 1rem; background: var(--color-surface-container-highest); border-radius: var(--radius-lg);">
                                        <div class="text-kicker" style="margin-bottom: 0.5rem;">
                                            <span class="material-symbols-outlined" style="font-size: 0.875rem;">gavel</span>
                                            OFFICER'S JUSTIFICATION
                                        </div>
                                        <p class="text-body" style="font-style: italic; margin: 0;">
                                            "${award.officerJustification}"
                                        </p>
                                        <div class="text-body-sm text-muted" style="margin-top: 0.5rem;">
                                            Issued by: ${award.awardedByName}
                                        </div>
                                    </div>

                                    <!-- Footer with Legal Disclaimer -->
                                    <div class="print-only" style="margin-top: 1rem; text-align: center; font-size: 0.6875rem; color: var(--color-outline);">
                                        This is a computer-generated document. No signature is required.
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Statistical Summary -->
                    <div class="grid-3" style="margin-top: 2rem;">
                        <div class="pg-stat-card">
                            <div class="pg-stat-card__label">Total Awards</div>
                            <div class="pg-stat-card__value">${awards.size()}</div>
                            <div class="pg-stat-card__sub">Contracts issued</div>
                        </div>
                        <div class="pg-stat-card">
                            <div class="pg-stat-card__label">Total Value</div>
                            <div class="pg-stat-card__value">
                                <fmt:formatNumber value="${awards.stream().map(a -> a.awardedValue).reduce(0, (a,b) -> a.add(b))}"
                                                  type="currency" currencySymbol="M" maxFractionDigits="0"/>
                            </div>
                            <div class="pg-stat-card__sub">Combined contract value</div>
                        </div>
                        <div class="pg-stat-card">
                            <div class="pg-stat-card__label">Unique Suppliers</div>
                            <div class="pg-stat-card__value">
                                ${awards.stream().map(a -> a.supplierId).distinct().count()}
                            </div>
                            <div class="pg-stat-card__sub">Businesses awarded</div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />
    </main>
</div>

<script>
    // Add print styles dynamically
    const printStyle = document.createElement('style');
    printStyle.textContent = `
        @media print {
            @page {
                size: A4;
                margin: 2cm;
            }
            body {
                background: white;
            }
            .pg-content {
                padding: 0 !important;
            }
            .award-card {
                box-shadow: none;
                border: 1px solid #ddd;
                margin-bottom: 20px;
            }
        }
    `;
    document.head.appendChild(printStyle);
</script>

<%-- Add JavaScript functions for PDF download --%>
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