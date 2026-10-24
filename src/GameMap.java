import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// the actual game: draws the map and runs everything that happens on it
public class GameMap extends JPanel {

    private static final int COLS = 11;
    private static final int ROWS = 8;
    private static final int TILE_SIZE = 60;

    private static final Color GRASS_COLOR = new Color(235, 220, 185);
    private static final Color GRASS_BORDER = new Color(210, 190, 155);
    private static final Color PATH_COLOR = new Color(193, 154, 107);
    private static final Color PATH_BORDER = new Color(163, 124, 77);

    // game settings
    private static final int SPAWN_DELAY = 50;   
    private static final int CAT_DAMAGE = 20;
    private static final int START_CHEESE = 20;
    private static final int MAX_ROUNDS = 5;
    private static final int START_FISH = 20;
    private static final int CAT_COST = 10;
    private static final int FISH_PER_RAT = 2;

    private boolean[][] isPath = new boolean[ROWS][COLS];  
    private List<Point> pathCells = new ArrayList<>();
    private List<Point> ratRoute;   // the route the rats follow, found with BFS

    private boolean[][] hasCat = new boolean[ROWS][COLS];   
    private List<Cat> cats = new ArrayList<>();
    private List<Rat> rats = new ArrayList<>();
    private List<YarnBall> yarnBalls = new ArrayList<>();

    private int ratsLeftToSpawn = 0;
    private int spawnCounter = 0;

    private int cheese = START_CHEESE;
    private int fish = START_FISH;
    private int round = 0;
    private boolean gameOver = false;

    // short message at the top of the map, like "not enough fish"
    private String infoMessage = "";
    private int infoTicks = 0;

    private MapScreen screen;
    private Timer gameTimer;

