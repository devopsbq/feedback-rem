package io.corbel.resources.rem.feedback;

import java.net.URI;
import java.util.Optional;

import javax.ws.rs.core.Response;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.corbel.lib.ws.api.error.ErrorResponseFactory;
import io.corbel.resources.rem.BaseRem;
import io.corbel.resources.rem.request.CollectionParameters;
import io.corbel.resources.rem.request.RequestParameters;

/**
 * @author Alberto J. Rubio
 */
public class JiraFeedbackPostRem extends BaseRem<JSONObject> {

    private static final Logger LOG = LoggerFactory.getLogger(JiraFeedbackPostRem.class);

    private final JiraClient jiraClient;

    public JiraFeedbackPostRem(JiraClient jiraClient) {
        this.jiraClient = jiraClient;
    }

    @Override
    public Response collection(String type, RequestParameters<CollectionParameters> parameters, URI uri, Optional<JSONObject> entity) {
        return entity.map(this::createFeedbackIssue).orElse(ErrorResponseFactory.getInstance().badRequest());
    }

    private Response createFeedbackIssue(JSONObject entity) {
        if (entity.has("project") && entity.has("issueType") && entity.has("summary")) {
            try {
                String project = entity.remove("project").toString();
                String issueType = entity.remove("issueType").toString();
                Issue.FluentCreate issueBuilder = jiraClient.createIssue(project, issueType);

                entity.keySet().forEach(key -> issueBuilder.field(key.toString(), entity.get(key)));
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
    public Class<JSONObject> getType() {
        return JSONObject.class;
    }
}
