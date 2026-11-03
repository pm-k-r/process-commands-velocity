package eu.pmkr;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import javax.naming.Name;
import java.util.logging.Logger;

public final class RunnerCommand implements RawCommand {

    private final ScriptParameters scriptParams;
    private final ProxyServer server;
    private final Logger logger;

    public RunnerCommand(ScriptParameters scriptParams, ProxyServer server, Logger logger) {
        this.scriptParams = scriptParams;
        this.server = server;
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
        if (scriptParams.allowArgs()) startLog += " with args "+invocation.arguments();
        server.getConsoleCommandSource().sendMessage(Component.text(startLog, NamedTextColor.BLUE));
        sendMessageAndBroadcast(scriptParams.startMessage(), scriptParams.startBroadcast(), invocation, senderName);

        // todo Continue from here

    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission(scriptParams.permission());
    }

    private void sendMessageAndBroadcast(String message, String broadcast, Invocation invocation, String name) {
        invocation.source().sendRichMessage(message
                .replaceAll("%s", name)
                .replaceAll("%a", invocation.arguments())
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
                        .replaceAll("%a", invocation.arguments())
                ));
    }
}
