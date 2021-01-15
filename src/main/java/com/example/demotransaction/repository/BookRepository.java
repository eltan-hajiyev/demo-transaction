package com.example.demotransaction.repository;

import com.example.demotransaction.model.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Integer>{

}
