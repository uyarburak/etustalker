package com.okapi.stalker.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.okapi.stalker.data.storage.Stash;

import com.okapi.stalker.search.FragmentarySearch.Heuristic;
import com.okapi.stalker.search.FragmentarySearch.SearchResult;

public class SearchAssist {

	public Stash stash;

	public SearchAssist(String input) {
		Stash stash = Stash.get();
		
		Set<String> deps = new HashSet<>();
		for(String key: stash.getStudentKeys()) {
			deps.add(stash.getStudent(key).major);
		}

		String[] literals = input.split(",");
		Set tokens[] = {deps, stash.getSectionKeys(), stash.getStudentKeys() };

		Map<String, SortedSet<SearchResult>> map = new HashMap<>();

		for (Set<String> token : tokens) {
			SortedSet<SearchResult> r = new FragmentarySearch(
					literals[0].trim(), Heuristic.CONSECUTIVE_DISTANCE, token)
					.getSearchResults();
			
			if (r.isEmpty()) 
				continue;
			
			if (map.get(literals[0]) == null
					|| map.get(literals[0]).isEmpty()
					|| map.get(literals[0]).first().precedence > r.first().precedence)
				map.put(literals[0], r);
		}

		System.out.println(map.get(literals[0]).first().key);
	}

	public static void main(String[] args) {
		Stash stash2 = new SearchAssist("FERDEM, FERDEM").stash;
		for (String string : stash2.getStudentKeys()) {
			System.out.println(stash2.getStudent(string).name);
		}
	}

}
