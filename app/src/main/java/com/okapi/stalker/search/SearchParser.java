package com.okapi.stalker.search;

import com.okapi.stalker.data.storage.Stash;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchParser {

    private class ParseResult {
        public SearchParam param;
        public List<String> results;
        public Integer precedence;

        public ParseResult(SearchParam param, List<String> results,
                           Integer precedence) {
            this.param = param;
            this.results = results;
            this.precedence = precedence;
        }
    }

    private static final List<SearchParam> PRIORITY = Arrays.asList(
            SearchParam.MAJOR, SearchParam.NAME, SearchParam.COURSE);

    private final Map<String, ParseResult> parsingResults = new HashMap<>();

    public void giveInput(String input) {
        String[] literals = input.split(",");
        for (int i = 0; i < literals.length; i++)
            literals[i] = literals[i].trim();

        Map<String, ParseResult> tmp = new HashMap<>(parsingResults);
        parsingResults.clear();

        try {
            parse(literals);
        } catch (ParsingException e) {
            parsingResults.putAll(tmp);
        }
    }

    private void parse(String[] literals) {
        List<SearchParam> priorityList = new ArrayList<>(PRIORITY);

        for (String literal : literals) {
            for (SearchParam param : priorityList) {
                FragmentarySearch frag = new FragmentarySearch(literal, param);
                List<String> results = frag.getResults();
                if (results.isEmpty())
                    continue;
                int precedence = frag.getPrecedence();

                if (parsingResults.get(literal) == null)
                    parsingResults.put(literal, new ParseResult(param, results,
                            precedence));
                else if (parsingResults.get(literal).precedence > precedence)
                    parsingResults.put(literal, new ParseResult(param, results,
                            precedence));
            }
            if (parsingResults.get(literal) == null)
                throw new ParsingException();
            priorityList.remove(parsingResults.get(literal).param);
        }
    }

    public List<String> getResults() {
        List<ParseResult> list = new ArrayList<>(parsingResults.values());
        List<String> inter = null;
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).param == SearchParam.NAME)
                inter = new ArrayList<>(list.get(i).results);
        if (inter == null)
            inter = new ArrayList<>(list.get(0).results);
        for (int i = 0; i < list.size(); i++)
            inter.retainAll(list.get(i).results);
        return inter;
    }

}