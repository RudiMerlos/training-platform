package org.rmc.training_platform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rmc.training_platform.domain.Course;
import org.rmc.training_platform.dto.CourseReadDto;
import org.rmc.training_platform.dto.CourseWriteDto;
import org.rmc.training_platform.exception.DuplicateFieldException;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.CourseMapper;
import org.rmc.training_platform.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    private static final String COURSE_NOT_FOUND = "Course with ID 1 not found.";
    private static final String COURSE_NAME_NOT_FOUND = "Course with name Java not found.";
    private static final String NAME_ALREADY_EXISTS = "There is already a course with the name Java";

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private CourseService courseService;

    @Test
    void getAll_shouldReturnCourseList() {
        Course course = new Course();
        CourseReadDto courseDto = new CourseReadDto();

        when(this.courseRepository.findAll()).thenReturn(List.of(course));
        when(this.courseMapper.entityToDto(List.of(course))).thenReturn(List.of(courseDto));

        List<CourseReadDto> result = this.courseService.getAll();

        assertThat(result).hasSize(1);
        verify(this.courseRepository).findAll();
        verify(this.courseMapper).entityToDto(List.of(course));
    }

    @Test
    void getById_shouldReturnCourse_whenFound() {
        Long id = 1L;
        Course course = new Course();
        CourseReadDto courseDto = new CourseReadDto();

        when(this.courseRepository.findById(id)).thenReturn(Optional.of(course));
        when(this.courseMapper.entityToDto(course)).thenReturn(courseDto);

        CourseReadDto result = this.courseService.getById(id);

        assertThat(result).isEqualTo(courseDto);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        Long id = 1L;

        when(this.courseRepository.findById(id)).thenReturn(Optional.empty());
        when(this.messageService.get("course.not.found", id)).thenReturn(COURSE_NOT_FOUND);

        assertThatThrownBy(() -> this.courseService.getById(id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(COURSE_NOT_FOUND);
    }

    @Test
    void getByEmail_shouldReturnEmployee_whenFound() {
        String name = "Java";
        Course course = new Course();
        CourseReadDto courseDto = new CourseReadDto();

        when(this.courseRepository.findByName(name)).thenReturn(Optional.of(course));
        when(this.courseMapper.entityToDto(course)).thenReturn(courseDto);

        CourseReadDto result = this.courseService.getByName(name);

        assertThat(result).isEqualTo(courseDto);
    }

    @Test
    void getByName_shouldThrow_whenNotFound() {
        String name = "Java";

        when(this.courseRepository.findByName(name)).thenReturn(Optional.empty());
        when(this.messageService.get("course.name.not.found", name)).thenReturn(COURSE_NAME_NOT_FOUND);

        assertThatThrownBy(() -> this.courseService.getByName(name)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(COURSE_NAME_NOT_FOUND);
    }

    @Test
    void create_shouldSaveNewEmployee() {
        CourseWriteDto courseDto = CourseWriteDto.builder().name("Java").expirationDays(30L).build();

        Course course = new Course();
        CourseReadDto resultDto = new CourseReadDto();

        when(this.courseRepository.existsByName(courseDto.getName())).thenReturn(false);
        when(this.courseMapper.dtoToEntity(courseDto)).thenReturn(course);
        when(this.courseRepository.save(course)).thenReturn(course);
        when(this.courseMapper.entityToDto(course)).thenReturn(resultDto);

        CourseReadDto result = this.courseService.create(courseDto);

        assertThat(result).isEqualTo(resultDto);
    }

    @Test
    void create_shouldThrow_whenEmailExists() {
        CourseWriteDto courseDto = CourseWriteDto.builder().name("Java").expirationDays(30L).build();

        when(this.courseRepository.existsByName(courseDto.getName())).thenReturn(true);
        when(this.messageService.get("course.name.already.exists", courseDto.getName()))
                .thenReturn(NAME_ALREADY_EXISTS);

        assertThatThrownBy(() -> this.courseService.create(courseDto)).isInstanceOf(DuplicateFieldException.class)
                .hasMessage(NAME_ALREADY_EXISTS);
    }

    @Test
    void update_shouldModifyEmployee_whenValid() {
        Long id = 1L;
        String newName = "Spring";

        CourseWriteDto writeDto = new CourseWriteDto();
        writeDto.setName(newName);

        Course existingCourse = new Course();
        existingCourse.setId(id);
        existingCourse.setName("Java");

        Course updatedCourse = new Course();
        updatedCourse.setId(id);
        updatedCourse.setName(newName);

        CourseReadDto expectedDto = new CourseReadDto();
        expectedDto.setName(newName);

        when(this.courseRepository.findById(id)).thenReturn(Optional.of(existingCourse));
        when(this.courseRepository.existsByName(newName)).thenReturn(false);
        doNothing().when(this.courseMapper).updateEntityFromDto(writeDto, existingCourse);
        when(this.courseRepository.save(existingCourse)).thenReturn(updatedCourse);
        when(this.courseMapper.entityToDto(updatedCourse)).thenReturn(expectedDto);

        CourseReadDto result = this.courseService.update(id, writeDto);

        assertThat(result.getName()).isEqualTo(newName);

        verify(this.courseRepository).findById(id);
        verify(this.courseRepository).existsByName(newName);
        verify(this.courseMapper).updateEntityFromDto(writeDto, existingCourse);
        verify(this.courseRepository).save(existingCourse);
        verify(this.courseMapper).entityToDto(updatedCourse);
    }

    @Test
    void delete_shouldCallRepositoryDelete_whenExists() {
        Long id = 1L;

        when(this.courseRepository.existsById(id)).thenReturn(true);

        this.courseService.delete(id);

        verify(this.courseRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        Long id = 1L;

        when(this.courseRepository.existsById(id)).thenReturn(false);
        when(this.messageService.get("course.not.found", id)).thenReturn(COURSE_NOT_FOUND);

        assertThatThrownBy(() -> this.courseService.delete(id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(COURSE_NOT_FOUND);

    }

}
