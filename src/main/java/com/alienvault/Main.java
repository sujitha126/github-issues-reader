package com.alienvault;

import com.alienvault.client.GitHubAPIClient;
import com.alienvault.json.Result;
import com.alienvault.service.GitHubReaderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.Optional;

/**
 * GitHub Issues -------------
 * Create a program that generates a report about the the Issues belonging to a
 * list of github repositories ordered by creation time, and information about
 * the day when most Issues were created.
 * Input: ----- List of 1 to n Strings with Github repositories references with
 * the format "owner/repository"
 * Output: ------ String representation of a Json dictionary with the following
 * content:
 * - "issues": List containing all the Issues related to all the repositories
 * provided. The list should be ordered by the Issue "created_at" field (From
 * oldest to newest) Each entry of the list will be a dictionary with basic
 * Issue information: "id", "state", "title", "repository" and "created_at"
 * fields. Issue entry example: { "id": 1, "state": "open", "title": "Found a
 * bug", "repository": "owner1/repository1", "created_at":
 * "2011-04-22T13:33:48Z" }
 * - "top_day": Dictionary with the information of the day when most Issues were
 * created. It will contain the day and the number of Issues that were created
 * on each repository this day If there are more than one "top_day", the latest
 * one should be used. example: { "day": "2011-04-22", "occurrences": {
 * "owner1/repository1": 8, "owner2/repository2": 0, "owner3/repository3": 2 } }
 * Output example: --------------
 * {
 * "issues":[
 * {
 * "id":38,
 * "state":"open",
 * "title":"Found a bug",
 * "repository":"owner1/repository1",
 * "created_at":"2011-04-22T13:33:48Z"
 * },
 * {
 * "id":23,
 * "state":"open",
 * "title":"Found a bug 2",
 * "repository":"owner1/repository1",
 * "created_at":"2011-04-22T18:24:32Z"
 * },
 * {
 * "id":24,
 * "state":"closed",
 * "title":"Feature request",
 * "repository":"owner2/repository2",
 * "created_at":"2011-05-08T09:15:20Z"
 * }
 * ],
 * "top_day":{
 * "day":"2011-04-22",
 * "occurrences":{
 * "owner1/repository1":2,
 * "owner2/repository2":0
 * }
 * }
 * }
 * --------------------------------------------------------
 * You can create the classes and methods you consider. You can use any library
 * you need. Good modularization, error control and code style will be taken
 * into account. Memory usage and execution time will be taken into account.
 * Good Luck!
 */
public class Main {
    final static Logger logger = Logger.getLogger(Main.class);


    /**
     * @param args String array with Github repositories with the format
     *             "owner/repository"
     */
    public static void main(String[] args) throws JsonProcessingException {

        GitHubReaderService gitHubReaderService = getGitHubReaderService();

        Result result = gitHubReaderService.getIssues(args, Optional.empty(), Optional.empty());

        String output = buildJsonOutput(result);

        logger.info("-------------------------------------------------------------------");
        logger.info(output);
        logger.info("-------------------------------------------------------------------");
    }

    private static GitHubReaderService getGitHubReaderService() {
        //SetUp Classes
        final Client client = ClientBuilder.newClient();
        GitHubAPIClient gitHubAPIClient = new GitHubAPIClient(client);
        return new GitHubReaderService(gitHubAPIClient);
    }

    public static String buildJsonOutput(Result result) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        //Convert object to JSON string and pretty print
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
    }

}
