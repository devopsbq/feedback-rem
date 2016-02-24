package io.corbel.resources.rem.feedback;

import java.io.IOException;
import java.nio.file.Files;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Optional;

import javax.ws.rs.core.Response;

import com.google.gson.JsonObject;
import io.corbel.resources.rem.feedback.model.AttachmentPreparation;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        // JacksonMessageBodyProvider require JsonObject and JiraClient require JSONObject
        JSONObject jsonObject = JSONObject.fromObject(entity.toString());
        if (jsonObject.has("project") && jsonObject.has("issueType") && jsonObject.has("summary")) {
            try {
                AttachmentPreparation attachmentPreparation = getAttachment(jsonObject);
                Issue issue = createIssue(jsonObject, jsonObject.remove("project").toString(), jsonObject.remove("issueType").toString());
                sendAttachmentIfExist(issue,attachmentPreparation);
                return Response.ok().build();
            } catch (JiraException exception) {
                String message = "Error creating Jira issue: " + exception.getMessage();
                if (exception.getCause() != null) {
                    message += ", cause: " + exception.getCause();
                }
                LOG.error(message);
            }
        }
        return ErrorResponseFactory.getInstance().badRequest();
    }

    private AttachmentPreparation getAttachment(JSONObject jsonObject){
        AttachmentPreparation attachmentPreparation = null;
        if(jsonObject.has("attachment")) {
            JSONObject jsonAttachment = jsonObject.getJSONObject("attachment");
            if((jsonAttachment != null) && jsonAttachment.has("name") && jsonAttachment.has("content")) {
                attachmentPreparation = new AttachmentPreparation(jsonAttachment.remove("name").toString(),
                        Base64.decodeBase64(jsonAttachment.remove("content").toString()));
            }
            jsonObject.remove("attachment");
        }
        return attachmentPreparation;
    }
    //protected for testing purposes
    protected Issue createIssue(JSONObject jsonObject, String project, String issueType) throws JiraException {
        Issue.FluentCreate issueBuilder = jiraClient.createIssue(project, issueType);
        jsonObject.keySet().forEach(key -> issueBuilder.field(key.toString(), jsonObject.get(key)));
        return issueBuilder.execute();
    }

    private void sendAttachmentIfExist(Issue issue, AttachmentPreparation attachmentPreparation) throws JiraException {
        if (attachmentPreparation == null) return;
        File attachment = getFileFromContent(attachmentPreparation.getContent(), attachmentPreparation.getName());
        try{
            issue.addAttachment(attachment); //ToDo when jira-client 0.6 releases, we should use NewAttachment(String filename, byte[] content)
        } finally {
            attachment.delete();
        }
    }
    //protected for testing purposes
    protected File getFileFromContent (byte[] content, String name) {
        File file = null;
        BufferedOutputStream writer = null;
        try {
            String tmpDirectory = Files.createTempDirectory("imgJira_").toString();
            file = new File(tmpDirectory + File.separator + name);
            writer = new BufferedOutputStream(new FileOutputStream(file));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            LOG.error("Exception writing attachment to file", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    LOG.error("Unexpected exception closing stream", ex);
                }
            }
            return file;
        }
    }

    @Override
    public Class<JsonObject> getType() {
        return JsonObject.class;
    }
}
