package eu.pmkr;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.logging.Logger;

@Plugin(id = "process-commands-velocity", name = "Process Commands", version = "0.0.1",
        url = "https://github.com/pm-k-r/process-commands-velocity", authors = {"pm_k_r"},
        description = "Run child processes with custom Velocity commands")
public final class ProcessCommandsVelocity {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public ProcessCommandsVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        CommandManager commandManager = server.getCommandManager();

        // todo These are only hardcoded temporarily
        final ScriptParameters params = new ScriptParameters(
                "cloneworld",
                new String[]{"worldclone"},
                "/srv/mc/common/clone-ppaper-world-to-pfabric.sh",
                "script.cloneworld",
                false,
                true,
                "script.cloneworld.receive",
                "<yellow>Cloning the world to the testing server...</yellow>",
                "<gray><i>[%s: <yellow>Cloning the world to the testing server...</yellow>]</i></gray>",
                "<yellow>World cloned. You may now switch servers.</yellow>",
                "<gray><i>[%s: <yellow>World cloned. You may now switch servers.</yellow>]</i></gray>",
                "<red>Error while trying to clone the world. Details in console.</red>",
                "<gray><i>[%s: <red>Error while trying to clone the world. Details in console.</red>]</i></gray>"
        );

        CommandMeta commandMeta = commandManager.metaBuilder(params.name())
                .aliases(params.aliases())
                .plugin(this).build();
        commandManager.register(commandMeta, new RunnerCommand(params, server, logger));
    }

}