import java.awt.*;
import java.util.List;

// the rat and how it moves
public class Rat {

    private static final int SIZE = 30;

    private double x;
    private double y;
    private double speed = 1.5;
    private int maxHp = 100;
    private int hp = maxHp;
    private int cheeseEaten = 5;   

    private List<Point> path;
    private int pathIndex = 0;   
    private int tileSize;
    private boolean reachedEnd = false;

    // starts in the middle of the first tile (the sewer grate)
    public Rat(List<Point> path, int tileSize) {
        this.path = path;
        this.tileSize = tileSize;
        Point start = path.get(0);
        x = start.x * tileSize + tileSize / 2.0;
        y = start.y * tileSize + tileSize / 2.0;
    }

    // here we wanted to make the rats walk smoothly instead of "jumping" to each new tile
    public void move() {
        if (pathIndex >= path.size() - 1) {
            reachedEnd = true;
            return;
        }

        Point next = path.get(pathIndex + 1);
        double targetX = next.x * tileSize + tileSize / 2.0;
        double targetY = next.y * tileSize + tileSize / 2.0;

        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= speed) {
            // the rat moves in small steps, so once it is less than one step away from the
            // centre of the tile, it is placed exactly on the centre to prevent overshooting it.
            // after that, the next tile in the route becomes the new target
            x = targetX;
            y = targetY;
            pathIndex++;
        } else {
            // move one step towards the centre of the tile. dividing dx and dy by the distance
            // gives only the direction, and multiplying by speed makes every step the same length,
            // so the rat moves at a constant speed no matter how far away the tile is
            x += dx / distance * speed;
            y += dy / distance * speed;
        }
    }

    public void takeDamage(int amount) {
        hp -= amount;
        if (hp < 0) {
            hp = 0;
        }
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public boolean hasReachedEnd() {
        return reachedEnd;
    }

    public int getCheeseEaten() {
        return cheeseEaten;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void draw(Graphics2D g2) {
        int left = (int) x - SIZE / 2;
        int top = (int) y - SIZE / 2;

        // tail
        g2.setColor(new Color(230, 150, 160));
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(left + 4, top + SIZE / 2, left - 8, top + SIZE / 2 - 6);
        g2.setStroke(new BasicStroke(1));

        // body and head
        g2.setColor(new Color(110, 110, 110));
        g2.fillOval(left, top + 6, SIZE - 6, SIZE - 12);
        g2.fillOval(left + SIZE - 14, top + 8, 14, 14);

        // ear
        g2.setColor(new Color(230, 150, 160));
        g2.fillOval(left + SIZE - 14, top + 4, 7, 7);

        // eye
        g2.setColor(Color.BLACK);
        g2.fillOval(left + SIZE - 6, top + 12, 3, 3);

        // health bar, the green part gets smaller when the rat gets hit
        int barWidth = SIZE;
        int filled = barWidth * hp / maxHp;
        g2.setColor(Color.RED);
        g2.fillRect(left, top - 4, barWidth, 5);
        g2.setColor(Color.GREEN);
        g2.fillRect(left, top - 4, filled, 5);
        g2.setColor(Color.BLACK);
        g2.drawRect(left, top - 4, barWidth, 5);
    }
}
