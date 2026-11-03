package eu.pmkr;

public record ScriptParameters(
    String name,
    String[] aliases,
    String path,
    String permission,
    boolean allowArgs,
    boolean sendBroadcasts,
    String broadcastPermission,
    String startMessage,
    String startBroadcast,
    String finishMessage,
    String finishBroadcast,
    String errorMessage,
    String errorBroadcast
) {}
