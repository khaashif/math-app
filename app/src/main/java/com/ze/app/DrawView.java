package com.ze.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawView extends View {

    TextPaint textPaint;
    String text;

    public DrawView(Context context) {
        super(context);
        init(null);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(50);
        textPaint.setColor(Color.BLACK);
        text = "";
    }

    public void getText(String string) {
        text = string;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.drawText(text, 50f, 50f, textPaint);

    }
}
