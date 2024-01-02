package mk.ukim.finki.wp.kol2022.g2.service.impl;

import mk.ukim.finki.wp.kol2022.g2.model.Course;
import mk.ukim.finki.wp.kol2022.g2.model.Student;
import mk.ukim.finki.wp.kol2022.g2.model.StudentType;
import mk.ukim.finki.wp.kol2022.g2.repository.CourseRepository;
import mk.ukim.finki.wp.kol2022.g2.repository.StudentRepository;
import mk.ukim.finki.wp.kol2022.g2.service.StudentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Student> listAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student findById(Long id) {
        Optional<Student> studentOptional = studentRepository.findById(id);
        return studentOptional.orElse(null);
    }

    @Override
    public Student create(String name, String email, String password, StudentType type, List<Long> courseId, LocalDate enrollmentDate) {
        List<Course> courses = courseRepository.findAllById(courseId);

        return studentRepository.save(new Student(
                name,
                email,
                password,
                type,
                courses,
                enrollmentDate
        ));
    }

    @Override
    public Student update(Long id, String name, String email, String password, StudentType type, List<Long> coursesId, LocalDate enrollmentDate) {
        Optional<Student> studentOptional = studentRepository.findById(id);

        if (studentOptional.isPresent()) {
            studentOptional.get().setName(name);
            studentOptional.get().setEmail(email);
            studentOptional.get().setPassword(password);
            studentOptional.get().setType(type);
            studentOptional.get().setCourses(courseRepository.findAllById(coursesId));
            studentOptional.get().setEnrollmentDate(enrollmentDate);

            return studentRepository.save(studentOptional.get());
        }

        return null;
    }

    @Override
    public Student delete(Long id) {
        Optional<Student> studentOptional = studentRepository.findById(id);

        if (studentOptional.isPresent()) {
            studentRepository.delete(studentOptional.get());
            return studentOptional.get();
        }

        return null;
    }

    @Override
    public List<Student> filter(Long courseId, Integer yearsOfStudying) {
        if (courseId != null && yearsOfStudying != null) {
            return studentRepository.findAllByCoursesContainsAndEnrollmentDateBefore(courseRepository.findById(courseId).get(), LocalDate.now().minusYears(yearsOfStudying));
        } else if (courseId == null && yearsOfStudying != null) {
            return studentRepository.findAllByEnrollmentDateBefore(LocalDate.now().minusYears(yearsOfStudying));
        } else if(courseId != null){
            return studentRepository.findAllByCoursesContains(courseRepository.findById(courseId).get());
        } else {
            return studentRepository.findAll();
        }
    }
}
