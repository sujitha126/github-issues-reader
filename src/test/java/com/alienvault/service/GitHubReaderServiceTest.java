package com.alienvault.service;

import com.alienvault.client.GitHubAPIClient;
import com.alienvault.exception.AppException;
import com.alienvault.json.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GitHubReaderServiceTest {

    @Mock
    private GitHubAPIClient gitHubAPIClient;

    @InjectMocks
    private GitHubReaderService gitHubReaderService = new GitHubReaderService(gitHubAPIClient);

    @Before
    public void setUp() throws Exception {

    }

    @Test (expected = AppException.class)
    public void test_getIssues_withNoRepositories() {
        try {
            gitHubReaderService.getIssues(new String[0], Optional.empty(), Optional.empty());
        } catch (AppException e) {
            Mockito.verifyZeroInteractions(gitHubAPIClient);
            assertEquals("repositories cannot be empty. GitHub repositories with the format owner/repository is required.", e.getMessage());
            throw e;
        }
    }

    @Test
    public void test_getIssues_avoids_duplicates() {
        test_getIssues(Optional.empty(), Optional.empty());

    }

    @Test
    public void test_pageSize_oAuth() {
        Optional<Integer> pageSize = Optional.of(50);
        Optional<String> oauthToken = Optional.of("OAUTH_TOKEN");

        test_getIssues(pageSize, oauthToken);
    }

    private void test_getIssues(Optional<Integer> pageSize, Optional<String> oauthToken) {
        String repository = "java/helloworld";
        String[] repositories = { repository, "JAVA/HELLOWORLD", "java/helloworld"};

        Result issues = gitHubReaderService.getIssues(repositories, pageSize, oauthToken);

        verify(gitHubAPIClient, times(1)).getIssues(repository, pageSize, oauthToken);

        //Asserts
        assertNotNull(issues);
        assertTrue(issues.getIssues().isEmpty());
        assertTrue(issues.getTopDay().getDay() == null);
        assertTrue(issues.getTopDay().getOccurrences() == null);
    }
}