
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Existing Bid Found" />
<c:set var="pageSection" value="Bid Conflict" />
<c:set var="activePage" value="tenders" />

<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/WEB-INF/jsp/includes/head.jsp" %></head>
<body>

<div class="pg-layout">
  <%@ include file="/WEB-INF/jsp/includes/sidebar.jsp" %>

  <div class="pg-main">
    <%@ include file="/WEB-INF/jsp/includes/topbar.jsp" %>

    <main class="pg-content">
      <div style="max-width:700px; margin:0 auto;">

        <div class="pg-page-header">
          <div class="pg-page-header__kicker">Bid Conflict Resolution</div>
          <h1 class="pg-page-header__title">Existing Bid Detected</h1>
          <p class="pg-page-header__subtitle">
            You already have an active bid on another tender. Please choose how to proceed.
          </p>
        </div>

        <!-- Warning Alert -->
        <div class="pg-alert pg-alert--warning" style="margin-bottom:2rem;" role="alert">
          <span class="material-symbols-outlined">warning</span>
          <div>
            <strong>Procurement Regulation 14-B:</strong> Suppliers may only have one active bid at a time.
            Submitting a new bid will automatically withdraw your previous bid.
          </div>
        </div>

        <!-- Existing Bid Card -->
        <div class="pg-card" style="margin-bottom:1.5rem;">
          <div class="pg-card__header">
            <h3 class="text-title">Your Current Bid</h3>
            <span class="badge badge--open">Active Submission</span>
          </div>
          <div class="pg-card__body">
            <div style="display:grid; gap:0.75rem;">
              <div>
                <div style="font-size:0.6875rem; font-weight:700; text-transform:uppercase; letter-spacing:0.08em; color:var(--color-outline);">Tender</div>
                <div style="font-family:var(--font-headline); font-size:1.125rem; font-weight:700;">
                  ${existingBid.tenderTitle}
                </div>
                <div style="font-size:0.8125rem; color:var(--color-outline);">
                  REF: ${existingBid.tenderReference}
                </div>
              </div>
              <div style="display:grid; grid-template-columns:1fr 1fr; gap:1rem;">
                <div>
                  <div style="font-size:0.6875rem; font-weight:700; text-transform:uppercase; letter-spacing:0.08em; color:var(--color-outline);">Submitted</div>
                  <div style="font-size:0.9375rem;">
                    <fmt:formatDate value="${existingBid.submittedAt}" pattern="dd MMMM yyyy 'at' HH:mm"/>
                  </div>
                </div>
                <div>
                  <div style="font-size:0.6875rem; font-weight:700; text-transform:uppercase; letter-spacing:0.08em; color:var(--color-outline);">Status</div>
                  <div>
                    <c:choose>
                      <c:when test="${existingBid.evaluationStatus eq 'EVALUATED'}">
                        <span class="badge badge--evaluation">Under Evaluation</span>
                      </c:when>
                      <c:when test="${existingBid.evaluationStatus eq 'AWARDED'}">
                        <span class="badge badge--awarded">Awarded</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge badge--open">Pending Review</span>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- New Tender Card -->
        <div class="pg-card" style="margin-bottom:2rem;">
          <div class="pg-card__header">
            <h3 class="text-title">Tender You Wish to Bid On</h3>
          </div>
          <div class="pg-card__body">
            <div style="display:grid; gap:0.75rem;">
              <div>
                <div style="font-family:var(--font-headline); font-size:1.125rem; font-weight:700;">
                  ${newTender.title}
                </div>
                <div style="font-size:0.8125rem; color:var(--color-outline);">
                  REF: ${newTender.referenceNumber}
                </div>
              </div>
              <p style="font-size:0.875rem; color:var(--color-on-surface-variant); margin:0;">
                ${newTender.description}
              </p>
              <div style="display:flex; gap:1rem; font-size:0.8125rem;">
                <span><span class="material-symbols-outlined" style="font-size:1rem;">folder</span> ${newTender.category}</span>
                <span><span class="material-symbols-outlined" style="font-size:1rem;">schedule</span>
                                    Closes <fmt:formatDate value="${newTender.closingDatetime}" pattern="dd MMM yyyy HH:mm"/>
                                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Action Options -->
        <div style="display:grid; gap:1rem;">

          <!-- Option 1: Replace existing bid -->
          <form action="${pageContext.request.contextPath}/app/bids/replace" method="post" style="margin:0;">
            <input type="hidden" name="existingBidId" value="${existingBid.bidId}"/>
            <input type="hidden" name="newTenderId" value="${newTender.tenderId}"/>
            <input type="hidden" name="action" value="replace"/>

            <button type="submit" class="pg-card" style="width:100%; text-align:left; cursor:pointer; background:none; border:2px solid var(--color-primary); transition:all 0.2s;">
              <div style="padding:1.25rem 1.5rem; display:flex; align-items:flex-start; gap:1rem;">
                <span class="material-symbols-outlined" style="color:var(--color-primary); font-size:2rem;">swap_horiz</span>
                <div style="flex:1;">
                  <div style="font-family:var(--font-headline); font-size:1.125rem; font-weight:700; color:var(--color-primary); margin-bottom:0.25rem;">
                    Replace Existing Bid
                  </div>
                  <p style="font-size:0.8125rem; color:var(--color-on-surface-variant); margin:0;">
                    Withdraw your current bid on <strong>${existingBid.tenderTitle}</strong>
                    and proceed to submit a new bid for <strong>${newTender.title}</strong>.
                  </p>
                </div>
                <span class="material-symbols-outlined" style="color:var(--color-primary);">arrow_forward</span>
              </div>
            </button>
          </form>

          <!-- Option 2: Keep existing bid -->
          <form action="${pageContext.request.contextPath}/app/bids/keep-existing" method="post" style="margin:0;">
            <input type="hidden" name="existingBidId" value="${existingBid.bidId}"/>
            <input type="hidden" name="newTenderId" value="${newTender.tenderId}"/>
            <input type="hidden" name="action" value="keep"/>

            <button type="submit" class="pg-card" style="width:100%; text-align:left; cursor:pointer; background:none; border:1px solid var(--color-outline-variant);">
              <div style="padding:1.25rem 1.5rem; display:flex; align-items:flex-start; gap:1rem;">
                <span class="material-symbols-outlined" style="color:var(--color-on-surface-variant); font-size:2rem;">block</span>
                <div style="flex:1;">
                  <div style="font-family:var(--font-headline); font-size:1.125rem; font-weight:700; color:var(--color-on-surface); margin-bottom:0.25rem;">
                    Keep Current Bid
                  </div>
                  <p style="font-size:0.8125rem; color:var(--color-on-surface-variant); margin:0;">
                    Maintain your existing bid on <strong>${existingBid.tenderTitle}</strong>
                    and cancel this new bid submission.
                  </p>
                </div>
                <span class="material-symbols-outlined" style="color:var(--color-on-surface-variant);">close</span>
              </div>
            </button>
          </form>

        </div>

        <!-- Cancel Link -->
        <div style="text-align:center; margin-top:1.5rem;">
          <a href="${pageContext.request.contextPath}/app/tenders/${newTender.tenderId}" class="btn btn-ghost">
            <span class="material-symbols-outlined">arrow_back</span>
            Return to Tender Details
          </a>
        </div>

      </div>
    </main>
    <%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
  </div>
</div>

</body>
</html>