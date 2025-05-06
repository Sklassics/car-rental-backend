package com.sklassics.cars.customadmin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sklassics.cars.admin.service.AddingAdminService;
import com.sklassics.cars.customadmin.service.CustomAddingAdminService;

import java.util.Map;

@RestController
@RequestMapping("/custom-admin")
public class AddingCustomAdminController {

    @Autowired
    private CustomAddingAdminService customAddingAdminService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createAdmin(@RequestParam String mobileNumber) 
    {
        return ResponseEntity.ok(customAddingAdminService.createAdmin(mobileNumber));
    }
}
