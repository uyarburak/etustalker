package com.okapi.stalker.data.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.okapi.stalker.data.storage.type.ClassRoom;
import com.okapi.stalker.data.storage.type.Course;
import com.okapi.stalker.data.storage.type.Instructor;
import com.okapi.stalker.data.storage.type.Interval;
import com.okapi.stalker.data.storage.type.Section;
import com.okapi.stalker.data.storage.type.Student;
import com.okapi.stalker.data.storage.type.Interval.Day;
import com.okapi.stalker.data.storage.type.Interval.Time;

import com.okapi.stalker.util.HTMLReader;

public class Stacker {

	private static Stacker stacker;

	Map<String, Course> courseMap;
	Map<String, Section> sectionMap;
	Map<String, Student> studentMap;
	Map<String, Instructor> instructorMap;
	Map<String, ClassRoom> classRoomMap;
	Map<String, Interval> intervalMap;
	Set<String> departmentSet;

	private Stacker() { }

	static Stacker call() {
		if (stacker == null)
			stacker = new Stacker();
		return stacker;
	}

	public void readFrom(String source, int size) {
		courseMap = new HashMap<>();
		sectionMap = new HashMap<>();
		studentMap = new HashMap<>();
		instructorMap = new HashMap<>();
		classRoomMap = new HashMap<>();
		intervalMap = new HashMap<>();
		departmentSet = new HashSet<>();

		for (int i = 1; i < size; i++)
			parseCoursePage(source + "/student_lists/" + i + ".html");

		for (int i = 1; i < size; i++)
			parseProgramPage(source + "/interval_lists/" + i + ".html");

	}

	public void writeTo(String path) {
		FileOutputStream fileStream; 
		ObjectOutputStream objectStream;
		try {
			fileStream = new FileOutputStream(path);
			objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(courseMap);
			objectStream.writeObject(sectionMap);
			objectStream.writeObject(studentMap);
			objectStream.writeObject(instructorMap);
			objectStream.writeObject(classRoomMap);
			objectStream.writeObject(intervalMap);
			objectStream.writeObject(departmentSet);
			objectStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseCoursePage(String path) {
		Course course = new Course();
		Instructor instructor;

		HTMLReader reader = new HTMLReader(new File(path), "ISO8859-9");
		List<String> sections = reader.findAfter("b");
		List<String> students = reader.findBetween("td");

		int index;
		String codeAndTitle;
		try{
			codeAndTitle = reader.findAfter("h3").get(0);
		}catch(IndexOutOfBoundsException e){
			return;
		}
		index = codeAndTitle.indexOf('-');
		course.code = codeAndTitle.substring(0, index - 1);
		course.title = codeAndTitle.substring(index + 2);

		for (int i = 0; i < sections.size(); i = i + 2) {
			Section section = new Section();
			section.course = course.key(); // changed: section.course = course.code();

			String numberAndInstructor = sections.get(i);
			index = numberAndInstructor.indexOf(' ');
			section.number = numberAndInstructor.substring(5, 7).trim();
			section.instructor = numberAndInstructor.substring(index + 2);

			String size = sections.get(i + 1);
			index = sections.get(i + 1).indexOf(' ');
			section.size = Integer.parseInt(size.substring(0, index));

			for (int j = 0; j < section.size * 7; j = j + 7) {
				Student student = new Student();

				student.id = students.get(j);
				student.name = students.get(j + 1);
				student.major = students.get(j + 2);
				student.year = students.get(j + 3);

				HTMLReader mailReader = new HTMLReader(students.get(j + 4));
				student.mail = mailReader.findBetween("a").get(0);

				if(studentMap.containsKey(student.key())){
					student = studentMap.get(student.key());
				}

				section.addStudent(student);
				student.addSection(section);
				studentMap.put(student.key(), student);

				departmentSet.add(student.major);
			}

			for (int k = 0; k < section.size * 7; k++)
				students.remove(0);
			if(instructorMap.containsKey(section.instructor)){
				instructor = instructorMap.get(section.instructor);
			}
			else{
				instructor = new Instructor();
				instructor.name = section.instructor;
				instructorMap.put(instructor.key(), instructor);
			}

			instructor.addSection(section);
			course.addSection(section);
			sectionMap.put(section.key(), section);
		}
		courseMap.put(course.key(), course);
	}

	private void parseProgramPage(String path) {
		Course course = new Course();
		Instructor instructor;

		HTMLReader reader = new HTMLReader(new File(path), "ISO8859-9");
		List<String> sections = reader.findAfter("b");
		List<String> intervals = reader.findBetween("tr");

		int index;
		String codeAndTitle;
		try{
			codeAndTitle = reader.findAfter("h3").get(0);
		}catch(IndexOutOfBoundsException e){
			return;
		}
		index = codeAndTitle.indexOf('-');
		course.code = codeAndTitle.substring(0, index - 1);
		course.title = codeAndTitle.substring(index + 2);

		for (int i = 0; i < sections.size(); i++) {
			Section section = new Section();
			section.course = course.code;

			String numberAndInstructor = sections.get(i);
			index = numberAndInstructor.indexOf(' ');
			section.number = numberAndInstructor.substring(5, 7).trim();
			section.instructor = numberAndInstructor.substring(index + 2);

			section = sectionMap.get(section.key());

			String[] split;
			for (int j = 1; j < 13; j++) {
				split = intervals.get(j).split("<td>");
				for (int j2 = 1; j2 < 7; j2++) {
					String classRoom = split[j2];
					if(classRoom.equals("-")){
						continue;
					}

					Interval interval = new Interval();
					interval.time = Time.values()[j-1];
					interval.day = Day.values()[j2-1];
					ClassRoom mClassRoom = new ClassRoom(classRoom.substring(9).trim());
					if(!classRoomMap.containsKey(mClassRoom.key())){
						classRoomMap.put(mClassRoom.key(), mClassRoom);
					}
					mClassRoom = classRoomMap.get(mClassRoom.key());
					interval.classRoom = mClassRoom;
					mClassRoom.addInterval(interval);
					intervalMap.put(interval.key(), interval);
					section.addInterval(interval);
				}
			}

			for (int k = 0; k < 13; k++)
				intervals.remove(0);
		}
	}
}
