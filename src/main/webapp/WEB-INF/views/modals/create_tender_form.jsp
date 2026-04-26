<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="isEdit" value="${not empty requestScope.isEdit ? requestScope.isEdit : false}" />
<c:set var="tender" value="${requestScope.tender}" />

<c:set var="activePage" value="create-tenders" />
<c:set var="pageSection" value="Tenders" />
<c:set var="pageTitle" value="${isEdit ? 'Edit' : 'Create'} Tender" />

<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/WEB-INF/jsp/includes/head.jsp" %>

    <style>
        .pg-alert--info {
            background: #E3F2FD;
            border-left: 4px solid #2196F3;
            padding: 1rem;
            margin-bottom: 1.5rem;
            border-radius: 4px;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .existing-file {
            background: #F5F5F5;
            padding: 0.75rem;
            border-radius: 4px;
            margin-top: 0.5rem;
            font-size: 0.8125rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
    </style>
</head>
<body>

<div class="pg-layout">
    <%@ include file="/WEB-INF/jsp/includes/sidebar.jsp" %>

    <div class="pg-main">
        <%@ include file="/WEB-INF/jsp/includes/topbar.jsp" %>

        <main class="pg-content">
            <div style="max-width:900px;">

                <!-- Page Header -->
                <div class="pg-page-header">
                    <div class="pg-page-header__kicker">Officer Action</div>
                    <h1 class="pg-page-header__title">${isEdit ? 'Edit' : 'New'} Tender Filing</h1>
                    <p class="pg-page-header__subtitle">
                        <c:choose>
                            <c:when test="${isEdit}">
                                Update draft tender. Save as Draft to continue later, or Publish to make it visible to suppliers.
                            </c:when>
                            <c:otherwise>
                                Draft a new procurement opportunity. Save as Draft to continue later, or Publish to make it visible to suppliers.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>

                <c:if test="${not empty requestScope.formError}">
                    <div class="pg-alert pg-alert--error" style="margin-bottom:1.5rem;" role="alert">
                        <span class="material-symbols-outlined">error</span>
                        <span>${requestScope.formError}</span>
                    </div>
                </c:if>

                <c:if test="${isEdit and not empty tender.reference_number}">
                    <div class="pg-alert--info" role="status">
                        <span class="material-symbols-outlined">edit_note</span>
                        <span>Editing Draft: <strong>${tender.reference_number}</strong></span>
                    </div>
                </c:if>

                <!-- Asymmetric 60/40 form layout -->
                <div style="display:grid; grid-template-columns:1fr 320px; gap:2rem; align-items:start;">

                    <!-- Left: Form -->
                    <div class="pg-card">
                        <form action="${pageContext.request.contextPath}${isEdit ? '/officer/edit-tender' : '/officer/create-tender'}"
                              method="post"
                              enctype="multipart/form-data"
                              id="tenderForm"
                              novalidate>

                            <c:if test="${isEdit}">
                                <input type="hidden" name="tenderId" value="${tender.tender_id}" />
                                <input type="hidden" name="existingFilePath" value="${tender.notice_file_path}" />
                            </c:if>

                            <div style="padding:1.5rem;">

                                <!-- Chapter 01 -->
                                <div class="form-chapter">
                                    <div class="form-chapter__number">Chapter 01</div>
                                    <div class="form-chapter__title">Basic Information</div>
                                </div>

                                <div class="form-group">
                                    <label class="form-label required" for="title">Tender Title</label>
                                    <input class="form-input" type="text" id="title" name="title"
                                           placeholder="e.g. Supply of Road Construction Materials — Northern District"
                                           value="${not empty prevTitle ? prevTitle : (isEdit ? tender.title : '')}"
                                           required maxlength="255"/>
                                </div>

                                <div style="display:grid; grid-template-columns:1fr 1fr; gap:1rem;">
                                    <div class="form-group">
                                        <label class="form-label required" for="category">Category</label>
                                        <div class="form-input-icon">
                                            <select class="form-select" id="category" name="category" required>
                                                <option value="" disabled ${empty prevCategory and not isEdit ? 'selected' : ''}>Select category…</option>
                                                <c:forEach var="cat" items="${['Construction', 'Roads', 'Electrical', 'Plumbing', 'General Services']}">
                                                    <option value="${cat}"
                                                            ${prevCategory eq cat ? 'selected' : (isEdit and empty prevCategory and tender.category eq cat ? 'selected' : '')}>
                                                    ${cat}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <span class="material-symbols-outlined icon">folder</span>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label required" for="estimatedValue">Estimated Budget (LSL)</label>
                                        <div class="form-input-icon">
                                            <input class="form-input" type="number" id="estimatedValue" name="estimatedValue"
                                                   placeholder="0.00"
                                                   value="${not empty prevBudget ? prevBudget : (isEdit ? tender.estimated_value : '')}"
                                                   min="1" step="0.01" required/>
                                            <span class="material-symbols-outlined icon">payments</span>
                                        </div>
                                    </div>
                                </div>

                                <!-- Chapter 02 -->
                                <div class="form-chapter">
                                    <div class="form-chapter__number">Chapter 02</div>
                                    <div class="form-chapter__title">Timeline &amp; Logistics</div>
                                </div>

                                <div class="form-group">
                                    <label class="form-label required" for="closingDatetime">Bid Closing Date &amp; Time</label>
                                    <div class="form-input-icon">
                                        <input class="form-input" type="datetime-local" id="closingDatetime" name="closingDatetime"
                                               value="${not empty prevClosing ? prevClosing : (isEdit ? formattedClosingDate : '')}"
                                               required/>
                                        <span class="material-symbols-outlined icon">calendar_today</span>
                                    </div>
                                    <div class="form-hint">Server time (SAST). Auto-close triggers at this datetime.</div>
                                </div>

                                <!-- Chapter 03 -->
                                <div class="form-chapter">
                                    <div class="form-chapter__number">Chapter 03</div>
                                    <div class="form-chapter__title">Description &amp; Scope</div>
                                </div>

                                <div class="form-group">
                                    <label class="form-label required" for="description">Tender Description</label>
                                    <textarea class="form-textarea" id="description" name="description"
                                              rows="8"
                                              placeholder="Provide a full description of the procurement scope, technical requirements, and evaluation criteria..."
                                              required>${not empty prevDescription ? prevDescription : (isEdit ? tender.description : '')}</textarea>
                                </div>

                                <!-- Chapter 04 -->
                                <div class="form-chapter">
                                    <div class="form-chapter__number">Chapter 04</div>
                                    <div class="form-chapter__title">Official Notice Document</div>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Attach PDF Notice <span style="font-weight:400; text-transform:none; letter-spacing:0;">(Optional, Max 5MB, PDF only)</span></label>

                                    <c:if test="${isEdit and not empty tender.notice_file_path}">
                                        <div class="existing-file">
                                            <span class="material-symbols-outlined">description</span>
                                            <span>Current file: ${tender.notice_file_path}</span>
                                        </div>
                                    </c:if>

                                    <div class="upload-zone" id="uploadZone"
                                         onclick="document.getElementById('noticeFile').click()"
                                         ondragover="event.preventDefault(); this.classList.add('dragover')"
                                         ondragleave="this.classList.remove('dragover')"
                                         ondrop="handleDrop(event)">
                                        <span class="material-symbols-outlined">upload_file</span>
                                        <div class="upload-zone__title" id="uploadLabel">
                                            ${isEdit ? 'Upload new PDF to replace existing, or leave empty to keep current' : 'Drag & drop PDF, or click to browse'}
                                        </div>
                                        <div class="upload-zone__hint">Max 5MB — PDF only</div>
                                    </div>
                                    <input type="file" id="noticeFile" name="noticeFile" accept="application/pdf,.pdf" style="display:none;"
                                           onchange="updateUploadLabel(this)"/>
                                </div>

                            </div>

                            <!-- Form Actions -->
                            <div class="pg-card__footer">
                                <a href="${pageContext.request.contextPath}/app/tenders" class="btn btn-ghost">
                                    Cancel
                                </a>
                                <div style="display:flex; gap:0.5rem;">
                                    <button type="submit" name="action" value="draft" class="btn btn-ghost">
                                        <span class="material-symbols-outlined">save</span>
                                        ${isEdit ? 'Update Draft' : 'Save as Draft'}
                                    </button>
                                    <button type="submit" name="action" value="publish" class="btn btn-primary">
                                        <span class="material-symbols-outlined">publish</span>
                                        ${isEdit ? 'Update & Publish' : 'Publish Tender'}
                                    </button>
                                </div>
                            </div>

                        </form>
                    </div>

                    <!-- Right: Contextual Editorial Panel -->
                    <div style="display:flex; flex-direction:column; gap:1rem; position:sticky; top:calc(var(--topbar-height) + 1.5rem);">

                        <div class="pg-card">
                            <div class="pg-card__header" style="padding-bottom:0.75rem;">
                                <h3 class="text-title" style="color:var(--color-primary);">Filing Guidelines</h3>
                            </div>
                            <div class="pg-card__body" style="padding-top:0; font-size:0.8125rem; color:var(--color-on-surface-variant); display:flex; flex-direction:column; gap:0.875rem;">
                                <div>
                                    <strong style="display:block; color:var(--color-on-surface); margin-bottom:0.25rem;">Reference Number</strong>
                                    Auto-generated on save in the format <code style="background:var(--color-surface-container-high); padding:0.1rem 0.3rem; border-radius:2px; font-size:0.75rem;">MPW-YYYY-NNNN</code>.
                                </div>
                                <div>
                                    <strong style="display:block; color:var(--color-on-surface); margin-bottom:0.25rem;">Draft vs. Publish</strong>
                                    Draft tenders are not visible to suppliers. Only publish when all details are finalised.
                                </div>
                                <div>
                                    <strong style="display:block; color:var(--color-on-surface); margin-bottom:0.25rem;">Closing Date</strong>
                                    The system automatically closes the tender and locks all bids at the specified datetime. Choose carefully.
                                </div>
                                <div>
                                    <strong style="display:block; color:var(--color-on-surface); margin-bottom:0.25rem;">Budget Accuracy</strong>
                                    Estimated value is used in price-score calculations during evaluation. Accuracy here reduces audit friction.
                                </div>

                                <c:if test="${isEdit}">
                                    <div style="margin-top:0.5rem; padding-top:0.75rem; border-top:1px solid var(--color-outline-variant);">
                                        <strong style="display:block; color:var(--color-primary); margin-bottom:0.25rem;">Editing Note</strong>
                                        <span>Changes will update the existing draft. The reference number (${tender.reference_number}) will remain the same.</span>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <div class="pg-pullquote">
                            Precision in the scope description significantly improves bid quality and reduces the need for clarification requests.
                            <footer style="margin-top:0.5rem; font-family:var(--font-body); font-style:normal; font-size:0.6875rem; font-weight:700; letter-spacing:0.06em; text-transform:uppercase; color:var(--color-outline);">— Procurement Directive 7.4</footer>
                        </div>

                    </div>
                </div>

            </div>
        </main>
        <%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
    </div>
</div>

<script>
    // Update upload label function
    function updateUploadLabel(input) {
        const label = document.getElementById('uploadLabel');
        if (label) {
            if (input.files.length > 0) {
                const file = input.files[0];
                // Validate PDF type
                if (file.type !== 'application/pdf' && !file.name.toLowerCase().endsWith('.pdf')) {
                    alert('Only PDF files are allowed');
                    input.value = '';
                    label.textContent = 'Drag & drop PDF, or click to browse';
                    return;
                }
                // Validate file size (5 MB)
                if (file.size > 5 * 1024 * 1024) {
                    alert('File size must not exceed 5 MB');
                    input.value = '';
                    label.textContent = 'Drag & drop PDF, or click to browse';
                    return;
                }
                label.textContent = file.name;
            } else {
                label.textContent = 'Drag & drop PDF, or click to browse';
            }
        }
    }

    // Handle file drop
    function handleDrop(e) {
        e.preventDefault();
        document.getElementById('uploadZone').classList.remove('dragover');

        const files = e.dataTransfer.files;
        if (files.length > 0) {
            const file = files[0];
            // Validate PDF type
            if (file.type !== 'application/pdf' && !file.name.toLowerCase().endsWith('.pdf')) {
                alert('Only PDF files are allowed');
                return;
            }
            // Validate file size
            if (file.size > 5 * 1024 * 1024) {
                alert('File size must not exceed 5 MB');
                return;
            }
            document.getElementById('noticeFile').files = files;
            updateUploadLabel(document.getElementById('noticeFile'));
        }
    }

    // Form validation before publish
    document.getElementById('tenderForm').addEventListener('submit', function(e) {
        const actionButton = e.submitter?.value;
        if (actionButton === 'publish') {
            const title = document.getElementById('title')?.value?.trim();
            const category = document.getElementById('category')?.value;
            const closingDatetime = document.getElementById('closingDatetime')?.value;
            const description = document.getElementById('description')?.value?.trim();

            let errors = [];
            if (!title) errors.push('Tender title is required');
            if (!category) errors.push('Category is required');
            if (!closingDatetime) errors.push('Closing date is required');
            if (!description) errors.push('Description is required');

            if (errors.length > 0) {
                e.preventDefault();
                alert('Please fix the following errors:\n' + errors.join('\n'));
            }
        }
    });
</script>

</body>
</html>