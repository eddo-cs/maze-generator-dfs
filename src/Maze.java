import java.util.*;

public class Maze {
    private final int rows, cols;
    private final Cell[][] grid;

    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c] = new Cell(r, c);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Cell getCell(int r, int c) { return grid[r][c]; }

    public void generate(MazePanel panel) throws InterruptedException {
        boolean[][] visited = new boolean[rows][cols];
        dfs(0, 0, visited, panel); // Partenza dall’angolo in alto a sinistra
    }

    private void dfs(int r, int c, boolean[][] visited, MazePanel panel) throws InterruptedException {
        visited[r][c] = true;
        Cell current = grid[r][c];
        current.setVisited(true);
        panel.repaint();
        Thread.sleep(30); // Velocità animazione

        List<int[]> directions = new ArrayList<>(Arrays.asList(
            new int[]{-1, 0}, // su
            new int[]{1, 0},  // giù
            new int[]{0, -1}, // sinistra
            new int[]{0, 1}   // destra
        ));
        Collections.shuffle(directions); // Rende la DFS casuale per far si' che non venga sempre generato lo stesso labirinto

        for (int[] dir : directions) {
            int nr = r + dir[0], nc = c + dir[1];
            if (isInBounds(nr, nc) && !visited[nr][nc]) {
                removeWall(current, grid[nr][nc]);
                dfs(nr, nc, visited, panel);
            }
        }
    }
    
    public void reset() {
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                cell.visited = false;
                cell.top = true;
                cell.bottom = true;
                cell.left = true;
                cell.right = true;
            }
        }
    }

    private void removeWall(Cell a, Cell b) {
        int dr = b.row - a.row;
        int dc = b.col - a.col;
        if (dr == 1) { a.bottom = false; b.top = false; }
        if (dr == -1) { a.top = false; b.bottom = false; }
        if (dc == 1) { a.right = false; b.left = false; }
        if (dc == -1) { a.left = false; b.right = false; }
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < rows && c < cols;
    }
}
