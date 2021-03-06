package org.thoughtslive.jenkins.plugins.jira.steps;

import javax.inject.Inject;

import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.thoughtslive.jenkins.plugins.jira.api.ResponseData;
import org.thoughtslive.jenkins.plugins.jira.api.input.TransitionInput;
import org.thoughtslive.jenkins.plugins.jira.util.JiraStepDescriptorImpl;
import org.thoughtslive.jenkins.plugins.jira.util.JiraStepExecution;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import lombok.Getter;

/**
 * Step to transition JIRA issue.
 *
 * @author Naresh Rayapati
 */
public class TransitionIssueStep extends BasicJiraStep {

	private static final long serialVersionUID = 5648167982018270684L;

	@Getter
	private final String idOrKey;

	@Getter
	private final TransitionInput input;

	@DataBoundConstructor
	public TransitionIssueStep(final String idOrKey, final TransitionInput input) {
		this.idOrKey = idOrKey;
		this.input = input;
	}

	@Extension
	public static class DescriptorImpl extends JiraStepDescriptorImpl {

		public DescriptorImpl() {
			super(Execution.class);
		}

		@Override
		public String getFunctionName() {
			return "jiraTransitionIssue";
		}

		@Override
		public String getDisplayName() {
			return getPrefix() + "Transition Issue";
		}

	}

	public static class Execution extends JiraStepExecution<ResponseData<Void>> {

		private static final long serialVersionUID = 6038231959460139190L;

		@StepContextParameter
		transient Run<?, ?> run;

		@StepContextParameter
		transient TaskListener listener;

		@StepContextParameter
		transient EnvVars envVars;

		@Inject
		transient TransitionIssueStep step;

		@Override
		protected ResponseData<Void> run() throws Exception {

			ResponseData<Void> response = verifyInput();

			if (response == null) {
				logger.println("JIRA: Site - " + siteName + " - Transition issue with idOrKey: " + step.getIdOrKey());
				response = jiraService.transitionIssue(step.getIdOrKey(), step.getInput());
			}

			return logResponse(response);
		}

		@Override
		protected <T> ResponseData<T> verifyInput() throws Exception {
			// TODO Add validation - Or change the input type here ?
			return verifyCommon(step, listener, envVars, run);
		}
	}
}
