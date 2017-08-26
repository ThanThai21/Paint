package com.esp.paint;

import android.graphics.Paint;
import android.graphics.Path;

public class Element {
    public Path path;
    public Paint paint;

    public Element() {
        this.path = new Path();
        this.paint = new Paint();
    }

    public Element(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
    }
}
