package com.library.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Custom Tag: <lib:badge>
 * Renders a colored availability badge for a book.
 * Usage: <lib:badge available="${book.available}" />
 */
public class BadgeTag extends SimpleTagSupport {

    private boolean available;

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        if (available) {
            out.write("<span class=\"badge badge-available\">&#10003; Available</span>");
        } else {
            out.write("<span class=\"badge badge-unavailable\">&#10007; Checked Out</span>");
        }
    }
}
