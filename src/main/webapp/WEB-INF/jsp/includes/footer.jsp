<%--
    footer.jsp — Minimal app footer for authenticated pages.
    Usage: <%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en"/>

<footer class="pg-app-footer" role="contentinfo">
    <p>© <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy"/> ProcureGov Authority — Ministry of Public Works, Kingdom of Lesotho.</p>
    <nav aria-label="Footer links" style="display:flex; gap:1.5rem;">
        <a href="#" style="font-size:0.6875rem; text-transform:uppercase; letter-spacing:0.06em; color:var(--color-outline);">Privacy</a>
        <a href="#" style="font-size:0.6875rem; text-transform:uppercase; letter-spacing:0.06em; color:var(--color-outline);">Accessibility</a>
        <a href="#" style="font-size:0.6875rem; text-transform:uppercase; letter-spacing:0.06em; color:var(--color-outline);">Support</a>
    </nav>
</footer>
