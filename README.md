Dungeons of Doom (DoD) is a turn based game where you explore the dungeon, pick-up gold and try to escape. However, there is a bot that will try to catch you or escape with the gold, so watch out!
To run DoD:
1.	Install Java v21
2.	Download the zip folder and extract the files
3.	Compile and run the program from the Main script
4.	When prompted, select a map and difficulty mode
(note: to add additional maps, ensure they follow the same format, then add them to the Maps folder)
DoD uses object-oriented programming (OOP) through encapsulation, abstraction and modularity. Classes such as Player and Bot, demonstrate encapsulation by managing their internal state through private fields and controlled methods. Abstraction is used to simplify complexity by delegating tasks like gameplay coordination to the Game class and map management to the Map class. Each class follows the Single Responsibility Principle which creates clarity and makes debugging easier. Whilst inheritance is not widely used, the structure of the program would support future extensions in this area. By adhering to OOP principles, components in the program are kept distinct and reusable which makes it easy to maintain and add new features.
