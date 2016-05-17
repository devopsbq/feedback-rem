package com.bq.corbel.resources.rem.feedback;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.bq.corbel.resources.rem.feedback.model.AttachmentPreparation;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Samuel Martin
 */
public class JiraFeedbackPostRemTest {

    private static final String PROJECT = "TES";
    private static final String ISSUE_TYPE = "Bug";
    private static final String SUMMARY = "test summaryWoW";
    private static final String ATTACHMENT_NAME = "pre.tt.y.bmp";
    private static final String CONTENT_STRING = "Qk2eAAAAAAAAAHoAAABsAAAAAwAAAAMAAAABABgAAAAAACQAAAATCwAAEwsAAAAAAAAAAAAAQkdScw"+
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAD//wD/AP//AAAAA"+
            "ABmZmYA/wAAAH8AAAAAAP9/AAAA//8AAAA=";
    private static final String FILE_NAME = "fi.le.name.bmp";


    private static final String JSON_AS_STRING_WITH_ATTACHMENT = "{'project':'" + PROJECT + "','issueType':'" + ISSUE_TYPE + "','summary':'" + SUMMARY + "','attachments':"+
            "[{'content':'" + CONTENT_STRING + "', name:'" + ATTACHMENT_NAME + "'}]}";
    private static final String JSON_AS_STRING_WITH_ATTACHMENTS = "{'project':'" + PROJECT + "','issueType':'" + ISSUE_TYPE + "','summary':'" + SUMMARY + "','attachments':"+
            "[{'content':'" + CONTENT_STRING + "', name:'" + ATTACHMENT_NAME + "1'},{'content':'\" + CONTENT_STRING + \"', name:'\" + ATTACHMENT_NAME + \"2'}]}";
    private static final String JSON_AS_STRING_WITH_ATTACHMENT_WITHOUT_ATTACHMENT_CONTENT = "{'project':'" + PROJECT + "','issueType':'" + ISSUE_TYPE + "','summary':'" + SUMMARY + "','attachments':"+
            "[{'name':'" + ATTACHMENT_NAME + "'}]}";
    private static final String JSON_AS_STRING_WITH_ATTACHMENT_WITHOUT_ATTACHMENT_NAME = "{'project':'" + PROJECT + "','issueType':'" + ISSUE_TYPE + "','summary':'" + SUMMARY + "','attachments':"+
            "[{'content':'" + CONTENT_STRING + "'}]}";
    private static final String JSON_AS_STRING_WITHOUT_ATTACHMENT = "{'project':'" + PROJECT + "','issueType':'" + ISSUE_TYPE + "','summary':'" + SUMMARY + "'}";
    private static final String JSON_AS_STRING_WITHOUT_ISSUETYPE = "{'project':'" + PROJECT + "','summary':'" + SUMMARY + "'}";
    private static final String JSON_AS_STRING_WITHOUT_SUMMARY = "{'project':'" + PROJECT + "','issueType':'" + ISSUE_TYPE + "'}";
    private static final String JSON_AS_STRING_WITHOUT_PROJECT = "{'issueType':'" + ISSUE_TYPE + "','summary':'" + SUMMARY + "'}";
    private static final String JSON_AS_STRING_WITH_SUMMARY = "{'summary':'" + SUMMARY + "'}";


    private byte[] content;
    private JiraFeedbackPostRem jiraFeedbackPostRem;
    private JiraFeedbackPostRem jiraFeedbackPostRemException;
    private JiraFeedbackPostRem jiraFeedbackPostRemFile;
    private JsonObject jsonObjectWithAttachment;
    private JsonObject jsonObjectWithAttachments;
    private JsonObject jsonObjectWithAttachmentButWithoutAttachmentContent;
    private JsonObject jsonObjectWithAttachmentButWithoutAttachmentName;
    private JsonObject jsonObjectWithoutAttachment;
    private JsonObject jsonObjectWithoutIssueType;
    private JsonObject jsonObjectWithoutSummary;
    private JsonObject jsonObjectWithoutProject;
    private JSONObject jSONObject;
    private Issue issue;
    private Issue issueWithException;
    private JiraClient jiraClient;
    private File file;
    private File fileException;
    private Optional jsonObjectWithAttachmentOptional;
    private Optional jsonObjectWithAttachmentsOptional;
    private Optional jsonObjectWithAttachmentButWithoutAttachmentContentOptional;
    private Optional jsonObjectWithAttachmentButWithoutAttachmentNameOptional;
    private Optional jsonObjectWithoutAttachmentOptional;
    private Optional jsonObjectWithoutIssueTypeOptional;
    private Optional jsonObjectWithoutSummaryOptional;
    private Optional jsonObjectWithoutProjectOptional;

