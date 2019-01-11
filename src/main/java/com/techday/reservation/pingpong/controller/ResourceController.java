package com.techday.reservation.pingpong.controller;

import static lombok.Lombok.checkNotNull;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techday.reservation.pingpong.model.Table;
import com.techday.reservation.pingpong.service.ResourceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/collaboration-resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PutMapping("/{resourceType}/release")
    public void releaseResource(@PathVariable String resourceType) {
        checkNotNull(resourceType, "Resource type cannot be undefined");
        resourceService.releaseResource(resourceType);
    }

    @PutMapping("/{resourceType}/occupy")
    public void occupyResource(@PathVariable String resourceType) {
        checkNotNull(resourceType, "Resource type cannot be undefined");
        resourceService.occupyResource(resourceType);
    }

    @GetMapping("/{resourceType}/status")
    public Table getStatus(@PathVariable String resourceType) {
        checkNotNull(resourceType, "Resource type cannot be undefined");
        return resourceService.getStatus(resourceType);
    }


}
