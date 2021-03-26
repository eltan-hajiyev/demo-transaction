package com.example.demotransaction.model;

import javax.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "school")
public class SchoolFieldBase {
	@Id
	@GeneratedValue
	private Integer id;

	@Column
	private String name;

	@Column
	private String location;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name + "-field";
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location + "-field";
	}

	@Override
	public String toString() {
		return "School [id=" + id + ", name=" + name + ", location=" + location + "]";
	}

}
