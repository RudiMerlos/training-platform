package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import org.rmc.training_platform.dto.CourseReadDto;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.CourseMapper;
import org.rmc.training_platform.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseQueryService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final MessageService messageService;

    public List<CourseReadDto> getAll() {
        return this.courseMapper.entityToDto(this.courseRepository.findAll());
    }

    public CourseReadDto getById(Long id) {
        return this.courseRepository.findById(id).map(this.courseMapper::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException(this.messageService.get("course.not.found")));
    }

}
