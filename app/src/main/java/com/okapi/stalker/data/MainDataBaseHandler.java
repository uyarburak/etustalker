package com.okapi.stalker.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.okapi.stalker.activity.SplashActivity;
import com.okapi.stalker.data.storage.model.Course;
import com.okapi.stalker.data.storage.model.Department;
import com.okapi.stalker.data.storage.model.Instructor;
import com.okapi.stalker.data.storage.model.Interval;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.data.storage.model.Student;

import java.io.IOException;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by burak on 6/12/2016.
 */
public class MainDataBaseHandler extends SQLiteOpenHelper {
    private static String DB_PATH = "/data/data/com.okapi.stalker/databases";
    // Database Name
    private static final String DATABASE_NAME = "STALKER_DB";
    // Table names
    private static final String TABLE_STUDENTS = "STUDENTS";
    private static final String TABLE_INSTRUCTORS = "INSTRUCTORS";
    private static final String TABLE_COURSES = "COURSES";
    private static final String TABLE_DEPARTMENTS = "DEPARTMENTS";
    private static final String TABLE_SECTIONS = "SECTIONS";
    private static final String TABLE_INTERVALS = "INTERVALS";
    private static final String TABLE_ENROLLMENTS = "ENROLLMENTS";

    // Students Table Columns names
    private static final String STUDENT_ID_KEY = "id";
    private static final String STUDENT_NAME_KEY = "name";
    private static final String STUDENT_MAIL_KEY = "mail";
    private static final String STUDENT_SEX_KEY = "sex";
    private static final String STUDENT_IMAGE_URL_KEY = "image_url";
    private static final String STUDENT_YEAR_KEY = "year";
    private static final String STUDENT_DEPARTMENT_ID_KEY = "department_id";
    private static final String STUDENT_DEPARTMENT_2_ID_KEY = "department_2_id";
    private static final String STUDENT_ACTIVE_KEY = "active";

    // Instructors Table Columns names
    private static final String INSTRUCTORS_ID_KEY = "id";
    private static final String INSTRUCTORS_NAME_KEY = "name";
    private static final String INSTRUCTORS_MAIL_KEY = "mail";
    private static final String INSTRUCTORS_SEX_KEY = "sex";
    private static final String INSTRUCTORS_OFFICE_KEY = "office";
    private static final String INSTRUCTORS_IMAGE_URL_KEY = "image_url";
    private static final String INSTRUCTORS_WEBSITE_KEY = "website";
    private static final String INSTRUCTORS_LAB_URL_KEY = "lab_url";
    private static final String INSTRUCTORS_DEPARTMENT_ID_KEY = "department_id";

    // Courses Table Columns names
    private static final String COURSES_CODE_KEY = "code";
    private static final String COURSES_TITLE_KEY= "title";
    private static final String COURSES_ACTIVE_KEY = "active";

    // Departments Table Columns names
    private static final String DEPARTMENTS_NAME_KEY = "name";
    private static final String DEPARTMENTS_MAIN_URL_KEY= "main_url";
    private static final String DEPARTMENTS_FACULTY_NAME_KEY = "faculty_name";

    // Sections Table Columns names
    private static final String SECTIONS_ID_KEY = "id";
    private static final String SECTIONS_SECTION_NO_KEY= "section_no";
    private static final String SECTIONS_SIZE_KEY = "size";
    private static final String SECTIONS_INSTRUCTOR_ID_KEY= "instructor_id";
    private static final String SECTIONS_COURSE_ID_KEY = "course_id";

    // Intervals Table Columns names
    private static final String INTERVALS_ID_KEY = "id";
    private static final String INTERVALS_DAY_KEY= "day";
    private static final String INTERVALS_HOUR_KEY = "hour";
    private static final String INTERVALS_ROOM_KEY= "room";
    private static final String INTERVALS_SECTION_ID_KEY = "section_id";

