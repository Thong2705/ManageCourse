package com.mockproject.group3;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mockproject.group3.model.*;
import com.mockproject.group3.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mockproject.group3.enums.EnrollmentStatus;
import com.mockproject.group3.enums.Role;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.model.Category;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Enrollment;
import com.mockproject.group3.model.EnrollmentLessonDetail;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Lesson;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.CategoryRepository;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.EnrollmentLessonDetailRepository;
import com.mockproject.group3.repository.EnrollmentRepository;
import com.mockproject.group3.repository.InstructorRepository;
import com.mockproject.group3.repository.LessonRepository;
import com.mockproject.group3.repository.StudentRepository;
import com.mockproject.group3.repository.UsersRepository;

@SpringBootApplication
public class Group3Application {
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private InstructorRepository instructorRepository;
	@Autowired
	private EnrollmentRepository enrollmentRepository;
	@Autowired
	private LessonRepository lessonRepository;
	@Autowired
	private EnrollmentLessonDetailRepository enrollmentLessonDetailRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private PaymentDetailRepository paymentDetailRepository;
	@Autowired
	private SubCategoryRepository subCategoryRepository;

	public static void main(String[] args) {
		SpringApplication.run(Group3Application.class, args);
	}

	@Bean
	public CommandLineRunner insertUsers(PaymentRepository paymentRepository) {
		return args -> {
			// Create and save Categories
			Category category1 = new Category();
			category1.setName("Technology");
			category1.setDescription("Courses on technology and programming");
			categoryRepository.save(category1);

			Category category2 = new Category();
			category2.setName("Business");
			category2.setDescription("Courses on business and management");
//			category2.setParentCategory(category1);
			categoryRepository.save(category2);

			SubCategory subCategory1 = new SubCategory();
			subCategory1.setName("Programming of Tech");
			subCategory1.setDescription("Courses on programming languages");
			subCategory1.setCategory(category1);
			subCategoryRepository.save(subCategory1);
//			Course course4 = new Course();
//			course4.setTitle("Advanced Java Programming");
//			course4.setDescription("Advanced Java programming concepts.");
//			course4.setPrice(200000);
//			course4.setStatus(Status.APPROVED);
//			course4.setCodeCourse(UUID.randomUUID().toString());
//			course4.setCategory(category1);
//			course4.setInstructor(instructorRepository.findById(1).get());
//			courseRepository.save(course4);
			// Create and save Instructors
			Users instructorUser1 = new Users();
			instructorUser1.setEmail("instructor1@example.com");
			instructorUser1.setPassword(passwordEncoder.encode("password1"));
			instructorUser1.setFull_name("Instructor One");
			instructorUser1.setAddress("123 Street, City");
			instructorUser1.setPhone("1234567890");
			instructorUser1.setCreate_at(LocalDateTime.now());
			instructorUser1.setUpdate_at(LocalDateTime.now());
			instructorUser1.setBlocked(false);
			instructorUser1.setRole(Role.INSTRUCTOR);
			usersRepository.save(instructorUser1);

			Instructor instructor1 = new Instructor();
			instructor1.setInstructor_code("INS001");
			instructor1.setFee(500.0);
			instructor1.setProfession_experience("5 years in software development");
			instructor1.setUser(instructorUser1);
			instructorRepository.save(instructor1);

			Users instructorUser2 = new Users();
			instructorUser2.setEmail("instructor2@example.com");
			instructorUser2.setPassword("password2");
			instructorUser2.setFull_name("Instructor Two");
			instructorUser2.setAddress("456 Avenue, City");
			instructorUser2.setPhone("0987654321");
			instructorUser2.setCreate_at(LocalDateTime.now());
			instructorUser2.setUpdate_at(LocalDateTime.now());
			instructorUser2.setBlocked(false);
			instructorUser2.setRole(Role.INSTRUCTOR);
			usersRepository.save(instructorUser2);

			Users studentuser1 = new Users();
			studentuser1.setEmail("student1@gmail.com");
			studentuser1.setPassword(passwordEncoder.encode("student1"));
			studentuser1.setFull_name("Student One");
			studentuser1.setAddress("456 Avenue, City");
			studentuser1.setPhone("0987654321");
			studentuser1.setCreate_at(LocalDateTime.now());
			studentuser1.setUpdate_at(LocalDateTime.now());
			studentuser1.setBlocked(false);
			studentuser1.setRole(Role.STUDENT);
			usersRepository.save(studentuser1);

			Student student1 = new Student();
			student1.setStudent_code("STU001");
			student1.setUser(studentuser1);
			studentRepository.save(student1);

			Users studentuser2 = new Users();
			studentuser2.setEmail("student2@gmail.com");
			studentuser2.setPassword(passwordEncoder.encode("student2"));
			studentuser2.setFull_name("Student Two");
			studentuser2.setAddress("456 Avenue, City");
			studentuser2.setPhone("0987654321");
			studentuser2.setCreate_at(LocalDateTime.now());
			studentuser2.setUpdate_at(LocalDateTime.now());
			studentuser2.setBlocked(false);
			studentuser2.setRole(Role.STUDENT);
			usersRepository.save(studentuser2);

			Student student2 = new Student();
			student2.setStudent_code("STU002");
			student2.setUser(studentuser2);
			studentRepository.save(student2);



			Instructor instructor2 = new Instructor();
			instructor2.setInstructor_code("INS002");
			instructor2.setFee(700.0);
			instructor2.setProfession_experience("10 years in business management");
			instructor2.setUser(instructorUser2);
			instructorRepository.save(instructor2);

			// Create and save Courses
			Course course1 = new Course();
			course1.setTitle("Java Programming");
			course1.setDescription("Learn Java from scratch.");
			course1.setPrice(100000);
			course1.setStatus(Status.APPROVED);
			course1.setCodeCourse(UUID.randomUUID().toString());
			course1.setCategory(category1);
			course1.setInstructor(instructor1);
			courseRepository.save(course1);

			Course course2 = new Course();
			course2.setTitle("Business Management");
			course2.setDescription("Learn how to manage a business.");
			course2.setPrice(180000);
			course2.setStatus(Status.APPROVED);
			course2.setCodeCourse(UUID.randomUUID().toString());
			course2.setCategory(category2);
			course2.setInstructor(instructor2);
			courseRepository.save(course2);

			Course course3 = new Course();
			course3.setTitle("Advanced Python");
			course3.setDescription("Deep dive into Python programming.");
			course3.setPrice(190000);
			course3.setStatus(Status.PENDING);
			course3.setCodeCourse(UUID.randomUUID().toString());
			course3.setCategory(category1);
			course3.setInstructor(instructor1);
			courseRepository.save(course3);

			Enrollment enrollment1 = new Enrollment();
			enrollment1.setDescription("Enrolled in Java Programming course");
			enrollment1.setStatus(EnrollmentStatus.ENROLLED);
			enrollment1.setCourse(course1);
			enrollment1.setStudent(student1);
			enrollmentRepository.save(enrollment1);

			Enrollment enrollment2 = new Enrollment();
			enrollment2.setDescription("Enrolled in Business Management course");
			enrollment2.setStatus(EnrollmentStatus.ENROLLED);
			enrollment2.setCourse(course2);
			enrollment2.setStudent(student1);
			enrollmentRepository.save(enrollment2);

			Enrollment enrollment3 = new Enrollment();
			enrollment3.setDescription("Enrolled in Advanced Python course");
			enrollment3.setStatus(EnrollmentStatus.ENROLLED);
			enrollment3.setCourse(course1);
			enrollment3.setStudent(student2);
			enrollmentRepository.save(enrollment3);

			Lesson lesson1 = new Lesson();
			lesson1.setTitle("Introduction to Java");
			lesson1.setContent("This lesson covers the basics of Java programming.");
			lesson1.setCourse(course1);
			lessonRepository.save(lesson1);

			Lesson lesson2 = new Lesson();
			lesson2.setTitle("Variables and Data Types");
			lesson2.setContent("This lesson covers variables and data types in Java.");
			lesson2.setCourse(course1);
			lessonRepository.save(lesson2);

			Lesson lesson3 = new Lesson();
			lesson3.setTitle("Control Structures");
			lesson3.setContent("This lesson covers control structures in Java.");
			lesson3.setCourse(course1);
			lessonRepository.save(lesson3);

			Lesson lesson4 = new Lesson();
			lesson4.setTitle("Functions and Methods");
			lesson4.setContent("This lesson covers functions and methods in Java.");
			lesson4.setCourse(course2);
			lessonRepository.save(lesson4);

			Lesson lesson5 = new Lesson();
			lesson5.setTitle("Advanced Functions");
			lesson5.setContent("This lesson covers advanced functions in Java.");
			lesson5.setCourse(course2);
			lessonRepository.save(lesson5);

			Lesson lesson6 = new Lesson();
			lesson6.setTitle("Object-Oriented Programming");
			lesson6.setContent("This lesson covers object-oriented programming in Java.");
			lesson6.setCourse(course2);
			lessonRepository.save(lesson6);

			Lesson lesson7 = new Lesson();
			lesson7.setTitle("Advanced Python Programming");
			lesson7.setContent("This lesson covers advanced Python programming concepts.");
			lesson7.setCourse(course2);
			lessonRepository.save(lesson7);


			Users adminUser1 = new Users();
			adminUser1.setEmail("admin@example.com");
			adminUser1.setPassword(passwordEncoder.encode("password"));
			adminUser1.setFull_name("Admin");
			adminUser1.setAddress("789 Boulevard, City");
			adminUser1.setPhone("1357924680");
			adminUser1.setCreate_at(LocalDateTime.now());
			adminUser1.setUpdate_at(LocalDateTime.now());
			adminUser1.setBlocked(false);
			adminUser1.setRole(Role.ADMIN);
			usersRepository.save(adminUser1);

			EnrollmentLessonDetail enrollmentLessonDetail1 = new EnrollmentLessonDetail();
			enrollmentLessonDetail1.setEnrollment(enrollment1);
			enrollmentLessonDetail1.setLesson(lesson1);
			enrollmentLessonDetailRepository.save(enrollmentLessonDetail1);

			EnrollmentLessonDetail enrollmentLessonDetail2 = new EnrollmentLessonDetail();
			enrollmentLessonDetail2.setEnrollment(enrollment2);
			enrollmentLessonDetail2.setLesson(lesson4);
			enrollmentLessonDetailRepository.save(enrollmentLessonDetail2);

			EnrollmentLessonDetail enrollmentLessonDetail3 = new EnrollmentLessonDetail();
			enrollmentLessonDetail3.setEnrollment(enrollment2);
			enrollmentLessonDetail3.setLesson(lesson5);
			enrollmentLessonDetailRepository.save(enrollmentLessonDetail3);

			EnrollmentLessonDetail enrollmentLessonDetail4 = new EnrollmentLessonDetail();
			enrollmentLessonDetail4.setEnrollment(enrollment1);
			enrollmentLessonDetail4.setLesson(lesson2);
			enrollmentLessonDetailRepository.save(enrollmentLessonDetail4);

			EnrollmentLessonDetail enrollmentLessonDetail5 = new EnrollmentLessonDetail();
			enrollmentLessonDetail5.setEnrollment(enrollment1);
			enrollmentLessonDetail5.setLesson(lesson3);
			enrollmentLessonDetailRepository.save(enrollmentLessonDetail5);

			Payment payment1 = new Payment();
			payment1.setStudent(student1);
			payment1.setStatus(Status.APPROVED);
			payment1.setPayment_date(LocalDateTime.now());
			payment1.setComment("Credit Card");
			paymentRepository.save(payment1);

			PaymentDetail paymentDetail1 = new PaymentDetail();
			paymentDetail1.setPayment(payment1);
			paymentDetail1.setCourses(course1);
			paymentDetailRepository.save(paymentDetail1);

			Payment payment2 = new Payment();
			payment2.setStudent(student1);
			payment2.setStatus(Status.APPROVED);
			payment2.setPayment_date(LocalDateTime.now());
			payment2.setComment("Credit Card");
			paymentRepository.save(payment2);

			PaymentDetail paymentDetail2 = new PaymentDetail();
			paymentDetail2.setPayment(payment2);
			paymentDetail2.setCourses(course3);
			paymentDetailRepository.save(paymentDetail2);
		};
	}
}
