package com.example.demotransaction.repository;

import com.example.demotransaction.model.Student;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/*-
 * PESSIMISTIC_READ acquires a shared (read) lock on the associated table row record
 * PESSIMISTIC_WRITE acquires an exclusive (write) lock.
 * if the database does not support shared locks (e.g. Oracle), then a shared lock request 
 */
public interface StudentRepository extends CrudRepository<Student, Integer> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Student a where a.id = :id")
	Student findStudentForWrite(@Param("id") Integer id);

	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("select a from Student a where a.id = :id")
	Student findStudentForRead(@Param("id") Integer id);

}
