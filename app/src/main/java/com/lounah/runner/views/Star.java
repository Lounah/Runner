package com.lounah.runner.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class Star extends View {


    private int color;
    private int shape; // 0 - circle, 1 - star
    private int x;
    private int y;

    private Paint paint;

    public Star(Context context) {
        super(context);
    }

    public Star(Context context, int x, int y, int shape, int color) {
        super(context);
        paint = new Paint();
        paint.setColor(color);
        this.x = x;
        this.y = y;
        this.color = color;
        this.shape = shape;
    }

    public Star(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shape == 0) {
            drawCircleStar(canvas, paint);
        } else drawStar(canvas, paint);
    }

    private void drawTriangle(int x, int y, int width, int height, boolean inverted, Paint paint, Canvas canvas){

        Point p1 = new Point(x,y);
        int pointX = x + width/2;
        int pointY = inverted?  y + height : y - height;

        Point p2 = new Point(pointX,pointY);
        Point p3 = new Point(x+width,y);


        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(p1.x,p1.y);
        path.lineTo(p2.x,p2.y);
        path.lineTo(p3.x,p3.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    private void drawCircleStar(Canvas canvas, Paint paint) {
        canvas.drawCircle(x, y, 7, paint);
    }

    private void drawStar(Canvas canvas, Paint paint) {
        paint.setStrokeWidth(6f);
        canvas.drawLine(x, y+20, x, y-20, paint);
        canvas.drawLine(x-20, y, x+20, y, paint);
    }
}
