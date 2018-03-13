import java.io.*;
import java.util.*;

public class Solve {
    FastScanner in;
    PrintWriter out;

    class Point {
        int x, y;
        double usage;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
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

        boolean isValid() {
            return Math.abs(x) <= MAX_X && Math.abs(y) <= MAX_X;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", usage=" + usage +
                    '}';
        }
    }

    class TestCase {
        Point[] taxis;
        Point[] people;
        Point[] fanZones;

        public TestCase(Point[] taxis, Point[] people, Point[] fanZones) {
            this.taxis = taxis;
            this.people = people;
            this.fanZones = fanZones;
        }
    }

    Point[] readPoints(int n) {
        Point[] pts = new Point[n];
        for (int i = 0; i < pts.length; i++) {
            pts[i] = new Point(in.nextInt(), in.nextInt());
        }
        return pts;
    }

    TestCase read() {
        Point[] a = readPoints(in.nextInt());
        Point[] b = readPoints(in.nextInt());
        Point[] c = readPoints(in.nextInt());
        return new TestCase(a, b, c);
    }

    class Move {
        int dx;
        int dy;
        int[] ids;

        public Move(int dx, int dy, int[] ids) {
            this.dx = dx;
            this.dy = dy;
            this.ids = ids;
        }
    }

    class MyObject {
        int id;
        int x;
        int y;
        boolean done;

        public MyObject(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        boolean isValid() {
            return Math.abs(x) <= MAX_X && Math.abs(y) <= MAX_X;
        }
    }

    final int MAX_X = 10_000;

    int[] getArray(List<Integer> list) {
        Collections.sort(list);
        int[] res = new int[list.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = list.get(i);
            if (i > 0 && res[i] == res[i - 1]) {
                throw new AssertionError();
            }
        }
        return res;
    }

    void makeMove(Move move, MyObject[] taxies, int mult) {
        for (int id : move.ids) {
            taxies[id].x += move.dx * mult;
            taxies[id].y += move.dy * mult;
        }
    }

    boolean isValidPosition(int who, MyObject where, TestCase t, MyObject[] taxies, boolean[] peopleTaken) {
        for (int i = 0; i < taxies.length; i++) {
            for (int j = i + 1; j < taxies.length; j++) {
                if (dist(taxies[i], taxies[j]) == 0) {
//                    System.err.println("not valid because " + i + " " + j);
                    return false;
                }
            }
            for (int j = 0; j < t.people.length; j++) {
                Point p = t.people[j];
                if (peopleTaken[j]) {
                    continue;
                }
                if (p.x == taxies[i].x && p.y == taxies[i].y) {
                    if (i != who) {
//                        System.err.println("not valid because people! "+ i);
                        return false;
                    }
                    if (p.x != where.x || p.y != where.y) {
//                        System.err.println("not valid because peopl e!");
                        return false;
                    }
                }
            }
            if (!taxies[i].isValid()) {
//                System.err.println("found not valid taxi: " + taxies[i].x + " " + taxies[i].y);
                return false;
            }
        }
        return true;
    }

    Move genRandomMove(int maxDist, List<Integer> ids) {
        return new Move(rnd.nextInt(maxDist * 2 + 1) - maxDist, rnd.nextInt(maxDist * 2 + 1) - maxDist, getArray(ids));
    }

    void doMove2(int[] ids, int who, MyObject where, List<Move> moves, TestCase t, MyObject[] taxies, boolean[] peopleTaken, int dx, int dy) {
        Move move = new Move(dx, dy, ids);
        makeMove(move, taxies, 1);
//        System.err.println("i am here!");
        if (isValidPosition(who, where, t, taxies, peopleTaken)) {
            moves.add(move);
        } else {
            ArrayList<Integer> moveIds = new ArrayList<>();
            for (int id : ids) {
                for (MyObject anotherTaxi : taxies) {
                    if (anotherTaxi.id != id) {
                        if (dist(anotherTaxi, taxies[id]) == 0) {
                            moveIds.add(anotherTaxi.id);
                        }
                    }
                }
                for (int j = 0; j < t.people.length; j++) {
                    if (peopleTaken[j]) {
                        continue;
                    }
                    Point p = t.people[j];
                    if (dist(taxies[id], p.x, p.y) == 0) {
                        if (id != who || where.id != j) {
                            moveIds.add(id);
                        }

                    }
                }
                if (!taxies[id].isValid()) {
                    moveIds.add(id);
                }
            }

            makeMove(move, taxies, -1);


            for (int d = 1; d < 50; d++) {
                for (int it = 0; it < 10; it++) {
                    Move firstMove = genRandomMove(d, moveIds);
                    makeMove(firstMove, taxies, 1);
                    if (isValidPosition(who, where, t, taxies, peopleTaken)) {
                        Move nextMove = new Move(dx, dy, ids);
                        makeMove(nextMove, taxies, 1);
                        if (isValidPosition(who, where, t, taxies, peopleTaken)) {
//                            System.err.println("ADDITIONAL MOVE (" + firstMove.dx + " " + firstMove.dy + ")");
                            moves.add(firstMove);
                            moves.add(nextMove);
                            return;
                        }
                        makeMove(nextMove, taxies, -1);
                    }
                    makeMove(firstMove, taxies, -1);

                }
            }
            System.err.println(Arrays.toString(ids));
            System.err.println(who);
            System.err.println(where.x + " " + where.y);
            System.err.println("cnt peoples = " + t.people.length);
            System.err.println("cnt taxies = " + t.taxis.length);
            System.err.println("ids = " + Arrays.toString(ids));
            System.err.println("dx = " + dx + ", dy = " + dy);
            for (MyObject tax : taxies) {
                System.err.println(tax.x + " " + tax.y);
            }
            System.err.println("who = " + who);
            Move firstMove = genRandomMove(10, moveIds);
            makeMove(firstMove, taxies, 1);
            if (isValidPosition(who, where, t, taxies, peopleTaken)) {
                Move nextMove = new Move(dx, dy, ids);
                makeMove(nextMove, taxies, 1);
                if (isValidPosition(who, where, t, taxies, peopleTaken)) {
                    System.err.println("strange");
                } else {
                    System.err.println("fail");
                }
            }
            throw new AssertionError();
        }
    }

