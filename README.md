# Cats vs Rats

A tower defence game written in Java with Swing. Rats crawl out of the sewer and race along a winding path towards your pantry. Place cats beside the path to stop them: each cat throws balls of yarn at the nearest rat in range. Let too many rats reach the pantry and they eat all your cheese.

Built as a team project for 2IP90 Programming (Challenge-Based Learning) at Eindhoven University of Technology (TU/e), October 2026.

## How to play

- Click **Start** on the main menu, then **Next Round** to send in the rats.
- Click any grass tile to place a cat. A cat costs 10 fish.
- Every rat you take down earns 2 fish. Spend them on more cats between rounds.
- Every rat that reaches the pantry steals 5 cheese. You start with 20.
- Survive all 5 rounds to win. Each round sends 5 more rats than the last.

## Features

- **BFS pathfinding.** The rats' route through the map is found with breadth-first search from the sewer grate to the pantry.
- **Smooth movement.** Rats glide between tile centres at a constant speed rather than jumping tile to tile.
- **Targeting.** Each cat finds the nearest rat within its range and fires on a cooldown. The yarn balls home in on their target.
- **Game loop.** A fixed 20 ms timer handles spawning, movement, shooting, damage, the in-game economy and redrawing.
- **Hand-drawn graphics.** Every cat, rat, yarn ball and map tile is drawn with Java 2D. No image files are used.
- **HUD.** Live counters for cheese, fish and rounds, plus on-screen messages such as "Not enough fish!".

## Running it

You need Java 8 or newer.

```bash
cd src
javac *.java
java MainMenu
```

Or open the project in IntelliJ IDEA or VS Code and run `MainMenu.java`.

## Project structure

| File | What it does |
| --- | --- |
| `MainMenu.java` | Start screen with Start and Quit buttons. This is the entry point. |
| `MapScreen.java` | Game window: the map plus the HUD side panel. |
| `GameMap.java` | Map layout, BFS route, game loop, cat placement and rendering. |
| `Cat.java` | Tower: targets the nearest rat in range and throws yarn. |
| `Rat.java` | Enemy: follows the route, has health and steals cheese. |
| `YarnBall.java` | Projectile that homes in on its target rat. |

## Authors

Mate Vadasdi and teammate. Developed for TU/e 2IP90.
