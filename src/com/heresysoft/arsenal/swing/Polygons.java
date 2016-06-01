package com.heresysoft.arsenal.swing;

import java.awt.*;

public class Polygons {

    private boolean insideTriangle(Point a, Point b, Point c, Point p) {
        float planeAB = (float) (a.x - p.x) * (b.y - p.y) - (float) (b.x - p.x) * (a.y - p.y);
        float planeBC = (float) (b.x - p.x) * (c.y - p.y) - (float) (c.x - p.x) * (b.y - p.y);
        float planeCA = (float) (c.x - p.x) * (a.y - p.y) - (float) (a.x - p.x) * (c.y - p.y);
        return Math.signum(planeAB) == Math.signum(planeBC) && Math.signum(planeBC) == Math.signum(planeCA);
    }

    private boolean pnpoly(Point[] points, Point p) {
        int i, j;
        boolean c = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if (((points[i].y > p.y) != (points[j].y > p.y)) &&
                    (p.x < (float) (points[j].x - points[i].x) * (float) (p.y - points[i].y) / (float) (points[j].y - points[i].y) + points[i].x))
                c = !c;
        }
        return c;
    }

}