    void doMove(int dx, int dy, int[] ids, List<Move> moves, MyObject[] taxis, boolean shouldBeOK, TestCase t) {
        HashSet<Point> badPoints = new HashSet<>();
        boolean[] goes = new boolean[taxis.length];
        for (int id : ids) {
            goes[id] = true;
            if (!shouldBeOK) {
                badPoints.add(new Point(taxis[id].x, taxis[id].y));
            }
            taxis[id].x += dx;
            taxis[id].y += dy;
            badPoints.add(new Point(taxis[id].x, taxis[id].y));
        }
        ArrayList<Integer> needMove = new ArrayList<>();
        for (int i = 0; i < taxis.length; i++) {
            if (!goes[i] && badPoints.contains(new Point(taxis[i].x, taxis[i].y))) {
                needMove.add(i);
            } else {
                if (!goes[i]) {
                    badPoints.add(new Point(taxis[i].x, taxis[i].y));
                }
            }
        }
        if (!needMove.isEmpty()) {
            if (shouldBeOK) {
                throw new AssertionError();
            }
            badPoints.addAll(Arrays.asList(t.people));
            boolean found = false;
            for (int d = 1; ; d++) {
                if (found) {
                    break;
                }
                for (int iters = 0; iters < 10; iters++) {
                    int ndx = rnd.nextInt(d * 2 + 1) - 1;
                    int ndy = rnd.nextInt(d * 2 + 1) - 1;
                    boolean ok = true;
                    for (int checkId : needMove) {
                        int nx = ndx + taxis[checkId].x;
                        int ny = ndy + taxis[checkId].y;
                        Point p = new Point(nx, ny);
                        if (badPoints.contains(p) || !p.isValid()) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) {
                        doMove(ndx, ndy, getArray(needMove), moves, taxis, true, t);
                        found = true;
                        break;
                    }
                }
            }
        }
        moves.add(new Move(dx, dy, ids));
        for (int i = 0; i < taxis.length; i++) {
            for (int j = i + 1; j < taxis.length; j++) {
                if (dist(taxis[i], taxis[j]) == 0) {
                    throw new AssertionError();
                }
            }
        }
        for (MyObject o : taxis) {
            if (!o.isValid()) {
                throw new AssertionError();
            }
        }
    }

    class MyMove {

    }

    class MyRealMove extends MyMove {
        ArrayList<Integer> ids;
        int dx;
        int dy;
        int id;
        MyObject shouldBetAt;

        public MyRealMove(int dx, int dy, int id, MyObject shouldBetAt) {
            this.dx = dx;
            this.dy = dy;
            ids = new ArrayList<>();
            this.id = id;
            this.shouldBetAt = shouldBetAt;
        }
    }

    ArrayList<Move> fixSolve(List<MyMove> moves, TestCase t) {
        ArrayList<Move> res = new ArrayList<>();
        MyObject[] taxies = new MyObject[t.taxis.length];
        for (int i = 0; i < taxies.length; i++) {
            taxies[i] = new MyObject(i, t.taxis[i].x, t.taxis[i].y);
        }
        boolean[] taken = new boolean[t.people.length];
        for (MyMove move : moves) {
            if (move instanceof MyRealMove) {
                MyRealMove real = (MyRealMove) move;
//                System.err.println("expected move " + real.dx + " " + real.dy);
//                int needDX = real.shouldBetAt.x - taxies[real.id].x;
//                int needDY = real.shouldBetAt.y - taxies[real.id].y;
//                int d2 = (real.dx - needDX) * (real.dx - needDX) + (real.dy - needDY) * (real.dy - needDY);
                doMove2(getArray(real.ids), real.id, real.shouldBetAt, res, t, taxies, taken, real.dx, real.dy);
                if (real.shouldBetAt != null) {
                    int needDX = real.shouldBetAt.x - taxies[real.id].x;
                    int needDY = real.shouldBetAt.y - taxies[real.id].y;
                    if (needDX != 0 || needDY != 0) {
                        if (needDX * needDX + needDY * needDY > 100) {
                            System.err.println("need = " + needDX + " " + needDY);
                            throw new AssertionError();
                        }
                        doMove2(new int[]{real.id}, real.id, real.shouldBetAt, res, t, taxies, taken, needDX, needDY);
                        if (dist(real.shouldBetAt, taxies[real.id]) != 0) {
                            System.err.println("my id = " + real.id);
                            throw new AssertionError();
                        }
                    }
                    if (real.shouldBetAt instanceof MyPeople) {
                        taken[real.shouldBetAt.id] = true;
                    }
                }
            } else {
                throw new AssertionError();
            }
        }
        return res;
    }

