package com.okapi.stalker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class HTMLReader {

	private String html;

	public HTMLReader(File file, String charSet) {
		/*
		try (Scanner scanner = new Scanner(file, charSet)) {
			StringBuilder stringBuilder = new StringBuilder();
			while (scanner.hasNextLine())
				stringBuilder.append(scanner.nextLine() + "\n");
			html = stringBuilder.toString();
			html = html.replaceAll(" <td>Evet", " </td><td>Evet").replaceAll("p<td>Devams�z",  "p</td><td>Devams�z").replaceAll("<center>", "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		*/
	}

	public HTMLReader(File file) {
		this(file, "UTF-8");
	}

	public HTMLReader(String html) {
		this.html = html;
	}

	public List<String> findAfter(String key) {
		List<String> list = new ArrayList<>(100);

		int a = html.indexOf('<');
		while (a != -1) {
			int b = html.indexOf('>', a + 1);
			String tag = html.substring(a + 1, b);
			if (tag.equals(key) || tag.contains(key + " ")) {
				int c = html.indexOf('<', b + 1);
				String element = html.substring(b + 1, c);
				list.add(element.trim());
			}
			a = html.indexOf('<', b + 1);
		}
		return list;
	}

	public List<String> findBetween(String key) {
		List<String> list = new ArrayList<>(100);
		Stack<Integer> stack = new Stack<>();

		int a = html.indexOf('<');
		while (a != -1) {
			int b = html.indexOf('>', a + 1);
			String tag = html.substring(a + 1, b);
			if (tag.equals(key) || tag.contains(key + " ")) {
				stack.push(b + 1);
			} else if (tag.equals("/" + key)) {
				try {
					String element = html.substring(stack.pop(), a);
					list.add(element.trim());
				} catch (EmptyStackException e) {
					System.out.println("Beginning/Ending tag is not found...");
				}
			}
			a = html.indexOf('<', b + 1);
		}
		return list;

	}

	public List<String> lookInside(String key) {
		List<String> list = new ArrayList<>(100);

		int a = html.indexOf('<');
		while (a != -1) {
			int b = html.indexOf('>', a + 1);
			String tag = html.substring(a + 1, b);
			if (tag.contains(key)) {
				list.add(tag);
			}
			a = html.indexOf('<', b + 1);
		}
		return list;
	}

	public List<String> findInside(String key, String att) {
		List<String> list = new ArrayList<>(100);

		int a = html.indexOf('<');
		while (a != -1) {
			int b = html.indexOf('>', a + 1);
			String tag = html.substring(a + 1, b);
			if (tag.contains(key)) {
				int c = tag.indexOf(att);
				if (c > 0) {
					c += att.length() + 2;
					int d = tag.indexOf("\"", c);
					list.add(tag.substring(c, d));
				} else {
					list.add("Not found!");
				}
			}
			a = html.indexOf('<', b + 1);
		}
		return list;
	}
}
