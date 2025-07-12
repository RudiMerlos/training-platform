package org.rmc.training_platform.service;

import java.util.List;

public interface CrudBaseService<TWriteDto, TReadDto> {

    List<TReadDto> getAll();

    TReadDto getById(Long id);

    TReadDto create(TWriteDto writeDto);

    TReadDto update(Long id, TWriteDto writeDto);

    void delete(Long id);

}
