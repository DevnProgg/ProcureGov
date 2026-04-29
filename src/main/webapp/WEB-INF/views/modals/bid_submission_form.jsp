
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="activePage" value="tenders" />
<c:set var="pageSection" value="Tenders" />
<c:set var="pageTitle" value="Submit Bid" />

<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/WEB-INF/jsp/includes/head.jsp" %></head>
<body>

<div class="pg-layout">
    <%@ include file="/WEB-INF/jsp/includes/sidebar.jsp" %>

    <div class="pg-main">
        <%@ include file="/WEB-INF/jsp/includes/topbar.jsp" %>

        <main class="pg-content">
            <div style="max-width:860px;">

                <!-- Page Header -->
                <div class="pg-page-header">
                    <div class="pg-page-header__kicker">Bid Submission</div>
                    <h1 class="pg-page-header__title">Drafting Proposal</h1>
                    <p class="pg-page-header__subtitle">
                        For: <strong>${tender.title}</strong>
                        &nbsp;&nbsp;
                        <span class="badge badge--open">Open Tender</span>
                        &nbsp;&nbsp;
                        <span style="font-size:0.8125rem; color:var(--color-outline);">REF: ${tender.reference_number}</span>
                    </p>
                </div>

                <!-- Tender summary bar -->
                <div class="pg-alert pg-alert--info" style="margin-bottom:1.5rem;">
                    <span class="material-symbols-outlined">schedule</span>
                    <div>
                        Bid closes on <strong><fmt:formatDate value="${tender.expiry_datetime}" pattern="dd MMMM yyyy 'at' HH:mm"/></strong>.
                        Submissions are locked after this time by the server.
                    </div>
                </div>

                <!-- Validation error -->
                <c:if test="${not empty formError}">
                    <div class="pg-alert pg-alert--error" style="margin-bottom:1.5rem;" role="alert">
                        <span class="material-symbols-outlined">error</span>
                        <span>${formError}</span>
                    </div>
                </c:if>

                <div style="display:grid; grid-template-columns:1fr 300px; gap:1.5rem; align-items:start;">

                    <!-- Bid Form -->
                    <div class="pg-card">
                        <form action="${pageContext.request.contextPath}/app/bids/submit"
                              method="post"
                              enctype="multipart/form-data"
                              data-validate="true">

                            <input type="hidden" name="tenderId" value="${tender.tender_id}"/>

                            <div style="padding:1.5rem;">

                                <div class="form-chapter">
                                    <div class="form-chapter__number">Chapter 01</div>
                                    <div class="form-chapter__title">Submission Particulars</div>
                                </div>

                                <div style="display:grid; grid-template-columns:1fr 1fr; gap:1rem;">
                                    <div class="form-group">
                                        <label class="form-label required" for="bidAmount">Bid Amount (LSL)</label>
                                        <div class="form-input-icon">
                                            <input class="form-input" type="number" id="bidAmount" name="bidAmount"
                                                   placeholder="0.00"
                                                   value="${not empty prevBidAmount ? prevBidAmount : ''}"
                                                   min="1" step="0.01" required/>
                                            <span class="material-symbols-outlined icon">payments</span>
                                        </div>
                                        <div class="form-hint">Total inclusive of all applicable taxes.</div>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label required" for="deliveryDays">Delivery Timeline (Calendar Days)</label>
                                        <div class="form-input-icon">
                                            <input class="form-input" type="number" id="deliveryDays" name="deliveryDays"
                                                   placeholder="e.g. 120"
                                                   value="${not empty prevDeliveryDays ? prevDeliveryDays : ''}"
                                                   min="1" required/>
                                            <span class="material-symbols-outlined icon">timer</span>
                                        </div>
                                        <div class="form-hint">From Notice to Proceed to project completion.</div>
                                    </div>
                                </div>

                                <div class="form-chapter">
                                    <div class="form-chapter__number">Chapter 02</div>
                                    <div class="form-chapter__title">Compliance &amp; Statement</div>
                                </div>

                                <div class="form-group">
                                    <label class="form-label required" for="complianceStatement">Compliance Statement</label>
                                    <textarea class="form-textarea"
                                              id="complianceStatement"
                                              name="complianceStatement"
                                              rows="6"
                                              maxlength="600"
                                              placeholder="State your organisation's compliance with all tender requirements, certifications held, environmental commitments, and any subcontractor arrangements..."
                                              oninput="updateCharCount(this)"
                                              required>${not empty prevStatement ? prevStatement : ''}</textarea>
                                    <div style="display:flex; justify-content:space-between; align-items:center; margin-top:0.25rem;">
                                        <div class="form-hint">Must be signed by an authorised signatory if selected for secondary review.</div>
                                        <div class="char-counter"><span id="charCount">0</span> / 600</div>
                                    </div>
                                </div>

                                <div class="form-chapter">
                                    <div class="form-chapter__number">Chapter 03</div>
                                    <div class="form-chapter__title">Supporting Documentation</div>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Technical Bid Document <span style="font-weight:400; text-transform:none; letter-spacing:0;">(PDF or DOCX — max 10MB)</span></label>
                                    <div class="upload-zone" id="uploadZone"
                                         onclick="document.getElementById('bidDocument').click()"
                                         ondragover="event.preventDefault(); this.classList.add('dragover')"
                                         ondragleave="this.classList.remove('dragover')">
                                        <span class="material-symbols-outlined">cloud_upload</span>
                                        <div class="upload-zone__title" id="uploadLabel">Drag &amp; drop your Technical Bid PDF/DOCX, or click to browse</div>
                                        <div class="upload-zone__hint">Max 10MB • PDF or DOCX</div>
                                    </div>
                                    <input type="file" id="bidDocument" name="bidDocument"
                                           accept=".pdf,.docx"
                                           style="display:none;"
                                           onchange="document.getElementById('uploadLabel').textContent = this.files[0]?.name ?? 'Browse files'"/>
                                </div>

                            </div>

                            <!-- Regulatory notice -->
                            <div style="margin:0 1.5rem 1.25rem;">
                                <div class="pg-pullquote" style="font-size:0.8125rem;">
                                    Failure to disclose secondary subcontractors at submission may result in disqualification under Regulation 14-B.
                                </div>
                            </div>

                            <!-- Actions -->
                            <div class="pg-card__footer">
                                <a href="${pageContext.request.contextPath}/tenders/${tender.tender_id}" class="btn btn-ghost">
                                    Cancel
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <span class="material-symbols-outlined">send</span>
                                    Submit Proposal
                                </button>
                            </div>

                        </form>
                    </div>

                    <!-- Right: Checklist + Scoring Info -->
                    <div style="display:flex; flex-direction:column; gap:1rem; position:sticky; top:calc(var(--topbar-height) + 1.5rem);">

                        <!-- Submission Checklist -->
                        <div class="pg-card">
                            <div class="pg-card__header" style="padding-bottom:0.75rem;">
                                <h3 class="text-title">Submission Checklist</h3>
                            </div>
                            <div class="pg-card__body" style="padding-top:0; font-size:0.8125rem; display:flex; flex-direction:column; gap:0.625rem;">
                                <div style="display:flex; align-items:center; gap:0.5rem;">
                                    <span class="material-symbols-outlined" style="font-size:1rem; color:var(--color-secondary);">check_circle</span>
                                    <span>Active Supplier Registration</span>
                                </div>
                                <div style="display:flex; align-items:center; gap:0.5rem;">
                                    <span class="material-symbols-outlined" style="font-size:1rem; color:var(--color-secondary);">check_circle</span>
                                    <span>Unique Entity Identifier on file</span>
                                </div>
                                <div style="display:flex; align-items:center; gap:0.5rem;" id="chk-doc">
                                    <span class="material-symbols-outlined" style="font-size:1rem; color:var(--color-outline);">radio_button_unchecked</span>
                                    <span>Technical Bid Document Attached</span>
                                </div>
                                <div style="display:flex; align-items:center; gap:0.5rem;">
                                    <span class="material-symbols-outlined" style="font-size:1rem; color:var(--color-outline);">radio_button_unchecked</span>
                                    <span>Compliance Statement Complete</span>
                                </div>
                            </div>
                        </div>

                        <!-- Scoring breakdown -->
                        <div class="pg-card">
                            <div class="pg-card__header" style="padding-bottom:0.75rem;">
                                <h3 class="text-title">Evaluation Weights</h3>
                            </div>
                            <div class="pg-card__body" style="padding-top:0; display:flex; flex-direction:column; gap:0.75rem;">
                                <div>
                                    <div style="display:flex; justify-content:space-between; margin-bottom:0.25rem;">
                                        <span style="font-size:0.8125rem; color:var(--color-on-surface-variant);">Price</span>
                                        <span style="font-size:0.8125rem; font-weight:700; color:var(--color-primary);">40%</span>
                                    </div>
                                    <div class="score-bar-track"><div class="score-bar-fill" style="width:40%;"></div></div>
                                </div>
                                <div>
                                    <div style="display:flex; justify-content:space-between; margin-bottom:0.25rem;">
                                        <span style="font-size:0.8125rem; color:var(--color-on-surface-variant);">Technical</span>
                                        <span style="font-size:0.8125rem; font-weight:700; color:var(--color-primary);">35%</span>
                                    </div>
                                    <div class="score-bar-track"><div class="score-bar-fill" style="width:35%;"></div></div>
                                </div>
                                <div>
                                    <div style="display:flex; justify-content:space-between; margin-bottom:0.25rem;">
                                        <span style="font-size:0.8125rem; color:var(--color-on-surface-variant);">Timeline</span>
                                        <span style="font-size:0.8125rem; font-weight:700; color:var(--color-primary);">25%</span>
                                    </div>
                                    <div class="score-bar-track"><div class="score-bar-fill" style="width:25%;"></div></div>
                                </div>
                                <div class="form-hint" style="margin-top:0.25rem;">
                                    Weighted total = (Price×0.40) + (Tech×0.35) + (Timeline×0.25)
                                </div>
                            </div>
                        </div>

                    </div>
                </div>

            </div>
        </main>
        <%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
    </div>
</div>

<script>
function updateCharCount(el) {
    document.getElementById('charCount').textContent = el.value.length;
}
// Init on load
if (document.getElementById('complianceStatement').value) {
    updateCharCount(document.getElementById('complianceStatement'));
}
</script>

</body>
</html>
