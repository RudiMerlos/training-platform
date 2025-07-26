INSERT INTO employee (id, name, email, department, created_at, modified_at)
              VALUES (1, 'Employee 1', 'employee1@example.com', 'Depart 1', '2025-01-01', '2025-01-01'),
                     (2, 'Employee 2', 'employee2@example.com', 'Depart 2', '2025-01-01', '2025-01-01'),
                     (3, 'Employee 3', 'employee3@example.com', 'Depart 2', '2025-01-01', '2025-01-01');

INSERT INTO course (id, name, description, expiration_days, created_at, modified_at)
              VALUES (1, 'Course 1', 'Description 1', 30, '2025-01-01', '2025-01-01'),
                     (2, 'Course 2', 'Description 2', 20, '2025-01-01', '2025-01-01'),
                     (3, 'Course 3', 'Description 3', 25, '2025-01-01', '2025-01-01');

INSERT INTO employee_course (id, employee_id, course_id, assigned_on, status, created_at, modified_at)
                     VALUES (1, 1, 1, '2025-06-08', 'ASSIGNED', '2025-01-01', '2025-01-01'),
                            (2, 1, 2, '2025-06-08', 'EXPIRED', '2025-01-01', '2025-01-01'),
                            (3, 1, 3, '2025-06-08', 'COMPLETED', '2025-01-01', '2025-01-01'),
                            (4, 2, 1, '2025-06-08', 'ASSIGNED', '2025-01-01', '2025-01-01'),
                            (5, 2, 2, '2025-06-08', 'ASSIGNED', '2025-01-01', '2025-01-01');