    class MyTaxi extends MyObject {
        int eventsDone;
        boolean havePassanger;
        BestMove bestMove;

        public MyTaxi(int id, int x, int y) {
            super(id, x, y);
            eventsDone = 0;
        }
    }

    class BestMove implements Comparable<BestMove> {
        List<MyRealMove> useMoves;
        int nDX;
        int nDY;
        int dist;
        MyObject target;
        MyTaxi taxi;

        public BestMove(int nDX, int nDY, MyObject target, MyTaxi taxi) {
            useMoves = new ArrayList<>();
            this.nDX = nDX;
            this.nDY = nDY;
            dist = nDX * nDX + nDY * nDY;
            this.target = target;
            this.taxi = taxi;
        }

        public BestMove(MyObject target, MyTaxi taxi) {
            useMoves = new ArrayList<>();
            dist = nDX * nDX + nDY * nDY;
            this.target = target;
            this.taxi = taxi;
        }

        @Override
        public int compareTo(BestMove bestMove) {
            if (bestMove == null) {
                return -1;
            }
            return Integer.compare(dist, bestMove.dist);
        }
    }

    final int MAX_MOVES = 8;

    boolean isValidPos(int x, int y) {
        return Math.abs(x) <= MAX_X && Math.abs(y) <= MAX_X;
    }

    int zz = 0;

    BestMove findBestMove(List<MyObject> targets, MyTaxi taxi, ArrayList<MyMove> moves, int version) { // it is called 20k times
        List<MyRealMove> realMoves = new ArrayList<>();
        zz++;
//        System.err.println(zz+"!");
        for (int i = moves.size() - 1; i >= taxi.eventsDone; i--) {
            if (moves.get(i) instanceof MyRealMove) {
                realMoves.add((MyRealMove) moves.get(i));
            }
            if (realMoves.size() > 300 && (version < 2 || version == 4)) {
                break;
            }
        }
        Collections.reverse(realMoves);
        BestMove res = null;
//        System.err.println(realMoves.size());
        if (version == 0 || realMoves.size() < 8) {
            while (realMoves.size() > MAX_MOVES) {
                realMoves.remove(0); // TODO: remove random?
            }
            int events = realMoves.size();
            int bestDist = Integer.MAX_VALUE;
            int bestMask = -1;
            int bestDX = -1, bestDY = -1;
            MyObject bestTarget = null;
            for (int mask = 0; mask < 1 << events; mask++) { // 20M at most
                int x = taxi.x, y = taxi.y;
                boolean ok = true;
                for (int it = 0; it < events; it++) {
                    if (((1 << it) & mask) != 0) {
                        MyRealMove move = realMoves.get(it);
                        x += move.dx;
                        y += move.dy;
                        if (!isValidPos(x, y)) { // 200M at most
                            ok = false;
                            break;
                        }
                    }
                }
                if (ok) {
                    for (MyObject target : targets) { // 20M * 500 = too much ?
                        int dist = dist(target, x, y);
                        if (dist < bestDist) {
                            bestDist = dist;
                            bestMask = mask;
                            bestTarget = target;
                            bestDX = target.x - x;
                            bestDY = target.y - y;
                        }
                    }
                }
            }
            if (bestMask == -1) {
                throw new AssertionError();
            }
            res = new BestMove(bestDX, bestDY, bestTarget, taxi);
            for (int it = 0; it < events; it++) {
                if (((1 << it) & bestMask) != 0) {
                    MyRealMove move = realMoves.get(it);
                    res.useMoves.add(move);
                }
            }
        } else {
//            realMoves.sort(Comparator.comparingInt(m -> m.dx * m.dx + m.dy * m.dy));
            int bestDist = Integer.MAX_VALUE;
            MyObject bestTarget = null;
            if (version < 2) {
//                realMoves = filterMoves(realMoves);
            }
            //            System.err.println(realMoves.size());
            for (MyObject target : targets) {
                int dist = getDistToTarget(taxi, target, realMoves, null);
                if (bestDist > dist) {
                    bestDist = dist;
                    bestTarget = target;
                }
            }
            res = new BestMove(bestTarget, taxi);
            getDistToTarget2(taxi, bestTarget, realMoves, res);
        }
        return res;
    }

    List<MyRealMove> filterMoves(List<MyRealMove> moves) {
        ArrayList<Integer> dist = new ArrayList<>();
        for (MyRealMove move : moves) {
            dist.add(move.dx * move.dx + move.dy * move.dy);
        }
        Collections.sort(dist);
        final int USE_MOVES = 50;
        int limit = dist.size() < USE_MOVES ? Integer.MAX_VALUE : dist.get(USE_MOVES - 1);
        List<MyRealMove> res = new ArrayList<>();
        for (MyRealMove move : moves) {
            if (move.dx * move.dx + move.dy * move.dy <= limit) {
                res.add(move);
            }
        }
        return res;
    }

    long sum = 0;

