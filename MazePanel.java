import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    private final Maze maze;
    private static final int CELL_SIZE = 25;
    private static final int WALL_THICKNESS = 2;
    
    // Design
    private static final Color WALL_COLOR = new Color(17, 24, 39);         // Grigio 900 - Muri
    private static final Color BACKGROUND = new Color(255, 255, 255);      // Bianco
    private static final Color VISITED_PRIMARY = new Color(59, 130, 246, 100);   // Blu 500 - Celle visitate (più opaco)
    private static final Color VISITED_SECONDARY = new Color(147, 197, 253, 60); // Blu 300 - Punto finale del gradiente (più opaco)
    private static final Color SHADOW = new Color(0, 0, 0, 8);             // Ombra
    private static final Color BORDER = new Color(229, 231, 235);          // Grigio 200 - Bordo
    private static final Color START_COLOR = new Color(34, 197, 94);       // Verde 500 - Punto di inizio
    private static final Color END_COLOR = new Color(239, 68, 68);         // Rosso 500 - Punto di fine
    private static final Color ENTRANCE_COLOR = new Color(59, 130, 246);   // Blu 500 - Entrata/Uscita

    public MazePanel(Maze maze) {
        this.maze = maze;
        setBackground(new Color(249, 250, 251)); // Grigio 50
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Elementi utili per una visualizzazione piu' pulita
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Centra la posizione
        int mazeWidth = maze.getCols() * CELL_SIZE;
        int mazeHeight = maze.getRows() * CELL_SIZE;
        int offsetX = (getWidth() - mazeWidth) / 2;
        int offsetY = (getHeight() - mazeHeight) / 2;
        
        // Renderizzazione dei Layers
        drawMazeBackground(g2, offsetX, offsetY, mazeWidth, mazeHeight);
        drawVisitedCells(g2, offsetX, offsetY);
        drawWalls(g2, offsetX, offsetY);
        drawEntranceAndExit(g2, offsetX, offsetY);
        drawStartEndMarkers(g2, offsetX, offsetY);
        
        g2.dispose();
    }
    
    private void drawMazeBackground(Graphics2D g2, int offsetX, int offsetY, int mazeWidth, int mazeHeight) {
        // Ombra
        g2.setColor(SHADOW);
        g2.fillRoundRect(offsetX + 2, offsetY + 2, mazeWidth + 4, mazeHeight + 4, 8, 8);
        
        // Sfondo
        g2.setColor(BACKGROUND);
        g2.fillRoundRect(offsetX, offsetY, mazeWidth, mazeHeight, 6, 6);
        
        // Bordo
        g2.setColor(BORDER);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawRoundRect(offsetX, offsetY, mazeWidth, mazeHeight, 6, 6);
    }
    
    private void drawVisitedCells(Graphics2D g2, int offsetX, int offsetY) {
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Cell cell = maze.getCell(r, c);
                if (cell.isVisited()) {
                    int x = offsetX + c * CELL_SIZE;
                    int y = offsetY + r * CELL_SIZE;
                    
                    // Gradiente dal centro ai bordi
                    float centerX = maze.getCols() / 2.0f;
                    float centerY = maze.getRows() / 2.0f;
                    float maxDistance = (float) Math.sqrt(centerX * centerX + centerY * centerY);
                    float distance = (float) Math.sqrt((c - centerX) * (c - centerX) + (r - centerY) * (r - centerY));
                    float factor = Math.min(1.0f, distance / maxDistance);
                    
                    Color cellColor = interpolateColor(VISITED_PRIMARY, VISITED_SECONDARY, factor);
                    
                    // Colorazione cella
                    g2.setColor(cellColor);
                    g2.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                }
            }
        }
    }
    
    private void drawWalls(Graphics2D g2, int offsetX, int offsetY) {
        g2.setColor(WALL_COLOR);
        g2.setStroke(new BasicStroke(WALL_THICKNESS, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Cell cell = maze.getCell(r, c);
                int x = offsetX + c * CELL_SIZE;
                int y = offsetY + r * CELL_SIZE;
                
                // Disegna i muri
                if (cell.top) {
                    g2.drawLine(x, y, x + CELL_SIZE, y);
                }
                if (cell.right) {
                    g2.drawLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE);
                }
                if (cell.bottom) {
                    g2.drawLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
                }
                if (cell.left) {
                    g2.drawLine(x, y, x, y + CELL_SIZE);
                }
            }
        }
    }
    
    private void drawEntranceAndExit(Graphics2D g2, int offsetX, int offsetY) {
        g2.setColor(BACKGROUND);
        g2.setStroke(new BasicStroke(WALL_THICKNESS + 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        
        // Entrata del labirinto
        int entranceSize = CELL_SIZE / 2;
        int entranceOffset = (CELL_SIZE - entranceSize) / 2;
        g2.drawLine(offsetX, offsetY + entranceOffset, offsetX, offsetY + entranceOffset + entranceSize);
        
        // Uscita del labirinto
        int exitX = offsetX + (maze.getCols() - 1) * CELL_SIZE + CELL_SIZE;
        int exitY = offsetY + (maze.getRows() - 1) * CELL_SIZE;
        g2.drawLine(exitX, exitY + entranceOffset, exitX, exitY + entranceOffset + entranceSize);
        
        g2.setColor(ENTRANCE_COLOR);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Freccia entrata
        int arrowX = offsetX - 12;
        int arrowY = offsetY + CELL_SIZE / 2;
        g2.drawLine(arrowX, arrowY, arrowX + 6, arrowY);
        g2.drawLine(arrowX + 3, arrowY - 2, arrowX + 6, arrowY);
        g2.drawLine(arrowX + 3, arrowY + 2, arrowX + 6, arrowY);
        
        // Freccia uscita
        int exitArrowX = exitX + 6;
        int exitArrowY = exitY + CELL_SIZE / 2;
        g2.drawLine(exitArrowX, exitArrowY, exitArrowX + 6, exitArrowY);
        g2.drawLine(exitArrowX + 3, exitArrowY - 2, exitArrowX + 6, exitArrowY);
        g2.drawLine(exitArrowX + 3, exitArrowY + 2, exitArrowX + 6, exitArrowY);
    }
    
    private void drawStartEndMarkers(Graphics2D g2, int offsetX, int offsetY) {
        int startX = offsetX + CELL_SIZE / 2;
        int startY = offsetY + CELL_SIZE / 2;
        
        g2.setColor(START_COLOR);
        g2.fillOval(startX - 6, startY - 6, 12, 12);
        g2.setColor(BACKGROUND);
        g2.fillOval(startX - 3, startY - 3, 6, 6);
        
        int endX = offsetX + (maze.getCols() - 1) * CELL_SIZE + CELL_SIZE / 2;
        int endY = offsetY + (maze.getRows() - 1) * CELL_SIZE + CELL_SIZE / 2;
        
        g2.setColor(END_COLOR);
        g2.fillOval(endX - 6, endY - 6, 12, 12);
        g2.setColor(BACKGROUND);
        g2.fillOval(endX - 3, endY - 3, 6, 6);
    }
    
    private Color interpolateColor(Color start, Color end, float factor) {
        factor = Math.max(0, Math.min(1, factor));
        
        int r = (int) (start.getRed() + factor * (end.getRed() - start.getRed()));
        int g = (int) (start.getGreen() + factor * (end.getGreen() - start.getGreen()));
        int b = (int) (start.getBlue() + factor * (end.getBlue() - start.getBlue()));
        int a = (int) (start.getAlpha() + factor * (end.getAlpha() - start.getAlpha()));
        
        return new Color(r, g, b, a);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
            maze.getCols() * CELL_SIZE + 60,
            maze.getRows() * CELL_SIZE + 60
        );
    }
}