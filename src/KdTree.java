import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    
    private Node root;
    private int size;

    public KdTree(){
        root = null;
        size = 0;
    }

    private class Node{
        private Point2D point;
        private RectHV rect;
        private Node left;
        private Node right;
        private boolean orientation;   // true if vertical, false if horizontal

        public Node(Point2D point, RectHV rect, boolean orientation){
            this.point = point;
            this.rect = rect;
            this.orientation = orientation;
        }
    }

    public boolean isEmpty(){
        return root == null;
    }

    public int size(){
        return size;
    }

    public void insert(Point2D p){
        if (p == null) throw new IllegalArgumentException("The argument is null");

        if (isEmpty()){
            RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
            root = new Node(p, rect, true);
            size++;
        } else insert(root, p);
    }

    private void insert(Node node, Point2D p){
        if (p.x() == node.point.x() && p.y() == node.point.y()) return;

        if (node.orientation){   // vertical
            if (p.x() < node.point.x()){   // go left
                if (node.left == null){
                    RectHV rect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.point.x(), node.rect.ymax());
                    node.left = new Node(p, rect, false);
                    size++;
                } else insert(node.left, p);
            } else {   // go right
                if (node.right == null){
                    RectHV rect = new RectHV(node.point.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
                    node.right = new Node(p, rect, false);
                    size++;
                } else insert(node.right, p);
            }
        } else {   // horizontal
            if (p.y() < node.point.y()){   // go left
                if (node.left == null){
                    RectHV rect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.point.y());
                    node.left = new Node(p, rect, true);
                    size++;
                } else insert(node.left, p);
            } else {   // go right
                if (node.right == null){
                    RectHV rect = new RectHV(node.rect.xmin(), node.point.y(), node.rect.xmax(), node.rect.ymax());
                    node.right = new Node(p, rect, true);
                    size++;
                } else insert(node.right, p);
            }
        }
    }

    public boolean contains(Point2D p){
        if (isEmpty()) return false;
        else return contains(root, p);
    }

    private boolean contains(Node node, Point2D p){
        if (node == null) return false;

        if (p.x() == node.point.x() && p.y() == node.point.y()) return true;

        if (node.orientation){   // vertical
            if (p.x() < node.point.x()){   // go left
                return contains(node.left, p);
            } else {   // go right
                return contains(node.right, p);
            }
        } else {   // horizontal
            if (p.y() < node.point.y()){   // go left
                return contains(node.left, p);
            } else {   // go right
                return contains(node.right, p);
            }
        }
    }

    public void draw(){
        StdDraw.line(0.0, 0.0, 1.0, 0.0);
        StdDraw.line(1.0, 0.0, 1.0, 1.0);
        StdDraw.line(1.0, 1.0, 0.0, 1.0);
        StdDraw.line(0.0, 1.0, 0.0, 0.0);

        draw(root);
    }

    private void draw(Node node){
        if (node == null) return;

        if (node.orientation){   // vertical
            StdDraw.setPenRadius(0.002);
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.point.x(), node.rect.ymin(), node.point.x(), node.rect.ymax());
            StdDraw.setPenRadius(0.005);
            StdDraw.setPenColor(StdDraw.BLACK);
            node.point.draw();
        } else {   // horizontal
            StdDraw.setPenRadius(0.002);
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.rect.xmin(), node.point.y(), node.rect.xmax(), node.point.y());
            StdDraw.setPenRadius(0.005);
            StdDraw.setPenColor(StdDraw.BLACK);
            node.point.draw();
        }

        draw(node.left);
        draw(node.right);
    }

    public Iterable<Point2D> range(RectHV rect){
        if (rect == null) throw new IllegalArgumentException("The argument is null");

        if (isEmpty()) return null;
        else {
            SET<Point2D> pointInside = new SET<Point2D>();
            pointInside = range(root, rect, pointInside);
            return pointInside;
        }
    }

    private SET<Point2D> range(Node node, RectHV rect, SET<Point2D> pointInside){
        // the query rectangle does not intersect the rectangle corresponding to a node
        if (node == null || !rect.intersects(node.rect)) return pointInside;

        if (rect.contains(node.point)) pointInside.add(node.point);

        if (node.orientation){   // vertical
            if (node.point.x() >= rect.xmin()){   // go left
                pointInside = range(node.left, rect, pointInside);
            }
            if (node.point.x() <= rect.xmax()){   // go right
                pointInside = range(node.right, rect, pointInside);
            }
        } else {   // horizontal
            if (node.point.y() >= rect.ymin()){   // go left
                pointInside = range(node.left, rect, pointInside);
            }
            if (node.point.y() <= rect.ymax()){   // right
                pointInside = range(node.right, rect, pointInside);
            }
        }

        return pointInside;
    }

    public Point2D nearest(Point2D p){
        if (p == null) throw new IllegalArgumentException("The argument is null");

        if (isEmpty()) return null;
        else {
            Point2D nearestPoint = new Point2D(2.0, 2.0);
            nearestPoint = nearest(root, p, nearestPoint);
            return nearestPoint;
        }
    }

    private Point2D nearest(Node node, Point2D p, Point2D nearestPoint){
        // the closest point discovered so far is closer than the distance 
        // between the query point and the rectangle corresponding to a node
        if (node == null || nearestPoint.distanceTo(p) < node.rect.distanceTo(p)) return nearestPoint;

        if (node.point.distanceTo(p) < nearestPoint.distanceTo(p)) nearestPoint = node.point;

        // choose the subtree that is on the same side of the splitting line as the query point 
        // as the first subtree to explore
        if (node.orientation){   // vertical
            if (p.x() <= node.point.x()){   // go left
                nearestPoint = nearest(node.left, p, nearestPoint);
            }
            if (p.x() >= node.point.x()){   // go right
                nearestPoint = nearest(node.right, p, nearestPoint);
            }
        } else {   // horizontal
            if (p.y() <= node.point.y()){   // go left
                nearestPoint = nearest(node.left, p, nearestPoint);
            }
            if (p.y() >= node.point.y()){   // go right
                nearestPoint = nearest(node.right, p, nearestPoint);
            }
        }

        return nearestPoint;
    }

    public static void main(String[] args){
        In in = new In(args[0]);
        KdTree points = new KdTree();
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
