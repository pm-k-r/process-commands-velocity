package eu.pmkr.pcv;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(id = "process-commands-velocity", name = "Process Commands", version = "1.0.1",
        url = "https://github.com/pm-k-r/process-commands-velocity", authors = {"pm_k_r"},
        description = "Run child processes with custom Velocity commands")
public final class ProcessCommandsVelocity {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDir;

    private ScriptParameters[] scripts;

    @Inject
    public ProcessCommandsVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDir) {
        this.server = server;
        this.logger = logger;
        this.dataDir = dataDir;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        try { loadScripts(); }
        catch (IOException | JsonIOException | JsonSyntaxException ex) {
            scripts = new ScriptParameters[]{};
            logger.log(Level.SEVERE, "Failed to load configuration", ex);
        }

        CommandManager commandManager = server.getCommandManager();
        Arrays.stream(scripts).forEach((params) -> {
            CommandMeta commandMeta = commandManager.metaBuilder(params.name())
                    .aliases(params.aliases())
                    .plugin(this).build();
            commandManager.register(commandMeta, new RunnerCommand(params, server, this, logger));
        });
    }

    private void loadScripts() throws IOException, JsonIOException, JsonSyntaxException {
        if (!Files.exists(dataDir)) Files.createDirectories(dataDir);
        Path file = dataDir.resolve("scripts.json");
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        if (!Files.exists(file)) {
            scripts = new ScriptParameters[]{new ScriptParameters(
                    "examplescript",
                    new String[]{"scriptexample"},
                    new String[]{"/bin/bash", "-c", "echo 'test' > pcvtest.txt"},
                    false,
                    "script.examplescript",
                    true,
                    "script.examplescript.receive",
                    "<blue>Running the example script...</blue>",
                    "<gray><i>[%s: <blue>Running the example script...</blue>]</i></gray>",
                    "<blue>Example script finished.</blue>",
                    "<gray><i>[%s: <blue>Example script finished.</blue>]</i></gray>",
                    "<red>Error while trying to run the example script. Details in console.</red>",
                    "<gray><i>[%s: <red>Error while trying to run the example script. Details in console.</red>]</i></gray>"
            )};
            try (Writer writer = Files.newBufferedWriter(file)) {
                gson.toJson(scripts, writer);
            }
            return;
        }
        try (Reader reader = Files.newBufferedReader(file)) {
            ArrayList<ScriptParameters> scriptList = gson.fromJson(reader, new TypeToken<List<ScriptParameters>>(){}.getType());
            scripts = scriptList.toArray(ScriptParameters[]::new);
        }
    }

}