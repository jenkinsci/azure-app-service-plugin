/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.jenkins.appservice;

import com.microsoft.azure.management.appservice.FunctionApp;
import com.microsoft.azure.management.appservice.PublishingProfile;
import com.microsoft.jenkins.appservice.commands.GitDeployCommand;
import com.microsoft.jenkins.azurecommons.command.BaseCommandContext;
import com.microsoft.jenkins.azurecommons.command.CommandState;
import com.microsoft.jenkins.azurecommons.command.IBaseCommandData;
import com.microsoft.jenkins.azurecommons.command.ICommand;
import com.microsoft.jenkins.azurecommons.command.TransitionInfo;
import com.microsoft.jenkins.exceptions.AzureCloudException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import java.util.HashMap;

public class FunctionAppDeploymentCommandContext extends BaseCommandContext
        implements GitDeployCommand.IGitDeployCommandData {

    private final String filePath;
    private String sourceDirectory;
    private String targetDirectory;
    private PublishingProfile pubProfile;

    public FunctionAppDeploymentCommandContext(final String filePath) {
        this.filePath = filePath;
        this.sourceDirectory = "";
        this.targetDirectory = "";
    }

    public void setSourceDirectory(final String sourceDirectory) {
        this.sourceDirectory = Util.fixNull(sourceDirectory);
    }

    public void setTargetDirectory(final String targetDirectory) {
        this.targetDirectory = Util.fixNull(targetDirectory);
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return null;
    }

    public void configure(
            final Run<?, ?> run,
            final FilePath workspace,
            final Launcher launcher,
            final TaskListener listener,
            final FunctionApp app) throws AzureCloudException {
        pubProfile = app.getPublishingProfile();

        HashMap<Class, TransitionInfo> commands = new HashMap<>();

        Class startCommandClass = GitDeployCommand.class;
        commands.put(GitDeployCommand.class, new TransitionInfo(new GitDeployCommand(), null, null));

        super.configure(run, workspace, launcher, listener, commands, startCommandClass);
        this.setCommandState(CommandState.Running);
    }

    @Override
    public IBaseCommandData getDataForCommand(final ICommand command) {
        return this;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getSourceDirectory() {
        return sourceDirectory;
    }

    @Override
    public String getTargetDirectory() {
        return targetDirectory;
    }

    @Override
    public PublishingProfile getPublishingProfile() {
        return pubProfile;
    }

}
