package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import org.rmc.training_platform.domain.Course;
import org.rmc.training_platform.dto.CourseReadDto;
import org.rmc.training_platform.dto.CourseWriteDto;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.CourseMapper;
import org.rmc.training_platform.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final MessageService messageService;

    public CourseReadDto create(CourseWriteDto courseWrite) {
        Course course = this.courseRepository.save(this.courseMapper.dtoToEntity(courseWrite));
        return this.courseMapper.entityToDto(course);
    }

    public CourseReadDto update(Long id, CourseWriteDto courseWrite) {
        Course course = this.courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(this.messageService.get("course.not.found")));

        this.courseMapper.updateEntityFromDto(courseWrite, course);
        return this.courseMapper.entityToDto(this.courseRepository.save(course));
    }

}
