# Unreasonable Odds

## Intro
This is a 2D player-verses-player puzzle / shooter game with gameplay similar to heat signature.

The player assumes direct control of the main character. When the player has enough energy they can time-travel, or multi-verse jump. 

When time-travel occurs the entity travels back in time to a chosen point, and a new universe is created from that point. All characters in this universe other than the new arrival will, without player influence, follow the same path they took in their parent world. The player can interact with these characters to change the new world. Point of arrival is present; so that other players can also jump to this new world.

When universe jumps occur the controlled character jumps to a new universe at the present time at the same location. This is needed so that players can have direct interaction with each-other.

When the currently controlled player is killed the player will time-travel if possible. Else they will die.

## Rule-crafting
Limiting the number of active universes is important; There are several possible ways to limit these:
 - The universes have a time-limit, after a certain time the universe will be destroyed.
 - Generation of jump energy is limited by the number of active universes.
 
With the time-travel on death mechanic the player will become very hard to kill... So possibly other aims will need to be considered

## Building
The project requires a reasonably recent version of Java and Gradle installed. It has been tested on:

| Java        | Gradle |
| ----------- | ------ |
| OpenJDK 17  | 7.2    |

Additionally, the gradle properties `gpr.usr` and `gpr.key` must be set to valid GitHub credentials as some of the libraries used in this project are hosted on GitHubPackages.

There are many ways to acheive this, but one way is to create `~/.gradle/gradle.properties` with the following properties:
```
gpr.usr=username
gpr.key=GitHub access key
```

This key only needs to access public packages, so a new key with minimal permissions should be created for this purpose.

## Running
Run with `gradle run`. This will download all required dependencies and compile then run the project.
