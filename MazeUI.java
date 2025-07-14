import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MazeUI extends JFrame {
    // Design generale dell'applicazione
    private static final Color BLUE_600 = new Color(37, 99, 235);
    private static final Color BLUE_500 = new Color(59, 130, 246);
    private static final Color BLUE_400 = new Color(96, 165, 250);
    private static final Color BLUE_50 = new Color(239, 246, 255);
    
    private static final Color GRAY_50 = new Color(249, 250, 251);
    private static final Color GRAY_100 = new Color(243, 244, 246);
    private static final Color GRAY_200 = new Color(229, 231, 235);
    private static final Color GRAY_300 = new Color(209, 213, 219);
    private static final Color GRAY_600 = new Color(75, 85, 99);
    private static final Color GRAY_700 = new Color(55, 65, 81);
    private static final Color GRAY_900 = new Color(17, 24, 39);
    
    private static final Color GREEN_500 = new Color(34, 197, 94);
    private static final Color AMBER_500 = new Color(245, 158, 11);
    private static final Color RED_500 = new Color(239, 68, 68);
    
    private static final Color WHITE = new Color(255, 255, 255);
    
    // Design dei Font
    private static final Font FONT_DISPLAY = new Font("Inter", Font.BOLD, 18);
    private static final Font FONT_HEADING = new Font("Inter", Font.BOLD, 14);
    private static final Font FONT_BODY = new Font("Inter", Font.PLAIN, 13);
    private static final Font FONT_CAPTION = new Font("Inter", Font.PLAIN, 12);
    private static final Font FONT_MONO = new Font("SF Mono", Font.PLAIN, 12);

    private JButton startButton;
    private JButton restartButton;
    private JLabel statusLabel;
    private JLabel dimensionLabel;
    private JLabel timeLabel;
    private JSpinner rowSpinner;
    private JSpinner colSpinner;
    private JButton resizeButton;
    private Maze maze;
    private MazePanel panel;
    private JProgressBar progressBar;
    private JScrollPane scrollPane;
    private long startTime;
    private long endTime;
    
    private JPanel performancePanel;
    private JTextArea performanceLog;
    private JScrollPane performanceScrollPane;
    private JLabel performanceTitle;
    private java.util.List<String> performanceData;
    private JButton clearLogButton;

    public MazeUI(Maze initialMaze, MazePanel initialPanel) {
        this.maze = initialMaze;
        this.panel = initialPanel;
        this.performanceData = new ArrayList<>();

        setupLookAndFeel();
        setupFrame();
        setupComponents();
        setupEventHandlers();
        setVisible(true);
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        	
        }
    }

    private void setupFrame() {
        setTitle("Generatore di Labirinti");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1300, 800));
        getContentPane().setBackground(GRAY_50);
        
        updateWindowSize();
        
        try {
            setIconImage(createAppIcon());
        } catch (Exception e) {
            // Continua senza icona
        }
    }

    private void setupComponents() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY_200),
            new EmptyBorder(24, 32, 24, 32)
        ));

        // Sinistra: Titolo e controlli
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(WHITE);
        
        JLabel titleLabel = new JLabel("Generatore di Labirinti");
        titleLabel.setFont(FONT_DISPLAY);
        titleLabel.setForeground(GRAY_900);
        leftPanel.add(titleLabel);

        // Centro: Pulsanti azione
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        centerPanel.setBackground(WHITE);

        startButton = createSecondaryButton("Genera");
        restartButton = createSecondaryButton("Riavvia");
        restartButton.setEnabled(false);

        centerPanel.add(startButton);
        centerPanel.add(restartButton);

        // Destra: Controllo dimensione
        JPanel rightPanel = createSizeControls();

        header.add(leftPanel, BorderLayout.WEST);
        header.add(centerPanel, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createSizeControls() {
        JPanel container = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        container.setBackground(WHITE);

        JPanel sizeCard = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        sizeCard.setBackground(GRAY_50);
        sizeCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            new EmptyBorder(4, 16, 4, 16)
        ));

        JLabel rowsLabel = createLabel("Righe");
        rowSpinner = createSpinner(maze.getRows());
        
        JLabel colsLabel = createLabel("Colonne");
        colSpinner = createSpinner(maze.getCols());
        
        resizeButton = createSecondaryButton("Ridimensiona");

        sizeCard.add(rowsLabel);
        sizeCard.add(rowSpinner);
        sizeCard.add(colsLabel);
        sizeCard.add(colSpinner);
        sizeCard.add(resizeButton);

        container.add(sizeCard);
        return container;
    }

    private JPanel createMainContentPanel() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(GRAY_50);

        // Area del labirinto
        JPanel mazeArea = new JPanel(new BorderLayout());
        mazeArea.setBackground(GRAY_50);
        mazeArea.setBorder(new EmptyBorder(32, 32, 32, 16));

        scrollPane = new JScrollPane(panel);
        customizeScrollPane(scrollPane);
        
        mazeArea.add(scrollPane, BorderLayout.CENTER);

        // Pannello delle performance
        JPanel performanceArea = createPerformancePanel();

        main.add(mazeArea, BorderLayout.CENTER);
        main.add(performanceArea, BorderLayout.EAST);

        return main;
    }

    private JPanel createPerformancePanel() {
        performancePanel = new JPanel(new BorderLayout());
        performancePanel.setBackground(WHITE);
        performancePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, GRAY_200),
            new EmptyBorder(32, 24, 32, 32)
        ));
        performancePanel.setPreferredSize(new Dimension(320, 0));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        performanceTitle = new JLabel("Log delle Performance");
        performanceTitle.setFont(FONT_HEADING);
        performanceTitle.setForeground(GRAY_900);

        clearLogButton = createDangerButton("Pulisci");
        clearLogButton.setPreferredSize(new Dimension(75, 32)); // Dimensione più grande per il testo

        header.add(performanceTitle, BorderLayout.WEST);
        header.add(clearLogButton, BorderLayout.EAST);

        // Log area
        performanceLog = new JTextArea();
        performanceLog.setEditable(false);
        performanceLog.setFont(FONT_MONO);
        performanceLog.setBackground(GRAY_50);
        performanceLog.setForeground(GRAY_700);
        performanceLog.setBorder(new EmptyBorder(16, 16, 16, 16));
        performanceLog.setLineWrap(false);
        performanceLog.setText("Nessuna generazione effettuata.\n\nI tempi di esecuzione appariranno\nqui dopo ogni generazione\ndi labirinto.\n\nPerfetto per analizzare le\nperformance dell'algoritmo DFS.");

        performanceScrollPane = new JScrollPane(performanceLog);
        customizeScrollPane(performanceScrollPane);

        // Stats
        JPanel stats = createStatsPanel();

        performancePanel.add(header, BorderLayout.NORTH);
        performancePanel.add(performanceScrollPane, BorderLayout.CENTER);
        performancePanel.add(stats, BorderLayout.SOUTH);

        return performancePanel;
    }

    private JPanel createStatsPanel() {
        JPanel stats = new JPanel();
        stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));
        stats.setBackground(BLUE_50);
        stats.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BLUE_400, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel statsTitle = new JLabel("Statistiche");
        statsTitle.setFont(FONT_CAPTION);
        statsTitle.setForeground(BLUE_600);
        statsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel totalRunsLabel = new JLabel("Esecuzioni totali: 0");
        totalRunsLabel.setFont(FONT_CAPTION);
        totalRunsLabel.setForeground(GRAY_600);
        totalRunsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avgTimeLabel = new JLabel("Media: -- ms");
        avgTimeLabel.setFont(FONT_CAPTION);
        avgTimeLabel.setForeground(GRAY_600);
        avgTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        stats.add(statsTitle);
        stats.add(Box.createVerticalStrut(8));
        stats.add(totalRunsLabel);
        stats.add(Box.createVerticalStrut(4));
        stats.add(avgTimeLabel);

        stats.putClientProperty("totalRuns", totalRunsLabel);
        stats.putClientProperty("avgTime", avgTimeLabel);

        return stats;
    }

    private JPanel createStatusPanel() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(WHITE);
        status.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY_200),
            new EmptyBorder(16, 32, 16, 32)
        ));

        // Sinistra: Informazioni su stato e dimensioni
        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftInfo.setBackground(WHITE);

        statusLabel = new JLabel("Pronto per la generazione");
        statusLabel.setFont(FONT_BODY);
        statusLabel.setForeground(GRAY_700);

        dimensionLabel = new JLabel("  •  " + maze.getRows() + "×" + maze.getCols());
        dimensionLabel.setFont(FONT_BODY);
        dimensionLabel.setForeground(GRAY_700);

        timeLabel = new JLabel("  •  --");
        timeLabel.setFont(FONT_BODY);
        timeLabel.setForeground(GRAY_700);

        leftInfo.add(statusLabel);
        leftInfo.add(dimensionLabel);
        leftInfo.add(timeLabel);

        // Destra: Barra dei progressi della generazione
        progressBar = createProgressBar();

        status.add(leftInfo, BorderLayout.WEST);
        status.add(progressBar, BorderLayout.EAST);

        return status;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BODY);
        button.setForeground(GRAY_700);
        button.setBackground(WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            new EmptyBorder(9, 19, 9, 19)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(GRAY_50);
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(WHITE);
                }
            }
        });
        
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_CAPTION);
        button.setForeground(RED_500);
        button.setBackground(WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RED_500, 1),
            new EmptyBorder(6, 12, 6, 12)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(RED_500);
                    button.setForeground(WHITE);
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(WHITE);
                    button.setForeground(RED_500);
                }
            }
        });
        
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_CAPTION);
        label.setForeground(GRAY_600);
        return label;
    }

    private JSpinner createSpinner(int value) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, 5, 50, 1));
        spinner.setFont(FONT_BODY);
        spinner.setPreferredSize(new Dimension(64, 32));
        
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setFont(FONT_BODY);
            textField.setForeground(GRAY_700);
            textField.setHorizontalAlignment(JTextField.CENTER);
            textField.setBorder(new EmptyBorder(4, 8, 4, 8));
        }
        
        return spinner;
    }

    private JProgressBar createProgressBar() {
        progressBar = new JProgressBar();
        progressBar.setStringPainted(false); // Disabilitiamo il testo sovrapposto
        progressBar.setPreferredSize(new Dimension(200, 8));
        progressBar.setFont(FONT_CAPTION);
        progressBar.setForeground(BLUE_500);
        progressBar.setBackground(GRAY_200);
        progressBar.setBorderPainted(false);
        
        progressBar.setUI(new BasicProgressBarUI() {
            private Timer animationTimer;
            private float animationPosition = 0f;
            
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = progressBar.getWidth();
                int height = progressBar.getHeight();
                int progress = (int) (width * progressBar.getPercentComplete());
                
                // Background
                g2.setColor(GRAY_200);
                g2.fillRoundRect(0, 0, width, height, height/2, height/2);
                
                // Progress
                if (progress > 0) {
                    g2.setColor(BLUE_500);
                    g2.fillRoundRect(0, 0, progress, height, height/2, height/2);
                }
                
                g2.dispose();
            }
            
            @Override
            protected void paintIndeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = progressBar.getWidth();
                int height = progressBar.getHeight();
                
                // Background
                g2.setColor(GRAY_200);
                g2.fillRoundRect(0, 0, width, height, height/2, height/2);
                
                // Animazione fluida
                if (animationTimer == null) {
                    animationTimer = new Timer(16, e -> { // 60 FPS
                        animationPosition += 0.02f;
                        if (animationPosition > 1.2f) {
                            animationPosition = -0.2f;
                        }
                        progressBar.repaint();
                    });
                    animationTimer.start();
                }
                
                // Moving indicator fluido
                g2.setColor(BLUE_500);
                int indicatorWidth = width / 4;
                int x = (int) (animationPosition * (width + indicatorWidth)) - indicatorWidth;
                
                // Gradiente per effetto più fluido
                Color transparent = new Color(BLUE_500.getRed(), BLUE_500.getGreen(), BLUE_500.getBlue(), 0);
                GradientPaint gradient = new GradientPaint(
                    x, 0, transparent,
                    x + indicatorWidth, 0, BLUE_500
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(x, 0, indicatorWidth, height, height/2, height/2);
                
                // Secondo gradiente per l'effetto di dissolvenza finale
                GradientPaint gradient2 = new GradientPaint(
                    x + indicatorWidth/2, 0, BLUE_500,
                    x + indicatorWidth, 0, transparent
                );
                g2.setPaint(gradient2);
                g2.fillRoundRect(x + indicatorWidth/2, 0, indicatorWidth/2, height, height/2, height/2);
                
                g2.dispose();
            }
            
            @Override
            public void setAnimationIndex(int newValue) {
                super.setAnimationIndex(newValue);
                if (!progressBar.isIndeterminate() && animationTimer != null) {
                    animationTimer.stop();
                    animationTimer = null;
                }
            }
        });
        
        return progressBar;
    }

    private void customizeScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(GRAY_200, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(WHITE);
        
        customizeScrollBar(scrollPane.getVerticalScrollBar());
        customizeScrollBar(scrollPane.getHorizontalScrollBar());
    }

    private void customizeScrollBar(JScrollBar scrollBar) {
        scrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = GRAY_300;
                this.trackColor = GRAY_100;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createInvisibleButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createInvisibleButton();
            }
            
            private JButton createInvisibleButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, 
                               thumbBounds.width - 4, thumbBounds.height - 4, 4, 4);
                g2.dispose();
            }
        });
        
        scrollBar.setPreferredSize(new Dimension(8, 8));
    }

    private void setupEventHandlers() {
        startButton.addActionListener(e -> startMazeGeneration());
        restartButton.addActionListener(e -> restartMazeGeneration());
        resizeButton.addActionListener(e -> resizeMaze());
        clearLogButton.addActionListener(e -> clearPerformanceLog());
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.repaint();
            }
        });
    }

    private void startMazeGeneration() {
        // Cambia il bottone a stile primario verde durante la generazione
        startButton.setForeground(WHITE);
        startButton.setBackground(GREEN_500);
        startButton.setBorder(new EmptyBorder(12, 24, 12, 24));
        
        startButton.setEnabled(false);
        resizeButton.setEnabled(false);
        rowSpinner.setEnabled(false);
        colSpinner.setEnabled(false);
        statusLabel.setText("Generazione in corso...");
        statusLabel.setForeground(BLUE_600);
        progressBar.setIndeterminate(true);
        timeLabel.setText("  •  --");
        
        startTime = System.currentTimeMillis();
        
        new Thread(() -> {
            try {
                maze.generate(panel);
                endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                SwingUtilities.invokeLater(() -> {
                    restartButton.setEnabled(true);
                    resizeButton.setEnabled(true);
                    rowSpinner.setEnabled(true);
                    colSpinner.setEnabled(true);
                    statusLabel.setText("Generazione completata");
                    statusLabel.setForeground(GREEN_500);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    timeLabel.setText("  •  " + duration + " ms");
                    
                    addPerformanceEntry(maze.getRows(), maze.getCols(), duration);
                });
            } catch (InterruptedException ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Generazione interrotta");
                    statusLabel.setForeground(RED_500);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(0);
                    resizeButton.setEnabled(true);
                    rowSpinner.setEnabled(true);
                    colSpinner.setEnabled(true);
                    timeLabel.setText("  •  --");
                });
            }
        }).start();
    }

    private void restartMazeGeneration() {
        maze.reset();
        panel.repaint();
        restartButton.setEnabled(false);
        
        // Riporta il bottone start allo stile secondario
        startButton.setForeground(GRAY_700);
        startButton.setBackground(WHITE);
        startButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            new EmptyBorder(11, 23, 11, 23)
        ));
        startButton.setEnabled(true);
        
        statusLabel.setText("Pronto per la generazione");
        statusLabel.setForeground(GRAY_700);
        progressBar.setValue(0);
        timeLabel.setText("  •  --");
    }

    private void resizeMaze() {
        int newRows = (Integer) rowSpinner.getValue();
        int newCols = (Integer) colSpinner.getValue();
        
        maze = new Maze(newRows, newCols);
        panel = new MazePanel(maze);
        
        scrollPane.setViewportView(panel);
        dimensionLabel.setText("  •  " + newRows + "×" + newCols);
        
        updateWindowSize();
        
        restartButton.setEnabled(false);
        
        // Riporta il bottone start allo stile secondario
        startButton.setForeground(GRAY_700);
        startButton.setBackground(WHITE);
        startButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            new EmptyBorder(11, 23, 11, 23)
        ));
        startButton.setEnabled(true);
        
        statusLabel.setText("Labirinto ridimensionato");
        statusLabel.setForeground(AMBER_500);
        progressBar.setValue(0);
        timeLabel.setText("  •  --");
        
        panel.repaint();
        revalidate();
    }

    private void updateWindowSize() {
        int mazeWidth = maze.getCols() * 25;
        int mazeHeight = maze.getRows() * 25;
        
        int totalWidth = Math.max(1300, mazeWidth + 600);
        int totalHeight = Math.max(800, mazeHeight + 300);
        
        totalWidth = Math.min(totalWidth, 1800);
        totalHeight = Math.min(totalHeight, 1200);
        
        setSize(totalWidth, totalHeight);
        setLocationRelativeTo(null);
    }

    private void addPerformanceEntry(int rows, int cols, long duration) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String timestamp = timeFormat.format(new Date());
        
        String entry = String.format("%s  %dx%d  %dms", timestamp, rows, cols, duration);
        performanceData.add(entry);
        
        if (performanceData.size() == 1) {
            performanceLog.setText("");
        }
        
        performanceLog.append(entry + "\n");
        performanceLog.setCaretPosition(performanceLog.getDocument().getLength());
        
        updateStats();
    }

    private void updateStats() {
        if (performanceData.isEmpty()) return;
        
        Component[] components = performancePanel.getComponents();
        JPanel statsPanel = null;
        for (Component comp : components) {
            if (comp instanceof JPanel && comp.getBackground().equals(BLUE_50)) {
                statsPanel = (JPanel) comp;
                break;
            }
        }
        
        if (statsPanel != null) {
            JLabel totalRunsLabel = (JLabel) statsPanel.getClientProperty("totalRuns");
            JLabel avgTimeLabel = (JLabel) statsPanel.getClientProperty("avgTime");
            
            if (totalRunsLabel != null && avgTimeLabel != null) {
                int totalRuns = performanceData.size();
                
                long totalTime = 0;
                for (String entry : performanceData) {
                    String[] parts = entry.split("\\s+");
                    if (parts.length >= 3) {
                        String timeStr = parts[parts.length - 1].replace("ms", "");
                        try {
                            totalTime += Long.parseLong(timeStr);
                        } catch (NumberFormatException e) {
                            // Ignore parsing errors
                        }
                    }
                }
                
                long avgTime = totalTime / totalRuns;
                
                totalRunsLabel.setText("Esecuzioni totali: " + totalRuns);
                avgTimeLabel.setText("Media: " + avgTime + " ms");
            }
        }
    }

    private void clearPerformanceLog() {
        performanceData.clear();
        performanceLog.setText("Log delle performance pulito.\n\nI nuovi tempi di esecuzione\nappariranno qui dopo le\nprossime generazioni.");
        
        Component[] components = performancePanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp.getBackground().equals(BLUE_50)) {
                JPanel statsPanel = (JPanel) comp;
                JLabel totalRunsLabel = (JLabel) statsPanel.getClientProperty("totalRuns");
                JLabel avgTimeLabel = (JLabel) statsPanel.getClientProperty("avgTime");
                
                if (totalRunsLabel != null && avgTimeLabel != null) {
                    totalRunsLabel.setText("Esecuzioni totali: 0");
                    avgTimeLabel.setText("Media: -- ms");
                }
                break;
            }
        }
    }

    private Image createAppIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icon.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(BLUE_500);
        g2.fillRoundRect(4, 4, 24, 24, 6, 6);
        
        g2.setColor(WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        
        g2.drawLine(8, 8, 24, 8);
        g2.drawLine(8, 8, 8, 24);
        g2.drawLine(8, 24, 24, 24);
        g2.drawLine(24, 8, 24, 24);
        g2.drawLine(14, 8, 14, 18);
        g2.drawLine(20, 14, 20, 24);
        g2.drawLine(8, 18, 18, 18);
        
        g2.dispose();
        return icon;
    }
}