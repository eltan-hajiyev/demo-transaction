package com.example.demotransaction.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
public class Book implements Serializable {

	private Integer id;

	private String name;

	private Student student;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@JoinColumn(name = "student_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	@Column
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Book{" +
				"id=" + id +
				", name='" + name + '\'' +
				", studentId=" + student.getId() +
				'}';
	}
}
