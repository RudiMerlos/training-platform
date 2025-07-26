package org.rmc.training_platform.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestConfigConstants {

    public static final String DB_FOLDER = "/db";
    public static final String JSON_FOLDER = "/json";
    public static final String EMPLOYEE_FOLDER = JSON_FOLDER + "/employee";
    public static final String COURSE_FOLDER = JSON_FOLDER + "/course";
    public static final String EMPLOYEE_COURSE_FOLDER = JSON_FOLDER + "/employee-course";
    public static final String USER_FOLDER = JSON_FOLDER + "/user";

}
