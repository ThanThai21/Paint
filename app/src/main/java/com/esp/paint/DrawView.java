package com.esp.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DrawView extends View {

    private Element currentElement;
    private Paint currentPaint;

    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private Bitmap backgroundImage;

    private int brushSize = 20;

    private List<Element> elements;
    private Stack<Element> undoStack;
    private Stack<Element> redoStack;

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        elements = new ArrayList<>();
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        setup();
    }

    private void setup() {
        currentElement = new Element();
        currentElement.paint.setColor(paintColor);
        currentElement.paint.setAntiAlias(true);
        currentElement.paint.setStyle(Paint.Style.STROKE);
        currentElement.paint.setStrokeWidth(brushSize);
        currentElement.paint.setStrokeJoin(Paint.Join.ROUND);
        currentElement.paint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (backgroundImage != null) {
            canvas.drawBitmap(backgroundImage, 0, 0, null);
        }
        for (Element element : elements) {
            canvas.drawPath(element.path, element.paint);
        }
        canvas.drawPath(currentElement.path, currentElement.paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentElement.path.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                currentElement.path.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(currentElement.path, currentElement.paint);
                Element e = currentElement;
                elements.add(e);
                undoStack.add(e);
                setup();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void clear() {
        elements.clear();
        undoStack.clear();
        redoStack.clear();
        currentElement.path.reset();
        invalidate();
    }

    public boolean undo() {
        if (undoStack.size() >= 1) {
            Element last = undoStack.pop();
            redoStack.add(last);
        } else {
            return false;
        }
        if (undoStack.size() >= 1) {
            currentElement = undoStack.peek();
        } else {
            currentElement.path.reset();
        }
        elements.remove(elements.size() - 1);
        invalidate();
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        Element element = redoStack.pop();
        undoStack.add(element);
        elements.add(element);
        currentElement = element;
        invalidate();
        return true;
    }

    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bmp;
    }

    public void setImage(@Nullable Bitmap backgroundImage) {
        if (backgroundImage != null) {
            this.backgroundImage = Bitmap.createScaledBitmap(backgroundImage, getWidth(), getHeight(), false);
        } else {
            this.backgroundImage = null;
        }
        invalidate();
    }

    public void setColor(int color) {
        this.paintColor = color;
        currentElement.paint.setColor(color);
    }

    public int getColor() {
        return this.paintColor;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }
}
