package com.example.pagestock.service;

import com.example.pagestock.dto.author.AuthorRequest;
import com.example.pagestock.dto.author.AuthorResponse;
import com.example.pagestock.exceptions.AuthorAlreadyExistsException;
import com.example.pagestock.exceptions.ItemNotFoundException;
import com.example.pagestock.model.Author;
import com.example.pagestock.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class AuthorService {

    private AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
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
        if (authorRepository.existsByNameContainingIgnoreCase(authorRequest.name())){
            throw new AuthorAlreadyExistsException(authorRequest.name());
        }
        Author author = new Author(authorRequest.name());

        return toResponse(authorRepository.save(author));
    }

    public Optional<AuthorResponse> update(AuthorRequest authorRequest, Long id) {
        if (!authorRepository.existsById(id)){
            throw new ItemNotFoundException("Could not find author with id: " + id);
        }

        return authorRepository.findById(id).map(author ->  {
            author.setName(authorRequest.name());
            return toResponse(authorRepository.save(author));
        });
    }

    public boolean delete(Long id) {
        if (!authorRepository.existsById(id)){
            throw new ItemNotFoundException("Could not find author with id: " + id);
        }
        authorRepository.deleteById(id);
        return true;
    }









    public AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName()
        );
    }




}
