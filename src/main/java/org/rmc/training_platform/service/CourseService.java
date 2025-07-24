package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rmc.training_platform.domain.Course;
import org.rmc.training_platform.dto.CourseReadDto;
import org.rmc.training_platform.dto.CourseWriteDto;
import org.rmc.training_platform.exception.DuplicateFieldException;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.CourseMapper;
import org.rmc.training_platform.repository.CourseRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseService implements CrudBaseService<CourseWriteDto, CourseReadDto> {

    private static final String COURSES = "courses.";

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final MessageService messageService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(COURSES + "getAll")
    public List<CourseReadDto> getAll() {
        return this.courseMapper.entityToDto(this.courseRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = COURSES + "getById", key = "#id")
    public CourseReadDto getById(Long id) {
        return this.courseMapper.entityToDto(this.getCourseById(id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = COURSES + "getAll", allEntries = true),
            @CacheEvict(cacheNames = COURSES + "getById", key = "#result.id", condition = "#result != null"),
            @CacheEvict(cacheNames = COURSES + "getByName", key = "#result.name", condition = "#result != null")
    })
    public CourseReadDto create(CourseWriteDto courseWrite) {
        LOGGER.info("Creating course with name: {}", courseWrite.getName());
        this.checkIfExistsName(courseWrite.getName());
        Course course = this.courseRepository.save(this.courseMapper.dtoToEntity(courseWrite));
        LOGGER.debug("Course created with ID: {}", course.getId());
        return this.courseMapper.entityToDto(course);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = COURSES + "getAll", allEntries = true),
            @CacheEvict(cacheNames = COURSES + "getById", key = "#id"),
            @CacheEvict(cacheNames = COURSES + "getByName", allEntries = true)
    })
    public CourseReadDto update(Long id, CourseWriteDto courseWrite) {
        LOGGER.info("Updating course with ID: {}", id);
        Course course = this.getCourseById(id);

        String name = courseWrite.getName();
        if (!name.isBlank() && !course.getName().equals(name)) {
            this.checkIfExistsName(name);
        }

        this.courseMapper.updateEntityFromDto(courseWrite, course);
        LOGGER.debug("Course with ID {} updated", id);
        return this.courseMapper.entityToDto(this.courseRepository.save(course));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = COURSES + "getAll", allEntries = true),
            @CacheEvict(cacheNames = COURSES + "getById", key = "#id"),
            @CacheEvict(cacheNames = COURSES + "getByName", allEntries = true)
    })
    public void delete(Long id) {
        LOGGER.info("Deleting course with ID: {}", id);
        if (!this.courseRepository.existsById(id)) {
            throw new ResourceNotFoundException(this.messageService.get("course.not.found", id));
        }
        this.courseRepository.deleteById(id);
        LOGGER.debug("Course with ID {} deleted", id);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = COURSES + "getByName", key = "#name")
    public CourseReadDto getByName(String name) {
        return this.courseRepository.findByName(name).map(this.courseMapper::entityToDto).orElseThrow(() -> {
            LOGGER.error("Course with name {} not exists", name);
            return new ResourceNotFoundException(this.messageService.get("course.name.not.found", name));
        });
    }

    private Course getCourseById(Long courseId) {
        return this.courseRepository.findById(courseId).orElseThrow(() -> {
            LOGGER.error("Course with ID {} not exists", courseId);
            return new ResourceNotFoundException(this.messageService.get("course.not.found", courseId));
        });
    }

    private void checkIfExistsName(String name) {
        if (this.courseRepository.existsByName(name)) {
            LOGGER.error("Course with name {} already exists", name);
            throw new DuplicateFieldException(this.messageService.get("course.name.already.exists", name));
        }
    }

}
