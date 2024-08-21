package com.mockproject.group3.controller;

import com.mockproject.group3.dto.ReportDTO;
import com.mockproject.group3.model.Report;
import com.mockproject.group3.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody ReportDTO reportDTO) {
        Report report = reportService.createReport(reportDTO);
        return ResponseEntity.ok(report);
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable int id) {
        Report report = reportService.getReportById(id).orElseThrow(() -> new RuntimeException("Report not found"));
        return ResponseEntity.ok(report);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Report> updateReport(@PathVariable int id, @RequestBody ReportDTO reportDTO) {
        Report updatedReport = reportService.updateReport(id, reportDTO);
        return ResponseEntity.ok(updatedReport);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable int id) {
        try {
            reportService.getReportById(id);
            return ResponseEntity.ok("Delete success");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
