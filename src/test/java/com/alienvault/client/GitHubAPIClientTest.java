package com.alienvault.client;

import com.alienvault.json.Issue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitHubAPIClientTest {

    @Mock
    private Client client;

    @Mock
    private WebTarget webTarget;

    @Mock
    private Invocation.Builder builder;

    @Mock
    private Response response;

    @Mock
    private MultivaluedMap<String, Object> multivaluedMap;

    @InjectMocks
    private GitHubAPIClient gitHubAPIClient = new GitHubAPIClient(client);

    @Before
    public void setUp() throws Exception {
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.get()).thenReturn(response);
        when(response.readEntity(String.class)).thenReturn(getResponse());
    }

    @Test
    public void getIssues_success() throws Exception {
        test_getIssues(Optional.empty(), Optional.empty(),
            "https://api.github.com/repos/hello/world/issues", 1, 4);
    }

    @Test
    public void getIssues_test_pageSize_oAuth() {
        test_getIssues(Optional.of(50), Optional.of("OAUTH_TOKEN"),
            "https://api.github.com/repos/hello/world/issues?access_token=OAUTH_TOKEN&per_page=50", 1, 4);

    }

    @Test
    public void getIssues_test_nextPage() {
        when(multivaluedMap.get("Link"))
            .thenReturn(Collections.singletonList(getNextLinkString()))
            .thenReturn(Collections.singletonList(getNextLinkString()))
            .thenReturn(Collections.singletonList(getNextLinkString()))
            .thenReturn(Collections.singletonList(getLastLinkString()));


        when(response.getHeaders()).thenReturn(multivaluedMap);

        test_getIssues(Optional.empty(), Optional.empty(),
            "https://api.github.com/repos/hello/world/issues", 2, 8);

    }

    private String getNextLinkString() {
        return "<https://api.github.com/repositories/1111111/issues?page=2>; rel=\"next\", <https://api.github.com/repositories/1111111/issues?page=24>; rel=\"last\"";
    }

    private String getLastLinkString() {
        return "<https://api.github.com/repositories/1111111/issues?page=23>; rel=\"previous\", <https://api.github.com/repositories/1111111/issues?page=24>; rel=\"last\"";
    }

    private void test_getIssues(Optional<Integer> pageSize, Optional<String> oAuthToken, String expectedUrl, int wantedNumberOfInvocations,
        int numberOfIssues) {
        List<Issue> issueList = gitHubAPIClient.getIssues("hello/world", pageSize, oAuthToken);

        assertNotNull(issueList);
        //based on getResponseOutput
        assertTrue(issueList.size() == numberOfIssues);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        //verifications
        verify(client, times(wantedNumberOfInvocations)).target(urlCaptor.capture());
        if (wantedNumberOfInvocations == 1) {
            assertEquals(expectedUrl, urlCaptor.getValue());
        } else {
            List<String> urlCaptorAllValues = urlCaptor.getAllValues();
            assertEquals(expectedUrl, urlCaptorAllValues.get(0));
            assertEquals("https://api.github.com/repositories/1111111/issues?page=2", urlCaptorAllValues.get(1));

        }
    }

    private String getResponse() {
        return " [{ \"id\" : 123, \"state\" : \"OPEN\", \"title\" : \"TITLE1\", \"repository_url\":\"https://api.github.com/repos/hello1/hello1\", \"created_at\" : \"2017-01-01T00:00:00Z\" }, { \"id\" : 456, \"state\" : \"CLOSED\", \"title\" : \"TITLE2\", \"repository_url\":\"https://api.github.com/repos/hello2/hello2\", \"created_at\" : \"2017-01-01T00:00:00Z\" }, { \"id\" : 789, \"state\" : \"OPEN\", \"title\" : \"TITLE3\", \"repository_url\":\"https://api.github.com/repos/hello3/hello3\", \"created_at\" : \"2017-01-01T00:00:00Z\" }, { \"id\" : 1222, \"state\" : \"OPEN\", \"title\" : \"TITLE3\", \"repository_url\":\"https://api.github.com/repos/hello2/hello2\", \"created_at\" : \"2017-01-01T00:00:00Z\" } ]";
    }

}