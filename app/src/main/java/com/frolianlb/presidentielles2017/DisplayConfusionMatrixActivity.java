package com.frolianlb.presidentielles2017;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class DisplayConfusionMatrixActivity extends AppCompatActivity {
    String[] candidates;
    String [] candidateInitials;
    int [][] confusionMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_confusion_matrix);

        // Get the Intent that started this activity and extract the confusion matrix
        // http://stackoverflow.com/questions/12214847/pass-2d-array-to-another-activity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        confusionMatrix = (int [][]) bundle.getSerializable(MainActivity.CONFUSION_MATRIX);

        candidates = new String[]{getResources().getString(R.string.candidate_BH),
                getResources().getString(R.string.candidate_EM),
                getResources().getString(R.string.candidate_FF),
                getResources().getString(R.string.candidate_JLM),
                getResources().getString(R.string.candidate_MLP),
                getResources().getString(R.string.candidate_NDA)};

        candidateInitials = new String[]{"BH", "EM", "FF", "JLM", "MLP", "NDA"};
    }

    void drawConfusionMatrix() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        //Create a new image bitmap and attach a brand new canvas to it
        // http://stackoverflow.com/questions/8445161/android-canvas-drawline-inside-imageview
        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // drawing options
        Paint myPaint = new Paint();
        myPaint.setColor(Color.rgb(0, 0, 0));
        myPaint.setStrokeWidth(10);
        // http://stackoverflow.com/questions/7344497/android-canvas-draw-rectangle
        myPaint.setStyle(Paint.Style.STROKE);

        Paint textPaint = new Paint();
        textPaint.setTextSize((float) (bitmap.getHeight() * 0.1));
        textPaint.setTextAlign(Paint.Align.CENTER);

        // dimensions
        float offsetBottom = (float) (.2 * bitmap.getHeight());
        float offsetLeft = (float) (.3 * bitmap.getWidth());
        float blockWidth = (bitmap.getWidth() - offsetLeft) / confusionMatrix.length;
        float blockHeight = (bitmap.getHeight() - offsetBottom) / confusionMatrix.length;

        // computing the total answers for each candidate
        int[] totalAnswers = new int[candidates.length];
        for (int i=0; i<candidates.length; i++) {
            totalAnswers[i] = 0;
        }

        // draw the confusion matrix
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix[i].length; j++) {
                //Draw block
                canvas.drawRect(i * blockWidth + offsetLeft, j*blockHeight,
                        (i+1) * blockWidth + offsetLeft, (j+1) * blockHeight, myPaint);
                canvas.drawText(Integer.toString(confusionMatrix[i][j]),
                        (float) (i + 0.5) * blockWidth + offsetLeft, (float) (j+0.75) * blockHeight,
                        textPaint);
            }

        }

        // drawing vertical labels
        Paint labelPaint = new Paint();
        labelPaint.setTextSize((float) (bitmap.getHeight() * 0.03));
        labelPaint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i< confusionMatrix.length; i++) {
            float x = (float) offsetLeft / 2;
            float y = (float) (i + 0.5) * blockHeight;
            for (String elem: candidates[i].split("\n")) {

                canvas.drawText(elem,
                        x, y,
                        labelPaint);
                y += labelPaint.getTextSize() * 1;
            }
        }
        // drawing horizontal labels

        for (int i = 0; i< confusionMatrix.length; i++) {
            float x = (float) (offsetLeft + (i + 0.5) * blockWidth);
            float y = (float) 6.5 * blockHeight;
            canvas.drawText(candidateInitials[i],
                    x, y,
                    labelPaint);
        }

        canvas.drawText("Bonne réponse",
                offsetLeft + candidates.length * blockWidth / 2,
                (float) 7 * blockHeight,
                labelPaint);
        imageView.setImageBitmap(bitmap);
    }

    public void onWindowFocusChanged(boolean hasFocus)  {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            drawConfusionMatrix();
        }
    }
}


