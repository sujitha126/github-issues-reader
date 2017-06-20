package alienvault.client;

import com.alienvault.client.GitHubAPIClient;
import com.alienvault.json.Issue;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GitHubAPIClientIT {
    private GitHubAPIClient gitHubAPIClient;

    @Before
    public void setUp() throws Exception {
        final Client client = ClientBuilder.newClient();
        gitHubAPIClient = new GitHubAPIClient(client);

    }

    @Test (expected = NotFoundException.class)
    public void getIssues_not_found() throws Exception {
        String org = RandomStringUtils.randomAlphanumeric(6);
        String repo = RandomStringUtils.randomAlphanumeric(6);

        try {
            List<Issue> issues = gitHubAPIClient.getIssues(org + "/" + repo, Optional.empty(), Optional.empty());
        } catch (NotFoundException e) {
            assertTrue(StringUtils.isNotEmpty(e.getMessage()));
            throw e;
        }
    }

//    Uncomment this to test GitHubClient
//    @Test
//    public void getIssues() throws Exception {
//        List<Issue> issues = gitHubAPIClient.getIssues("fabric8io/fabric8", Optional.empty(), Optional.empty());
//        assertFalse(issues.isEmpty());
//        assertTrue(issues.size() > 0);
//
//    }

}