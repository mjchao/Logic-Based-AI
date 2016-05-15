# Logic-Based-Maze-Navigation
 The main purpose of this project is to produce an extendible First-Order Logic (FOL) manipulator. The secondary purpose is to explore logic applied to the Wumpus World presented in *Artificial Intelligence A Modern Approach* (Russel and Norvig). Our goal is to implement an intelligent agent who is able to safely explore the world and find a bag of gold without dying. It would also be nice to apply this code to the Wumpus World and potentially other "worlds" (e.g. logic could be used to prove theorems in a mathematical world).

#First-Order Logic
A logic-based AI will typically has a database of known facts, called a knowledgebase. When encountered with a new situation, the logic-based AI will make hypotheses and check if it is consistent with the knowledgebase. If the knowledgebase implies a certain hypotheses, then the agent can be sure that hypotheses is true and add it to the knowledgebase.

##Flexibility
We need an flexible method of representing functions, relations, and objects so that any arbitrary function, relation or object can be represented. To achieve this, we combine Java with an additional simple language specific to this project. The logic processor scans through files containing the declarations of functions, relations and objects. The interpreter then uses the Java code for definitions of those functions, relations and objects and uses their definitions as specificied in manipulating FOL statements.

##Statements
The first challenge of this logic-based AIs is representing the statements in the knowledgebase. There are various forms of logic that can be used, such as propositional logic, first-order logic, temporal logic, and other higher orders of logic. This project is an experiment with trying to work with first-order logic (FOL). 

There are a few main steps in processing FOL statements:

1. Tokenize statements into functions, relations, and objects
2. Convert statements into conjunctive normal form
3. Apply a resolution algorithm which attempts to prove new hypotheses by contradiction.

###Tokenizing

This can be performed by scanning forward and attempting match keywords and known variables, objects, functions, and relations to segments of the input. If nothing matches, then we automatically treat the text segment a new variable and add it to a known list of variables.

###Conversion to Conjunctive Normal Form (CNF)

First, we convert our tokenized infix expression into postfix so that we don't have to deal with parentheses anymore. Next, we construct a tree to represent our postfix expression. All non-leaf nodes are operators, functions, relations, or quantifiers and all leaf nodes are variables and objects. This structure enables us to more-easily apply the algorithm presented in Ruseel and Norvig for converting to CNF.

This algorithm in Russel and Norvig follows these steps:

1. Eliminate arrows (implications and biconditionals)
2. Distribute Nots inward (specifically, converting to negation-normal form)
3. Standardize variables names: if two variables have the same name in different quantified expressions, we differentiate between them
4. Skolemize existential quantifiers: replaces all existential quantifiers with a skolem function parameterized by all universal variables that are in scope at the time of the existential quantifier
5. Drop universal quantifiers
6. Distribute ORs inward over ANDs which gets us to CNF

###Resolution

The resolution algorithm presented in Russel and Norvig follows these steps:

1. Express the knowledgebase ANDed with the negated hypothesis as a single statement in CNF
2. Attempting to prove a contradiction in the statement constructed in step 1 as follows. (Note: a clause means a single list of disjunctions in the CNF statement)
 1. Factor the statement by removing any redundant terms from clauses. A term is redundant if it unifies with another term in the same clause.
 2. For every pair of clauses see if they can be resolved. Two clauses can be resolved if they a term in the first clause unifies with the negation of a term in the second clause. The resolvent is the first clause ORed with the second clause minus the two terms that unified. Add the resolvent to a list of things proved. If the resolvent of any two clauses yields an empty clause, then we return true. We only get the empty clause when resolved something of the form P AND !P, which is a contradiction, and the proof by contradiction succeeds.
 3. After checking all pairs of clauses to see if they unify, see if the list of things proved contains any new knowledge - clauses that we didn't know before and don't unify with any clauses from before.
 4. If we found new knowledge, factor those clauses and then add them to the giant single conjunction formed in step 1. If we could not find new knowledge, then return false because our proof by contradiction fails - we did not arrive at a contradiction.
 5. Go back to step 2ii and repeat.
 
Note that resolution can be tricky because there are certain things you do not want to unify. For example, one issue that test cases exposed was that we cannot unify any variables that appear in the hypothesis because that would prove a less general result. It would be disastrous if the knowledgebase contained "Person(John)" and then we asked "FORALL(x) Person(x)" and the algorithm unified x with John. However, in the opposite case we would need to unify x with John if instead the knowledgebase contained "FORALL(x) Person(x)" and we asked "Person(John)". There were several other subtleties that arose.

##Complexity
Propositional resolution takes at least exponential time in the worst case. For example, there is a [proof](http://cs.stackexchange.com/questions/2230/resolution-complexity-versus-a-constrained-sat-algorithm) that it must take at exponential time to prove the pigeonhole principle. Therefore, the more general first-order resolution must take at least exponential time as well. To help speed things up somewhat, there are a few optimizations we made. For example, we enforced linear resolution where we only resolve two clauses if at least one clause belongs to the original knowledgebase or one clause is a parent of the other one in the resolution search tree. According to Russel and Norvig, this heuristic maintains completeness. In addition, to filter out as many redundant clauses as possible, we disregard the order of the terms when we check for equality. Furthermore, we flag two clauses as duplicates if the *i*th term of the first clause unifies with the *i*th term of the second clause for all *i*. Unfortunately, we cannot the second check for arbitrary ordering as it would take exponential time to check if some ordering of the first clause allows it to unify term-by-term with the second clause.

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
