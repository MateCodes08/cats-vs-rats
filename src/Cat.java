import java.awt.*;
import java.util.List;

// the cat (sits on one tile and throws yarn at rats that come close)
public class Cat {

    private int row;
    private int col;
    private int tileSize;

    private int damage;      
    private double range;    
    private int fireDelay;   
    private int cooldown = 0;

    public Cat(int row, int col, int tileSize, int damage) {
        this.row = row;
        this.col = col;
        this.tileSize = tileSize;
        this.damage = damage;
        this.range = tileSize * 2.0;
        this.fireDelay = 30;
    }

    public double getCenterX() {
        return col * tileSize + tileSize / 2.0;
    }

    public double getCenterY() {
        return row * tileSize + tileSize / 2.0;
    }

    // runs every tick, waits for the cooldown and then throws at the closest rat
    public void update(List<Rat> rats, List<YarnBall> yarnBalls) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        Rat target = findNearestRat(rats);
        if (target != null) {
            yarnBalls.add(new YarnBall(getCenterX(), getCenterY(), target, damage));
            cooldown = fireDelay;
        }
    }

    // goes through all rats and keeps the closest one that is in range
    private Rat findNearestRat(List<Rat> rats) {
        Rat nearest = null;
        double nearestDistance = range;

        for (Rat rat : rats) {
            double dx = rat.getX() - getCenterX();
            double dy = rat.getY() - getCenterY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance <= nearestDistance) {
                nearest = rat;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    public void draw(Graphics2D g2) {
        int x = col * tileSize;
        int y = row * tileSize;
        Color fur = new Color(240, 150, 60);

        // ears
        g2.setColor(fur);
        g2.fillPolygon(new int[] {x + 12, x + 20, x + 26}, new int[] {y + 22, y + 6, y + 20}, 3);
        g2.fillPolygon(new int[] {x + 34, x + 40, x + 48}, new int[] {y + 20, y + 6, y + 22}, 3);

        // head
        g2.fillOval(x + 10, y + 14, tileSize - 20, tileSize - 22);
        g2.setColor(new Color(180, 100, 30));
        g2.drawOval(x + 10, y + 14, tileSize - 20, tileSize - 22);

        // eyes
        g2.setColor(new Color(60, 140, 60));
        g2.fillOval(x + 20, y + 24, 6, 6);
        g2.fillOval(x + 34, y + 24, 6, 6);

        // nose
        g2.setColor(new Color(230, 120, 140));
        g2.fillOval(x + 28, y + 32, 4, 4);
    }
}
