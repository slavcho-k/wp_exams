package mk.ukim.finki.wp.kol2022.g2.repository;

import mk.ukim.finki.wp.kol2022.g2.model.Course;
import mk.ukim.finki.wp.kol2022.g2.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByCoursesContainsAndEnrollmentDateBefore(Course course, LocalDate date);
    List<Student> findAllByCoursesContains(Course course);
    List<Student> findAllByEnrollmentDateBefore(LocalDate date);
}
