package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import org.rmc.training_platform.domain.Course;
import org.rmc.training_platform.dto.CourseReadDto;
import org.rmc.training_platform.dto.CourseWriteDto;
import org.rmc.training_platform.exception.DuplicateNameException;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.CourseMapper;
import org.rmc.training_platform.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService implements CrudBaseService<CourseWriteDto, CourseReadDto> {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final MessageService messageService;

    @Override
    @Transactional(readOnly = true)
    public List<CourseReadDto> getAll() {
        return this.courseMapper.entityToDto(this.courseRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseReadDto getById(Long id) {
        return this.courseMapper.entityToDto(this.getCourseById(id));
    }

    @Override
    public CourseReadDto create(CourseWriteDto courseWrite) {
        this.checkIfExistsName(courseWrite.getName());
        Course course = this.courseRepository.save(this.courseMapper.dtoToEntity(courseWrite));
        return this.courseMapper.entityToDto(course);
    }

    @Override
    public CourseReadDto update(Long id, CourseWriteDto courseWrite) {
        Course course = this.getCourseById(id);

        String name = courseWrite.getName();
        if (!name.isBlank() && !course.getName().equals(name)) {
            this.checkIfExistsName(name);
        }

        this.courseMapper.updateEntityFromDto(courseWrite, course);
        return this.courseMapper.entityToDto(this.courseRepository.save(course));
    }

    @Override
    public void delete(Long id) {
        if (!this.courseRepository.existsById(id)) {
            throw new ResourceNotFoundException(this.messageService.get("course.not.found", id));
        }
        this.courseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CourseReadDto getByName(String name) {
        return this.courseRepository.findByName(name).map(this.courseMapper::entityToDto).orElseThrow(() ->
                new ResourceNotFoundException(this.messageService.get("course.name.not.found", name)));
    }

    private Course getCourseById(Long courseId) {
        return this.courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException(this.messageService.get("course.not.found", courseId)));
    }

    private void checkIfExistsName(String name) {
        if (this.courseRepository.existsByName(name)) {
            throw new DuplicateNameException(this.messageService.get("course.name.already.exists", name));
        }
    }

}
