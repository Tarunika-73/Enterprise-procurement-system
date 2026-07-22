package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.CreateDepartmentRequest;
import com.procurement.enterprise.dto.request.UpdateDepartmentRequest;
import com.procurement.enterprise.dto.response.DepartmentResponse;
import com.procurement.enterprise.service.DepartmentService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/v1/departments", "/departments"})
@RequiredArgsConstructor
public class DepartmentController {

    private static final Logger log =
            LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request) {

        log.info("Creating department {}", request.getName());

        DepartmentResponse response = departmentService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Department created successfully",
                        response,
                        HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DepartmentResponse>>> getAllDepartments(
            @ParameterObject Pageable pageable) {

        Page<DepartmentResponse> response =
                departmentService.getAll(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Departments fetched successfully",
                        response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(
            @PathVariable Long id) {

        DepartmentResponse response =
                departmentService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Department fetched successfully",
                        response));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentByCode(
            @PathVariable String code) {

        DepartmentResponse response =
                departmentService.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Department fetched successfully",
                        response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentRequest request) {

        DepartmentResponse response =
                departmentService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Department updated successfully",
                        response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @PathVariable Long id) {

        departmentService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Department deleted successfully",
                        null));
    }
}