    @Before
    public void setUp() throws Exception {
        jiraClient = mock(JiraClient.class);
        jsonObjectWithAttachment = new JsonParser().parse(JSON_AS_STRING_WITH_ATTACHMENT).getAsJsonObject();
        jsonObjectWithAttachments = new JsonParser().parse(JSON_AS_STRING_WITH_ATTACHMENTS).getAsJsonObject();
        jsonObjectWithAttachmentButWithoutAttachmentContent = new JsonParser().parse(JSON_AS_STRING_WITH_ATTACHMENT_WITHOUT_ATTACHMENT_CONTENT).getAsJsonObject();
        jsonObjectWithAttachmentButWithoutAttachmentName = new JsonParser().parse(JSON_AS_STRING_WITH_ATTACHMENT_WITHOUT_ATTACHMENT_NAME).getAsJsonObject();
        jsonObjectWithoutAttachment = new JsonParser().parse(JSON_AS_STRING_WITHOUT_ATTACHMENT).getAsJsonObject();
        jsonObjectWithoutProject = new JsonParser().parse(JSON_AS_STRING_WITHOUT_PROJECT).getAsJsonObject();
        jsonObjectWithoutSummary = new JsonParser().parse(JSON_AS_STRING_WITHOUT_SUMMARY).getAsJsonObject();
        jsonObjectWithoutIssueType = new JsonParser().parse(JSON_AS_STRING_WITHOUT_ISSUETYPE).getAsJsonObject();
        fillOptionals();
        issue = mock(Issue.class);
        file = mock(File.class);
        doNothing().when(issue).addAttachment(any());
        jiraFeedbackPostRem = Mockito.spy(new JiraFeedbackPostRem(jiraClient));
        jiraFeedbackPostRemFile = Mockito.spy(new JiraFeedbackPostRem(jiraClient));
        doReturn(issue).when(jiraFeedbackPostRem).createIssue(any(JSONObject.class),any(String.class),any(String.class));
        doReturn(file).when(jiraFeedbackPostRem).getFileFromContent(any(),any());
        content = Base64.decodeBase64(CONTENT_STRING);
    }

    private void fillOptionals() {
        jsonObjectWithAttachmentOptional = Optional.of( jsonObjectWithAttachment );
        jsonObjectWithAttachmentsOptional = Optional.of( jsonObjectWithAttachments );
        jsonObjectWithAttachmentButWithoutAttachmentContentOptional = Optional.of( jsonObjectWithAttachmentButWithoutAttachmentContent );
        jsonObjectWithAttachmentButWithoutAttachmentNameOptional = Optional.of( jsonObjectWithAttachmentButWithoutAttachmentName );
        jsonObjectWithoutAttachmentOptional = Optional.of( jsonObjectWithoutAttachment );
        jsonObjectWithoutIssueTypeOptional = Optional.of( jsonObjectWithoutIssueType );
        jsonObjectWithoutSummaryOptional = Optional.of( jsonObjectWithoutSummary );
        jsonObjectWithoutProjectOptional = Optional.of( jsonObjectWithoutProject );

    }

    @Test
    public void testCollectionWithAttachmentWithExceptionShouldCallFileDelete() throws Exception {
        issueWithException = mock(Issue.class);
        jiraFeedbackPostRemException = Mockito.spy(new JiraFeedbackPostRem(jiraClient));
        fileException = mock(File.class);

        doThrow(new JiraException(ATTACHMENT_NAME)).when(issueWithException).addAttachment(any());
        doReturn(issueWithException).when(jiraFeedbackPostRemException).createIssue(any(JSONObject.class),any(String.class),any(String.class));
        doReturn(file).when(jiraFeedbackPostRem).getFileFromContent(any(),any());
        doReturn(fileException).when(jiraFeedbackPostRemException).getFileFromContent(any(),any());

        jSONObject = JSONObject.fromObject(JSON_AS_STRING_WITH_SUMMARY.toString());
        Response feedbackIssue = jiraFeedbackPostRemException.collection(null, null, null, jsonObjectWithAttachmentOptional);

        assertEquals(feedbackIssue.getStatus(), 400);
        verify(fileException).delete();
        verify(jiraFeedbackPostRemException).createIssue(eq(jSONObject),eq(PROJECT),eq(ISSUE_TYPE));
        verify(issueWithException).addAttachment(fileException);
    }

    @Test
    public void testCollectionWithAttachment() throws Exception {
        jSONObject = JSONObject.fromObject(JSON_AS_STRING_WITH_SUMMARY.toString());
        Response feedbackIssue = jiraFeedbackPostRem.collection(null, null, null, jsonObjectWithAttachmentOptional);
        assertEquals(feedbackIssue.getStatus(), 200);
        verify(jiraFeedbackPostRem).createIssue(eq(jSONObject),eq(PROJECT),eq(ISSUE_TYPE));
        verify(issue).addAttachment(file);
    }

