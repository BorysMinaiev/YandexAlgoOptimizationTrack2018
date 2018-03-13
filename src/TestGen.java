import java.io.*;
import java.util.*;

public class TestGen {
    FastScanner in;
    PrintWriter out;


    void genTest() {
        int taxis = 1 + rnd.nextInt(20);
        int people = 1 + rnd.nextInt(500);
        int zones = 1 + rnd.nextInt(20);
        int s = 1 + rnd.nextInt(10000);
        int sum = (2 * s + 1) * (2 * s + 1);
        if (sum < taxis + people + zones) {
            genTest();
            return;
        }
        HashSet<Point> pts = new HashSet<>();
        for (int sz : new int[]{taxis, people, zones}) {
            out.println(sz);
            for (int i = 0; i < sz; i++) {
                Point p = getRandomPoint(s, pts);
                out.println(p);
            }
        }
    }

    Point getRandomPoint(int s, HashSet<Point> alreadyPoints) {
        Point p = new Point(rnd.nextInt(2 * s + 1) - s, rnd.nextInt(2 * s + 1) - s);
        if (alreadyPoints.contains(p)) {
            return getRandomPoint(s, alreadyPoints);
        }
        alreadyPoints.add(p);
        return p;
    }

    void solve() {
        genTest();
    }

    Random rnd = new Random();

    class Point {
        final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return x + " " + y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y;
        }

        @Override
        public int hashCode() {

            return Objects.hash(x, y);
        }
    }

    void run() {
        try {
//            in = new FastScanner(new File("TestGen.in"));
            for (int t = 1; t <= 300; t++) {
                out = new PrintWriter(new File("tests/" + t + ".in"));

                solve();

                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void runIO() {

        in = new FastScanner(System.in);
        out = new PrintWriter(System.out);

        solve();

        out.close();
    }

    class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        public FastScanner(File f) {
            try {
                br = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public FastScanner(InputStream f) {
            br = new BufferedReader(new InputStreamReader(f));
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                String s = null;
                try {
                    s = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (s == null)
                    return null;
                st = new StringTokenizer(s);
            }
            return st.nextToken();
        }

        boolean hasMoreTokens() {
            while (st == null || !st.hasMoreTokens()) {
                String s = null;
                try {
                    s = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (s == null)
                    return false;
                st = new StringTokenizer(s);
            }
            return true;
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        long nextLong() {
            return Long.parseLong(next());
        }

        double nextDouble() {
            return Double.parseDouble(next());
        }
    }

    public static void main(String[] args) {
        new TestGen().run();
    }
}