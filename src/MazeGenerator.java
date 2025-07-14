import javax.swing.SwingUtilities;

public class MazeGenerator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int rows = 20;
            int cols = 20;

            Maze maze = new Maze(rows, cols);
            MazePanel panel = new MazePanel(maze);
            new MazeUI(maze, panel); // interfaccia utente
        });
    }
}