    @Test
    public void testCollectionWithAttachments() throws Exception {
        jSONObject = JSONObject.fromObject(JSON_AS_STRING_WITH_SUMMARY.toString());
        Response feedbackIssue = jiraFeedbackPostRem.collection(null, null, null, jsonObjectWithAttachmentsOptional);
        assertEquals(feedbackIssue.getStatus(), 200);
        verify(jiraFeedbackPostRem).createIssue(eq(jSONObject),eq(PROJECT),eq(ISSUE_TYPE));
        verify(issue, times(2)).addAttachment(file);
    }

    @Test
    public void testCollectionWithoutAttachment() throws Exception {
        jSONObject = JSONObject.fromObject(JSON_AS_STRING_WITH_SUMMARY.toString());
        Response feedbackIssue = jiraFeedbackPostRem.collection(null, null, null, jsonObjectWithoutAttachmentOptional);
        assertEquals(feedbackIssue.getStatus(), 200);
        verify(jiraFeedbackPostRem).createIssue(eq(jSONObject),eq(PROJECT),eq(ISSUE_TYPE));
        verify(issue, never()).addAttachment(file);
    }

    @Test
    public void testCollectionWithoutProject() throws Exception {
        jSONObject = JSONObject.fromObject(JSON_AS_STRING_WITH_SUMMARY.toString());
        Response feedbackIssue = jiraFeedbackPostRem.collection(null, null, null, jsonObjectWithoutProjectOptional);
        assertEquals(feedbackIssue.getStatus(), 400);
        verify(jiraFeedbackPostRem, never()).createIssue(any(JSONObject.class),any(String.class),any(String.class));
        verify(issue, never()).addAttachment(file);
    }

    @Test
    public void testCollectionWithoutIssueType() throws Exception {
        jSONObject = JSONObject.fromObject(JSON_AS_STRING_WITH_SUMMARY.toString());
        Response feedbackIssue = jiraFeedbackPostRem.collection(null, null, null, jsonObjectWithoutIssueTypeOptional);
        assertEquals(feedbackIssue.getStatus(), 400);
        verify(jiraFeedbackPostRem, never()).createIssue(any(JSONObject.class),any(String.class),any(String.class));
        verify(issue, never()).addAttachment(file);
    }

    @Test
    public void testCollectionWithoutSummary() throws Exception {
        jSONObject = JSONObject.fromObject(JSON_AS_STRING_WITH_SUMMARY.toString());
        Response feedbackIssue = jiraFeedbackPostRem.collection(null, null, null, jsonObjectWithoutSummaryOptional);
        assertEquals(feedbackIssue.getStatus(), 400);
        verify(jiraFeedbackPostRem, never()).createIssue(any(JSONObject.class),any(String.class),any(String.class));
        verify(issue, never()).addAttachment(file);
    }

    @Test
    public void testCollectionWithAttachmentButWithoutAttachmentContent() throws Exception {
        jSONObject = JSONObject.fromObject(JSON_AS_STRING_WITH_SUMMARY.toString());
        Response feedbackIssue = jiraFeedbackPostRem.collection(null, null, null, jsonObjectWithAttachmentButWithoutAttachmentContentOptional);
        assertEquals(feedbackIssue.getStatus(), 200);
        verify(jiraFeedbackPostRem).createIssue(eq(jSONObject),eq(PROJECT),eq(ISSUE_TYPE));
        verify(issue, never()).addAttachment(file);
    }

    @Test
    public void testCollectionWithAttachmentButWithoutAttachmentName() throws Exception {
        jSONObject = JSONObject.fromObject(JSON_AS_STRING_WITH_SUMMARY.toString());
        Response feedbackIssue = jiraFeedbackPostRem.collection(null, null, null, jsonObjectWithAttachmentButWithoutAttachmentNameOptional);
        assertEquals(feedbackIssue.getStatus(), 200);
        verify(jiraFeedbackPostRem).createIssue(eq(jSONObject),eq(PROJECT),eq(ISSUE_TYPE));
        verify(issue, never()).addAttachment(file);
    }

    @Test
    public void getFileFromContent() throws Exception {
        File file = jiraFeedbackPostRemFile.getFileFromContent(content,FILE_NAME);
        assertEquals(file.getName(), FILE_NAME);
    }

    @Test
    public void getFileFromContentErrorName() throws Exception {
        File file = jiraFeedbackPostRemFile.getFileFromContent(content,null);
        assertEquals(file.getName(), "null");
    }

    @Test
    public void getFileFromContentErrorContent() throws Exception {
        File file = jiraFeedbackPostRemFile.getFileFromContent(null,FILE_NAME);
        assertEquals(file.length(), 0);
    }
}