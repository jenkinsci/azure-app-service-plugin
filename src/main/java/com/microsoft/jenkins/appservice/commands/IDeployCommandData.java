/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.jenkins.appservice.commands;

import com.microsoft.azure.management.appservice.WebAppBase;

/**
 * Common data for all deployment.
 */
public interface IDeployCommandData extends IBaseCommandData {
    WebAppBase getWebApp();

    String getSlotName();
}
