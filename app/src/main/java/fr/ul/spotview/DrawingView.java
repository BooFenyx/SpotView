package fr.ul.spotview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import fr.ul.spotview.donnees.Acces;

public class DrawingView extends View {

    // Interface de callback pour le dessin d'un accès
    public interface OnAccesDrawnListener {
        void onAccesDrawn(Acces acces);
    }

    private OnAccesDrawnListener listener;

    // Coordonnées de départ et d'arrivée
    private float startX, startY, currentX, currentY;
    private boolean drawing = false;
    private boolean editable = false;
    private Paint paint;
    private ArrayList<Acces> accesList = new ArrayList<>();

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        setClickable(editable);
    }

    // Ajoute un nouvel accès à la liste et redessine la vue
    public void addAcces(Acces acces) {
        accesList.add(acces);
        invalidate();
    }

    // Permet de définir ou réinitialiser la liste des accès
    public void setAccesList(ArrayList<Acces> accesList) {
        this.accesList = accesList;
        invalidate();
    }

    public void setOnAccesDrawnListener(OnAccesDrawnListener listener) {
        this.listener = listener;
    }

    // Active ou désactive le mode dessin
    public void setDraw(boolean b) {
        editable = b;
        setClickable(b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Dessine tous les accès enregistrés
        for (Acces acces : accesList) {
            float left = acces.getX();
            float top = acces.getY();
            float right = acces.getX() + acces.getLargeur();
            float bottom = acces.getY() + acces.getHauteur();
            canvas.drawRect(left, top, right, bottom, paint);
        }

        // Dessine le rectangle en cours si l'utilisateur est en train de dessiner
        if (drawing) {
            float left = Math.min(startX, currentX);
            float top = Math.min(startY, currentY);
            float right = Math.max(startX, currentX);
            float bottom = Math.max(startY, currentY);
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Si le mode dessin n'est pas activé, on n'interprète pas l'événement tactile
        if (!editable) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                currentX = startX;
                currentY = startY;
                drawing = true;
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                currentY = event.getY();
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                drawing = false;
                currentX = event.getX();
                currentY = event.getY();
                invalidate();

                float left = Math.min(startX, currentX);
                float top = Math.min(startY, currentY);
                float width = Math.abs(startX - currentX);
                float height = Math.abs(startY - currentY);

                // Création de l'objet Acces avec les dimensions calculées
                if (listener != null) {
                    // Remarque : adaptez l'ordre des paramètres selon votre constructeur Acces
                    Acces acces = new Acces(null, left, top, width, height);
                    listener.onAccesDrawn(acces);
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
}
