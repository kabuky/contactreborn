package com.trunkle.contactreborn;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

public class CustomShapeDrawable extends ShapeDrawable{

	private final Paint fillpaint, strokepaint;
	
	public CustomShapeDrawable(Shape s, int fill, int stroke, int strokeWidth) {
        super(s);
        fillpaint = new Paint(this.getPaint());
        fillpaint.setColor(fill);
        strokepaint = new Paint(fillpaint);
        strokepaint.setStyle(Paint.Style.STROKE);
        strokepaint.setStrokeWidth(strokeWidth);
        strokepaint.setColor(stroke);
    }
 
    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
    	shape.resize(canvas.getClipBounds().right,
                canvas.getClipBounds().bottom);
        shape.draw(canvas, fillpaint);
     
        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, canvas.getClipBounds().right,
                    canvas.getClipBounds().bottom),
                new RectF(strokepaint.getStrokeWidth()/2, 
                		strokepaint.getStrokeWidth()/2, canvas.getClipBounds().right - strokepaint.getStrokeWidth()/2,
                        canvas.getClipBounds().bottom - strokepaint.getStrokeWidth()/2),
                Matrix.ScaleToFit.FILL);
        canvas.concat(matrix);
     
        shape.draw(canvas, strokepaint);
    }
}
