package com.codewithyousry.coursemanagementsystemmvc.config;

import com.codewithyousry.coursemanagementsystemmvc.entity.Course;
import com.codewithyousry.coursemanagementsystemmvc.entity.Enrollment;
import com.codewithyousry.coursemanagementsystemmvc.entity.EnrollmentStatus;
import com.codewithyousry.coursemanagementsystemmvc.entity.Instructor;
import com.codewithyousry.coursemanagementsystemmvc.entity.Student;
import com.codewithyousry.coursemanagementsystemmvc.repository.CourseRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.EnrollmentRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.InstructorRepository;
import com.codewithyousry.coursemanagementsystemmvc.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = false)
public class DataInitializer implements CommandLineRunner {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public DataInitializer(InstructorRepository instructorRepository,
                           CourseRepository courseRepository,
                           StudentRepository studentRepository,
                           EnrollmentRepository enrollmentRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public void run(String... args) {
        if (instructorRepository.count() > 0) {
            return;
        }

        Instructor yousry = instructorRepository.save(Instructor.builder()
                .name("Yousry Ahmed")
                .email("yousry@example.com")
                .bio("Senior Java & Spring Boot instructor with 10+ years of experience.")
                .build());

        Instructor sara = instructorRepository.save(Instructor.builder()
                .name("Sara Mahmoud")
                .email("sara@example.com")
                .bio("Full-stack developer specialising in web architecture.")
                .build());

        Course spring = courseRepository.save(Course.builder()
                .title("Spring Boot Mastery")
                .description("Build production-grade applications with Spring Boot and MVC.")
                .price(new BigDecimal("199.00"))
                .startDate(LocalDate.now().plusDays(7))
                .endDate(LocalDate.now().plusMonths(2))
                .registrationStartTime(LocalDateTime.now().minusDays(1))
                .registrationEndTime(LocalDateTime.now().plusMonths(1))
                .instructor(yousry)
                .deleted(false)
                .build());

        Course java = courseRepository.save(Course.builder()
                .title("Java 21 Deep Dive")
                .description("Modern Java features from records to virtual threads.")
                .price(new BigDecimal("149.50"))
                .startDate(LocalDate.now().plusDays(14))
                .endDate(LocalDate.now().plusMonths(3))
                .instructor(sara)
                .deleted(false)
                .build());

        courseRepository.save(Course.builder()
                .title("Thymeleaf & Bootstrap UI")
                .description("Craft responsive server-rendered UIs.")
                .price(new BigDecimal("99.00"))
                .instructor(yousry)
                .deleted(false)
                .build());

        Student ali = studentRepository.save(Student.builder()
                .name("Ali Hassan").email("ali@example.com").joinedOn(LocalDate.now().minusDays(30)).build());
        Student mona = studentRepository.save(Student.builder()
                .name("Mona Adel").email("mona@example.com").joinedOn(LocalDate.now().minusDays(10)).build());
        studentRepository.save(Student.builder()
                .name("Omar Khaled").email("omar@example.com").joinedOn(LocalDate.now().minusDays(3)).build());

        enrollmentRepository.saveAll(List.of(
                Enrollment.builder().student(ali).course(spring)
                        .enrolledOn(LocalDate.now().minusDays(5)).progress(40)
                        .status(EnrollmentStatus.ACTIVE).grade("B").build(),
                Enrollment.builder().student(mona).course(spring)
                        .enrolledOn(LocalDate.now().minusDays(2)).progress(10)
                        .status(EnrollmentStatus.ACTIVE).build(),
                Enrollment.builder().student(ali).course(java)
                        .enrolledOn(LocalDate.now().minusDays(1)).progress(100)
                        .status(EnrollmentStatus.COMPLETED).grade("A").build()
        ));
    }
}
