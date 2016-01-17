# Logic-Based-Maze-Navigation
In this project, we consider the Wumpus World presented in *Artificial Intelligence A Modern Approach* (Russel and Norvig). Our goal is to implement an intelligent agent who is able to safely explore the world and find a bag of gold without dying.

# The Players
The Wumpus World is a very simple n-by-m grid. There are multiple pits, one wumpus, and one bag of gold distributed over the squares. 

##Pits
The agent must avoid falling into pits. To help the agent achieve this, the agent will feel a "Breeze" whenever it is on a square adjacent to a Pit.

##Wumpus
The agent must avoid the Wumpus. To help the agent achieve this, the agent will smell a "Stench" whenever it is on a square adjacent to the Wumpus. The agent also has n arrows that it can shoot. When an arrow is shot, it moves in a straight line and if it reaches the square in which the Wumpus resides, the Wumpus will be killed. If the Wumpus is illed, the agent will hear a "Scream."

##Gold
The agent must find the gold. When the agent reaches the square with the gold, the agent will see a "Glitter." The agent may grab the gold.

#Playing the Game
The world is static, but unknown. The agent starts in square (0, 0). Square (0, 0) is guaranteed to be empty and have no "Stench" or "Breeze" or "Glitter" percepts. The agent knows nothing else and must explore the world. Given percept sequences, the agent must deduce where the Pits and Wumpus are to avoid them. To win, the agent must find the gold and return to square (0, 0).
