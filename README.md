# Process Commands Velocity

Run child processes with custom Velocity commands on your Minecraft network. A very lightweight and configurable plugin.

<!-- [![Available on Modrinth](/assets/modrinth_vector.svg)](https://modrinth.com/project/KeBIhOih) -->
[![Available on Hangar](/assets/hangar_vector.svg)](https://hangar.papermc.io/pm_k_r/process-commands-velocity)
[![Available on GitHub](/assets/github_vector.svg)](https://github.com/pm-k-r/process-commands-velocity)

## Configuration

This plugin is configured in its `scripts.json` file. It should include a list of objects whose properties are described below. An example valid configuration is automatically generated on plugin load if the file doesn't exit.

| Property            | Type     | Description                                                                                                            |
|---------------------|----------|------------------------------------------------------------------------------------------------------------------------|
| name                | string   | The main command used to run the script                                                                                |
| aliases             | string[] | Additional commands that can be used to run the script                                                                 |
| path                | string[] | The path to the executable in the filesystem, along with any pre-set arguments                                         |
| allowArgs           | boolean  | Whether or not to allow players to pass additional arguments                                                           |
| permission          | string   | The permission node required to run the command                                                                        |
| sendBroadcasts      | boolean  | Whether or not to send the broadcast messages defined below to players who have the broadcast permission defined below |
| broadcastPermission | string   | The permission node required to receive broadcasts (if sendBroadcasts is true)                                         |
| startMessage        | string   | The message sent to the command sender right after the command is ran                                                  |
| startBroadcast      | string   | The message sent to players with the broadcast permission right after the command is ran (if sendBroadcasts is true)   |
| finishMessage       | string   | The message sent to the command sender once script execution finishes                                                  |
| finishBroadcast     | string   | The message sent to players with the broadcast permission once script execution finishes (if sendBroadcasts is true)   |
| errorMessage        | string   | The message sent to the command sender if script execution fails                                                       |
| errorBroadcast      | string   | The message sent to players with the broadcast permission if script execution fails (if sendBroadcasts is true)        |

## License

This plugin is licensed under the [MIT license](/LICENSE).<br>
Author: [pm_k_r](https://pmkr.eu)