package com.mockproject.group3.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mockproject.group3.dto.ChangePasswordDTO;
import com.mockproject.group3.dto.InstructorDTO;
import com.mockproject.group3.dto.UsersDTO;
import com.mockproject.group3.dto.request.instructor.EarningReq;
import com.mockproject.group3.dto.response.BaseApiResponse;
import com.mockproject.group3.dto.response.instructor.EarningInstructorRes;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.AuthenticationResponse;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Review;
import com.mockproject.group3.service.InstructorService;
import com.mockproject.group3.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/instructor")
public class InstructorController {

    private final InstructorService instructorService;
    private final UsersService usersService;
    

    public InstructorController(InstructorService instructorService, UsersService usersService) {
        this.instructorService = instructorService;
        this.usersService = usersService;
    }


    @GetMapping("/dashboard")
    public ResponseEntity<?> getInstructorDashboard() {
        return ResponseEntity.ok(instructorService.getDashBoard());
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return usersService.refreshToken(request, response);
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            instructorService.changePassword(changePasswordDTO); 
            return ResponseEntity.ok("Đổi mật khẩu thành công");
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UsersDTO userDto) {
        try {
            Instructor instructor = instructorService.updateProfile(userDto);
            return ResponseEntity.ok(instructor);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    @GetMapping("/earning")
    public ResponseEntity<BaseApiResponse<List<EarningInstructorRes>>> getEarningAnalytics(
            @Valid @ModelAttribute EarningReq req) {
        return ResponseEntity.ok().body(new BaseApiResponse<List<EarningInstructorRes>>(0,
                "Get earning detail successfully", instructorService.viewEarning(req)));
    }

    @GetMapping("/courses/{courseId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByInstructorAndCourse(@PathVariable int courseId) {
        List<Review> reviews = instructorService.findReviewsByInstructorAndCourseId(courseId);
        return ResponseEntity.ok(reviews);
    }
    @GetMapping("/search")
    public ResponseEntity<List<InstructorDTO>> searchInstructor(@RequestParam("name") String name) {
        List<InstructorDTO> listInstructorByName = instructorService.searchInstructorDTO(name);
        return ResponseEntity.ok(listInstructorByName);
    }
    
}
