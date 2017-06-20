package com.alienvault.client;

import com.alienvault.json.Issue;
import com.alienvault.exception.AppException;
import com.alienvault.utils.PropertyReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * This class is used to make API calls to GitHub API.
 */
public class GitHubAPIClient {

    private final static Logger logger = Logger.getLogger(GitHubAPIClient.class);

    private static final String GITHUB_API_URL_KEY = "github.api.url";

    private static final String PATH_REPOS = "/repos/";

    private static final String PATH_ISSUES = "/issues";

    private static final String QUERY_PARAM_ACCESS_TOKEN = "access_token";

    private static final String QUERY_PARAM_PER_PAGE = "per_page";

    private static final String REL_NEXT = "rel=\"next\"";

    private Client client;

    public GitHubAPIClient(Client client) {
        this.client = client;
    }

    /**
     * Method calls GitHub API and retrieves list of Issues
     *
     * @param gitHubRepo
     * @param pageSize
     * @param oAuthToken
     * @return
     */
    public List<Issue> getIssues(String gitHubRepo, Optional<Integer> pageSize, Optional<String> oAuthToken) {
        final List<Issue> issues = new ArrayList<>();
        MultivaluedMap<String, Object> headers;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String url = buildGitHubApiUrl(gitHubRepo, pageSize, oAuthToken, stringBuilder);

            headers = invokeGitHubApiForIssues(gitHubRepo, issues, url);

            //If headers contain Link - Check if it has rel="next", which means it have more pages.
            //Traverse each next page using the link provided in the next.

            while (headers != null && headers.get("Link") != null && readNextPageUrl(headers.get("Link")).isPresent()) {
                String nextPageUrl = readNextPageUrl(headers.get("Link")).get();
                headers = invokeGitHubApiForIssues(gitHubRepo, issues, nextPageUrl);
            }

        } catch (ForbiddenException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "Exception occurred while retrieving issues for gitHubRepo: " + gitHubRepo;
            logger.error(errorMsg, e);
            throw new AppException(errorMsg, e);
        }

        return issues;
    }

    /**
     * Looks for rel="next" in the linkHeader, if exists it will parse the url and return.
     *
     * @param linkHeader
     * @return
     */
    private Optional<String> readNextPageUrl(List<Object> linkHeader) {
        for (Object obj : linkHeader) {
            String linkString = obj.toString();
            if (linkString.contains(REL_NEXT)) {
                //This Link response header contains one or more Hypermedia link relations separated by ","
                StringTokenizer stringTokenizer = new StringTokenizer(linkString, ",");
                while (stringTokenizer.hasMoreTokens()) {
                    String token = stringTokenizer.nextToken();
                    if (token.contains(REL_NEXT)) {
                        StringTokenizer tokenizer = new StringTokenizer(token, ";");
                        while (tokenizer.hasMoreTokens()) {
                            String url = tokenizer.nextToken();
                            return Optional.of(url.trim().substring(1, url.length() - 1));
                        }
                    }
                }

            }
        }
        return Optional.empty();
    }

    private String buildGitHubApiUrl(String gitHubRepo, Optional<Integer> pageSize, Optional<String> oAuthToken,
        StringBuilder stringBuilder) throws URISyntaxException {
        final String gitHubUrl = PropertyReader.readProperty(GITHUB_API_URL_KEY);
        stringBuilder.append(gitHubUrl);
        //add path
        stringBuilder.append(PATH_REPOS);
        //add gitHubRepo
        stringBuilder.append(gitHubRepo);
        stringBuilder.append(PATH_ISSUES);

        URIBuilder uriBuilder = new URIBuilder(stringBuilder.toString());

        if (oAuthToken.isPresent()) {
            uriBuilder.addParameter(QUERY_PARAM_ACCESS_TOKEN, oAuthToken.get());
        }

        if (pageSize.isPresent()) {
            uriBuilder.addParameter(QUERY_PARAM_PER_PAGE, String.valueOf(pageSize.get()));
        }

        return uriBuilder.toString();
    }

    private MultivaluedMap<String, Object> invokeGitHubApiForIssues(String gitHubRepo, List<Issue> issues, String url) {
        Response response;
        MultivaluedMap<String, Object> headers;
        response = invokeGitHubApiForIssues(gitHubRepo, url);
        try {
            Issue[] issuesArray = new ObjectMapper().readValue(response.readEntity(String.class), Issue[].class);
            //Sort issues in the order of created date
            issues.addAll(Arrays.asList(issuesArray));
        } catch (IOException e) {
            logger.debug("Exception occurred while DeSerializing response to Issues.", e);
        }
        headers = response.getHeaders();
        return headers;
    }

    private Response invokeGitHubApiForIssues(String gitHubRepo, String url) {
        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();

        int httpStatus = response.getStatus();

        if (httpStatus == HttpStatus.SC_NOT_FOUND) {
            String errorMsg =
                String.format("Git Hub Repo: %s not found. Failure Response: %s.", gitHubRepo, response.readEntity(String.class));
            logger.error(errorMsg);
            throw new NotFoundException(errorMsg);
        } else if (httpStatus == HttpStatus.SC_FORBIDDEN) {
            throw new ForbiddenException(response.readEntity(String.class));
        }
        return response;
    }

}
