package com.example.lkm.controller;

import com.example.lkm.entity.UserEntity;
import com.example.lkm.service.UserService;
import com.example.lkm.vo.ApiResponse;
import com.example.lkm.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<?> userList() {

        return ResponseEntity.ok(userService.selectAllUser());
    }

    @PutMapping("/users/{username}/role")
    public ResponseEntity<?> updateRole(@PathVariable String username, @RequestBody Map<String, String> body) {
        String newRole = body.get("role"); // 예: "ADMIN,USER"
        Map<String, Object> result = userService.updataUserRole(username, newRole);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(@RequestParam String userId) {
        boolean isDuplicate = userService.isUserIdDuplicate(userId);
        Map<String, Boolean> response = Map.of("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save-user")
    public ResponseEntity<?> createUser(@RequestBody UserVo vo) {
        try {
            UserEntity savedUser = userService.saveUser(vo);

            ApiResponse<UserEntity> response = new ApiResponse<>(
                    "success",
                    "회원가입이 완료되었습니다.",
                    savedUser
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Object> errorResponse = new ApiResponse<>(
                    "fail",
                    "회원가입 중 오류가 발생했습니다.",
                    null
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


}
