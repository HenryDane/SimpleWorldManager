# SimpleWorldManager

A simple system for adding and deleting worlds.

## World Creation
`/world create <name> [flat|empty]`

Creates a new world with the given name. If flat is chosen, the world will be a grid of black and white concrete at y=64 with stone from y=[0,63]. If empty is chosen, the world will be entirely empty except for a stone block at (0, 64, 0). Default generator is empty.

## World Deletion
`/world delete <name>`

Queues the deletion of the world with the given name. 

`/world confirm <name>`

Confirms the queued deletion of the world. This is where the actual deletion takes place.

`/world cancel`

Cancels any pending deletion.

## World Listing

`/world list`

Prints all loaded worlds.

## Teleportation

`/world tp <player> <world>`

Teleports a player to a world's spawn location.

`/world tp <player> <world> <x> <y> <z>`

Teleports a player to the coordinates in a particular world.

`/world tp <world> <x> <y> <z>`

Teleports the sender to that location. Works for players only.

`/world tp <world>`

Teleports the sender to the spawn of that location. Works for players only.

`/wtp ...` is an alias for `/world tp ...`.

