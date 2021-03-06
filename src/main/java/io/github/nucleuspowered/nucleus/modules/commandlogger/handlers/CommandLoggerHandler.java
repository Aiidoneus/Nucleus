/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandlogger.handlers;

import io.github.nucleuspowered.nucleus.logging.AbstractLoggingHandler;
import io.github.nucleuspowered.nucleus.modules.commandlogger.config.CommandLoggerConfigAdapter;

public class CommandLoggerHandler extends AbstractLoggingHandler {

    private final CommandLoggerConfigAdapter clca;

    public CommandLoggerHandler(CommandLoggerConfigAdapter clca) {
        super("command", "cmds");
        this.clca = clca;
    }

    public void onReload() throws Exception {
        if (clca.getNodeOrDefault().isLogToFile() && logger == null) {
            this.createLogger();
        } else if (!clca.getNodeOrDefault().isLogToFile() && logger != null) {
            onShutdown();
        }
    }

    @Override protected boolean enabledLog() {
        return clca.getNodeOrDefault().isLogToFile();
    }
}
