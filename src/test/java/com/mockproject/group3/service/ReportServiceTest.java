package com.mockproject.group3.service;

import com.mockproject.group3.dto.ReportDTO;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Report;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.ReportRepository;
import com.mockproject.group3.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateReport() {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setContent("Test Content");
        reportDTO.setCourseId(1);
        reportDTO.setStudentId(1);

        Course course = new Course();
        Student student = new Student();
        Report report = new Report();
        report.setContent("Test Content");
        report.setSentAt(LocalDateTime.now());
        report.setCourse(course);
        report.setStudent(student);

        when(courseRepository.findById(anyInt())).thenReturn(Optional.of(course));
        when(studentRepository.findById(anyInt())).thenReturn(Optional.of(student));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        Report createdReport = reportService.createReport(reportDTO);

        assertNotNull(createdReport);
        assertEquals("Test Content", createdReport.getContent());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    public void testGetAllReports() {
        List<Report> reports = new ArrayList<>();
        reports.add(new Report());
        reports.add(new Report());

        when(reportRepository.findAll()).thenReturn(reports);

        List<Report> result = reportService.getAllReports();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    public void testGetReportById() {
        Report report = new Report();
        report.setId(1);

        when(reportRepository.findById(anyInt())).thenReturn(Optional.of(report));

        Optional<Report> result = reportService.getReportById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(reportRepository, times(1)).findById(anyInt());
    }

    @Test
    public void testUpdateReport() {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setContent("Updated Content");
        reportDTO.setCourseId(1);
        reportDTO.setStudentId(1);

        Report report = new Report();
        Course course = new Course();
        Student student = new Student();

        when(reportRepository.findById(anyInt())).thenReturn(Optional.of(report));
        when(courseRepository.findById(anyInt())).thenReturn(Optional.of(course));
        when(studentRepository.findById(anyInt())).thenReturn(Optional.of(student));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        Report updatedReport = reportService.updateReport(1, reportDTO);

        assertNotNull(updatedReport);
        assertEquals("Updated Content", updatedReport.getContent());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    public void testDeleteReport() {
        doNothing().when(reportRepository).deleteById(anyInt());

        reportService.deleteReport(1);

        verify(reportRepository, times(1)).deleteById(anyInt());
    }
}