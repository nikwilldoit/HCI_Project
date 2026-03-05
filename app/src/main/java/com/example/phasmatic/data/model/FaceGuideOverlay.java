package com.example.phasmatic.data.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.example.phasmatic.ui.RegisterActivity;

public class FaceGuideOverlay extends View {

    private Paint ovalPaint;
    private Paint arrowPaint;

    private boolean showLeft = true;
    private boolean showRight = true;
    private boolean showUp = true;
    private boolean showDown = true;

    private RegisterActivity.FaceAction currentAction;

    private boolean completed = false;

    public FaceGuideOverlay(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);

        ovalPaint = new Paint();
        ovalPaint.setStyle(Paint.Style.STROKE);
        ovalPaint.setStrokeWidth(8f);

        arrowPaint = new Paint();
        arrowPaint.setColor(0xFFFFFFFF);
        arrowPaint.setStrokeWidth(6f);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        RectF oval = new RectF(
                width * 0.25f,
                height * 0.20f,
                width * 0.75f,
                height * 0.80f
        );

        if(completed)
            ovalPaint.setColor(0xFF00FF00); // green
        else
            ovalPaint.setColor(0xFFFF0000); // red

        canvas.drawOval(oval, ovalPaint);

        if(showLeft)
            canvas.drawLine(width*0.2f,height*0.5f,width*0.3f,height*0.5f,arrowPaint);

        if(showRight)
            canvas.drawLine(width*0.8f,height*0.5f,width*0.7f,height*0.5f,arrowPaint);

        if(showUp)
            canvas.drawLine(width*0.5f,height*0.15f,width*0.5f,height*0.25f,arrowPaint);

        if(showDown)
            canvas.drawLine(width*0.5f,height*0.85f,width*0.5f,height*0.75f,arrowPaint);
    }

    public void setAction(RegisterActivity.FaceAction action){
        this.currentAction = action;
        invalidate();
    }
}