package com.alienvault;

import com.alienvault.json.Issue;
import com.alienvault.json.Occurrences;
import com.alienvault.json.Result;
import com.alienvault.json.TopDay;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GitHubMainTest {

    @Test
    public void test_buildJsonOutput() throws ParseException, JsonProcessingException {
        String output = Main.buildJsonOutput(buildResultObject());
        assertEquals(getExpectedString(), output);
    }

    private String getExpectedString() {
        return "{\n"
            + "  \"issues\" : [ {\n"
            + "    \"id\" : 123,\n"
            + "    \"state\" : \"OPEN\",\n"
            + "    \"title\" : \"TITLE1\",\n"
            + "    \"repository\" : \"hello1/hello1\",\n"
            + "    \"created_at\" : \"2017-01-01T00:00:00\"\n"
            + "  }, {\n"
            + "    \"id\" : 456,\n"
            + "    \"state\" : \"CLOSED\",\n"
            + "    \"title\" : \"TITLE2\",\n"
            + "    \"repository\" : \"hello2/hello2\",\n"
            + "    \"created_at\" : \"2017-01-01T00:00:00\"\n"
            + "  }, {\n"
            + "    \"id\" : 789,\n"
            + "    \"state\" : \"OPEN\",\n"
            + "    \"title\" : \"TITLE3\",\n"
            + "    \"repository\" : \"hello3/hello3\",\n"
            + "    \"created_at\" : \"2017-01-01T00:00:00\"\n"
            + "  }, {\n"
            + "    \"id\" : 1222,\n"
            + "    \"state\" : \"OPEN\",\n"
            + "    \"title\" : \"TITLE3\",\n"
            + "    \"repository\" : \"hello1/hello1\",\n"
            + "    \"created_at\" : \"2017-01-01T00:00:00\"\n"
            + "  } ],\n"
            + "  \"top-day\" : {\n"
            + "    \"day\" : \"2017-01-01\",\n"
            + "    \"occurrences\" : {\n"
            + "      \"hello3/hello3\" : 1,\n"
            + "      \"hello2/hello2\" : 1,\n"
            + "      \"hello1/hello1\" : 2\n"
            + "    }\n"
            + "  }\n"
            + "}";
    }

    private Result buildResultObject() throws ParseException {
        Result result = new Result();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = "2017-01-01";
        Date date = simpleDateFormat.parse(dateString);
        List<Issue> issuesList = new ArrayList<>();
        issuesList.add(new Issue(123, "OPEN", "TITLE1", "hello1/hello1", date));
        issuesList.add(new Issue(456, "CLOSED", "TITLE2", "hello2/hello2", date));
        issuesList.add(new Issue(789, "OPEN", "TITLE3", "hello3/hello3", date));
        issuesList.add(new Issue(1222, "OPEN", "TITLE3", "hello1/hello1", date));
        result.addIssues(issuesList);

        Occurrences occurences = new Occurrences();
        occurences.getAdditionalProperties().put("hello1/hello1", 2);
        occurences.getAdditionalProperties().put("hello2/hello2", 1);
        occurences.getAdditionalProperties().put("hello3/hello3", 1);

        TopDay topDay = new TopDay();
        topDay.setDay(dateString);
        topDay.setOccurrences(occurences);
        result.setTopDay(topDay);
        return result;
    }

}