# Logic-Based-Maze-Navigation
In this project, we consider the Wumpus World presented in *Artificial Intelligence A Modern Approach* (Russel and Norvig). Our goal is to implement an intelligent agent who is able to safely explore the world and find a bag of gold without dying. The main purpose of this project is to produce an extendible First-Order Logic (FOL) library. The secondary purpose is to apply this library to the Wumpus World and potentially other "worlds" (e.g. logic could be used to prove theorems in a mathematical world).

#First-Order Logic
A logic-based AI will typically has a database of known facts, called a knowledgebase. When encountered with a new situation, the logic-based AI will make hypotheses and check if it is consistent with the knowledgebase. If the knowledgebase implies a certain hypotheses, then the agent can be sure that hypotheses is true and add it to the knowledgebase.

##Flexibility
We need an flexible method of representing functions, relations, and objects so that any arbitrary function, relation or object can be represented. To achieve this, we combine Java with a package-specific language. Our logic processor scans through files containing the declarations of functions, relations and objects. The interpreter then uses the Java code for definitions of those functions, relations and objects and uses their definitions as specificied in manipulating FOL statements.

##Statements
One of the challenges of logic-based AIs is representing the statements in the knowledgebase. There are various forms of logic that can be used, such as propositional logic, first-order logic, temporal logic, and other higher orders of logic. This project is an experiment with trying to work with first-order logic (FOL). 

There are a few main steps in processing FOL statements:

1. Tokenize statements into functions, relations, and objects
2. Convert statements into conjunctive normal form
3. Implement a resolution algorithm which attempts to prove new hypotheses by contradiction.

###Tokenizing

###Conversion to Conjunctive Normal Form (CNF)

###Resolution

#Wumpus World
The Wumpus World is a very simple n-by-m grid. There are multiple pits, one wumpus, and one bag of gold distributed over the squares. 

##Pits
The agent must avoid falling into pits. To help the agent achieve this, the agent will feel a "Breeze" whenever it is on a square adjacent to a Pit. If there are 2 adjacent Pits, the agent will feel the "Breeze" twice, and so on. This makes is somewhat like minesweeper and more solveable.

##Wumpus
The agent must avoid the Wumpus. To help the agent achieve this, the agent will smell a "Stench" whenever it is on a square adjacent to the Wumpus. The agent also has n arrows that it can shoot. When an arrow is shot, it moves in a straight line and if it reaches the square in which the Wumpus resides, the Wumpus will be killed. If the Wumpus is illed, the agent will hear a "Scream."

##Gold
The agent must find the gold. When the agent reaches the square with the gold, the agent will see a "Glitter." The agent may grab the gold.

##Playing the Game
The world is static, but unknown. The agent starts in square (0, 0). Square (0, 0) is guaranteed to be empty and have no "Stench" or "Breeze" or "Glitter" percepts. The agent knows nothing else and must explore the world. Given percept sequences, the agent must deduce where the Pits and Wumpus are to avoid them. To win, the agent must find the gold and return to square (0, 0).

##Examples of Challenges
Here are two 5x5 maps that demonstrate two challenges of this problem. Recall that we as observers can see the entire map, but the agent actually starts with no knowledge except for the fact that its starting square (0, 0) and the neighboring squares (1, 0) and (0, 1) are empty. Everything else beyond that is unknown.

###Killing the Wumpus

![Maze 1] 
(https://raw.githubusercontent.com/mjchao/Logic-Based-Maze-Navigation/master/maze_explanation_1.png)

In this map, the Wumpus is on the gold, so the agent actually has to kill the Wumpus before it can grab the gold. Let the lower left square be the origin (0, 0) with x-coordinate increasing as we go further right and y-coordinate increasing as we go further up. 

Suppose the agent wandered onto square (2, 1). It perceives a stench, but it does not know if the Wumpus is north, south, east, or west, so it cannot shoot the Wumpus yet - it needs more information. In particular, once it wanders onto square (3,0), it will perceive another stench. Only b piecing together the fact that there was a stench on square (2, 1) and a stench on square (3, 0) can the agent can infer that the Wumpus must be in square (3, 1). Then, it can shoot the Wumpus.

###Locating the Pits
![Maze 2]
(https://raw.githubusercontent.com/mjchao/Logic-Based-Maze-Navigation/master/maze_explanation_2.png)

In this map, there is a straight path to the gold, and suppose the agent is actually lucky enough to guess that path. It will encounter a problem though when it reaches (2, 0) because there is a breeze. It would conclude that there is a pit either at (2, 1) or (3, 0), but it cannot proceed towards the gold in case there is a pit at (3, 0). Only when it later explores (1, 1) can it infer that the pit is at (2, 1) and then proceed to (3, 0).
