<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ taglib prefix="c"   uri="jakarta.tags.core"         %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"          %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions"    %>
<%@ taglib prefix="lib" uri="http://library.com/tags"   %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library Book Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header class="site-header">
    <div class="header-inner">
        <h1>&#128218; Library Book Manager</h1>

        <%-- fmt:formatDate Format current date using Formatting tag library --%>
        <p class="date-display">
            Today: <fmt:formatDate value="${today}" pattern="EEEE, dd MMMM yyyy" />
        </p>
    </div>
</header>

<section class="stats-section">

    <div class="stat-card">
        <span class="stat-number">${totalBooks}</span>
        <span class="stat-label">Total Books</span>
    </div>

    <div class="stat-card available">
        <span class="stat-number">${availableCount}</span>
        <span class="stat-label">Available</span>
    </div>

    <div class="stat-card unavailable">
        <span class="stat-number">${unavailableCount}</span>
        <span class="stat-label">Checked Out</span>
    </div>

    <div class="stat-card value">
        <%-- fmt:formatNumber Format average price as currency --%>
        <span class="stat-number">
            <fmt:formatNumber value="${avgPrice}" type="currency" currencySymbol="LSL" maxFractionDigits="2"/>
        </span>
        <span class="stat-label">Avg. Price</span>
    </div>

</section>

<section class="filter-section">
    <form action="${pageContext.request.contextPath}/books" method="get" class="filter-form">

        <%-- c:set Store a variable for use later --%>
        <c:set var="searchTerm" value="${searchTerm}" />

        <div class="filter-group">
            <label for="search">Search:</label>
            <%-- c:out — Safe output --%>
            <input type="text" id="search" name="search"
                   value="<c:out value='${searchTerm}'/>"
                   placeholder="Title or Author...">
        </div>

        <div class="filter-group">
            <label for="genre">Genre:</label>
            <select id="genre" name="genre">
                <%-- c:forEach Iterate over the genre list from the servlet --%>
                <c:forEach var="g" items="${genres}">
                    <option value="${g}"
                        <%-- c:if Conditional selected attribute --%>
                        <c:if test="${g == selectedGenre}">selected</c:if>
                    >${g}</option>
                </c:forEach>
            </select>
        </div>

        <div class="filter-group">
            <label for="sortBy">Sort By:</label>
            <select id="sortBy" name="sortBy">
                <option value="title"  <c:if test="${sortBy == 'title'}">selected</c:if>>Title</option>
                <option value="price"  <c:if test="${sortBy == 'price'}">selected</c:if>>Price</option>
                <option value="year"   <c:if test="${sortBy == 'year'}">selected</c:if>>Year</option>
            </select>
        </div>

        <button type="submit" class="btn-filter">Apply Filters</button>
        <a href="${pageContext.request.contextPath}/books" class="btn-reset">Reset</a>
    </form>
</section>

<c:if test="${fn:length(searchTerm) > 0}">
    <div class="search-notice">
        <%-- fn:length JSTL function to get string/collection length --%>
        <%-- fn:toUpperCase JSTL function for string transformation --%>
        Showing <strong>${totalBooks}</strong> result(s) for
        "<strong><c:out value="${fn:toUpperCase(searchTerm)}"/></strong>"
    </div>
</c:if>

<section class="table-section">

    <%-- c:choose / c:when / c:otherwise Multi-branch conditional --%>
    <c:choose>
        <c:when test="${totalBooks == 0}">
            <div class="no-results">
                <p>&#128269; No books found matching your criteria.</p>
                <a href="${pageContext.request.contextPath}/books">View all books</a>
            </div>
        </c:when>

        <c:otherwise>
            <table class="books-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Genre</th>
                        <th>Title</th>
                        <th>Author</th>
                        <th>Year</th>
                        <th>Price</th>
                        <th>Status</th>
                        <th>Added On</th>
                    </tr>
                </thead>
                <tbody>

                    <%-- c:forEach with varStatus for row numbering --%>
                    <c:forEach var="book" items="${books}" varStatus="status">

                        <%-- c:choose to alternate row classes --%>
                        <c:choose>
                            <c:when test="${status.index % 2 == 0}">
                                <c:set var="rowClass" value="row-even" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="rowClass" value="row-odd" />
                            </c:otherwise>
                        </c:choose>

                        <tr class="${rowClass} <c:if test='${!book.available}'>row-checkedout</c:if>">

                            <%-- varStatus.count gives 1 based row number --%>
                            <td class="td-num">${status.count}</td>

                            <%-- Custom Tag: lib:genreIcon --%>
                            <td class="td-genre">
                                <lib:genreIcon genre="${book.genre}" />
                                <span class="genre-label">${book.genre}</span>
                            </td>

                            <%-- c:out for safe text output, fn:substring for truncation --%>
                            <td class="td-title">
                                <c:choose>
                                    <c:when test="${fn:length(book.title) > 25}">
                                        <c:out value="${fn:substring(book.title, 0, 25)}"/>...
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${book.title}" />
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td class="td-author"><c:out value="${book.author}" /></td>

                            <td class="td-year">${book.year}</td>

                            <%-- fmt:formatNumber — Format price with 2 decimal places --%>
                            <td class="td-price">
                                <fmt:formatNumber value="${book.price}"
                                                  type="currency"
                                                  currencySymbol="LSL"
                                                  maxFractionDigits="2"/>
                            </td>

                            <%-- Custom Tag: lib:badge --%>
                            <td class="td-status">
                                <lib:badge available="${book.available}" />
                            </td>

                            <%-- fmt:formatDate — Format the addedDate field --%>
                            <td class="td-date">
                                <fmt:formatDate value="${book.addedDate}" pattern="dd MMM yyyy" />
                            </td>

                        </tr>
                    </c:forEach>

                </tbody>
            </table>

            <div class="summary-footer">
                <p>
                    Showing <strong>${totalBooks}</strong> book(s) &nbsp;|&nbsp;
                    Total Collection Value:
                    <strong>
                        <fmt:formatNumber value="${totalValue}"
                                          type="currency"
                                          currencySymbol="LSL"
                                          maxFractionDigits="2"/>
                    </strong>
                </p>

                <%-- fn:contains check if search string contains keyword --%>
                <c:if test="${fn:contains(fn:toLowerCase(searchTerm), 'code')}">
                    <p class="tip">Tip: Showing books related to "code".</p>
                </c:if>
            </div>

        </c:otherwise>
    </c:choose>

</section>

<footer class="site-footer">
    <p>
        Library Book Manager &copy;
        <%-- fmt:formatDate to show year in footer --%>
        <fmt:formatDate value="${today}" pattern="yyyy" />
        &nbsp;|&nbsp; Built with JSTL &amp; Custom Tags
    </p>
</footer>

</body>
</html>
