# Maze Generator - DFS Algorithm

A Java-based maze generator implementing the Depth-First Search (DFS) algorithm with backtracking. Features real-time visualization, performance monitoring, and a modern user interface.

## Features

- **Dynamic Maze Generation**: Create mazes from 5×5 up to 50×50 cells
- **Real-time Visualization**: Watch the DFS algorithm in action with smooth animations
- **Performance Monitoring**: Built-in logging system to track generation times and statistics
- **Responsive UI**: Modern interface with adaptive window sizing
- **Customizable Parameters**: Adjust maze dimensions and animation speed

## Algorithm

The project implements a **Depth-First Search (DFS)** algorithm with backtracking:

1. Start from the top-left corner (0,0)
2. Mark the current cell as visited
3. Randomly select an unvisited neighbor
4. Remove the wall between current cell and chosen neighbor
5. Recursively repeat from the neighbor
6. Backtrack when no unvisited neighbors are available

## Project Structure

```
├── src/
│   ├── MazeGenerator.java    # Main entry point
│   ├── Maze.java             # Core DFS algorithm implementation
│   ├── Cell.java             # Individual cell representation
│   ├── MazePanel.java        # Visualization and rendering
│   └── MazeUI.java           # User interface and controls
└── README.md
```

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/eddo-cs/maze-generator-dfs.git
cd maze-generator-dfs
```

2. Compile the Java files:
```bash
javac *.java
```

3. Run the application:
```bash
java MazeGenerator
```

## Usage

1. **Generate Maze**: Click the "Genera" (Generate) button to start the algorithm
2. **Resize Maze**: Use the spinners to adjust rows and columns, then click "Ridimensiona" (Resize)
3. **Restart**: Click "Riavvia" (Restart) to reset the current maze
4. **Monitor Performance**: View generation times and statistics in the right panel

## Customization

### Animation Speed
Modify the animation delay in `Maze.java` (line ~45):
```java
Thread.sleep(30); // Change value in milliseconds
```

### Cell Size
Adjust cell dimensions in `MazePanel.java` (line ~15):
```java
private static final int CELL_SIZE = 25; // Change size
```

### Size Limits
Modify size constraints in `MazeUI.java`:
```java
new SpinnerNumberModel(value, 5, 50, 1) // min=5, max=50
```

## Screenshots

*Add screenshots of your application here*

## Technical Details

- **Language**: Java
- **GUI Framework**: Swing
- **Algorithm**: Depth-First Search with backtracking
- **Threading**: Separate thread for maze generation to maintain UI responsiveness
- **Graphics**: Custom 2D rendering with anti-aliasing

## Performance

The algorithm has a time complexity of **O(n)** where n is the number of cells, as each cell is visited exactly once. Space complexity is **O(n)** for the recursion stack in the worst case.

## Author

**Edoardo Ambrogi** 
Algorithms and Data Structures Course

## Acknowledgments

- Inspired by classic maze generation algorithms
- Built as part of university coursework in Data Structures and Algorithms
