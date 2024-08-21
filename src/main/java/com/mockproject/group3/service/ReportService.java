package com.mockproject.group3.service;

import com.mockproject.group3.dto.ReportDTO;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Report;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.ReportRepository;
import com.mockproject.group3.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasAuthority('STUDENT')")
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Report createReport(ReportDTO reportDTO) {
        Report report = new Report();
        report.setContent(reportDTO.getContent());
        report.setSentAt(LocalDateTime.now());

        Course course = courseRepository.findById(reportDTO.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found"));
        report.setCourse(course);

        Student student = studentRepository.findById(reportDTO.getStudentId()).orElseThrow(() -> new RuntimeException("Student not found"));
        report.setStudent(student);

        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(int id) {
        return reportRepository.findById(id);
    }

    public Report updateReport(int id, ReportDTO reportDTO) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));

        report.setContent(reportDTO.getContent());
        report.setSentAt(LocalDateTime.now());

        Course course = courseRepository.findById(reportDTO.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found"));
        report.setCourse(course);

        Student student = studentRepository.findById(reportDTO.getStudentId()).orElseThrow(() -> new RuntimeException("Student not found"));
        report.setStudent(student);

        return reportRepository.save(report);
    }

    public void deleteReport(int id) {
        reportRepository.deleteById(id);
    }
}
