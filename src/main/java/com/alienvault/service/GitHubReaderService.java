package com.alienvault.service;

import com.alienvault.client.GitHubAPIClient;
import com.alienvault.exception.AppException;
import com.alienvault.json.Issue;
import com.alienvault.json.Occurrences;
import com.alienvault.json.Result;
import com.alienvault.json.TopDay;
import org.apache.log4j.Logger;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GitHubReaderService {

    private final static Logger logger = Logger.getLogger(GitHubReaderService.class);

    private static final String ERR_EMPTY_REPOSITORIES =
        "repositories cannot be empty. GitHub repositories with the format owner/repository is required.";

    private GitHubAPIClient gitHubAPIClient;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public GitHubReaderService(GitHubAPIClient gitHubAPIClient) {
        this.gitHubAPIClient = gitHubAPIClient;
    }

    /**
     * @param repositories - Array of GitHub repositories with the format owner/repository
     * @param pageSize     - PageSize will be set in GitHub API. Max Value is 100.
     * @param oAuthToken   - If you want to make authenticated requests you can pass the oAuthToken.
     * @return
     */
    public Result getIssues(String[] repositories, Optional<Integer> pageSize, Optional<String> oAuthToken) {

        try {

            if (repositories == null || repositories.length == 0) {
                logger.error(ERR_EMPTY_REPOSITORIES);
                throw new AppException(ERR_EMPTY_REPOSITORIES);
            }

            //convert each repository in repositories to lowerCase and create a set to avoid duplicates
            Set<String> repositoriesSet = Arrays.stream(repositories).filter(str -> str != null).map(str -> str.toLowerCase()).collect(
                Collectors.toSet());

            Result result = new Result();

            for (String repository : repositoriesSet) {
                result.addIssues(gitHubAPIClient.getIssues(repository, pageSize, oAuthToken));
            }

            // Sort issues based on created date
            List<Issue> issues = result.getIssues();

            //Sort issues using CreatedAtComparator which sorts the issues w.r.t created_at (older to latest)
            Collections.sort(issues, new CreatedAtComparator());

            // Find top day with maximum issues
            TopDay topDay = findDayWithMostIssues(issues);

            result.setTopDay(topDay);

            return result;
        } catch (ForbiddenException | NotFoundException e) {
            throw e;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "Exception occurred in getIssues. ";
            logger.error(errorMsg, e);
            throw new AppException(errorMsg, e);
        }

    }

    /**
     * Find the day which has most issues
     *
     * @param issues
     * @return
     */
    private TopDay findDayWithMostIssues(List<Issue> issues) {

        Map<String, Occurrences> mapOfDateAndOccurrences = new HashMap<>();
        int maxIssues = Integer.MIN_VALUE;
        String dateWithMaxIssues = null;

        for (Issue issue : issues) {
            String date = format.format(issue.getCreatedDate());
            int count = 0;
            if (mapOfDateAndOccurrences.containsKey(date)) {

                Occurrences occurrences = mapOfDateAndOccurrences.get(date);
                if (occurrences.getAdditionalProperties().containsKey(issue.getRepository())) {
                    count = occurrences.getAdditionalProperties().get(issue.getRepository());
                }
                occurrences.getAdditionalProperties().put(issue.getRepository(), count + 1);
                mapOfDateAndOccurrences.put(date, occurrences);
            } else {
                Occurrences occurrences = new Occurrences();
                occurrences.setAdditionalProperty(issue.getRepository(), 1);
                mapOfDateAndOccurrences.put(date, occurrences);
            }

            try {
                if (isMaximumAndLatestIssuesDate(mapOfDateAndOccurrences, maxIssues, dateWithMaxIssues, date)) {
                    maxIssues = mapOfDateAndOccurrences.get(date).getTotal();
                    dateWithMaxIssues = date;
                }
            } catch (ParseException e) {
                logger.debug(e);
            }
        }

        TopDay topDay = new TopDay();
        topDay.setDay(dateWithMaxIssues);
        topDay.setOccurrences(mapOfDateAndOccurrences.get(dateWithMaxIssues));

        return topDay;
    }

    private boolean isMaximumAndLatestIssuesDate(Map<String, Occurrences> mapOfDateAndOccurences, int maxIssues, String dateWithMaxIssues,
        String date) throws ParseException {
        return (mapOfDateAndOccurences.get(date).getTotal() > maxIssues) ||
            (mapOfDateAndOccurences.get(date).getTotal() == maxIssues
                && format.parse(date).after(format.parse(dateWithMaxIssues)));
    }

}
