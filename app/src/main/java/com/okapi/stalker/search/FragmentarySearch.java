package com.okapi.stalker.search;

import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.data.storage.type.Student;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class FragmentarySearch {

    private class SearchResult implements Comparable<SearchResult> {
        public final String key;
        public final Integer precedence;
        public final Integer lcs; // longest common substring

        private SearchResult(String key, Integer precedence, Integer lcs) {
            this.key = key;
            this.precedence = precedence;
            this.lcs = lcs;
        }

        @Override
        public int compareTo(SearchResult other) {
            int cmpPrec = precedence.compareTo(other.precedence);
            if (cmpPrec != 0)
                return cmpPrec;
            int cmpLCS = other.lcs.compareTo(lcs);
            if (cmpLCS != 0)
                return cmpLCS;
            return key.compareTo(other.key);
        }
    }

    private String input;
    private SearchParam param;
    private SortedSet<SearchResult> searchResults = new TreeSet<>();

    public FragmentarySearch(String input, SearchParam param) {
        if (param == SearchParam.COURSE && input.contains(" "))
            input = input.substring(0, 3) + input.substring(4);
        this.input = input.toUpperCase(new Locale("tr", "TR")).trim();
        this.param = param;

        if (param == SearchParam.COURSE) {
            StudentIterator iter = new StudentIterator();
            while (iter.hasNext()) {
                Student s = iter.next();
                if (s.attribute(SearchParam.COURSE).contains(this.input)) {
                    searchResults.add(new SearchResult(s.key(),
                            Integer.MAX_VALUE, Integer.MAX_VALUE));
                }
            }
            return;
        }

        StudentIterator iter = new StudentIterator();
        while (iter.hasNext()) {
            SearchResult result = searchStudent(iter.next());
            if (result != null)
                searchResults.add(result);
        }
    }

    public List<String> getResults() {
        List<String> keys = new ArrayList<>();
        for (SearchResult result : searchResults)
            keys.add(result.key);
        return keys;
    }

    public int getPrecedence() {
        return searchResults.first().precedence;
    }

    private SearchResult searchStudent(Student student) {
        String attribute = student.attribute(param).toUpperCase(
                new Locale("tr", "TR"));

        char[] E = attribute.toCharArray();
        char[] I = input.toCharArray();

        int longestCommonSubstring = 0;
        int fragmentCounter = 0;
        int elementIndex = 0;
        int inputIndex = 0;
        int precedence = 0;
        int distance = 0;

        boolean started = false;
        boolean fragmented = false;

        while (elementIndex < E.length && inputIndex < I.length) {
            if (E[elementIndex] == I[inputIndex]) {
                if (I[inputIndex] != ' ')
                    precedence += distance;
                if (fragmented)
                    fragmentCounter = 1;
                else
                    fragmentCounter++;
                if (fragmentCounter > longestCommonSubstring)
                    longestCommonSubstring++;
                distance = 0;
                inputIndex++;
                started = true;
                fragmented = false;
            } else if (started) {
                if (E[elementIndex] == ' ')
                    distance = 0;
                else
                    distance++;
                fragmented = true;
            }
            elementIndex++;
        }

        if (inputIndex != I.length)
            return null;

        for (String s : attribute.split(" "))
            if (s.equals(input))
                return new SearchResult(student.key(), precedence,
                        Integer.MAX_VALUE);

        return new SearchResult(student.key(), precedence,
                longestCommonSubstring);
    }

    private static class StudentIterator implements Iterator<Student> {
        private Stash stash;
        private Set<String> keys;
        private Iterator<String> iter;

        public StudentIterator() {
            stash = Stash.get();
            keys = stash.getStudentKeys();
            iter = keys.iterator();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public Student next() {
            return stash.getStudent(iter.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}