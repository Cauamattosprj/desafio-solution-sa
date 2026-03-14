package com.cauamattosprj.solutionsa.controllers;

import com.cauamattosprj.solutionsa.dtos.address.AddressRequest;
import com.cauamattosprj.solutionsa.dtos.address.AddressResponse;
import com.cauamattosprj.solutionsa.services.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/{userId}/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> findAll(@PathVariable UUID userId) {
        return ResponseEntity.ok(addressService.findAllByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> findById(
            @PathVariable UUID userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(addressService.findById(id, userId));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(
            @PathVariable UUID userId,
            @RequestBody @Valid AddressRequest request) {

        AddressResponse response = addressService.create(userId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(
            @PathVariable UUID userId,
            @PathVariable UUID id,
            @RequestBody @Valid AddressRequest request) {
        return ResponseEntity.ok(addressService.update(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId,
            @PathVariable UUID id) {
        addressService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}