    int getDistToTarget2(MyTaxi cur, MyObject target, List<MyRealMove> moves, BestMove bestMove) {
        int best = getDistToTarget(cur, target, moves, null);
        List<MyRealMove> bestList = moves;
        for (int it = 0; it < 10; it++) {
            List<MyRealMove> nextMoves = new ArrayList<>();
            double p = rnd.nextDouble() * rnd.nextInt(5) / moves.size();
            for (MyRealMove mv : moves) {
                if (rnd.nextDouble() > p) {
                    nextMoves.add(mv);
                }
            }
            int nDist = getDistToTarget(cur, target, nextMoves, null);
            if (nDist < best) {
                best = nDist;
                bestList = nextMoves;
            }
        }
        return getDistToTarget(cur, target, bestList, bestMove);
    }

    int getDistToTarget(MyTaxi cur, MyObject target, List<MyRealMove> moves, BestMove bestMove) {
        sum += moves.size();
//        System.err.println("TOTAL =  " + sum);
        int x = cur.x, y = cur.y;
        int nowDist = dist(target, x, y);
        for (int i = 0; i < moves.size(); i++) {
            MyRealMove move = moves.get(i);
            int nx = x + move.dx;
            int ny = y + move.dy;
            if (!isValidPos(nx, ny)) {
                continue;
            }
            int nDist = dist(target, nx, ny);
            if (nDist < nowDist) {
                nowDist = nDist;
                x = nx;
                y = ny;
                if (bestMove != null) {
                    bestMove.useMoves.add(move);
                }
            }
        }
        if (bestMove != null) {
            bestMove.nDX = target.x - x;
            bestMove.nDY = target.y - y;
            bestMove.dist = bestMove.nDX * bestMove.nDX + bestMove.nDY * bestMove.nDY;
        }
        return nowDist;
    }

    class MyPeople extends MyTaxi {

        public MyPeople(int id, int x, int y) {
            super(id, x, y);
        }
    }

    List<Move> solve2(TestCase t, int version) {
        double totalScoreUpperBound = 0.0;
        ArrayList<MyMove> res = new ArrayList<>();
        MyPeople[] peoples = new MyPeople[t.people.length];
        for (int i = 0; i < peoples.length; i++) {
            peoples[i] = new MyPeople(i, t.people[i].x, t.people[i].y);
        }
        MyTaxi[] taxis = new MyTaxi[t.taxis.length];
        for (int i = 0; i < t.taxis.length; i++) {
            taxis[i] = new MyTaxi(i, t.taxis[i].x, t.taxis[i].y);
        }
        MyObject[] fanZones = new MyObject[t.fanZones.length];
        for (int i = 0; i < fanZones.length; i++) {
            fanZones[i] = new MyObject(i, t.fanZones[i].x, t.fanZones[i].y);
        }
        int peopleNotReady = peoples.length;
        List<MyObject> fans = new ArrayList<>(Arrays.asList(fanZones));
        while (peopleNotReady > 0) { // x1000
            List<MyObject> notUsedPeoples = new ArrayList<>();
            for (MyObject p : peoples) {
                if (!p.done) {
                    notUsedPeoples.add(p);
                }
            }
            BestMove bestMove = null;
            if (version == 2) {
                for (MyTaxi taxi : taxis) { // x20
                    List<MyObject> targets = taxi.havePassanger ? fans : notUsedPeoples;
                    if (targets.size() == 0) {
                        continue;
                    }
                    if (taxi.bestMove == null || taxi.bestMove.target.done || taxi.eventsDone + 15 > res.size()) {
                        taxi.bestMove = findBestMove(targets, taxi, res, version);
                    }
                    if (taxi.bestMove.compareTo(bestMove) < 0) {
                        bestMove = taxi.bestMove;
                    }
                }
            } else {
//                System.err.println("!!!");
                for (MyTaxi taxi : taxis) { // x20
                    List<MyObject> targets = taxi.havePassanger ? fans : notUsedPeoples;
                    if (targets.size() == 0) {
                        continue;
                    }
                    BestMove move = findBestMove(targets, taxi, res, version);
//                    System.err.println(move.dist);
                    if (move.compareTo(bestMove) < 0) {
                        bestMove = move;
                    }
                }
//                System.err.println("???, got = " + bestMove.dist + " " + bestMove.nDX + " " + bestMove.nDY);
                totalScoreUpperBound += Math.sqrt(bestMove.dist) * 2;
            }
            if (bestMove == null) {
                throw new AssertionError();
            }
            MyTaxi taxi = bestMove.taxi;
            if (taxi.havePassanger) {
                peopleNotReady--;
            } else {
                bestMove.target.done = true;
            }
            taxi.bestMove = null;
            MyRealMove nextMove = new MyRealMove(bestMove.nDX, bestMove.nDY, taxi.id, bestMove.target);
            nextMove.ids.add(taxi.id);
            for (MyRealMove usedMoves : bestMove.useMoves) {
                usedMoves.ids.add(taxi.id);
            }
            res.add(nextMove);
//            if (taxi.havePassanger) {
//                System.err.println("TAXI " + taxi.id + " SHOULD END AT " + bestMove.target.id);
//            } else {
//                System.err.println("TAXI " + taxi.id + " SHOULD TAKE " + bestMove.target.id);
//            }
            taxi.havePassanger = !taxi.havePassanger;
            taxi.eventsDone = res.size();
            taxi.x = bestMove.target.x;
            taxi.y = bestMove.target.y;
        }
        for (int i = 0; i < peoples.length; i++) {
            if (!peoples[i].done) {
                throw new AssertionError();
            }
        }
//        System.err.println("upper = " + totalScoreUpperBound);
//        double cc = 0;
//        for (MyMove myMove : res) {
//            MyRealMove r = (MyRealMove) myMove;
//            cc += Math.sqrt(r.dx * r.dx + r.dy * r.dy) * (1 + r.ids.size() / (0. + t.taxis.length));
//        }
//        System.err.println("cc  = " + cc);
        res = doLocalOpt(res, t, version);
        return fixSolve(res, t);
    }

