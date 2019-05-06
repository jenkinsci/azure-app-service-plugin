package com.microsoft.jenkins.appservice.commands;

import com.microsoft.azure.management.appservice.DeploymentSlot;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.jenkins.appservice.Messages;
import com.microsoft.jenkins.azurecommons.command.CommandState;
import com.microsoft.jenkins.azurecommons.command.IBaseCommandData;
import com.microsoft.jenkins.azurecommons.command.ICommand;

import java.util.NoSuchElementException;

public class SwapSlotsCommand implements ICommand<SwapSlotsCommand.ISwapSlotsCommandData> {

    @Override
    public void execute(ISwapSlotsCommandData context) {
        String sourceSlotName = context.getSourceSlotName();
        String targetSlotName = context.getTargetSlotName();
        WebApp webApp = context.getWebApp();
        DeploymentSlot sourceSlot;
        try {
            sourceSlot = webApp.deploymentSlots().getByName(sourceSlotName);
            webApp.deploymentSlots().getByName(targetSlotName);
        } catch (NoSuchElementException e) {
            context.logError(Messages.Slot_not_exist(sourceSlotName, targetSlotName, webApp.name()));
            context.setCommandState(CommandState.HasError);
            return;
        }
        sourceSlot.swap(targetSlotName);

        context.setCommandState(CommandState.Success);
    }

    public interface ISwapSlotsCommandData extends IBaseCommandData {
        String getSourceSlotName();

        String getTargetSlotName();

        WebApp getWebApp();
    }
}
