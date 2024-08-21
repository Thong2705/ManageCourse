package com.mockproject.group3.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mockproject.group3.enums.Role;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Users;

@Component
public class GetAuthUserInfo {
    /**
     * @exception AppException
     * @return ID of user authentication
     */
    public int getAuthUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated())
            throw new AppException(ErrorCode.UNAUTHORIZED);

        Object principal = authentication.getPrincipal();
        if (principal instanceof Users) {
            Users userDetails = (Users) principal;
            return getIdByRole(userDetails);
        } else {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * If instructor: return instructor ID,
     * If student: return student ID,
     * If admin: return user ID
     * 
     * @param user
     * @return ID base on Role
     */
    private int getIdByRole(Users user) {
        switch (user.getRole()) {
            case Role.ADMIN:
                return user.getId();
            case Role.INSTRUCTOR:
                return user.getInstructor().getId();
            case Role.STUDENT:
                return user.getStudent().getId();
            default:
                throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
