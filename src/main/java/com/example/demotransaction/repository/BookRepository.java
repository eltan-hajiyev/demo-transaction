package com.example.demotransaction.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demotransaction.model.Book;

public interface BookRepository extends CrudRepository<Book, Integer>{

}
