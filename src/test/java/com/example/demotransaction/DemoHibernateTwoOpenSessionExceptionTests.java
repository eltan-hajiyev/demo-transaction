package com.example.demotransaction;

import com.example.demotransaction.model.Book;
import com.example.demotransaction.repository.BookRepository;
import java.util.concurrent.CompletableFuture;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * If use @Transactional
 *
 * <pre>
 * {@code
 * }
 * </pre>
 * <p>
 * * If not use @Transactional
 *
 * <pre>
 * {@code
 * }
 * </pre>
 */
@SpringBootTest
class DemoHibernateTwoOpenSessionExceptionTests {
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    BookRepository bookRepository;

    @Test
    /*-
     * Hibernate: illegally attempted to associate proxy
     */
    void hibernate_session_multithread_call() {
        Integer bookId = 1;
        final Book book = getBook(bookId);

        CompletableFuture<Book> future = taskExecutor.submitListenable(() -> {
            Book savedBook = saveBook(book);
            return savedBook;
        }).completable();

        assertThrows(Exception.class, () -> {
            future.join();
        }).printStackTrace();
    }

    public Book getBook(Integer id) {
        Session session = sessionFactory.openSession();

        Book book = session.find(Book.class, id);

        //to avoid 'two open Sessions' exception
        //do one of next
        //  book.getStudent().getName(); load proxy field by calling field
        //  session.close(); close session
        //  create new student and set

        return book;
    }

    public Book saveBook(Book book) {
        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();
        session.update(book);
        transaction.commit();

        return book;
    }
}