    // Enrollments Table Columns names
    private static final String ENROLLMENTS_STUDENT_ID_KEY = "student_id";
    private static final String ENROLLMENTS_SECTION_ID_KEY= "section_id";

    public MainDataBaseHandler(Context context) {
        this(context, 54);
    }
    public MainDataBaseHandler(Context context, Integer databaseVersion) {
        super(context, DATABASE_NAME, null, databaseVersion);
    }

    public void thatseEnoughBitch(String result){
        SQLiteDatabase db = getReadableDatabase();
        List<String> tables = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String tableName = cursor.getString(1);
            if (!tableName.equals("android_metadata") &&
                    !tableName.equals("sqlite_sequence"))
                tables.add(tableName);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        db = getWritableDatabase();
        for(String tableName:tables) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
        onCreate(db);
        db.close();
        fillDB(result);
    }

    private void fillDB(String result){
        String DELIMITER = "\\#";
        String OBJ_DELIMITER = "\\$";
        String ARRAY_DELIMITER = "\\^";

        SQLiteDatabase db = getWritableDatabase();
        try{
            db.setLockingEnabled(false);
            db.beginTransaction();
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, TABLE_STUDENTS);

            // Get the numeric indexes for each of the columns that we're updating
            final int studentIdIndex = ih.getColumnIndex(STUDENT_ID_KEY);
            final int studentNameIndex = ih.getColumnIndex(STUDENT_NAME_KEY);
            final int studentMailIndex = ih.getColumnIndex(STUDENT_MAIL_KEY);
            final int studentSexIndex = ih.getColumnIndex(STUDENT_SEX_KEY);
            final int studentImageIndex = ih.getColumnIndex(STUDENT_IMAGE_URL_KEY);
            final int studentYearIndex = ih.getColumnIndex(STUDENT_YEAR_KEY);
            final int studentDepartmentIndex = ih.getColumnIndex(STUDENT_DEPARTMENT_ID_KEY);
            final int studentDepartment2Index = ih.getColumnIndex(STUDENT_DEPARTMENT_2_ID_KEY);
            final int studentActiveIndex = ih.getColumnIndex(STUDENT_ACTIVE_KEY);

            String[] entities = result.split(ARRAY_DELIMITER);
            String[] objects = entities[0].split(OBJ_DELIMITER);

            try {
                for (String object : objects) {
                    String[] fields = object.split(DELIMITER);
                    ih.prepareForInsert();

                    ih.bind(studentIdIndex, fields[0]);
                    ih.bind(studentNameIndex, fields[1]);
                    ih.bind(studentMailIndex, fields[2].concat("@etu.edu.tr"));
                    ih.bind(studentYearIndex, Integer.parseInt(fields[3]));
                    ih.bind(studentDepartmentIndex, fields[4]);
                    ih.bind(studentSexIndex, fields[5]);
                    ih.bind(studentImageIndex, fields[6]);
                    ih.bind(studentDepartment2Index, fields[7]);
                    ih.bind(studentActiveIndex, fields[8]);

                    ih.execute();
                }
            }finally {
                ih.close();
            }


            ih = new DatabaseUtils.InsertHelper(db, TABLE_INSTRUCTORS);

            // Get the numeric indexes for each of the columns that we're updating
            final int instructorIdIndex = ih.getColumnIndex(INSTRUCTORS_ID_KEY);
            final int instructorNameIndex = ih.getColumnIndex(INSTRUCTORS_NAME_KEY);
            final int instructorMailIndex = ih.getColumnIndex(INSTRUCTORS_MAIL_KEY);
            final int instructorSexIndex = ih.getColumnIndex(INSTRUCTORS_SEX_KEY);
            final int instructorOfficeIndex = ih.getColumnIndex(INSTRUCTORS_OFFICE_KEY);
            final int instructorImageIndex = ih.getColumnIndex(INSTRUCTORS_IMAGE_URL_KEY);
            final int instructorWebsiteIndex = ih.getColumnIndex(INSTRUCTORS_WEBSITE_KEY);
            final int instructorLabIndex = ih.getColumnIndex(INSTRUCTORS_LAB_URL_KEY);
            final int instructorDepartmentIndex = ih.getColumnIndex(INSTRUCTORS_DEPARTMENT_ID_KEY);
            objects = entities[1].split(OBJ_DELIMITER);
            try {
                for (String object : objects) {
                    String[] fields = object.split(DELIMITER);
                    ih.prepareForInsert();

                    ih.bind(instructorIdIndex, Integer.parseInt(fields[8]));
                    ih.bind(instructorNameIndex, fields[7]);
                    ih.bind(instructorMailIndex, fields[1]);
                    ih.bind(instructorSexIndex, fields[4]);
                    ih.bind(instructorOfficeIndex, fields[2]);
                    ih.bind(instructorImageIndex, fields[6]);
                    ih.bind(instructorWebsiteIndex, fields[5]);
                    ih.bind(instructorLabIndex, fields[0]);
                    ih.bind(instructorDepartmentIndex, fields[3]);

                    ih.execute();
                }
            }finally {
                ih.close();
            }


            ih = new DatabaseUtils.InsertHelper(db, TABLE_COURSES);

            // Get the numeric indexes for each of the columns that we're updating
            final int courseCodeIndex = ih.getColumnIndex(COURSES_CODE_KEY);
            final int courseTitleIndex = ih.getColumnIndex(COURSES_TITLE_KEY);
            final int courseActiveIndex = ih.getColumnIndex(COURSES_ACTIVE_KEY);
            objects = entities[2].split(OBJ_DELIMITER);

            try {
                for (String object : objects) {
                    String[] fields = object.split(DELIMITER);
                    ih.prepareForInsert();

                    ih.bind(courseCodeIndex, fields[0]);
                    ih.bind(courseTitleIndex, fields[1]);
                    ih.bind(courseActiveIndex, Integer.parseInt(fields[2]));

                    ih.execute();
                }
            }finally {
                ih.close();
            }


            ih = new DatabaseUtils.InsertHelper(db, TABLE_DEPARTMENTS);

            // Get the numeric indexes for each of the columns that we're updating
            final int departmentNameIndex = ih.getColumnIndex(DEPARTMENTS_NAME_KEY);
            final int departmentFacultyIndex = ih.getColumnIndex(DEPARTMENTS_FACULTY_NAME_KEY);
            final int departmentMainURLIndex = ih.getColumnIndex(DEPARTMENTS_MAIN_URL_KEY);
            objects = entities[3].split(OBJ_DELIMITER);

            try {
                for (String object : objects) {
                    String[] fields = object.split(DELIMITER);
                    ih.prepareForInsert();

                    ih.bind(departmentNameIndex, fields[2]);
                    ih.bind(departmentFacultyIndex, fields[1]);
                    ih.bind(departmentMainURLIndex, fields[0]);

                    ih.execute();
                }
            }finally {
                ih.close();
            }


            ih = new DatabaseUtils.InsertHelper(db, TABLE_SECTIONS);

            // Get the numeric indexes for each of the columns that we're updating
            final int sectionIdIndex = ih.getColumnIndex(SECTIONS_ID_KEY);
            final int sectionNoIndex = ih.getColumnIndex(SECTIONS_SECTION_NO_KEY);
            final int sectionSizeIndex = ih.getColumnIndex(SECTIONS_SIZE_KEY);
            final int sectionInstructorIndex = ih.getColumnIndex(SECTIONS_INSTRUCTOR_ID_KEY);
            final int sectionCourseIndex = ih.getColumnIndex(SECTIONS_COURSE_ID_KEY);
            objects = entities[4].split(OBJ_DELIMITER);

            try {
                for (String object : objects) {
                    String[] fields = object.split(DELIMITER);
                    ih.prepareForInsert();

                    ih.bind(sectionIdIndex, Integer.parseInt(fields[0]));
                    ih.bind(sectionNoIndex, Integer.parseInt(fields[1]));
                    ih.bind(sectionSizeIndex, Integer.parseInt(fields[2]));
                    ih.bind(sectionCourseIndex, fields[3]);
                    ih.bind(sectionInstructorIndex, Integer.parseInt(fields[4]));

                    ih.execute();
                }
            }finally {
                ih.close();
            }

            ih = new DatabaseUtils.InsertHelper(db, TABLE_INTERVALS);

            // Get the numeric indexes for each of the columns that we're updating
            final int intervalIdIndex = ih.getColumnIndex(INTERVALS_ID_KEY);
            final int intervalDayIndex = ih.getColumnIndex(INTERVALS_DAY_KEY);
            final int intervalHourIndex = ih.getColumnIndex(INTERVALS_HOUR_KEY);
            final int intervalRoomIndex = ih.getColumnIndex(INTERVALS_ROOM_KEY);
            final int intervalSectionIndex = ih.getColumnIndex(INTERVALS_SECTION_ID_KEY);

            objects = entities[5].split(OBJ_DELIMITER);

            try {
                for (String object : objects) {
                    String[] fields = object.split(DELIMITER);
                    ih.prepareForInsert();

                    ih.bind(intervalIdIndex, Integer.parseInt(fields[0]));
                    ih.bind(intervalDayIndex, Integer.parseInt(fields[1]));
                    ih.bind(intervalHourIndex, Integer.parseInt(fields[2]));
                    ih.bind(intervalRoomIndex, fields[3]);
                    ih.bind(intervalSectionIndex, Integer.parseInt(fields[4]));

                    ih.execute();
                }
            }finally {
                ih.close();
            }


            ih = new DatabaseUtils.InsertHelper(db, TABLE_ENROLLMENTS);

            // Get the numeric indexes for each of the columns that we're updating
            final int enrollmentStudentIndex = ih.getColumnIndex(ENROLLMENTS_STUDENT_ID_KEY);
            final int enrollmentSectionIndex = ih.getColumnIndex(ENROLLMENTS_SECTION_ID_KEY);

            objects = entities[6].split(OBJ_DELIMITER);

            try {
                for (String object : objects) {
                    String[] fields = object.split(DELIMITER);
                    ih.prepareForInsert();

                    ih.bind(enrollmentStudentIndex, fields[0]);
                    ih.bind(enrollmentSectionIndex, Integer.parseInt(fields[1]));

                    ih.execute();
                }
            }finally {
                ih.close();
            }


            db.setTransactionSuccessful();
        }finally {
            db.setLockingEnabled(true);
            db.endTransaction();
            db.close();
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "("
                + STUDENT_ID_KEY + " TEXT PRIMARY KEY,"
                + STUDENT_NAME_KEY + " TEXT, "
                + STUDENT_MAIL_KEY + " TEXT, "
                + STUDENT_SEX_KEY + " TEXT, "
                + STUDENT_IMAGE_URL_KEY + " TEXT, "
                + STUDENT_YEAR_KEY + " NUMERIC, "
                + STUDENT_DEPARTMENT_ID_KEY + " TEXT, "
                + STUDENT_DEPARTMENT_2_ID_KEY + " TEXT, "
                + STUDENT_ACTIVE_KEY + " NUMERIC)";

        String CREATE_INSTRUCTORS_TABLE = "CREATE TABLE " + TABLE_INSTRUCTORS + "("
                + INSTRUCTORS_ID_KEY + " NUMERIC PRIMARY KEY,"
                + INSTRUCTORS_NAME_KEY + " TEXT,"
                + INSTRUCTORS_MAIL_KEY + " TEXT, "
                + INSTRUCTORS_OFFICE_KEY + " TEXT, "
                + INSTRUCTORS_DEPARTMENT_ID_KEY + " TEXT, "
                + INSTRUCTORS_SEX_KEY + " TEXT, "
                + INSTRUCTORS_WEBSITE_KEY + " TEXT, "
                + INSTRUCTORS_IMAGE_URL_KEY + " TEXT, "
                + INSTRUCTORS_LAB_URL_KEY + " TEXT)";

        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + "("
                + COURSES_CODE_KEY + " TEXT PRIMARY KEY,"
                + COURSES_TITLE_KEY + " TEXT, "
                + COURSES_ACTIVE_KEY + " NUMBERIC)";

        String CREATE_DEPARTMENTS_TABLE = "CREATE TABLE " + TABLE_DEPARTMENTS + "("
                + DEPARTMENTS_NAME_KEY + " TEXT PRIMARY KEY,"
                + DEPARTMENTS_FACULTY_NAME_KEY + " TEXT, "
                + DEPARTMENTS_MAIN_URL_KEY + " TEXT)";

        String CREATE_SECTIONS_TABLE = "CREATE TABLE " + TABLE_SECTIONS + "("
                + SECTIONS_ID_KEY + " NUMERIC PRIMARY KEY,"
                + SECTIONS_SECTION_NO_KEY + " NUMERIC, "
                + SECTIONS_SIZE_KEY + " NUMERIC,"
                + SECTIONS_COURSE_ID_KEY + " TEXT, "
                + SECTIONS_INSTRUCTOR_ID_KEY + " NUMERIC)";

        String CREATE_INTERVALS_TABLE = "CREATE TABLE " + TABLE_INTERVALS + "("
                + INTERVALS_ID_KEY + " NUMERIC PRIMARY KEY,"
                + INTERVALS_DAY_KEY + " NUMERIC, "
                + INTERVALS_HOUR_KEY + " NUMERIC,"
                + INTERVALS_ROOM_KEY + " TEXT, "
                + INTERVALS_SECTION_ID_KEY + " NUMERIC)";

        String CREATE_ENROLLMENTS_TABLE = "CREATE TABLE " + TABLE_ENROLLMENTS + "("
                + ENROLLMENTS_STUDENT_ID_KEY + " TEXT,"
                + ENROLLMENTS_SECTION_ID_KEY + " NUMERIC,"
                + " PRIMARY KEY ("+ENROLLMENTS_STUDENT_ID_KEY+", "+ENROLLMENTS_SECTION_ID_KEY+"))";

        db.execSQL(CREATE_STUDENTS_TABLE);
        db.execSQL(CREATE_INSTRUCTORS_TABLE);
        db.execSQL(CREATE_COURSES_TABLE);
        db.execSQL(CREATE_DEPARTMENTS_TABLE);
        db.execSQL(CREATE_SECTIONS_TABLE);
        db.execSQL(CREATE_INTERVALS_TABLE);
        db.execSQL(CREATE_ENROLLMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPARTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERVALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENROLLMENTS);

        // Create tables again
        onCreate(db);
    }
    /*
 * get single student
 */
    public Student getStudent(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_STUDENTS + " WHERE "
                + STUDENT_ID_KEY + " = '" + id+"'";

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Student student = new Student();
        try{
            if (c != null)
                c.moveToFirst();
            student.setId(id);
            student.setName(c.getString(c.getColumnIndex(STUDENT_NAME_KEY)));
            student.setMail(c.getString(c.getColumnIndex(STUDENT_MAIL_KEY)));
            student.setYear(c.getInt(c.getColumnIndex(STUDENT_YEAR_KEY)));
            Department dep = new Department();
            dep.setName(c.getString(c.getColumnIndex(STUDENT_DEPARTMENT_ID_KEY)));
            student.setDepartment(dep);
            String secondDepName = c.getString(c.getColumnIndex(STUDENT_DEPARTMENT_2_ID_KEY));
            if(secondDepName != null && secondDepName.length() > 1){
                Department dep2 = new Department();
                dep2.setName(c.getString(c.getColumnIndex(STUDENT_DEPARTMENT_2_ID_KEY)));
                student.setDepartment2(dep2);
            }
            student.setSections(getSectionsOfStudent(id));
            String gender = c.getString(c.getColumnIndex(STUDENT_SEX_KEY));
            student.setGender(gender.isEmpty() ? 'U' : gender.charAt(0));
            student.setImage(c.getString(c.getColumnIndex(STUDENT_IMAGE_URL_KEY)));
        }finally {
            c.close();
        }
        return student;
    }
    public Student getBarelyStudent(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_STUDENTS + " WHERE "
                + STUDENT_ID_KEY + " = '" + id+"'";

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Student student = new Student();
        try{
            if (c != null)
                c.moveToFirst();
            student.setId(id);
            student.setName(c.getString(c.getColumnIndex(STUDENT_NAME_KEY)));
            Department dep = new Department();
            dep.setName(c.getString(c.getColumnIndex(STUDENT_DEPARTMENT_ID_KEY)));
            student.setDepartment(dep);
            String secondDepName = c.getString(c.getColumnIndex(STUDENT_DEPARTMENT_2_ID_KEY));
            if(secondDepName != null && secondDepName.length() > 1){
                Department dep2 = new Department();
                dep2.setName(c.getString(c.getColumnIndex(STUDENT_DEPARTMENT_2_ID_KEY)));
                student.setDepartment2(dep2);
            }
            String gender = c.getString(c.getColumnIndex(STUDENT_SEX_KEY));
            student.setGender(gender.isEmpty() ? 'U' : gender.charAt(0));
            student.setImage(c.getString(c.getColumnIndex(STUDENT_IMAGE_URL_KEY)));
        }finally {
            c.close();
        }
        return student;
    }

