package upvictoria.pm_ene_abr_2024.iti_271164.pe1u3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

public class VennDiagramView extends View {
    private Paint paint;
    private int numCircles;
    private int[] colors;
    private String[] setNames;

    private int[] elementCounts; // Arreglo para almacenar el número de elementos de cada conjunto
    private int[] intersectionElementCounts; // Arreglo para almacenar el número de elementos en común entre las intersecciones
    private int[] intersectionCounts;
    private Set<String>[] elementSets; // Conjuntos para almacenar los elementos de cada conjunto
    public VennDiagramView(Context context) {
        super(context);
        init();
    }

    public VennDiagramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        numCircles = 6; // Se cambia el número de círculos a 6
        colors = new int[]{
                Color.argb(128, 255, 0, 0),
                Color.argb(128, 0, 255, 0),
                Color.argb(128, 0, 0, 255),
                Color.argb(128, 255, 255, 0),
                Color.argb(128, 255, 0, 255), // Se agrega un nuevo color
                Color.argb(128, 0, 255, 255)  // Se agrega otro nuevo color
        };
        setNames = generateSequentialSetNames(numCircles);

        // Inicializa los conjuntos de elementos
        elementSets = new Set[numCircles];
        for (int i = 0; i < numCircles; i++) {
            elementSets[i] = new HashSet<>();
        }
    }

    private String[] generateSequentialSetNames(int numSets) {
        String[] sequentialSetNames = new String[numSets];
        char currentChar = 'A';
        for (int i = 0; i < numSets; i++) {
            sequentialSetNames[i] = "Set " + currentChar;
            currentChar++;
        }
        return sequentialSetNames;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        float ovalWidth = width / 2f;
        float ovalHeight = height / 2f;

        float distance = ovalWidth / 2f;
        float offsetX = width / 2f;
        float offsetY = height / 2f;

        float[] centersX = {
                offsetX - distance / 2,
                offsetX + distance,
                offsetX - distance / 2,
                offsetX + distance / 2,
                offsetX - distance / 2,
                offsetX + distance / 2
        };

        float[] centersY = {
                offsetY - distance / 2,
                offsetY - distance / 2,
                offsetY + distance / 2,
                offsetY + distance / 2,
                offsetY - distance,
                offsetY - distance
        };

        // Dibujar los círculos
        for (int i = 0; i < numCircles; i++) {
            paint.setColor(colors[i % colors.length]);
            float left = centersX[i] - ovalWidth / 2;
            float top = centersY[i] - ovalHeight / 2;
            float right = centersX[i] + ovalWidth / 2;
            float bottom = centersY[i] + ovalHeight / 2;
            canvas.drawOval(left, top, right, bottom, paint);

            if (setNames != null && setNames.length > i && setNames[i] != null) {
                paint.setColor(Color.BLACK);
                paint.setTextSize(30);
                float textWidth = paint.measureText(setNames[i]);
                float textX = centersX[i] - textWidth / 2;
                float textY = (i < 4) ? (centersY[i] - ovalHeight / 2 - 40) : (centersY[i] + ovalHeight / 2 + 40);
                canvas.drawText(setNames[i], textX, textY, paint);

                // Dibujar el número de elementos en el centro del óvalo
                if (elementCounts != null && elementCounts.length > i) {
                    String countText = String.valueOf(elementCounts[i]);
                    float countTextWidth = paint.measureText(countText);
                    float countTextX = centersX[i] - countTextWidth / 2;
                    float countTextY = centersY[i] + (paint.descent() + paint.ascent()) / 2;
                    canvas.drawText(countText, countTextX, countTextY, paint);
                }
            }
        }

        // Dibujar los contadores de intersecciones
        paint.setColor(Color.RED);
        paint.setTextSize(30);

        if (intersectionCounts != null && intersectionCounts.length > 0) {
            int index = 0;
            for (int i = 0; i < numCircles; i++) {
                for (int j = i + 1; j < numCircles; j++) {
                    float intersectionX = (centersX[i] + centersX[j]) / 2;
                    float intersectionY = (centersY[i] + centersY[j]) / 2;

                    // Asegurar que el índice esté dentro de los límites del array intersectionCounts
                    if (index < intersectionCounts.length) {
                        String intersectionText = String.valueOf(intersectionCounts[index]);
                        float textWidth = paint.measureText(intersectionText);
                        float textX = intersectionX - textWidth / 2;
                        float textY = intersectionY + (paint.descent() + paint.ascent()) / 2;
                        canvas.drawText(intersectionText, textX, textY, paint);
                    }
                    index++;
                }
            }
        }
    }



    private int getIntersectionIndex(int i, int j) {
        if (i < j) {
            return numCircles * i + j - i * (i + 1) / 2 - 1;
        } else {
            return numCircles * j + i - j * (j + 1) / 2 - 1;
        }
    }


    public void setNumCircles(int numCircles) {
        this.numCircles = numCircles;
        invalidate();
    }

    public int getNumCircles() {
        return numCircles;
    }

    public void setIntersectionCounts(int[] intersectionCounts) {
        this.intersectionCounts = intersectionCounts;
        invalidate(); // Vuelve a dibujar la vista para reflejar los cambios
    }

    public void setColors(int[] colors) {
        if (colors != null) {
            this.colors = new int[colors.length];
            for (int i = 0; i < colors.length; i++) {
                int alpha = 128;
                int red = Color.red(colors[i]);
                int green = Color.green(colors[i]);
                int blue = Color.blue(colors[i]);
                this.colors[i] = Color.argb(alpha, red, green, blue);
            }
        }
        invalidate();
    }

    public String[] getSetNames() {
        return setNames;
    }

    public void setSetNames(String[] setNames) {
        this.setNames = setNames;
        invalidate();
    }

    public void setElementCounts(int[] elementCounts) {

        this.elementCounts = elementCounts;
        invalidate();
    }

    public void setIntersectionElementCounts(int[] intersectionElementCounts) {
        this.intersectionElementCounts = intersectionElementCounts;
        invalidate();
    }
}