    double s1 = 0;
    double s2 = 0;

    int vectMul(LocalOptMove a, LocalOptMove b) {
        return a.dx * b.dy - a.dy * b.dx;
    }

    final double eps = 1e-7;

    void restoreMoves(List<LocalOptMove> pts, List<LocalOptMove> oldMoves, int taxiId) {
        for (LocalOptMove mv : pts) {
            mv.pr[taxiId] = 0.0;
        }
        for (LocalOptMove mv : oldMoves) {
            mv.pr[taxiId] = 1.0;
        }
    }

    void optimize(ArrayList<LocalOptMove> pts, int startX, int startY, int taxiId) {
        int finalDX = 0, finalDY = 0;
        List<LocalOptMove> oldMoves = new ArrayList<>();
        double ds1 = 0;
        for (LocalOptMove p : pts) {
            if (p.dx == 0 && p.dy == 0) {
                throw new AssertionError();
            }
            if (p.pr[taxiId] == 1) {
                oldMoves.add(p);
                finalDX += p.dx;
                finalDY += p.dy;
                ds1 += Math.sqrt(p.dx * p.dx + p.dy * p.dy);
            }
            p.pr[taxiId] = 0.0;
        }
        s1 += ds1;
//        System.err.println("s1 = " + s1);
        LocalOptMove finP = new LocalOptMove(finalDX, finalDY);
        ArrayList<LocalOptMove> left = new ArrayList<>();
        ArrayList<LocalOptMove> right = new ArrayList<>();
        for (LocalOptMove p : pts) {
            int vmul = vectMul(finP, p);
            if (vmul == 0) {
                int scal = finP.dx * p.dx + finP.dy * p.dy;
                if (scal > 0) {
                    left.add(p);
                } else {
                    right.add(p);
                }
            } else {
                if (vmul > 0) {
                    left.add(p);
                } else {
                    right.add(p);
                }
            }
        }
        Collections.sort(left, new Comparator<LocalOptMove>() {
            @Override
            public int compare(LocalOptMove p1, LocalOptMove p2) {
                return -vectMul(p1, p2);
            }
        });
        Collections.sort(right, new Comparator<LocalOptMove>() {
            @Override
            public int compare(LocalOptMove p1, LocalOptMove p2) {
                return vectMul(p1, p2);
            }
        });
        if (left.size() == 0 || right.size() == 0) {
            restoreMoves(pts, oldMoves, taxiId);
            s2 += ds1;
            return;
        }
        double realDist = Math.sqrt(finalDX * finalDX + finalDY * finalDY);
        int itLeft = 0, itRight = 0;
        double gotDist = 0;
        while (itLeft < left.size() && itRight < right.size()) {
            LocalOptMove p1 = left.get(itLeft);
            LocalOptMove p2 = right.get(itRight);
            // vectMul(p1 * alpha + (1 - alpha) * p2, finP) == 0
            // (p1.x * alpha + p2.x - p2.x * alpha) * finP.y = finP.x * (p1.y * alpha + p2.y - p2.y * alpha)
            // alpha * ((p1.x - p2.x) * finP.y - finP.x * (p1.y - p2.y)) = finP.x * p2.y - finP.y *p2.x;
            double up = finP.dx * p2.dy - finP.dy * p2.dx;
            double down = finP.dy * (p1.dx - p2.dx) - finP.dx * (p1.dy - p2.dy);
            double alpha = 1;
            if (down != 0) {
                alpha = up / down;
            }
            double xx = p1.dx * alpha + p2.dx * (1 - alpha);
            double yy = p1.dy * alpha + p2.dy * (1 - alpha);
            double vmul = xx * finP.dy - finP.dx * yy;
            if (Math.abs(vmul) > 1e-6) {
                throw new AssertionError();
            }
            double scalMul = xx * finP.dx + yy * finP.dy;
            if (scalMul < 0) {
                System.err.println("need vector = " + finP);
                System.err.println("left = " + p1);
                System.err.println("right = " + p2);
                throw new AssertionError();
            }
            double curD = Math.sqrt(xx * xx + yy * yy);
            double leftCanUse = (alpha < eps) ? Double.MAX_VALUE : (1 - p1.pr[taxiId]) / alpha;
            double rightCanUse = (1 - alpha < eps) ? Double.MAX_VALUE : (1 - p2.pr[taxiId]) / (1 - alpha);
            double canUse = Math.min(leftCanUse, rightCanUse);
            double needUse = (realDist - gotDist) / curD;
            double use = Math.min(needUse, canUse);
            if (use <= 0) {
                throw new AssertionError(canUse + " " + needUse + " " + p1 + " " + p2 + " " + alpha);
            }
            p1.pr[taxiId] += use * alpha;
            p2.pr[taxiId] += use * (1 - alpha);
            gotDist += use * curD;
            if (gotDist >= realDist - eps) {
                break;
            }
            if (p1.pr[taxiId] >= 1) {
                itLeft++;
            }
            if (p2.pr[taxiId] >= 1) {
                itRight++;
            }
        }
        if (Math.abs(gotDist - realDist) > 1) {
            throw new AssertionError(gotDist + " " + realDist + " " + left.size() + " " + right.size());
        }
        double sumX = 0, sumY = 0;
        boolean ok = true;
        for (LocalOptMove p : pts) {
            sumX += p.dx * p.pr[taxiId];
            sumY += p.dy * p.pr[taxiId];
            s2 += p.pr[taxiId] * Math.sqrt(p.dx * p.dx + p.dy * p.dy);
            if (!isValidPos((int) (startX + sumX), (int) (startY + sumY))) {
                ok = false;
                break;
            }
        }
//        System.err.println("ok" + ok);
        if (!ok) {
            restoreMoves(pts, oldMoves, taxiId);
//            System.err.println(":((");
            return;
        }
//        System.err.println("ss = " + s1 + " " + s2);
//        System.err.println(totalUsage + "/" + pts.size());
        if (Math.abs(sumX - finP.dx) > 1 || Math.abs(sumY - finP.dy) > 1) {
            System.err.println("got dist " + gotDist);
            System.err.println("real = " + realDist);
            throw new AssertionError(sumX + " " + finP.dx + " " + sumY + " " + finP.dy);
        }
//        System.err.println(realDist + " " + dist);
//        s1 += dist;
//        s2 += realDist;
    }

