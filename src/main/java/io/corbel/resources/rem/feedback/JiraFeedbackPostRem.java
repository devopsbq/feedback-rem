package io.corbel.resources.rem.feedback;

import java.net.URI;
import java.util.Optional;

import javax.ws.rs.core.Response;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import io.corbel.lib.ws.api.error.ErrorResponseFactory;
import io.corbel.resources.rem.BaseRem;
import io.corbel.resources.rem.request.CollectionParameters;
import io.corbel.resources.rem.request.RequestParameters;

/**
 * @author Alberto J. Rubio
 */
public class JiraFeedbackPostRem extends BaseRem<JsonObject> {

    private static final Logger LOG = LoggerFactory.getLogger(JiraFeedbackPostRem.class);

    private final JiraClient jiraClient;

    public JiraFeedbackPostRem(JiraClient jiraClient) {
        this.jiraClient = jiraClient;
    }

    @Override
    public Response collection(String type, RequestParameters<CollectionParameters> parameters, URI uri, Optional<JsonObject> entity) {
        return entity.map(this::createFeedbackIssue).orElse(ErrorResponseFactory.getInstance().badRequest());
    }

    private Response createFeedbackIssue(JsonObject entity) {
        if (entity.has("project") && entity.has("issueType") && entity.has("summary")) {
            try {
                String project = entity.remove("project").getAsString();
                String summary = entity.remove("summary").getAsString();
                String issueType = entity.remove("issueType").getAsString();
                Issue.FluentCreate issueBuilder = jiraClient.createIssue(project, issueType).field("summary", summary);
                entity.entrySet().forEach(entry -> issueBuilder.field(entry.getKey(), entry.getValue()));
                issueBuilder.execute();
                return Response.ok().build();
            } catch (JiraException exception) {
                String message = "Error creating Jira issue: " + exception.getMessage();
                if (exception.getCause() != null) {
                    message += ", cause: " + exception.getMessage();
                }
                LOG.error(message);
            }
        }
        return ErrorResponseFactory.getInstance().badRequest();
    }

    @Override
    public Class<JsonObject> getType() {
        return JsonObject.class;
    }
}
