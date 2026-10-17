package com.example.pagestock.service;

import com.example.pagestock.dto.author.AuthorRequest;
import com.example.pagestock.dto.author.AuthorResponse;
import com.example.pagestock.exceptions.AuthorAlreadyExistsException;
import com.example.pagestock.exceptions.AuthorNotEmptyException;
import com.example.pagestock.exceptions.ItemNotFoundException;
import com.example.pagestock.model.Author;
import com.example.pagestock.repository.AuthorRepository;
import com.example.pagestock.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private AuthorRepository authorRepository;
    private BookRepository bookRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository,  BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public List<AuthorResponse> findAll() {
        return authorRepository.findAll().stream().map(this::toResponse).toList();
    }

    public Optional<AuthorResponse> findById(Long id) {
        return authorRepository.findById(id).map(this::toResponse);
    }

    public List<AuthorResponse> findByName(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name).stream().map(this::toResponse).toList();
    }

    public AuthorResponse save(AuthorRequest authorRequest) {
        if (authorRepository.existsByNameEqualsIgnoreCase(authorRequest.name().trim())){
            throw new AuthorAlreadyExistsException(authorRequest.name());
        }
        Author author = new Author(authorRequest.name().trim());

        return toResponse(authorRepository.save(author));
    }

    public Optional<AuthorResponse> update(AuthorRequest authorRequest, Long id) {
        if (!authorRepository.existsById(id)){
            throw new ItemNotFoundException("Could not find author with id: " + id);
        }

        String name = authorRequest.name().trim();

        if (authorRepository.existsByNameEqualsIgnoreCaseAndIdNot(name, id)){
            throw new AuthorAlreadyExistsException(name);
        }

        return authorRepository.findById(id).map(author ->  {
            author.setName(name);
            return toResponse(authorRepository.save(author));
        });
    }

    public void delete(Long id) {
        if (!authorRepository.existsById(id)){
            throw new ItemNotFoundException("Could not find author with id: " + id);
        }

        if (bookRepository.existsByAuthorsId(id)){
            throw  new AuthorNotEmptyException(id, bookRepository.countByAuthorsId(id));
        }

        authorRepository.deleteById(id);
    }

    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName()
        );
    }




}
