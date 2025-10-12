package com.proshop.auth.controller;

import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.auth.dto.response.PageResponse;
import com.proshop.auth.dto.response.ResponseStatus;
import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<UserInfoResponse>> getUser(@PathVariable long id) {
        log.info("Fetching user with id: {}", id);
        GeneralResponse<UserInfoResponse> response = userService.getUserById(id);
        return ResponseEntity.status(response.getStatus().getCode().equals(ResponseStatus.SUCCESS_CODE) ? 200 : 500)
                .body(response);
    }


    @GetMapping("/getAllUser")
    public ResponseEntity<GeneralResponse<PageResponse<UserInfoResponse>>> getAllUsers(Pageable pageable) {
        GeneralResponse<PageResponse<UserInfoResponse>> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<GeneralResponse<Void>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        GeneralResponse<Void> response = getVoidGeneralResponse("User deactivated successfully");
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}/activate")
    public ResponseEntity<GeneralResponse<Void>> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        GeneralResponse<Void> response = getVoidGeneralResponse("User activated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        GeneralResponse<Void> response = getVoidGeneralResponse("User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/soft")
    public ResponseEntity<GeneralResponse<Void>> softDeleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        GeneralResponse<Void> response = getVoidGeneralResponse("User deleted successfully");
        return ResponseEntity.ok(response);
    }

    private static GeneralResponse<Void> getVoidGeneralResponse(String message) {
        GeneralResponse<Void> response = new GeneralResponse<>();
        ResponseStatus status = new ResponseStatus();
        status.setCode(ResponseStatus.SUCCESS_CODE);
        status.setLabel(ResponseStatus.SUCCESS_LABEL);
        status.setMessage(message);
        response.setStatus(status);
        response.setData(null);
        return response;
    }
}
