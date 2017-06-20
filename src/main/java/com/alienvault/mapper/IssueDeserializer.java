package com.alienvault.mapper;

import com.alienvault.json.Issue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssueDeserializer extends JsonDeserializer<Issue> {

    private final static Logger logger = Logger.getLogger(IssueDeserializer.class);

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final String ID = "id";

    private static final String STATE = "state";

    private static final String TITLE = "title";

    private static final String CREATED_AT = "created_at";

    private static final String REPOSITORY_URL = "repository_url";

    @Override public Issue deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException {

        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        final Long id = node.get(ID).asLong();

        final String state = node.get(STATE).asText();
        final String title = node.get(TITLE).asText();
        final String createdAtString = node.get(CREATED_AT).asText();
        Date createdAt = null;

        if (createdAtString != null) {
            try {
                createdAt = SIMPLE_DATE_FORMAT.parse(node.get(CREATED_AT).asText());
            } catch (ParseException e) {
                logger.debug("Exception occurred while DeSerializing Issue.", e);
            }
        }

        final String repositoryUrl = node.get(REPOSITORY_URL).asText();

        return new Issue(id, state, title, deriveRepositoryName(repositoryUrl), createdAt);
    }

    private String deriveRepositoryName(String repositoryUrl) {
        if (StringUtils.isNotEmpty(repositoryUrl)) {
            Pattern pattern = Pattern.compile("(?<=repos/).*");
            Matcher matcher = pattern.matcher(repositoryUrl);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }
}
