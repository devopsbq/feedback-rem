package io.corbel.resources.rem.plugin;

import io.corbel.resources.rem.Rem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import io.corbel.lib.config.ConfigurationHelper;
import io.corbel.resources.rem.RemRegistry;
import io.corbel.resources.rem.feedback.ioc.FeedbackRemIoc;

/**
 * @author Alberto J. Rubio
 */
@Component
public class FeedbackRemPlugin extends RemPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackRemPlugin.class);

    private final String FEEDBACK_REM = "feedback-rem";

    @Override
    protected void init() {
        LOG.info("Initializing Feedback plugin.");
        super.init();
        ConfigurationHelper.setConfigurationNamespace(FEEDBACK_REM);
        context = new AnnotationConfigApplicationContext(FeedbackRemIoc.class);
    }

    @Override
    protected void register(RemRegistry registry) {
        registry.registerRem(context.getBean("JiraFeedbackRem", Rem.class), "feedback:Jira", MediaType.APPLICATION_JSON, HttpMethod.POST);
    }

    @Override
    protected String getArtifactName() {
        return FEEDBACK_REM;
    }
}
