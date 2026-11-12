package eu.pmkr.pcv;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final class RunnerCommand implements RawCommand {

    private final ScriptParameters scriptParams;
    private final ProxyServer server;
    private final ProcessCommandsVelocity plugin;
    private final Logger logger;

    RunnerCommand(ScriptParameters scriptParams, ProxyServer server, ProcessCommandsVelocity plugin, Logger logger) {
        this.scriptParams = scriptParams;
        this.server = server;
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public void execute(final Invocation invocation) {
        if (!scriptParams.allowArgs() && !invocation.arguments().isBlank()) {
            invocation.source().sendMessage(Component.empty()
                    .append(Component.translatable("command.unknown.argument", NamedTextColor.RED))
                    .appendNewline()
                    .append(Component.text('/'+invocation.alias()+' ', NamedTextColor.GRAY))
                    .append(Component.text(invocation.arguments(), NamedTextColor.RED, TextDecoration.UNDERLINED))
                    .append(Component.translatable("command.context.here", NamedTextColor.RED, TextDecoration.ITALIC))
            );
            return;
        }

        String senderName;
        if (invocation.source() instanceof Player player) {
            senderName = player.getUsername();
        } else if (invocation.source() instanceof ConsoleCommandSource) {
            senderName = "CONSOLE";
        } else {
            senderName = invocation.source().toString();
        }

        String startLog = "[pcv] "+senderName+" ran the "+scriptParams.name()+" script";
        if (!invocation.arguments().isBlank()) startLog += " with args "+invocation.arguments();
        server.getConsoleCommandSource().sendMessage(Component.text(startLog, NamedTextColor.BLUE));
        sendMessageAndBroadcast(scriptParams.startMessage(), scriptParams.startBroadcast(), invocation, senderName);

        server.getScheduler().buildTask(plugin, () -> {
            try {
                ArrayList<String> pathList = new ArrayList<>(List.of(scriptParams.path()));
                if (!invocation.arguments().isBlank()) pathList.add(invocation.arguments());
                ProcessBuilder pb = new ProcessBuilder(pathList);
                pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
                Process process = pb.start();

                StringBuilder errorOutput = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = reader.readLine()) != null) {
                        errorOutput.append(errorLine).append('\n');
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    String finishLog = "[pcv] Script " + scriptParams.name() + " finished, invoked by " + senderName;
                    if (!invocation.arguments().isBlank()) finishLog += " with args " + invocation.arguments();
                    server.getConsoleCommandSource().sendMessage(Component.text(finishLog, NamedTextColor.BLUE));
                    sendMessageAndBroadcast(scriptParams.finishMessage(), scriptParams.finishBroadcast(), invocation, senderName);
                } else {
                    StringBuilder errorLog = new StringBuilder();
                    errorLog.append(scriptParams.name()).append(" exited with an error, invoked by ").append(senderName);
                    if (!invocation.arguments().isBlank()) errorLog.append(" with args ").append(invocation.arguments());
                    errorLog.append('\n').append(errorOutput);
                    logger.log(Level.SEVERE, errorLog.toString());
                    sendMessageAndBroadcast(scriptParams.errorMessage(), scriptParams.errorBroadcast(), invocation, senderName);
                }
            } catch (IOException | InterruptedException ex) {
                String errorLog = "Failed to launch script "+scriptParams.name()+", invoked by "+senderName;
                if (!invocation.arguments().isBlank()) errorLog += " with args "+invocation.arguments();
                logger.log(Level.SEVERE, errorLog, ex);
                sendMessageAndBroadcast(scriptParams.errorMessage(), scriptParams.errorBroadcast(), invocation, senderName);
            }
        }).schedule();
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission(scriptParams.permission());
    }

    private void sendMessageAndBroadcast(String message, String broadcast, Invocation invocation, String name) {
        invocation.source().sendRichMessage(message
                .replaceAll("%s", name)
        );
        boolean sourceIsAPlayer = invocation.source() instanceof Player;
        server.getAllPlayers().stream()
                .filter(player -> {
                    if (!player.hasPermission(scriptParams.broadcastPermission())) return false;
                    return !sourceIsAPlayer
                            || ((Player) invocation.source()).getUniqueId() != player.getUniqueId();
                })
                .forEach(player -> player.sendRichMessage(broadcast
                        .replaceAll("%s", name)
                ));
    }
}
