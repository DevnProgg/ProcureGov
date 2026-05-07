
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jsp/includes/head.jsp" %>
    <style>
        /* Award Contract Specific Styles */
        .award-layout {
            display: grid;
            grid-template-columns: 1fr 380px;
            gap: 1.5rem;
            min-height: calc(100vh - var(--topbar-height) - 4rem);
        }

        @media (max-width: 1024px) {
            .award-layout {
                grid-template-columns: 1fr;
            }
        }

        /* Bid Selection Card */
        .bid-option-card {
            background: var(--color-surface-container-lowest);
            border-radius: var(--radius-card);
            padding: 1.5rem;
            box-shadow: var(--shadow-card);
            cursor: pointer;
            transition: all 0.2s ease;
            border: 2px solid transparent;
            position: relative;
        }

        .bid-option-card:hover {
            box-shadow: var(--shadow-float);
            transform: translateY(-2px);
        }

        .bid-option-card.selected {
            border-color: var(--color-primary);
            background: linear-gradient(135deg, var(--color-primary-fixed) 0%, var(--color-surface-container-lowest) 100%);
        }

        .bid-option-card.selected::before {
            content: 'check_circle';
            font-family: 'Material Symbols Outlined';
            position: absolute;
            top: 1rem;
            right: 1rem;
            font-size: 1.5rem;
            color: var(--color-primary);
            font-variation-settings: 'FILL' 1, 'wght' 700;
        }

        .bid-option-card.awarded {
            opacity: 0.7;
            cursor: not-allowed;
            border-color: var(--color-secondary-fixed-dim);
            background: var(--color-surface-container-low);
        }

        .bid-option-card.awarded::after {
            content: 'AWARDED';
            position: absolute;
            top: 1rem;
            right: 1rem;
            font-family: var(--font-label);
            font-size: 0.625rem;
            font-weight: 700;
            letter-spacing: 0.1em;
            background: var(--color-secondary-fixed);
            color: var(--color-on-secondary-fixed-variant);
            padding: 0.25rem 0.625rem;
            border-radius: var(--radius-full);
        }

        /* Supplier Info Header */
        .bid-supplier-header {
            display: flex;
            align-items: center;
            gap: 1rem;
            margin-bottom: 1.25rem;
        }

        .bid-supplier-avatar {
            width: 3rem;
            height: 3rem;
            border-radius: var(--radius-full);
            background: linear-gradient(135deg, var(--color-primary), var(--color-primary-container));
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--color-on-primary);
            font-family: var(--font-headline);
            font-size: 1.25rem;
            font-weight: 700;
            flex-shrink: 0;
        }

        .bid-supplier-name {
            font-family: var(--font-headline);
            font-size: 1.125rem;
            font-weight: 600;
            color: var(--color-on-surface);
            margin-bottom: 0.125rem;
        }

        .bid-supplier-reg {
            font-size: 0.75rem;
            color: var(--color-outline);
            font-variant-numeric: tabular-nums;
        }

        /* Score Grid */
        .score-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 0.75rem;
            margin-bottom: 1rem;
        }

        .score-item {
            background: var(--color-surface-container-low);
            border-radius: var(--radius-lg);
            padding: 0.875rem;
            text-align: center;
        }

        .score-label {
            font-size: 0.625rem;
            font-weight: 700;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            color: var(--color-outline);
            margin-bottom: 0.375rem;
        }

        .score-value {
            font-family: var(--font-headline);
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--color-primary);
            line-height: 1;
        }

        .score-value.excellent { color: var(--color-secondary); }
        .score-value.good { color: var(--color-primary); }
        .score-value.average { color: var(--color-tertiary); }

        .score-max {
            font-size: 0.6875rem;
            color: var(--color-outline);
        }

        /* Final Score Badge */
        .final-score-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.5rem 1rem;
            background: var(--color-primary-fixed);
            border-radius: var(--radius-full);
            font-family: var(--font-headline);
            font-size: 1rem;
            font-weight: 700;
            color: var(--color-primary);
            margin-bottom: 0.75rem;
        }

        .final-score-badge.top-ranked {
            background: var(--color-secondary-fixed);
            color: var(--color-on-secondary-fixed-variant);
        }

        /* Bid Details */
        .bid-details-row {
            display: flex;
            justify-content: space-between;
            padding: 0.5rem 0;
            border-bottom: 1px solid var(--color-surface-container-high);
            font-size: 0.8125rem;
        }

        .bid-details-row:last-child {
            border-bottom: none;
        }

        .detail-label {
            color: var(--color-outline);
            font-weight: 500;
        }

        .detail-value {
            color: var(--color-on-surface);
            font-weight: 600;
            text-align: right;
        }

        .detail-value.price {
            font-family: var(--font-headline);
            font-size: 0.9375rem;
            color: var(--color-primary);
        }

        /* Award Panel (Right Side) */
        .award-panel {
            position: sticky;
            top: calc(var(--topbar-height) + 1.5rem);
            max-height: calc(100vh - var(--topbar-height) - 3rem);
            overflow-y: auto;
        }

        .award-form-card {
            background: var(--color-surface-container-lowest);
            border-radius: var(--radius-card);
            box-shadow: var(--shadow-float);
            overflow: hidden;
        }

        .award-form-header {
            background: linear-gradient(135deg, var(--color-primary), var(--color-primary-container));
            padding: 1.5rem;
            color: white;
        }

        .award-form-header h2 {
            font-family: var(--font-headline);
            font-size: 1.25rem;
            font-weight: 700;
            margin: 0 0 0.25rem;
            color: white;
        }

        .award-form-header p {
            font-size: 0.8125rem;
            opacity: 0.9;
            margin: 0;
        }

        .award-form-body {
            padding: 1.5rem;
        }

        .selected-bid-preview {
            background: var(--color-primary-fixed);
            border-radius: var(--radius-lg);
            padding: 1rem;
            margin-bottom: 1.25rem;
            display: none;
        }

        .selected-bid-preview.visible {
            display: block;
        }

        .selected-bid-preview .preview-label {
            font-size: 0.625rem;
            font-weight: 700;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            color: var(--color-on-primary-fixed-variant);
            margin-bottom: 0.375rem;
        }

        .selected-bid-preview .preview-name {
            font-weight: 600;
            color: var(--color-on-primary-fixed);
            font-size: 0.9375rem;
        }

        .selected-bid-preview .preview-value {
            font-family: var(--font-headline);
            font-size: 1.25rem;
            font-weight: 700;
            color: var(--color-primary);
            margin-top: 0.25rem;
        }

        /* Form within award panel */
        .award-form-body .form-group {
            margin-bottom: 1.25rem;
        }

        .amount-input-wrapper {
            position: relative;
        }

        .amount-input-wrapper .currency-symbol {
            position: absolute;
            left: 1rem;
            top: 50%;
            transform: translateY(-50%);
            font-family: var(--font-headline);
            font-weight: 700;
            color: var(--color-outline);
            font-size: 1rem;
        }

        .amount-input-wrapper .form-input {
            padding-left: 2.25rem;
            font-family: var(--font-headline);
            font-size: 1.125rem;
            font-weight: 700;
            color: var(--color-primary);
        }

        /* Justification textarea */
        .justification-counter {
            font-size: 0.6875rem;
            color: var(--color-outline);
            text-align: right;
            margin-top: 0.25rem;
        }

        /* Empty state */
        .empty-state {
            text-align: center;
            padding: 3rem 2rem;
            background: var(--color-surface-container-lowest);
            border-radius: var(--radius-card);
            box-shadow: var(--shadow-card);
        }

        .empty-state .material-symbols-outlined {
            font-size: 3rem;
            color: var(--color-outline-variant);
            margin-bottom: 1rem;
        }

        .empty-state h3 {
            font-family: var(--font-headline);
            font-size: 1.25rem;
            color: var(--color-on-surface);
            margin: 0 0 0.5rem;
        }

        .empty-state p {
            color: var(--color-on-surface-variant);
            font-size: 0.875rem;
            margin: 0;
        }

        /* Tender info banner */
        .tender-info-banner {
            background: var(--color-surface-container-lowest);
            border-radius: var(--radius-card);
            padding: 1.25rem 1.5rem;
            box-shadow: var(--shadow-card);
            margin-bottom: 1.5rem;
        }

        .tender-info-banner .tender-ref {
            font-family: var(--font-label);
            font-size: 0.6875rem;
            font-weight: 700;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            color: var(--color-secondary);
            margin-bottom: 0.25rem;
        }

        .tender-info-banner .tender-title {
            font-family: var(--font-headline);
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--color-on-surface);
            line-height: 1.2;
        }

        /* Ranking indicator */
        .rank-indicator {
            display: inline-flex;
            align-items: center;
            gap: 0.375rem;
            font-size: 0.75rem;
            font-weight: 600;
            padding: 0.25rem 0.625rem;
            border-radius: var(--radius-full);
        }

        .rank-indicator.rank-1 {
            background: #FFF8E1;
            color: #F5A623;
        }

        .rank-indicator.rank-2 {
            background: #F5F5F5;
            color: #A0AEC0;
        }

        .rank-indicator.rank-3 {
            background: #FFF3ED;
            color: #C05621;
        }

        /* Responsive adjustments */
        @media (max-width: 640px) {
            .score-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>

<div class="pg-layout">
    <%@ include file="/WEB-INF/jsp/includes/sidebar.jsp" %>
    <c:set var="activePage" value="award" scope="request"/>

    <main class="pg-main">
        <%-- Include Topbar --%>
        <%@ include file="/WEB-INF/jsp/includes/topbar.jsp" %>

        <div class="pg-content">
            <%-- Page Header --%>
            <div class="pg-page-header">
                <div class="pg-page-header__kicker">
                    <span class="material-symbols-outlined" style="font-size:0.75rem;">workspace_premium</span>
                    Contract Award
                </div>
                <h1 class="pg-page-header__title">Award Tender Contract</h1>
                <p class="pg-page-header__subtitle">
                    Select the winning bid based on evaluation scores and award the contract.
                </p>
            </div>

            <%-- Tender Selection Form (if no tender is pre-selected) --%>
            <c:if test="${empty tender}">
                <div class="mb-6">
                    <form method="get" action="${pageContext.request.contextPath}/app/officer/award" data-validate="true" class="flex gap-3 items-end">
                        <div class="form-group" style="flex:1; max-width:400px;">
                            <label class="form-label required" for="tenderSelect">Select Tender</label>
                            <select name="tenderId" id="tenderSelect" class="form-select" required
                                    onchange="this.form.submit()">
                                <option value="">— Choose a tender —</option>
                                <c:forEach var="t" items="${evaluatedTenders}">
                                    <option value="${t.tender_id}" ${t.tender_id eq param.tenderId ? 'selected' : ''}>
                                    ${t.reference_number} — ${t.title}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary">
                            <span class="material-symbols-outlined">search</span>
                            View Bids
                        </button>
                    </form>
                </div>
            </c:if>

            <c:choose>
                <%-- No tender selected yet --%>
                <c:when test="${empty tender and empty param.tenderId}">
                    <div class="empty-state">
                        <span class="material-symbols-outlined">gavel</span>
                        <h3>Select a Tender</h3>
                        <p>Choose an evaluated tender above to review bids and award the contract.</p>
                    </div>
                </c:when>

                <%-- No bids available --%>
                <c:when test="${empty bids}">
                    <div class="empty-state">
                        <span class="material-symbols-outlined">inbox</span>
                        <h3>No Bids Available</h3>
                        <p>This tender has no bids submitted yet, or all bids have already been awarded.</p>
                    </div>
                </c:when>

                <%-- Bids available for selection --%>
                <c:otherwise>
                    <div class="award-layout">
                        <%-- Left Column: Bid Options --%>
                        <div>
                            <%-- Tender Info Banner --%>
                            <div class="tender-info-banner">
                                <div class="tender-ref">${tender.reference_number}</div>
                                <div class="tender-title">${tender.title}</div>
                                <div class="flex items-center gap-3 mt-3" style="font-size:0.8125rem; color:var(--color-on-surface-variant);">
                                    <span class="flex items-center gap-1">
                                        <span class="material-symbols-outlined" style="font-size:0.875rem;">category</span>
                                        ${tender.category}
                                    </span>
                                    <span class="flex items-center gap-1">
                                        <span class="material-symbols-outlined" style="font-size:0.875rem;">payments</span>
                                        Est. Value: <fmt:formatNumber value="${tender.estimated_value}" type="currency" currencySymbol="LSL"/>
                                    </span>
                                    <span class="flex items-center gap-1">
                                        <span class="material-symbols-outlined" style="font-size:0.875rem;">group</span>
                                        ${bids.size()} bid${bids.size() gt 1 ? 's' : ''}
                                    </span>
                                </div>
                            </div>

                            <%-- Bid Cards --%>
                            <div class="flex flex-col gap-3" id="bidOptionsContainer">
                                <c:forEach var="bid" items="${bids}" varStatus="status">
                                    <div class="bid-option-card ${bid.awarded ? 'awarded' : ''}"
                                         data-bid-id="${bid.bidId}"
                                         data-supplier-name="${bid.businessName}"
                                         data-bid-price="${bid.price}"
                                         data-evaluation-score="${bid.evaluationScore}"
                                         data-delivery-days="${bid.deliveryDays}"
                                         onclick="selectBid(this, ${bid.bidId}, '${bid.businessName}', ${bid.price}, ${bid.evaluationScore})"
                                         role="button"
                                         tabindex="0"
                                         aria-label="Select bid from ${bid.businessName}">

                                        <%-- Supplier Header --%>
                                        <div class="bid-supplier-header">
                                            <div class="bid-supplier-avatar">
                                                ${bid.businessName.substring(0,1).toUpperCase()}
                                            </div>
                                            <div style="flex:1; min-width:0;">
                                                <div class="bid-supplier-name">
                                                    <c:out value="${bid.businessName}"/>
                                                </div>
                                                <div class="bid-supplier-reg">
                                                    Reg: ${bid.regNumber} ${bid.awarded ? '— ALREADY AWARDED' : ''}
                                                </div>
                                            </div>
                                            <c:if test="${!bid.awarded}">
                                                <div class="rank-indicator rank-${status.index + 1}">
                                                    <span class="material-symbols-outlined" style="font-size:0.875rem;">
                                                        ${status.index eq 0 ? 'military_tech' : (status.index eq 1 ? 'social_leaderboard' : 'leaderboard')}
                                                    </span>
                                                    #${status.index + 1}
                                                </div>
                                            </c:if>
                                        </div>

                                        <%-- Final Score --%>
                                        <div class="final-score-badge ${status.index eq 0 ? 'top-ranked' : ''}">
                                            <span class="material-symbols-outlined" style="font-size:1rem;">score</span>
                                            Final Score:
                                            <fmt:formatNumber value="${bid.evaluationScore}" maxFractionDigits="2" minFractionDigits="2"/>
                                            <span style="font-size:0.75rem; opacity:0.7;">/100</span>
                                        </div>

                                        <%-- Score Grid --%>
                                        <div class="score-grid">
                                            <div class="score-item">
                                                <div class="score-label">Price Score</div>
                                                <div class="score-value ${bid.priceScore ge 35 ? 'excellent' : (bid.priceScore ge 25 ? 'good' : 'average')}">
                                                    <fmt:formatNumber value="${bid.priceScore}" maxFractionDigits="1"/>
                                                </div>
                                                <div class="score-max">/40</div>
                                            </div>
                                            <div class="score-item">
                                                <div class="score-label">Technical Score</div>
                                                <div class="score-value ${bid.technicalScore ge 35 ? 'excellent' : (bid.technicalScore ge 25 ? 'good' : 'average')}">
                                                    <fmt:formatNumber value="${bid.technicalScore}" maxFractionDigits="1"/>
                                                </div>
                                                <div class="score-max">/40</div>
                                            </div>
                                            <div class="score-item">
                                                <div class="score-label">Delivery Score</div>
                                                <div class="score-value ${bid.deliveryScore ge 17 ? 'excellent' : (bid.deliveryScore ge 12 ? 'good' : 'average')}">
                                                    <fmt:formatNumber value="${bid.deliveryScore}" maxFractionDigits="1"/>
                                                </div>
                                                <div class="score-max">/20</div>
                                            </div>
                                        </div>

                                        <%-- Bid Details --%>
                                        <div class="bid-details-row">
                                            <span class="detail-label">Bid Price</span>
                                            <span class="detail-value price">
                                                <fmt:formatNumber value="${bid.price}" type="currency" currencySymbol="LSL"/>
                                            </span>
                                        </div>
                                        <div class="bid-details-row">
                                            <span class="detail-label">Delivery Timeline</span>
                                            <span class="detail-value">${bid.deliveryDays} days</span>
                                        </div>
                                        <div class="bid-details-row">
                                            <span class="detail-label">Submitted</span>
                                            <span class="detail-value">
                                                <fmt:formatDate value="${bid.submittedAt}" pattern="dd MMM yyyy, HH:mm"/>
                                            </span>
                                        </div>
                                        <div class="bid-details-row">
                                            <span class="detail-label">Compliance</span>
                                            <span class="detail-value" style="color: var(--color-secondary);">
                                                <span class="material-symbols-outlined" style="font-size:0.875rem;">verified</span>
                                                Compliant
                                            </span>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <%-- Right Column: Award Form Panel --%>
                        <div class="award-panel">
                            <div class="award-form-card">
                                <div class="award-form-header">
                                    <h2>Award Contract</h2>
                                    <p>Complete the award details below</p>
                                </div>
                                <div class="award-form-body">
                                    <form id="awardForm" method="post" data-validate="true"
                                          action="${pageContext.request.contextPath}/app/awards"
                                          onsubmit="return validateAwardForm()">

                                        <input type="hidden" name="tenderId" value="${tender.tender_id}">
                                        <input type="hidden" name="bidId" id="selectedBidId" value="">
                                        <input type="hidden" name="awardedValue" id="awardedValueInput" value="">

                                        <%-- Selected Bid Preview --%>
                                        <div class="selected-bid-preview" id="selectedBidPreview">
                                            <div class="preview-label">
                                                <span class="material-symbols-outlined" style="font-size:0.75rem;">check_circle</span>
                                                Selected Bid
                                            </div>
                                            <div class="preview-name" id="previewSupplierName">—</div>
                                            <div class="preview-value" id="previewBidPrice">—</div>
                                        </div>

                                        <%-- No bid selected message --%>
                                        <div class="pg-alert pg-alert--warning mb-4" id="noBidMessage">
                                            <span class="material-symbols-outlined">info</span>
                                            <span>Select a bid from the left panel to proceed with the award.</span>
                                        </div>

                                        <%-- Award Value --%>
                                        <div class="form-group">
                                            <label class="form-label required" for="awardValue">Contract Award Value</label>
                                            <div class="amount-input-wrapper">
                                                <span class="currency-symbol">LSL</span>
                                                <input type="number"
                                                       id="awardValue"
                                                       name="displayAwardValue"
                                                       class="form-input"
                                                       placeholder="0.00"
                                                       step="0.01"
                                                       min="0"
                                                       required
                                                       disabled
                                                       oninput="syncAwardValue(this.value)">
                                            </div>
                                            <div class="form-hint">
                                                <span class="material-symbols-outlined" style="font-size:0.75rem;">lightbulb</span>
                                                Defaults to the bid price. You may adjust if necessary.
                                            </div>
                                        </div>

                                        <%-- Justification --%>
                                        <div class="form-group">
                                            <label class="form-label required" for="justification">
                                                Award Justification
                                            </label>
                                            <textarea id="justification"
                                                      name="justification"
                                                      class="form-textarea"
                                                      rows="5"
                                                      maxlength="1000"
                                                      required
                                                      disabled
                                                      placeholder="Provide a detailed justification for awarding this contract..."
                                                      oninput="updateCharCount(this)"></textarea>
                                            <div class="justification-counter">
                                                <span id="charCount">0</span>/1000 characters
                                            </div>
                                        </div>

                                        <%-- Confirmation Checkbox --%>
                                        <div class="form-group">
                                            <label style="display:flex; align-items:flex-start; gap:0.75rem; cursor:pointer;">
                                                <input type="checkbox"
                                                       id="confirmAward"
                                                       required
                                                       disabled
                                                       style="margin-top:0.2rem; accent-color:var(--color-primary);">
                                                <span style="font-size:0.8125rem; color:var(--color-on-surface); line-height:1.5;">
                                                    I confirm that I have reviewed all evaluation scores and bid details.
                                                    I understand this action will create a binding contract award that cannot be undone.
                                                </span>
                                            </label>
                                        </div>

                                        <%-- Submit Button --%>
                                        <button type="submit"
                                                class="btn btn-primary btn-full btn-lg"
                                                id="awardSubmitBtn"
                                                disabled>
                                            <span class="material-symbols-outlined">workspace_premium</span>
                                            Award Contract
                                        </button>

                                        <%-- Success/Error Messages --%>
                                        <c:if test="${not empty error}">
                                            <div class="pg-alert pg-alert--error mt-4">
                                                <span class="material-symbols-outlined">error</span>
                                                <span>${error}</span>
                                            </div>
                                        </c:if>
                                    </form>
                                </div>
                            </div>

                            <%-- Evaluation Summary Card --%>
                            <div class="pg-card mt-4">
                                <div class="pg-card__header">
                                    <h3 class="text-title">Evaluation Summary</h3>
                                </div>
                                <div class="pg-card__body">
                                    <div class="text-body-sm" style="color: var(--color-on-surface-variant);">
                                        <p style="margin:0 0 0.75rem;">
                                            <strong>Total Evaluators:</strong> ${totalEvaluators}
                                        </p>
                                        <p style="margin:0 0 0.75rem;">
                                            <strong>Completed Evaluations:</strong> ${completedEvaluations} / ${totalEvaluators}
                                        </p>
                                        <div class="score-bar-track mb-2">
                                            <div class="score-bar-fill" style="width: ${completionPercentage}%;"></div>
                                        </div>
                                        <p style="font-size:0.75rem; color:var(--color-outline); margin:0;">
                                            ${completedEvaluations ge totalEvaluators ? '✓ All evaluations complete' : 'Evaluation in progress'}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- Include Footer --%>
        <%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
    </main>
</div>

<script>
    /**
     * Award Contract Page — Interactive Functionality
     */

        // State
    let selectedBidId = null;
    let selectedBidPrice = 0;

    /**
     * Handle bid selection
     */
    function selectBid(cardElement, bidId, supplierName, bidPrice, evaluationScore) {
        // Don't allow selecting awarded bids
        if (cardElement.classList.contains('awarded')) {
            return;
        }

        // Remove selection from all cards
        document.querySelectorAll('.bid-option-card').forEach(card => {
            card.classList.remove('selected');
        });

        // Add selection to clicked card
        cardElement.classList.add('selected');

        // Update state
        selectedBidId = bidId;
        selectedBidPrice = bidPrice;

        // Update hidden inputs
        document.getElementById('selectedBidId').value = bidId;
        document.getElementById('awardedValueInput').value = bidPrice;

        // Show preview
        const preview = document.getElementById('selectedBidPreview');
        preview.classList.add('visible');
        document.getElementById('previewSupplierName').textContent = supplierName;
        document.getElementById('previewBidPrice').textContent =
            'LSL ' + parseFloat(bidPrice).toLocaleString('en', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            });

        // Hide "no bid" message
        document.getElementById('noBidMessage').style.display = 'none';

        // Enable form fields
        const awardValue = document.getElementById('awardValue');
        const justification = document.getElementById('justification');
        const confirmAward = document.getElementById('confirmAward');
        const submitBtn = document.getElementById('awardSubmitBtn');

        awardValue.disabled = false;
        awardValue.value = bidPrice;
        justification.disabled = false;
        confirmAward.disabled = false;
        submitBtn.disabled = false;

        // Focus on justification
        justification.focus();

        // Scroll award panel into view on mobile
        if (window.innerWidth <= 1024) {
            document.querySelector('.award-panel').scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    }

    /**
     * Sync award value between display input and hidden input
     */
    function syncAwardValue(value) {
        document.getElementById('awardedValueInput').value = value || 0;
    }

    /**
     * Update character count for justification
     */
    function updateCharCount(textarea) {
        const count = textarea.value.length;
        document.getElementById('charCount').textContent = count;

        if (count > 900) {
            document.getElementById('charCount').style.color = 'var(--color-tertiary)';
        } else {
            document.getElementById('charCount').style.color = 'var(--color-outline)';
        }
    }

    /**
     * Validate form before submission
     */
    function validateAwardForm() {
        const bidId = document.getElementById('selectedBidId').value;
        const awardValue = parseFloat(document.getElementById('awardValue').value);
        const justification = document.getElementById('justification').value.trim();
        const confirmed = document.getElementById('confirmAward').checked;

        // Check bid selected
        if (!bidId) {
            alert('Please select a bid from the list.');
            return false;
        }

        // Validate award value
        if (isNaN(awardValue) || awardValue <= 0) {
            alert('Please enter a valid contract award value.');
            return false;
        }

        // Validate justification
        if (justification.length < 20) {
            alert('Please provide a detailed justification (at least 20 characters).');
            return false;
        }

        // Validate confirmation
        if (!confirmed) {
            alert('Please confirm that you have reviewed all details before proceeding.');
            return false;
        }

        // Final confirmation dialog
        return confirm(
            'Are you sure you want to award this contract?\n\n' +
            'Award Value: LSL ' + awardValue.toLocaleString('en', {minimumFractionDigits: 2}) + '\n' +
            'This action will:\n' +
            '• Create a binding contract award\n' +
            '• Send notification to the supplier\n' +
            '• Publish the award notice\n\n' +
            'This cannot be undone. Proceed?'
        );
    }

    /**
     * Keyboard navigation for bid cards
     */
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' || e.key === ' ') {
            const focused = document.activeElement;
            if (focused && focused.classList.contains('bid-option-card')) {
                e.preventDefault();
                const bidId = focused.getAttribute('data-bid-id');
                const supplierName = focused.getAttribute('data-supplier-name');
                const bidPrice = focused.getAttribute('data-bid-price');
                const evaluationScore = focused.getAttribute('data-evaluation-score');

                selectBid(focused, parseInt(bidId), supplierName,
                    parseFloat(bidPrice), parseFloat(evaluationScore));
            }
        }
    });

    // Initialize character counter on page load
    document.addEventListener('DOMContentLoaded', function() {
        const textarea = document.getElementById('justification');
        if (textarea) {
            updateCharCount(textarea);
        }
    });
</script>

</body>
</html>