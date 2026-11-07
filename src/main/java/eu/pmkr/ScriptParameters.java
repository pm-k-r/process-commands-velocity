package eu.pmkr;

record ScriptParameters(
    String name,
    String[] aliases,
    String[] path,
    boolean allowArgs,
    String permission,
    boolean sendBroadcasts,
    String broadcastPermission,
    String startMessage,
    String startBroadcast,
    String finishMessage,
    String finishBroadcast,
    String errorMessage,
    String errorBroadcast
) {}
