package com.vianadanieldev.dispatch.controller;

import com.vianadanieldev.dispatch.dto.DispatchRequest;
import com.vianadanieldev.dispatch.dto.DispatchResponse;
import com.vianadanieldev.dispatch.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/technician-dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @PostMapping
    public ResponseEntity<DispatchResponse> dispatchTechnician(@RequestBody DispatchRequest request) {
        DispatchResponse response = dispatchService.dispatch(request);
        return ResponseEntity.ok(response);
    }
}
