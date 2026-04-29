
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- Determine error details --%>
<%
Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");

if (statusCode == null) {
statusCode = 500;
}
if (errorMessage == null && exception != null) {
errorMessage = exception.getMessage();
}
if (errorMessage == null) {
errorMessage = "An unexpected error occurred";
}
%>

<c:set var="statusCode" value="<%= statusCode %>" />
<c:set var="errorMessage" value="<%= errorMessage != null ? errorMessage : "Unknown error" %>" />
<c:set var="requestUri" value="<%= requestUri != null ? requestUri : "" %>" />

<%-- Determine error type and appropriate messaging --%>
<c:choose>
  <c:when test="${statusCode == 403}">
    <c:set var="errorIcon" value="gavel" />
    <c:set var="errorTitle" value="Access Denied" />
    <c:set var="errorSubtitle" value="You don't have permission to access this resource." />
    <c:set var="errorKicker" value="403 Forbidden" />
    <c:set var="errorClass" value="pg-alert--warning" />
  </c:when>
  <c:when test="${statusCode == 404}">
    <c:set var="errorIcon" value="search_off" />
    <c:set var="errorTitle" value="Page Not Found" />
    <c:set var="errorSubtitle" value="The page you're looking for doesn't exist or has been moved." />
    <c:set var="errorKicker" value="404 Not Found" />
    <c:set var="errorClass" value="pg-alert--info" />
  </c:when>
  <c:when test="${statusCode == 405}">
    <c:set var="errorIcon" value="block" />
    <c:set var="errorTitle" value="Method Not Allowed" />
    <c:set var="errorSubtitle" value="This action is not supported for this resource." />
    <c:set var="errorKicker" value="405 Method Not Allowed" />
    <c:set var="errorClass" value="pg-alert--warning" />
  </c:when>
  <c:when test="${statusCode == 500}">
    <c:set var="errorIcon" value="build" />
    <c:set var="errorTitle" value="Internal Server Error" />
    <c:set var="errorSubtitle" value="Something went wrong on our end. Our team has been notified." />
    <c:set var="errorKicker" value="500 Server Error" />
    <c:set var="errorClass" value="pg-alert--error" />
  </c:when>
  <c:when test="${statusCode == 503}">
    <c:set var="errorIcon" value="engineering" />
    <c:set var="errorTitle" value="Service Unavailable" />
    <c:set var="errorSubtitle" value="The system is temporarily unavailable. Please try again shortly." />
    <c:set var="errorKicker" value="503 Service Unavailable" />
    <c:set var="errorClass" value="pg-alert--warning" />
  </c:when>
  <c:when test="${statusCode >= 400 && statusCode < 500}">
    <c:set var="errorIcon" value="error" />
    <c:set var="errorTitle" value="Request Error" />
    <c:set var="errorSubtitle" value="There was a problem with your request." />
    <c:set var="errorKicker" value="${statusCode} Client Error" />
    <c:set var="errorClass" value="pg-alert--warning" />
  </c:when>
  <c:otherwise>
    <c:set var="errorIcon" value="warning" />
    <c:set var="errorTitle" value="Unexpected Error" />
    <c:set var="errorSubtitle" value="An unexpected error occurred." />
    <c:set var="errorKicker" value="Error ${statusCode}" />
    <c:set var="errorClass" value="pg-alert--error" />
  </c:otherwise>
</c:choose>