    class LocalOptMove {
        int dx, dy;
        double[] pr;
        int id;
        MyObject shouldBeAt;

        LocalOptMove(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        LocalOptMove(MyRealMove move, TestCase t) {
            dx = move.dx;
            dy = move.dy;
            pr = new double[t.taxis.length];
            for (int id : move.ids) {
                pr[id] = 1.0;
            }
            id = move.id;
            this.shouldBeAt = move.shouldBetAt;
        }

        @Override
        public String toString() {
            return dx + " " + dy;
        }
    }

    ArrayList<MyMove> doLocalOpt(ArrayList<MyMove> moves, TestCase t, int version) {
        if (version != 4) {
            return moves;
        }
//        System.err.println("do local opt!");
//        System.err.println("version = " + version);
        List<LocalOptMove> localMoves = new ArrayList<>();
        for (MyMove move : moves) {
            localMoves.add(new LocalOptMove((MyRealMove) move, t));
        }
        s1 = 0;
        s2 = 0;
        for (int i = 0; i < t.taxis.length; i++) {
//            System.err.println("taxi " + i);
            int realX = t.taxis[i].x;
            int realY = t.taxis[i].y;
            int lastX = realX, lastY = realY;
            ArrayList<LocalOptMove> pts = new ArrayList<>();
            for (LocalOptMove move : localMoves) {
                if (move.dx != 0 || move.dy != 0) {
                    pts.add(move);
                    if (move.pr[i] == 1) {
                        realX += move.dx;
                        realY += move.dy;
                    }
                }
                if (move.id == i) {
                    optimize(pts, lastX, lastY, i);
                    lastX = realX;
                    lastY = realY;
                    pts.clear();
                }
            }
        }
//        System.err.println("s1 =" + s1 + ", s2 = " + s2);
        double sumLen = 0;
        for (MyMove mv : moves) {
            MyRealMove real = (MyRealMove) mv;
            sumLen += Math.sqrt(real.dx * real.dx + real.dy * real.dy);
        }
//        System.err.println("sumLen = " + sumLen);
        double sumLenLocal = 0;
        for (LocalOptMove mv : localMoves) {
            sumLenLocal += Math.sqrt(mv.dx * mv.dx + mv.dy * mv.dy);
        }
//        System.err.println("local = " + sumLenLocal);
//        if (version != 4) {
//            return moves;
//        } else {
        return genMovesByOpt(localMoves, t);
//        }
    }

    ArrayList<MyMove> genMovesByOpt(List<LocalOptMove> moves, TestCase t) {
        ArrayList<MyMove> res = new ArrayList<>();
        double sum = 0;
        boolean[] done = new boolean[t.taxis.length];
        for (LocalOptMove mv : moves) {
            Arrays.fill(done, false);
            for (int i = 0; i < mv.pr.length; i++) {
                if (mv.pr[i] == 0 && mv.id != i) {
                    done[i] = true;
                }
            }
            int alreadyDX = 0, alreadyDY = 0;
            while (true) {
                double minP = Double.MAX_VALUE;
                for (int i = 0; i < mv.pr.length; i++) {
                    if (done[i]) {
                        continue;
                    }
                    minP = Math.min(minP, mv.pr[i]);
                }
                if (minP == Double.MAX_VALUE) {
                    break;
                }
                int goDX = (int) Math.round(mv.dx * minP);
                int goDY = (int) Math.round(mv.dy * minP);
                MyObject shouldBeAt = null;
                int id = -1;
                if (!done[mv.id] && mv.pr[mv.id] <= minP) {
                    shouldBeAt = mv.shouldBeAt;
                    id = mv.id;
                }
                MyRealMove nextMove = new MyRealMove(goDX - alreadyDX, goDY - alreadyDY, id, shouldBeAt);
                alreadyDX = goDX;
                alreadyDY = goDY;
                for (int i = 0; i < mv.pr.length; i++) {
                    if (mv.pr[i] <= minP) {
                        done[i] = true;
                    }
                    if (mv.pr[i] >= minP - eps) {
                        nextMove.ids.add(i);
                    }
                }
                res.add(nextMove);
                sum += Math.sqrt(nextMove.dx * nextMove.dx + nextMove.dy * nextMove.dy);
            }
        }
//        System.err.println("sum = " + sum);
        return res;
    }

