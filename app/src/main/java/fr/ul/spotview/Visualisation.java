package fr.ul.spotview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import fr.ul.spotview.donnees.Acces;
import fr.ul.spotview.donnees.Modele;
import fr.ul.spotview.donnees.Photo;
import fr.ul.spotview.donnees.Piece;

public class Visualisation extends AppCompatActivity implements View.OnClickListener{

    private ImageButton btnMenu;
    private ArrayList<Modele> listeModeleRetour;
    private int posMRetour;
    private Modele modeleVisu;
    private Piece pEnCoursVisu;
    private Photo.Orientation oriEnCoursVisu;
    private ImageView imageEnCours;
    private DrawingView drawingView;
    private TextView v_modeleName, v_pieceName, v_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visualisation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Variables Utiles
        imageEnCours = findViewById(R.id.v_image);
        drawingView = findViewById(R.id.drawingView);
        v_modeleName = findViewById(R.id.v_modeleName);
        v_pieceName = findViewById(R.id.v_pieceName);
        v_text = findViewById(R.id.v_text);

        //Assignation des écouteurs
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.v_leftB).setOnClickListener(this);
        findViewById(R.id.v_rightB).setOnClickListener(this);

        //Récupération de la liste de modèle et le modèle, pièce et ori pour la visu
        Bundle extras = getIntent().getExtras();
        this.listeModeleRetour = (ArrayList<Modele>) extras.get("list");
        this.posMRetour = (int) extras.get("pos");
        this.modeleVisu = (Modele) extras.get("modele");
        this.oriEnCoursVisu = (Photo.Orientation) extras.get("ori");
        this.pEnCoursVisu = (Piece) extras.get("pieceD");

        v_modeleName.setText(modeleVisu.getNom());

        setImageEnCours();

        drawingView.setOnAccesClickListener(new DrawingView.OnAccesClickListener() {
            @Override
            public void onAccesClicked(Acces acces) {
                pEnCoursVisu = acces.getDestination();
                setImageEnCours();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            Intent intentSM = new Intent(this, SelectionModele.class);
            intentSM.putExtra("list", this.listeModeleRetour);
            intentSM.putExtra("modele", this.modeleVisu);
            intentSM.putExtra("pos", this.posMRetour);
            startActivity(intentSM);
        }
        if(v.getId() == R.id.v_leftB){
            switch(oriEnCoursVisu){
                case NORD:
                    oriEnCoursVisu = Photo.Orientation.OUEST;
                    setImageEnCours();
                    break;
                case OUEST:
                    oriEnCoursVisu = Photo.Orientation.SUD;
                    setImageEnCours();
                    break;
                case SUD:
                    oriEnCoursVisu = Photo.Orientation.EST;
                    setImageEnCours();
                    break;
                case EST:
                    oriEnCoursVisu = Photo.Orientation.NORD;
                    setImageEnCours();
                    break;
            }
        }
        if(v.getId() == R.id.v_rightB){
            switch(oriEnCoursVisu){
                case NORD:
                    oriEnCoursVisu = Photo.Orientation.EST;
                    setImageEnCours();
                    break;
                case EST:
                    oriEnCoursVisu = Photo.Orientation.SUD;
                    setImageEnCours();
                    break;
                case SUD:
                    oriEnCoursVisu = Photo.Orientation.OUEST;
                    setImageEnCours();
                    break;
                case OUEST:
                    oriEnCoursVisu = Photo.Orientation.NORD;
                    setImageEnCours();
                    break;
            }
        }
    }

    public void setImageEnCours(){
        switch (oriEnCoursVisu){
            case NORD:
                v_text.setText("Orientation : N");
                break;
            case SUD:
                v_text.setText("Orientation : S");
                break;
            case EST:
                v_text.setText("Orientation : E");
                break;
            case OUEST:
                v_text.setText("Orientation : W");
                break;
        }
        ArrayList<Acces> aa = new ArrayList<>();
        Photo p = pEnCoursVisu.getListePhoto().get(oriEnCoursVisu);
        Uri uriImage = Uri.parse(p.getCheminImage());
        v_pieceName.setText(pEnCoursVisu.getNom());
        for(Piece pi: modeleVisu.getListePiece()){
            if(pi.getNom().equals(pEnCoursVisu.getNom())){
                aa = pi.getListePhoto().get(oriEnCoursVisu).getListeAcces();
            }
        }
        drawingView.setAccesList(aa);
        imageEnCours.setImageURI(uriImage);
    }
}