    /*
* get single student
*/
    public Set<Student> getAllStudents() {
        final Collator coll = Collator.getInstance(new Locale("tr", "TR"));
        coll.setStrength(Collator.PRIMARY);
        Set<Student> students = new TreeSet<>(new Comparator<Student>() {
            @Override
            public int compare(Student lhs, Student rhs) {
                int comp = coll.compare(lhs.getName(), rhs.getName());
                if(comp != 0)
                    return comp;
                comp = coll.compare(lhs.getDepartment().getName(), rhs.getDepartment().getName());
                if (comp != 0)
                    return comp;
                return coll.compare(lhs.getId(), rhs.getId());
            }
        });
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_STUDENTS + " ORDER BY " + STUDENT_NAME_KEY;

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(c.getString(c.getColumnIndex(SECTIONS_ID_KEY)));
                student.setName(c.getString(c.getColumnIndex(STUDENT_NAME_KEY)));
                Department department = new Department();
                department.setName(c.getString(c.getColumnIndex(STUDENT_DEPARTMENT_ID_KEY)));
                student.setDepartment(department);
                String secondDepName = c.getString(c.getColumnIndex(STUDENT_DEPARTMENT_2_ID_KEY));
                if(secondDepName != null && secondDepName.length() > 1){
                    Department department2 = new Department();
                    department2.setName(secondDepName);
                    student.setDepartment2(department2);
                }
                String gender = c.getString(c.getColumnIndex(STUDENT_SEX_KEY));
                student.setGender(gender.isEmpty() ? 'U' : gender.charAt(0));
                student.setImage(c.getString(c.getColumnIndex(STUDENT_IMAGE_URL_KEY)));
                students.add(student);
            } while (c.moveToNext());
        }
        c.close();

        return students;
    }

    /*
* get single student
*/
    public Department getDepartment(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_DEPARTMENTS + " WHERE "
                + DEPARTMENTS_NAME_KEY + " = '" + id+"'";

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Department department = new Department();
        department.setName(id);
        department.setFaculty(c.getString(c.getColumnIndex(DEPARTMENTS_FACULTY_NAME_KEY)));
        department.setMainURL(c.getString(c.getColumnIndex(DEPARTMENTS_MAIN_URL_KEY)));
        c.close();
        return department;
    }

    public Instructor getInstructorFull(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_INSTRUCTORS + " WHERE "
                + INSTRUCTORS_ID_KEY + " = " + id;

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Instructor instructor = new Instructor();
        instructor.setId(id);
        instructor.setName(c.getString(c.getColumnIndex(INSTRUCTORS_NAME_KEY)));
        instructor.setMail(c.getString(c.getColumnIndex(INSTRUCTORS_MAIL_KEY)));
        Department department = new Department();
        department.setName(c.getString(c.getColumnIndex(INSTRUCTORS_DEPARTMENT_ID_KEY)));
        instructor.setDepartment(department);
        instructor.setOffice(c.getString(c.getColumnIndex(INSTRUCTORS_OFFICE_KEY)));
        instructor.setWebsite(c.getString(c.getColumnIndex(INSTRUCTORS_WEBSITE_KEY)));
        instructor.setLab(c.getString(c.getColumnIndex(INSTRUCTORS_LAB_URL_KEY)));
        instructor.setImage(c.getString(c.getColumnIndex(INSTRUCTORS_IMAGE_URL_KEY)));
        String gender = c.getString(c.getColumnIndex(INSTRUCTORS_SEX_KEY));
        instructor.setGender(gender.isEmpty() ? 'U' : gender.charAt(0));
        instructor.setSections(getSectionsOfInstructors(id));

        c.close();
        return instructor;
    }

    public Instructor getInstructor(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_INSTRUCTORS + " WHERE "
                + INSTRUCTORS_ID_KEY + " = " + id;

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Instructor instructor = new Instructor();
        instructor.setId(id);
        instructor.setName(c.getString(c.getColumnIndex(INSTRUCTORS_NAME_KEY)));
        c.close();
        return instructor;
    }

    /*
* get single student
*/
    public  Set<Section> getSectionsOfStudent(String studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ENROLLMENTS + " WHERE "
                + ENROLLMENTS_STUDENT_ID_KEY + " = '" + studentId+"'";

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Set<Section> sections = new HashSet<Section>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Section section = new Section();
                section.setId(c.getInt(c.getColumnIndex(ENROLLMENTS_SECTION_ID_KEY)));
                sections.add(section);
            } while (c.moveToNext());
        }

        c.close();
        return sections;
    }

    public  Set<Section> getSectionsOfInstructors(Integer instructorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_SECTIONS + " WHERE "
                + SECTIONS_INSTRUCTOR_ID_KEY + " = " + instructorId;

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Set<Section> sections = new HashSet<Section>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Section section = new Section();
                section.setId(c.getInt(c.getColumnIndex(SECTIONS_ID_KEY)));
                Course course = new Course();
                course.setCode(c.getString(c.getColumnIndex(SECTIONS_COURSE_ID_KEY)));
                section.setCourse(course);
                sections.add(section);
            } while (c.moveToNext());
        }

        c.close();
        return sections;
    }

    public  Set<Student> getStudentsOfSection(Integer sectionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ENROLLMENTS + " WHERE "
                + ENROLLMENTS_SECTION_ID_KEY + " = " + sectionId;

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        final Collator coll = Collator.getInstance(new Locale("tr", "TR"));
        coll.setStrength(Collator.PRIMARY);
        Set<Student> students = new TreeSet<Student>(new StudentComparator());
        // looping through all rows and adding to list
        try{
            if (c.moveToFirst()) {
                do {
                    Student student = getBarelyStudent(c.getString(c.getColumnIndex(ENROLLMENTS_STUDENT_ID_KEY)));
                    students.add(student);
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }

        return students;
    }

    public  Set<Interval> getIntervalsOfSection(Integer section_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_INTERVALS + " WHERE "
                + INTERVALS_SECTION_ID_KEY + " = " + section_id;

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Set<Interval> intervals = new HashSet<Interval>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Interval interval = new Interval();
                interval.setDay(c.getInt(c.getColumnIndex(INTERVALS_DAY_KEY)));
                interval.setHour(c.getInt(c.getColumnIndex(INTERVALS_HOUR_KEY)));
                interval.setRoom(c.getString(c.getColumnIndex(INTERVALS_ROOM_KEY)));
                intervals.add(interval);
            } while (c.moveToNext());
        }
        c.close();
        return intervals;
    }


    /*
* get single student
*/
    public Section getSectionFull(Integer sectionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_SECTIONS + " WHERE "
                + SECTIONS_ID_KEY + " = " + sectionId;

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);


        Section section = new Section();
        try{
            if (c != null)
                c.moveToFirst();
            section.setId(sectionId);
            section.setSize(c.getInt(c.getColumnIndex(SECTIONS_SIZE_KEY)));
            section.setSectionNo(c.getInt(c.getColumnIndex(SECTIONS_SECTION_NO_KEY)));
            Instructor instructor = getInstructor(c.getInt(c.getColumnIndex(SECTIONS_INSTRUCTOR_ID_KEY)));
            section.setInstructor(instructor);
            section.setCourse(getCourse(c.getString(c.getColumnIndex(SECTIONS_COURSE_ID_KEY))));
            section.setIntervals(getIntervalsOfSection(sectionId));
            section.setStudents(getStudentsOfSection(sectionId));
        }finally {
            c.close();
        }

        return section;
    }
    public Section getSectionWithoutStudents(Integer sectionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_SECTIONS + " WHERE "
                + SECTIONS_ID_KEY + " = " + sectionId;

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);


        Section section = new Section();
        try{
            if (c != null)
                c.moveToFirst();
            section.setId(sectionId);
            section.setSize(c.getInt(c.getColumnIndex(SECTIONS_SIZE_KEY)));
            section.setSectionNo(c.getInt(c.getColumnIndex(SECTIONS_SECTION_NO_KEY)));
            Instructor instructor = getInstructor(c.getInt(c.getColumnIndex(SECTIONS_INSTRUCTOR_ID_KEY)));
            section.setInstructor(instructor);
            section.setCourse(getCourse(c.getString(c.getColumnIndex(SECTIONS_COURSE_ID_KEY))));
            section.setIntervals(getIntervalsOfSection(sectionId));
        }finally {
            c.close();
        }

        return section;
    }


    public Course getCourse(String courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_COURSES + " WHERE "
                + COURSES_CODE_KEY + " = '" + courseId +"'";

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();

        Course course = new Course();
        course.setCode(courseId);
        course.setTitle(c.getString(c.getColumnIndex(COURSES_TITLE_KEY)));
        course.setActive(true);
        c.close();
        return course;
    }

    public Section getSection(Integer sectionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_SECTIONS + " WHERE "
                + SECTIONS_ID_KEY + " = " + sectionId;

        Log.e("DB LOG", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();

        Section section = new Section();
        section.setId(sectionId);
        section.setSize(c.getInt(c.getColumnIndex(SECTIONS_SIZE_KEY)));
        section.setSectionNo(c.getInt(c.getColumnIndex(SECTIONS_SECTION_NO_KEY)));
        Instructor instructor = getInstructor(c.getInt(c.getColumnIndex(SECTIONS_INSTRUCTOR_ID_KEY)));
        section.setInstructor(instructor);
        Course course = new Course();
        course.setCode(c.getString(c.getColumnIndex(SECTIONS_COURSE_ID_KEY)));
        section.setCourse(course);
        c.close();
        return section;
    }
}
@SuppressWarnings("serial")
class StudentComparator implements Comparator<Student>, Serializable {
    private transient  Collator coll;

    public StudentComparator() {
        coll = Collator.getInstance(new Locale("tr", "TR"));
        coll.setStrength(Collator.PRIMARY);
    }

    @Override
    public int compare(Student lhs, Student rhs) {
        return coll.compare(lhs.getName(), rhs.getName());
    }
}