    int dist(MyObject a, MyObject b) {
        int dx = a.x - b.x;
        int dy = a.y - b.y;
        return dx * dx + dy * dy;
    }

    int dist(MyObject a, int bx, int by) {
        int dx = a.x - bx;
        int dy = a.y - by;
        return dx * dx + dy * dy;
    }

    void printSol(List<Move> moves) {
        out.println(moves.size());
        for (Move move : moves) {
            out.print("MOVE " + move.dx + " " + move.dy + " " + move.ids.length);
            for (int i : move.ids) {
                out.print(" " + (1 + i));
            }
            out.println();
        }
    }

    void checkSol(List<Move> moves, TestCase t) {
        MyObject[] taxies = new MyObject[t.taxis.length];
        for (int i = 0; i < taxies.length; i++) {
            taxies[i] = new MyObject(i, t.taxis[i].x, t.taxis[i].y);
        }
        int[] where = new int[t.people.length];
        Arrays.fill(where, -1);
        boolean[] empty = new boolean[taxies.length];
        Arrays.fill(empty, true);
        for (Move move : moves) {
            Arrays.sort(move.ids);
            for (int i = 1; i < move.ids.length; i++) {
                if (move.ids[i] == move.ids[i - 1]) {
                    throw new AssertionError();
                }
            }
            for (int id : move.ids) {
                taxies[id].x += move.dx;
                taxies[id].y += move.dy;
                if (!taxies[id].isValid()) {
                    throw new AssertionError();
                }
            }
            for (int i = 0; i < taxies.length; i++) {
                for (int j = i + 1; j < taxies.length; j++) {
                    if (dist(taxies[i], taxies[j]) == 0) {
                        throw new AssertionError(i + " " + j + " " + taxies[i].x + " " + taxies[i].y);
                    }
                }
            }
            for (int i = 0; i < where.length; i++) {
                if (where[i] == -2) {
                    continue;
                }
                if (where[i] == -1) {
                    for (int j = 0; j < taxies.length; j++) {
                        if (taxies[j].x == t.people[i].x && taxies[j].y == t.people[i].y && empty[j]) {
                            where[i] = j;
                            empty[j] = false;
//                            System.err.println(i + " -> " + j);
                        }
                    }
                }
                if (where[i] >= 0) {
                    for (int j = 0; j < t.fanZones.length; j++) {
                        if (t.fanZones[j].x == taxies[where[i]].x && t.fanZones[j].y == taxies[where[i]].y) {
                            empty[where[i]] = true;
                            where[i] = -2;
//                            System.err.println(i + " is done!");
                            break;
                        }
                    }
                }
            }
//            System.err.println("move end");
        }
        for (int i : where) {
            if (i != -2) {
                throw new AssertionError();
            }
        }
    }

    Random rnd = new Random(787788);

    Point[] genRandomPoints(HashSet<Point> already, int cnt, int MAX_COORD) {
        Point[] res = new Point[cnt];
        for (int i = 0; i < cnt; i++) {
            Point p = new Point(rnd.nextInt(MAX_COORD * 2 + 1) - MAX_COORD, rnd.nextInt(MAX_COORD * 2 + 1) - MAX_COORD);
            if (already.contains(p)) {
                i--;
                continue;
            }
            already.add(p);
            res[i] = p;
        }
        return res;
    }

    TestCase genRandomTC(boolean max) {
        final int MAX_TAX = 20;
        final int MAX_PEOPLE = 500;
        final int MAX_ZONES = 20;
        int tax = 1 + rnd.nextInt(MAX_TAX);
        int people = 1 + rnd.nextInt(MAX_PEOPLE);
        int zones = 1 + rnd.nextInt(MAX_ZONES);
        if (max) {
            tax = MAX_TAX;
            people = MAX_PEOPLE;
            zones = MAX_ZONES;
        }
        int S = 1 + rnd.nextInt(10000);
        int total = 2 * S + 1;
        total *= total;
        if (tax + people + zones > total) {
            return genRandomTC(max);
        }
        HashSet<Point> already = new HashSet<>();
        Point[] t = genRandomPoints(already, tax, S);
        Point[] p = genRandomPoints(already, people, S);
        Point[] z = genRandomPoints(already, zones, S);
        return new TestCase(t, p, z);
    }

    int solLen(List<Move> solution) {
        int res = 0;
        for (Move move : solution) {
            res += move.ids.length + 2;
        }
        return res;
    }

