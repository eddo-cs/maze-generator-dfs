public class Cell {
    public final int row, col;
    public boolean top = true, bottom = true, left = true, right = true;
    public boolean visited = false;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisited() {
        return visited;
    }
}