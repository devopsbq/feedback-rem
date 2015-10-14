package io.corbel.resources.rem.feedback.ioc;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.JiraClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import io.corbel.lib.config.ConfigurationIoC;
import io.corbel.resources.rem.Rem;
import io.corbel.resources.rem.feedback.JiraFeedbackPostRem;

/**
 * @author Alberto J. Rubio
 */
@Configuration
@Import({ConfigurationIoC.class})
public class FeedbackRemIoc {

    @Autowired
    private Environment env;

    @Bean(name = "JiraFeedbackRem")
    public Rem jiraFeedbackRem(JiraClient jiraClient) {
        return new JiraFeedbackPostRem(jiraClient);
    }

    @Bean
    public JiraClient jiraClient(BasicCredentials jiraCredentials) {
        return new JiraClient(env.getProperty("rem.feedback.jira.url"), jiraCredentials);
    }

    @Bean
    public BasicCredentials jiraCredentials() {
        return new BasicCredentials(env.getProperty("rem.feedback.jira.user"), env.getProperty("rem.feedback.jira.password"));
    }
}
