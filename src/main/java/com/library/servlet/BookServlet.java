package com.library.servlet;

import com.library.model.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private final List<Book> bookList = new ArrayList<>();

    @Override
    public void init() {
        // Sample data
        Calendar cal = Calendar.getInstance();

        cal.set(2024, Calendar.JANUARY, 15);
        bookList.add(new Book(1, "Clean Code", "Robert C. Martin", "Programming", 45.99, 2008, true, cal.getTime()));

        cal.set(2023, Calendar.MARCH, 20);
        bookList.add(new Book(2, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", 12.99, 1925, true, cal.getTime()));

        cal.set(2024, Calendar.FEBRUARY, 10);
        bookList.add(new Book(3, "Introduction to Algorithms", "Cormen et al.", "Computer Science", 89.99, 2009, false, cal.getTime()));

        cal.set(2023, Calendar.NOVEMBER, 5);
        bookList.add(new Book(4, "To Kill a Mockingbird", "Harper Lee", "Fiction", 10.99, 1960, true, cal.getTime()));

        cal.set(2024, Calendar.APRIL, 1);
        bookList.add(new Book(5, "Design Patterns", "Gang of Four", "Programming", 54.99, 1994, false, cal.getTime()));

        cal.set(2024, Calendar.MAY, 18);
        bookList.add(new Book(6, "1984", "George Orwell", "Dystopia", 9.99, 1949, true, cal.getTime()));

        cal.set(2023, Calendar.DECEMBER, 22);
        bookList.add(new Book(7, "The Pragmatic Programmer", "Hunt & Thomas", "Programming", 49.99, 1999, true, cal.getTime()));

        cal.set(2024, Calendar.JUNE, 3);
        bookList.add(new Book(8, "Brave New World", "Aldous Huxley", "Dystopia", 11.99, 1932, false, cal.getTime()));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String genre    = request.getParameter("genre");
        String search   = request.getParameter("search");
        String sortBy   = request.getParameter("sortBy");
        String action   = request.getParameter("action");

        List<Book> filtered = new ArrayList<>(bookList);

        // Filter by genre
        if (genre != null && !genre.isEmpty() && !genre.equals("All")) {
            filtered.removeIf(b -> !b.getGenre().equalsIgnoreCase(genre));
        }

        // Filter by search term
        if (search != null && !search.isEmpty()) {
            String lc = search.toLowerCase();
            filtered.removeIf(b ->
                !b.getTitle().toLowerCase().contains(lc) &&
                !b.getAuthor().toLowerCase().contains(lc)
            );
        }

        // Sort
        if ("price".equals(sortBy)) {
            filtered.sort(Comparator.comparingDouble(Book::getPrice));
        } else if ("year".equals(sortBy)) {
            filtered.sort(Comparator.comparingInt(Book::getYear));
        } else {
            filtered.sort(Comparator.comparing(Book::getTitle));
        }

        // Stats
        long availableCount = filtered.stream().filter(Book::isAvailable).count();
        double totalValue   = filtered.stream().mapToDouble(Book::getPrice).sum();
        double avgPrice     = filtered.isEmpty() ? 0 : totalValue / filtered.size();

        Set<String> genres = new LinkedHashSet<>();
        genres.add("All");
        bookList.forEach(b -> genres.add(b.getGenre()));

        request.setAttribute("books", filtered);
        request.setAttribute("totalBooks", filtered.size());
        request.setAttribute("availableCount", availableCount);
        request.setAttribute("unavailableCount", filtered.size() - availableCount);
        request.setAttribute("totalValue", totalValue);
        request.setAttribute("avgPrice", avgPrice);
        request.setAttribute("genres", genres);
        request.setAttribute("selectedGenre", genre != null ? genre : "All");
        request.setAttribute("searchTerm", search != null ? search : "");
        request.setAttribute("sortBy", sortBy != null ? sortBy : "title");
        request.setAttribute("today", new Date());

        request.getRequestDispatcher("/WEB-INF/views/books.jsp")
               .forward(request, response);
    }
}
