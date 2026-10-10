
package com.eventhub.backend.controller;

import com.eventhub.backend.dto.StudentResponse;
import com.eventhub.backend.entity.User;
import com.eventhub.backend.repository.UserRepository;
import com.eventhub.backend.repository.NotificationRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public StudentController(
            UserRepository userRepository,
            NotificationRepository notificationRepository
    ) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {

        List<StudentResponse> students =
                userRepository.findByRole("STUDENT")
                        .stream()
                        .map(this::toStudentResponse)
                        .toList();

        return ResponseEntity.ok(students);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {

        return userRepository.findById(id)
                .map(user -> {

                    if (!"STUDENT".equals(user.getRole())) {
                        return ResponseEntity.badRequest()
                                .body("User is not a student");
                    }

                    notificationRepository.deleteByUserId(id);
                    userRepository.delete(user);

                    return ResponseEntity.ok()
                            .body("Student deleted successfully");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private StudentResponse toStudentResponse(User user) {

        return new StudentResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getDepartment(),
                user.getYear()
        );
    }
}
