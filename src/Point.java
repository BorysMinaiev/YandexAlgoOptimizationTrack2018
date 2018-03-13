import java.awt.*;

public class Point {
    final int x, y;
    Color color;

    public Point(int x, int y, Color c) {
        this.x = x;
        this.y = y;
        this.color =c;
    }

    Point move(int dx, int dy) {
        return new Point(x + dx, y + dy, color);
    }
}
