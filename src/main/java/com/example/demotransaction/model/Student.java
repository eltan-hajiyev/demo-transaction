package com.example.demotransaction.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.transaction.Transactional;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@DynamicInsert
public class Student implements Serializable {
	@Id
	@GeneratedValue
	private Integer id;

	@Column
	private Boolean status = false;

	@Column
	private String name;

	@Column
	private Integer count = 0;

	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
	private List<Book> books;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transactional
	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", status=" + status + ", name=" + name + ", count=" + count + ", books=" + books
				+ "]";
	}

}