<%
// Determine if user is authenticated
Object userObj = session.getAttribute("user");
boolean isAuthenticated = userObj != null;
String userRole = "";
if (isAuthenticated) {
try {
// Try to get role_name using reflection to avoid compile-time dependency
Object roleName = userObj.getClass().getMethod("getRole_name").invoke(userObj);
if (roleName != null) {
userRole = roleName.toString();
}
} catch (Exception e) {
// Ignore
}
}
request.setAttribute("isAuthenticated", isAuthenticated);
request.setAttribute("userRole", userRole);
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/WEB-INF/jsp/includes/head.jsp" %>
  <style>
    /* Error page specific enhancements */
    .error-container {
      max-width: 640px;
      margin: 0 auto;
    }

    .error-illustration {
      font-size: 4.5rem;
      color: var(--color-outline);
      line-height: 1;
      margin-bottom: 1.5rem;
      animation: fadeInDown 0.6s ease-out;
    }

    .error-code-display {
      font-family: var(--font-headline);
      font-size: 6rem;
      font-weight: 700;
      color: var(--color-primary);
      line-height: 1;
      margin-bottom: 0.5rem;
      animation: fadeIn 0.4s ease-out;
    }

    .error-details {
      background: var(--color-surface-container-low);
      border-radius: var(--radius-lg);
      padding: 1.25rem;
      margin-top: 1.5rem;
      font-family: monospace;
      font-size: 0.75rem;
      color: var(--color-outline);
      max-height: 200px;
      overflow-y: auto;
      display: none;
    }

    .error-details.visible {
      display: block;
    }

    .error-details dt {
      font-weight: 600;
      color: var(--color-on-surface-variant);
      margin-top: 0.5rem;
    }

    .error-details dt:first-child {
      margin-top: 0;
    }

    .error-details dd {
      margin-left: 0;
      color: var(--color-outline);
      word-break: break-all;
    }

    .navigation-options {
      display: flex;
      gap: 0.75rem;
      flex-wrap: wrap;
      margin-top: 1.5rem;
    }

    @keyframes fadeInDown {
      from {
        opacity: 0;
        transform: translateY(-20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }
  </style>
</head>
<body>

<div class="login-layout">
  <%-- Minimal header bar --%>
  <header class="login-header">
    <div class="pg-sidebar__brand" style="padding:0;">
      Procurement<span style="font-weight:400; font-style:normal;">Gov</span>
      <span class="badge">Alpha</span>
    </div>

    <c:if test="${isAuthenticated}">
      <a href="${pageContext.request.contextPath}/auth/logout"
         class="btn btn-ghost btn-sm">
        Sign Out
      </a>
    </c:if>
  </header>

  <main class="login-form-pane" style="align-items:center; min-height:100vh; padding-top:4rem;">
    <div class="error-container">

      <%-- Error Icon --%>
      <div class="error-illustration" aria-hidden="true">
                <span class="material-symbols-outlined" style="font-size:5rem; opacity:0.6;">
                    ${errorIcon}
                </span>
      </div>

      <%-- Error Code --%>
      <div class="error-code-display">${statusCode}</div>

      <%-- Error Kicker --%>
      <div class="text-kicker" style="color:var(--color-secondary); margin-bottom:0.75rem;">
        ${errorKicker}
      </div>

      <%-- Error Title --%>
      <h1 class="text-headline-lg" style="color:var(--color-primary); margin-bottom:0.5rem;">
        ${errorTitle}
      </h1>

      <%-- Error Description --%>
      <p class="text-body" style="color:var(--color-on-surface-variant); font-size:1rem;">
        ${errorSubtitle}
      </p>

      <%-- Error Alert with specific message --%>
      <div class="pg-alert ${errorClass}" style="margin-top:1.5rem;">
        <span class="material-symbols-outlined" style="font-size:1.25rem;">info</span>
        <div>
          <c:choose>
            <c:when test="${statusCode == 403}">
              Please contact your administrator if you believe this is an error.
            </c:when>
            <c:when test="${statusCode == 404}">
              Check the URL or navigate to a different page.
              <c:if test="${not empty requestUri}">
                <br><code style="font-size:0.75rem; opacity:0.8;">Requested: ${requestUri}</code>
              </c:if>
            </c:when>
            <c:when test="${statusCode == 500}">
              Please try again later. If the problem persists, contact support.
            </c:when>
            <c:otherwise>
              ${errorMessage}
            </c:otherwise>
          </c:choose>
        </div>
      </div>

      <%-- Navigation Options --%>
      <div class="navigation-options">
        <%-- Go Back --%>
        <button onclick="history.back()" class="btn btn-tonal">
          <span class="material-symbols-outlined">arrow_back</span>
          Go Back
        </button>

        <%-- Dashboard/Home --%>
        <c:choose>
          <c:when test="${isAuthenticated && userRole == 'PROCUREMENT_OFFICER'}">
            <a href="${pageContext.request.contextPath}/app/officer/dashboard"
               class="btn btn-primary">
              <span class="material-symbols-outlined">newspaper</span>
              Back to Dashboard
            </a>
          </c:when>
          <c:when test="${isAuthenticated && userRole == 'BOARD_MEMBER'}">
            <a href="${pageContext.request.contextPath}/app/officer/dashboard"
               class="btn btn-primary">
              <span class="material-symbols-outlined">newspaper</span>
              Back to Dashboard
            </a>
          </c:when>
          <c:when test="${isAuthenticated && userRole == 'SUPPLIER'}">
            <a href="${pageContext.request.contextPath}/app/supplier/dashboard"
               class="btn btn-primary">
              <span class="material-symbols-outlined">home</span>
              Back to Dashboard
            </a>
          </c:when>
          <c:otherwise>
            <a href="${pageContext.request.contextPath}/auth/login"
               class="btn btn-primary">
              <span class="material-symbols-outlined">login</span>
              Back to Login
            </a>
          </c:otherwise>
        </c:choose>

        <%-- Tenders --%>
        <c:if test="${isAuthenticated}">
          <a href="${pageContext.request.contextPath}/app/tenders"
             class="btn btn-ghost">
            <span class="material-symbols-outlined">gavel</span>
            Browse Tenders
          </a>
        </c:if>
      </div>

      <%-- Technical details (expandable) --%>
      <c:if test="${not empty errorMessage && (statusCode >= 500 || not empty pageContext.errorData) && (empty sessionScope.user or sessionScope.user.role_name ne 'SUPPLIER')}">
        <button onclick="document.getElementById('errorDetails').classList.toggle('visible')"
                class="btn btn-ghost btn-sm"
                style="margin-top:1.5rem; width:100%; justify-content:center;">
          <span class="material-symbols-outlined" style="font-size:1rem;">bug_report</span>
          Technical Details
          <span class="material-symbols-outlined" style="font-size:1rem;">expand_more</span>
        </button>

        <dl class="error-details" id="errorDetails">
          <dt>Status Code</dt>
          <dd>${statusCode}</dd>

          <dt>Error Message</dt>
          <dd>${errorMessage}</dd>

          <c:if test="${not empty requestUri}">
            <dt>Request URI</dt>
            <dd>${requestUri}</dd>
          </c:if>

          <c:if test="${not empty pageContext.errorData.servletName}">
            <dt>Servlet</dt>
            <dd>${pageContext.errorData.servletName}</dd>
          </c:if>

          <c:if test="${not empty pageContext.errorData.requestURI}">
            <dt>Request URL</dt>
            <dd>${pageContext.errorData.requestURI}</dd>
          </c:if>

          <c:if test="${not empty pageContext.exception}">
            <dt>Exception Type</dt>
            <dd><%= exception != null ? exception.getClass().getName() : "" %></dd>

            <dt>Exception Message</dt>
            <dd><%= exception != null && exception.getMessage() != null ? exception.getMessage() : "No message" %></dd>

            <% if (exception != null) { %>
            <dt>Stack Trace</dt>
            <dd><pre style="white-space:pre-wrap;"><%
                                java.io.StringWriter sw = new java.io.StringWriter();
                                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                                exception.printStackTrace(pw);
                                out.print(fn:escapeXml(sw.toString()));
                            %></pre></dd>
            <% } %>
          </c:if>

          <dt>Timestamp</dt>
          <dd><%= new java.util.Date() %></dd>
        </dl>
      </c:if>

      <%-- Support Information --%>
      <div class="pg-card" style="margin-top:2rem; background:var(--color-surface-container-low);">
        <div class="pg-card__body" style="text-align:center;">
          <p class="text-body-sm" style="color:var(--color-outline); margin:0;">
            Need assistance? Contact the ProcureGov support team at
            <a href="mailto:support@procuregov.gov.ls"
               style="color:var(--color-primary); font-weight:600;">
              support@procuregov.gov.ls
            </a>
          </p>
        </div>
      </div>
    </div>
  </main>
</div>

</body>
</html>