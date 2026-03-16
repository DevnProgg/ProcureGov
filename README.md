# Library Book Manager
## JSP Web Application — JSTL & Custom Tag Libraries

---

## Project Structure

```
library-app/
├── pom.xml                                        ← Maven build file
└── src/main/
    ├── java/com/library/
    │   ├── model/
    │   │   └── Book.java                          ← Data model (POJO)
    │   ├── servlet/
    │   │   └── BookServlet.java                   ← Controller (Jakarta Servlet)
    │   └── tags/
    │       ├── BadgeTag.java                      ← Custom Tag: availability badge
    │       └── GenreIconTag.java                  ← Custom Tag: genre emoji icon
    └── webapp/
        ├── index.jsp                              ← Redirects to /books
        ├── css/
        │   └── style.css                          ← Stylesheet
        └── WEB-INF/
            ├── web.xml                            ← Deployment descriptor
            ├── tld/
            │   └── library.tld                   ← Tag Library Descriptor
            └── views/
                └── books.jsp                     ← Main JSP page (ALL JSTL usage)
```

---

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+
- Apache Tomcat 10+ (or use embedded via Maven plugin)

### Option 1: Maven Embedded Tomcat
```bash
cd library-app
mvn tomcat10:run
```
Then open: http://localhost:8080/library/books

### Option 2: Deploy to Tomcat
```bash
mvn clean package
# Copy target/library-book-manager-1.0-SNAPSHOT.war to <TOMCAT_HOME>/webapps/library.war
```
Then open: http://localhost:8080/library/books

---

## JSTL Tags Used

### Core Tags (`c:`)
| Tag             | Location in books.jsp      | Purpose                              |
|-----------------|---------------------------|--------------------------------------|
| `<c:set>`       | Filter section             | Store searchTerm variable            |
| `<c:out>`       | Title, Author cells        | Safe HTML-escaped output             |
| `<c:if>`        | Genre select, search notice| Single-condition rendering           |
| `<c:choose>`    | Row coloring, empty state  | Multi-branch conditional             |
| `<c:when>`      | Inside c:choose            | Each branch of the conditional       |
| `<c:otherwise>` | Inside c:choose            | Default/fallback branch              |
| `<c:forEach>`   | Genre dropdown, book table | Iterate over collections             |

### Formatting Tags (`fmt:`)
| Tag                  | Location             | Purpose                          |
|----------------------|---------------------|----------------------------------|
| `<fmt:formatDate>`   | Header, Date column | Format Date objects              |
| `<fmt:formatNumber>` | Price, stats        | Format numbers as currency       |

### Functions (`fn:`)
| Function           | Location            | Purpose                          |
|--------------------|---------------------|----------------------------------|
| `fn:length()`      | Search notice       | Get string/collection length     |
| `fn:toUpperCase()` | Search notice       | Transform string to uppercase    |
| `fn:substring()`   | Title column        | Truncate long titles             |
| `fn:contains()`    | Summary footer tip  | Check if string contains value   |
| `fn:toLowerCase()` | Summary footer tip  | Transform string to lowercase    |

### Custom Tags (`lib:`)
| Tag               | Location       | Purpose                               |
|-------------------|----------------|---------------------------------------|
| `<lib:badge>`     | Status column  | Colored availability badge           |
| `<lib:genreIcon>` | Genre column   | Genre-specific emoji icon            |
