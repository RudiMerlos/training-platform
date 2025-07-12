package org.rmc.training_platform.controller;

import jakarta.validation.Valid;
import org.rmc.training_platform.service.CrudBaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public abstract class CrudBaseController<TWriteDto, TReadDto> {

    protected final CrudBaseService<TWriteDto, TReadDto> service;

    protected CrudBaseController(CrudBaseService<TWriteDto, TReadDto> service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    public List<TReadDto> getAll() {
        return this.service.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    public TReadDto getById(@PathVariable Long id) {
        return this.service.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TReadDto> create(@Valid @RequestBody TWriteDto writeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(writeDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TReadDto> update(@PathVariable Long id, @Valid @RequestBody TWriteDto writeDto) {
        return ResponseEntity.ok(this.service.update(id, writeDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
