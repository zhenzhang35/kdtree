import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class PointSET{

    private SET<Point2D> pointSet;

    public PointSET(){
        pointSet = new SET<Point2D>();
    }

    public boolean isEmpty(){
        return pointSet.isEmpty();
    }

    public int size(){
        return pointSet.size();
    }

    public void insert(Point2D p){
        if (p == null) throw new IllegalArgumentException("The argument is null");

        pointSet.add(p);
    }

    public boolean contains(Point2D p){
        return pointSet.contains(p);
    }

    public void draw(){
        for (Point2D p : pointSet){
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect){
        if (rect == null) throw new IllegalArgumentException("The argument is null");

        if (pointSet.isEmpty()) return null;
        SET<Point2D> pointInside = new SET<Point2D>();
        for (Point2D p : pointSet){
            if (rect.contains(p)) pointInside.add(p);
        }
        return pointInside;
    }

    public Point2D nearest(Point2D p){
        if (p == null) throw new IllegalArgumentException("The argument is null");

        if (pointSet.isEmpty()) return null;
        double nearestDistance = 2;
        Point2D nearestPoint = p;
        for (Point2D q : pointSet){
            double distance = p.distanceTo(q);
            if (distance < nearestDistance){
                nearestDistance = distance;
                nearestPoint = q;
            }
        }
        return nearestPoint;
    }

    public static void main(String[] args){
        In in = new In(args[0]);
        PointSET points = new PointSET();
        while (!in.isEmpty()){
            double x = in.readDouble();
            double y = in.readDouble();
            points.insert(new Point2D(x, y));
        }

        double rx0 = StdRandom.uniform();
        double ry0 = StdRandom.uniform();
        Point2D pointRandom = new Point2D(rx0, ry0);
        StdOut.println(pointRandom.toString());

        StdOut.println(points.nearest(pointRandom).toString());

        double rx1 = StdRandom.uniform();
        double rx2 = StdRandom.uniform();
        double ry1 = StdRandom.uniform();
        double ry2 = StdRandom.uniform();
        double rxmin = Math.min(rx1, rx2);
        double rxmax = Math.max(rx1, rx2);
        double rymin = Math.min(ry1, ry2);
        double rymax = Math.max(ry1, ry2);
        RectHV rectRandom = new RectHV(rxmin, rymin, rxmax, rymax);
        StdOut.println(rectRandom.toString());

        Iterable<Point2D> pointsInside = points.range(rectRandom);
        int count = 0;
        for (Point2D p : pointsInside) count++;
        StdOut.println(count);
    }
}