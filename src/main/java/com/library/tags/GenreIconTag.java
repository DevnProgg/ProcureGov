package com.library.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Custom Tag: <lib:genreIcon>
 * Renders an emoji icon appropriate for the book genre.
 * Usage: <lib:genreIcon genre="${book.genre}" />
 */
public class GenreIconTag extends SimpleTagSupport {

    private String genre;

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        String icon = switch (genre != null ? genre.toLowerCase() : "") {
            case "programming", "computer science" -> "&#128187;"; // 💻
            case "fiction" -> "&#128218;"; // 📚
            case "dystopia" -> "&#127758;"; // 🌎
            default -> "&#128214;"; // 📖
        };
        out.write("<span class=\"genre-icon\" title=\"" + escapeHtml(genre) + "\">" + icon + "</span>");
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
