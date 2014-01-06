Fishing in World of Warcraft is easy.
You press a button to cast out the fishing rod, wait for the bob to move, click it, and repeat until you get bored
The problem is that last part. It's so easy that it can hardly be considered fun, but the spoils can sell for gold, so I made a bot for it.

Botting is not strickly allowed in WoW.. so there was no way to interact directly with the program.
To get around this I used the java robot library, which provided me with a way to simulate mouse and key presses and access to individual pixel colors on the screen
Using only the get pixel color at location (x,y) function I had to find the location of the fishing bob on the screen (which varies randomly every time you cast the fishing rod)
The bob was reddish, with water being bluish, but this varies greatly from map to map. The program had to scan the area looking for a small red spot, then keep scanning that spot, waiting for it to move. When it saw movement, it simulated a mouse click on that spot, then waited a second or so to recast.
The GUI includes a way to change certain properties like the color of the bob, general location, etc.