    public GameMap(MapScreen screen) {
        this.screen = screen;
        setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE));
        buildPath();
        ratRoute = findRoute(pathCells.get(0), pathCells.get(pathCells.size() - 1));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                placeCat(e.getX(), e.getY());
            }
        });

        gameTimer = new Timer(20, e -> updateGame());
        gameTimer.start();
    }

    // makes the snake shaped path: down, right, up, right, down
    private void buildPath() {
        int col = 0;
        for (int row = 0; row < ROWS; row++) addCell(row, col);
        for (col = 1; col <= 2; col++) addCell(ROWS - 1, col);
        for (int row = ROWS - 2; row >= 0; row--) addCell(row, 2);
        for (col = 3; col <= 4; col++) addCell(0, col);
        for (int row = 1; row < ROWS; row++) addCell(row, 4);
        for (col = 5; col <= 6; col++) addCell(ROWS - 1, col);
        for (int row = ROWS - 2; row >= 0; row--) addCell(row, 6);
        for (col = 7; col <= 8; col++) addCell(0, col);
        for (int row = 1; row < ROWS; row++) addCell(row, 8);
        for (col = 9; col <= 10; col++) addCell(ROWS - 1, col);
        for (int row = ROWS - 2; row >= 0; row--) addCell(row, 10);
    }

    private void addCell(int row, int col) {
        if (!isPath[row][col]) {
            isPath[row][col] = true;
            pathCells.add(new Point(col, row));
        }
    }

    // BFS pathfinding: searches outwards from the start tile by tile until it reaches the pantry
    private List<Point> findRoute(Point start, Point end) {
        boolean[][] visited = new boolean[ROWS][COLS];
        Point[][] cameFrom = new Point[ROWS][COLS];   // for each tile, the tile BFS reached it from
        Queue<Point> queue = new LinkedList<>();       // tiles we still have to check

        queue.add(start);
        visited[start.y][start.x] = true;

        int[] rowSteps = {-1, 1, 0, 0};
        int[] colSteps = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.equals(end)) {
                break;
            }

            for (int i = 0; i < 4; i++) {
                int newRow = current.y + rowSteps[i];
                int newCol = current.x + colSteps[i];
                boolean insideMap = newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS;
                // only go to path tiles we haven't seen yet
                if (insideMap && isPath[newRow][newCol] && !visited[newRow][newCol]) {
                    visited[newRow][newCol] = true;
                    cameFrom[newRow][newCol] = current;
                    queue.add(new Point(newCol, newRow));
                }
            }
        }

        // go back from the pantry to the start using cameFrom, adding each tile to the front of the list
        List<Point> route = new ArrayList<>();
        Point step = end;
        while (step != null) {
            route.add(0, step);
            step = cameFrom[step.y][step.x];
        }
        return route;
    }

    public boolean isRoundRunning() {
        return ratsLeftToSpawn > 0 || !rats.isEmpty();
    }

    public boolean canStartNextRound() {
        return !gameOver && !isRoundRunning() && round < MAX_ROUNDS;
    }

    public boolean isGameWon() {
        return !gameOver && round == MAX_ROUNDS && !isRoundRunning();
    }

    public void startNextRound() {
        if (!canStartNextRound()) {
            return;
        }
        round++;
        ratsLeftToSpawn = 5 + round * 5;   // every round has 5 more rats
        spawnCounter = 0;
        showInfo("Round " + round + " - here come the rats!");
        screen.updateLabels();
    }

    private void showInfo(String text) {
        infoMessage = text;
        infoTicks = 100;
    }

    public int getCheese() {
        return cheese;
    }

    public int getFish() {
        return fish;
    }

    public int getCatCost() {
        return CAT_COST;
    }

    public int getRound() {
        return round;
    }

    public int getMaxRounds() {
        return MAX_ROUNDS;
    }

    // turns the click position into a tile and puts a cat there if allowed
    private void placeCat(int mouseX, int mouseY) {
        int col = mouseX / TILE_SIZE;
        int row = mouseY / TILE_SIZE;
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return;
        }

        // if the cat can't go here, tell the player why
        if (gameOver) {
            return;
        }
        if (isPath[row][col]) {
            showInfo("You can't place a cat on the path!");
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        if (hasCat[row][col]) {
            showInfo("There is already a cat here!");
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        if (fish < CAT_COST) {
            showInfo("Not enough fish! A cat costs " + CAT_COST + " fish.");
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        fish -= CAT_COST;
        hasCat[row][col] = true;
        cats.add(new Cat(row, col, TILE_SIZE, CAT_DAMAGE));
        showInfo("Cat placed! -" + CAT_COST + " fish");
        screen.updateLabels();
        repaint();
    }

    // one tick of the game: spawn rats, move everything, shoot, check cheese and fish
    private void updateGame() {
        if (ratsLeftToSpawn > 0) {
            spawnCounter--;
            if (spawnCounter <= 0) {
                rats.add(new Rat(ratRoute, TILE_SIZE));
                ratsLeftToSpawn--;
                spawnCounter = SPAWN_DELAY;
            }
        }

        for (Rat rat : rats) {
            rat.move();
        }
        for (Cat cat : cats) {
            cat.update(rats, yarnBalls);
        }
        for (YarnBall ball : yarnBalls) {
            ball.move();
        }

        // rats in the pantry steal cheese, dead rats give fish
        for (Rat rat : rats) {
            if (rat.hasReachedEnd()) {
                cheese -= rat.getCheeseEaten();
                showInfo("A rat stole " + rat.getCheeseEaten() + " cheese!");
            } else if (rat.isDead()) {
                fish += FISH_PER_RAT;
            }
        }

        if (cheese <= 0) {
            cheese = 0;
            gameOver = true;
            gameTimer.stop();
        }

        yarnBalls.removeIf(ball -> ball.isFinished());
        rats.removeIf(rat -> rat.isDead() || rat.hasReachedEnd());

        if (infoTicks > 0) {
            infoTicks--;
        }

        screen.updateLabels();
        repaint();
    }

    // draws the whole map again every frame
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // grass and path tiles
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;
                if (isPath[row][col]) {
                    g2.setColor(PATH_COLOR);
                    g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                    g2.setColor(PATH_BORDER);
                    g2.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                } else {
                    g2.setColor(GRASS_COLOR);
                    g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                    g2.setColor(GRASS_BORDER);
                    g2.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        drawSewerGrate(g2, pathCells.get(0));
        drawPantry(g2, pathCells.get(pathCells.size() - 1));

        // cats first, so rats and yarn balls are drawn on top
        for (Cat cat : cats) {
            cat.draw(g2);
        }
        for (Rat rat : rats) {
            rat.draw(g2);
        }
        for (YarnBall ball : yarnBalls) {
            ball.draw(g2);
        }

        if (gameOver) {
            drawMessage(g2, "GAME OVER", new Color(230, 60, 60));
        } else if (isGameWon()) {
            drawMessage(g2, "YOU WIN!", new Color(90, 200, 90));
        } else if (infoTicks > 0) {
            drawInfo(g2);
        }
    }

    // dark box at the top with the info message in it
    private void drawInfo(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics metrics = g2.getFontMetrics();
        int boxWidth = metrics.stringWidth(infoMessage) + 30;
        int boxHeight = 36;
        int boxX = (getWidth() - boxWidth) / 2;
        int boxY = 10;

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        g2.setColor(Color.WHITE);
        g2.drawString(infoMessage, boxX + 15, boxY + 24);
    }

    // makes the map darker and puts big text in the middle
    private void drawMessage(Graphics2D g2, String text, Color color) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics metrics = g2.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(text)) / 2;
        int textY = getHeight() / 2 + metrics.getAscent() / 2;
        g2.setColor(color);
        g2.drawString(text, textX, textY);
    }

    // start of the path 
    private void drawSewerGrate(Graphics2D g2, Point cell) {
        int x = cell.x * TILE_SIZE;
        int y = cell.y * TILE_SIZE;
        g2.setColor(new Color(90, 90, 90));
        g2.fillOval(x + 8, y + 8, TILE_SIZE - 16, TILE_SIZE - 16);
        g2.setColor(Color.BLACK);
        for (int i = 1; i < 4; i++) {
            int lx = x + 8 + i * (TILE_SIZE - 16) / 4;
            g2.drawLine(lx, y + 8, lx, y + TILE_SIZE - 8);
        }
    }

    // end of the path 
    private void drawPantry(Graphics2D g2, Point cell) {
        int x = cell.x * TILE_SIZE;
        int y = cell.y * TILE_SIZE;
        g2.setColor(new Color(120, 80, 45));
        g2.fillRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);
        g2.setColor(new Color(90, 55, 25));
        g2.drawRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);
        g2.setColor(new Color(222, 184, 61));
        g2.fillOval(x + TILE_SIZE / 2 - 4, y + TILE_SIZE / 2 - 4, 8, 8);
    }

    public List<Point> getPathCells() {
        return pathCells;
    }
}
