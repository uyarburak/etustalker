package com.okapi.stalker.search;

import java.util.SortedSet;
import java.util.TreeSet;

import com.okapi.stalker.data.storage.Stash;

public class FragmentarySearch {

	public enum Heuristic {
		CONSECUTIVE_DISTANCE
	}

	public class SearchResult implements Comparable<SearchResult> {
		public final String key;
		public final Integer precedence;

		private SearchResult(String key, Integer priority) {
			this.key = key;
			this.precedence = priority;
		}

		@Override
		public int compareTo(SearchResult other) {
			int comparePriority = other.precedence.compareTo(precedence);
			if (comparePriority == 0)
				return key.compareTo(other.key);
			else
				return comparePriority;
		}

	}

	private String input;
	private Heuristic heuristic;
	private Iterable<String> space;

	private SortedSet<SearchResult> searchResults = new TreeSet<>();

	public FragmentarySearch(String input, Heuristic heuristic,
			Iterable<String> space) {
		this.input = input.replaceAll("i", "İ").toUpperCase();
		this.space = space;
		this.heuristic = heuristic;

		fragmentarySearch();
	}

	private void fragmentarySearch() {
		for (String element : space) {
			int h1 = consecutiveDistances(element);

			if (h1 < 0)
				continue;

			switch (heuristic) {
			case CONSECUTIVE_DISTANCE:
				searchResults.add(new SearchResult(element, h1));
				break;
			}
		}
	}

	private int consecutiveDistances(String element) {

		Stash st = Stash.get();
		char[] I = input.toCharArray();
		char[] E = element.toCharArray();
		int inputCounter = 0;
		int elementCounter = 0;
		int counterSubString = 0;
		int max = 0;
		while (inputCounter < I.length && elementCounter < E.length && E[elementCounter] != '\0') {
			if (E[elementCounter++] == I[inputCounter]) {
				inputCounter++;
				counterSubString++;
				if(counterSubString > max){
					max = counterSubString;
				}
			}else{
				counterSubString = 0;
			}
		}

		return inputCounter == I.length ? max : -1;
	}

	public SortedSet<SearchResult> getSearchResults() {
		return searchResults;
	}

	// test
	public static void main(String[] args) {
		//Stash.set("res/stash.bin", "res/htmls/spring2016", 595, 603);
		Stash stash = Stash.get();

		SortedSet<SearchResult> results = new FragmentarySearch("FERDEM".trim(),
				Heuristic.CONSECUTIVE_DISTANCE, stash.getStudentKeys()).getSearchResults();
		System.out.println("TOP RESULT: ");
		System.out.println(stash.getStudent(results.first().key));

		System.out.println("FULL LIST:");
		for (SearchResult s : results)
			System.out.println(s.precedence + " " + s.key);

		stash.writeBack();
	}

}
