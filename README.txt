Fishing in World of Warcraft is easy.
You click a button to cast out the fishing rod, waiting for the bob to move, click it, and repeat until you get bored
The problem is that last part. It's so easy that it can hardly be considered fun, but the spoils can sell for gold, so I made a bot for it.

Botting is not strickly allowed in WoW.. so there was no way to interact directly with the program.
To get around this I used the java robot library, which provided me with a way to simulate keypress and access to individual pixel colors on the screen
Using only the get pixel color at location (x,y) function I had to find the location of the fishing bob on the screen (which varies randomly every time you cast the fishing rod)
The GUI includes a way to change certain properties like the color of the bob, general location, etc.
