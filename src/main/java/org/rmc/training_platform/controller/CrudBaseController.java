package org.rmc.training_platform.controller;

import jakarta.validation.Valid;
import org.rmc.training_platform.service.CrudBaseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

public abstract class CrudBaseController<TWriteDto, TReadDto> {

    protected final CrudBaseService<TWriteDto, TReadDto> service;

    protected CrudBaseController(CrudBaseService<TWriteDto, TReadDto> service) {
        this.service = service;
    }

    @GetMapping
    public List<TReadDto> getAll() {
        return this.service.getAll();
    }

    @GetMapping("/{id}")
    public TReadDto getById(@PathVariable final Long id) {
        return this.service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TReadDto create(@Valid @RequestBody final TWriteDto writeDto) {
        return this.service.create(writeDto);
    }

    @PutMapping("/{id}")
    public TReadDto update(@PathVariable final Long id, @Valid @RequestBody final TWriteDto writeDto) {
        return this.service.update(id, writeDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final Long id) {
        this.service.delete(id);
    }

}
