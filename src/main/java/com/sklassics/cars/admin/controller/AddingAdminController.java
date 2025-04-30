package com.sklassics.cars.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sklassics.cars.admin.service.AddingAdminService;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AddingAdminController {

    @Autowired
    private AddingAdminService adminService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createAdmin(@RequestHeader(value = "Authorization", required = false)@RequestParam String mobileNumber) {
        return ResponseEntity.ok(adminService.createAdmin(mobileNumber));
    }
}
