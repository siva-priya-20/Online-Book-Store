package com.onlinebookstore.service;

import com.onlinebookstore.dto.BookDTO;
import com.onlinebookstore.exception.ResourceNotFoundException;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public BookDTO addBook(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPrice(dto.getPrice());
        return toDTO(bookRepository.save(book));
    }

    public BookDTO updateBook(Long id, BookDTO dto) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPrice(dto.getPrice());
        return toDTO(bookRepository.save(book));
    }

    public String deleteBook(Long id) {
        bookRepository.deleteById(id);
        return "Book deleted successfully";
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return toDTO(book);
    }

    private BookDTO toDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPrice(book.getPrice());
        return dto;
    }
}