    void solvexx() {
        long START = System.currentTimeMillis();
        Solver[] solvers = new Solver[]{new GreedySolver(), new SolveV1(), new SolveV2(), new SolveV3(), new SolveV4()};
//        Solver[] solvers = new Solver[]{new GreedySolver(), new SolveV2()};
        double[] sum = new double[solvers.length];
        for (int it = 0; it < 300; it++) {
            System.err.println("it = " + it);
            TestCase t = genRandomTC(false);
            double[] scores = new double[solvers.length];
            double minScore = Double.MAX_VALUE;
            for (int solvIt = 0; solvIt < solvers.length; solvIt++) {
                Solver solver = solvers[solvIt];
                long SOL_START = System.currentTimeMillis();
                List<Move> solution = solver.solve(t);
                long time = (System.currentTimeMillis() - SOL_START);
                System.err.println("done in " + time + "ms");
                checkSol(solution, t);
                scores[solvIt] = calcCost(solution, t.taxis.length);
                minScore = Math.min(minScore, scores[solvIt]);
            }
            for (int i = 0; i < solvers.length; i++) {
                scores[i] = minScore / scores[i];
                System.err.printf("%.4f ", scores[i]);
                sum[i] += scores[i];
            }
            System.err.println();
        }
        for (double s : sum) {
            System.err.printf("%.4f ", s);
        }
        System.err.println();
        System.err.println(System.currentTimeMillis() - START);
    }

    void solve() {
        TestCase t = read();
        long START = System.currentTimeMillis();
        List<Move> bestSol = null;
        double bestScore = Double.MAX_VALUE;
        while (true) {
            long ITER_START = System.currentTimeMillis();
            List<Move> solution = new SolveV4().solve(t);
            double curScore = calcCost(solution, t.taxis.length);
            if (curScore < bestScore) {
                bestScore = curScore;
                bestSol = solution;
//                System.err.println("cur score = " + curScore);
            }
            long cur_time = System.currentTimeMillis();
            long iter_time = (cur_time - ITER_START);
            if (cur_time - START + iter_time < 1850) {
                continue;
            }
            break;
        }
        checkSol(bestSol, t);
        printSol(bestSol);
        System.err.println("score = " + bestScore);
        System.err.println("time = " + (System.currentTimeMillis() - START));
    }

    double calcCost(List<Move> moves, int taxisN) {
        double res = 0;
        for (Move move : moves) {
            double dist = Math.sqrt(move.dx * move.dx + move.dy * move.dy);
            res += dist * (1 + move.ids.length / (taxisN + 0.));
        }
        return res;
    }

    class SolveV1 implements Solver {

        @Override
        public List<Move> solve(TestCase t) {
            return solve2(t, 0);
        }
    }

    class SolveV2 implements Solver {

        @Override
        public List<Move> solve(TestCase t) {
            return solve2(t, 1);
        }
    }

    class SolveV4 implements Solver {

        @Override
        public List<Move> solve(TestCase t) {
            return solve2(t, 4);
        }
    }

    class SolveV3 implements Solver {

        @Override
        public List<Move> solve(TestCase t) {
            return solve2(t, 2);
        }
    }

    class GreedySolver implements Solver {

        @Override
        public List<Move> solve(TestCase t) {
            ArrayList<Move> res = new ArrayList<>();
            MyObject[] peoples = new MyObject[t.people.length];
            for (int i = 0; i < peoples.length; i++) {
                peoples[i] = new MyObject(i, t.people[i].x, t.people[i].y);
            }
            MyObject[] taxis = new MyObject[t.taxis.length];
            for (int i = 0; i < t.taxis.length; i++) {
                taxis[i] = new MyObject(i, t.taxis[i].x, t.taxis[i].y);
            }
            MyObject[] fanZones = new MyObject[t.fanZones.length];
            for (int i = 0; i < fanZones.length; i++) {
                fanZones[i] = new MyObject(i, t.fanZones[i].x, t.fanZones[i].y);
            }
            for (int it = 0; it < t.people.length; it++) {
                MyObject bestTaxi = null, bestPeople = null;
                int bestDist = Integer.MAX_VALUE;
                for (MyObject pe : peoples) {
                    if (pe.done) {
                        continue;
                    }
                    for (MyObject tax : taxis) {
                        int dist = dist(pe, tax);
                        if (dist < bestDist) {
                            bestDist = dist;
                            bestPeople = pe;
                            bestTaxi = tax;
                        }
                    }
                }
                if (bestPeople == null) {
                    throw new AssertionError();
                }
                int dx = bestPeople.x - bestTaxi.x;
                int dy = bestPeople.y - bestTaxi.y;
                doMove(dx, dy, new int[]{bestTaxi.id}, res, taxis, false, t);
                bestDist = Integer.MAX_VALUE;
                MyObject bestFan = null;
                for (MyObject fan : fanZones) {
                    int dist = dist(fan, bestPeople);
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestFan = fan;
                    }
                }
                if (bestFan == null) {
                    throw new AssertionError();
                }
                dx = bestFan.x - bestTaxi.x;
                dy = bestFan.y - bestTaxi.y;
                doMove(dx, dy, new int[]{bestTaxi.id}, res, taxis, false, t);
                bestPeople.done = true;
            }
            return res;
        }
    }

    interface Solver {
        List<Move> solve(TestCase t);
    }


    void run(String testIn, String testOut) {
        try {
            if (testIn == null) {
                in = new FastScanner(System.in);    
            } else {
                in = new FastScanner(new File(testIn));
            }
            
            if (testOut == null) {
                out = new PrintWriter(System.out);
            } else {
                out = new PrintWriter(new File(testOut));
            }

            solve();

            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
        new Solve().run(args.length >= 1 ? args[0] : null, args.length >= 2 ? args[1] : null);
    }
}