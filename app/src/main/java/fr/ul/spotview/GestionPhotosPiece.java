package fr.ul.spotview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import fr.ul.spotview.donnees.Modele;
import fr.ul.spotview.donnees.Photo;
import fr.ul.spotview.donnees.Piece;

public class GestionPhotosPiece extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Modele> listeModele = new ArrayList<>();  //La liste de modèle
    private int posM;   //La position du modèle
    private Modele modele;  //Le modèle
    private Piece pSelect;   //La pièce sélectionnée
    private ImageButton imageButtonN, imageButtonS, imageButtonE, imageButtonW; //Les différentes diréctions des images de la pièce
    private TextView gpp_textP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_photos_piece);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Gestion des variables utiles
        this.gpp_textP = findViewById(R.id.gpp_textP);

        //Gestion des écouteurs
        findViewById(R.id.imageButtonN).setOnClickListener(this);
        findViewById(R.id.imageButtonS).setOnClickListener(this);
        findViewById(R.id.imageButtonE).setOnClickListener(this);
        findViewById(R.id.imageButtonW).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);

        //Récupération de la liste de modèle et de la position
        Bundle extras = getIntent().getExtras();
        this.listeModele = (ArrayList<Modele>) extras.get("list");
        this.posM = (int) extras.get("pos");
        this.modele = this.listeModele.get(posM);
        this.pSelect = (Piece) extras.get("piece");
        this.gpp_textP.setText("Pièce séléctionné: " + pSelect.toString());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButtonN) {
            Intent intentGPH = new Intent(this, GestionPhoto.class);
            intentGPH.putExtra("ori", Photo.Orientation.NORD);
            intentGPH.putExtra("piece", this.pSelect);
            intentGPH.putExtra("list", this.listeModele);
            intentGPH.putExtra("pos", this.posM);
            startActivity(intentGPH);
        }
        if (v.getId() == R.id.imageButtonS) {
            Intent intentGPH = new Intent(this, GestionPhoto.class);
            intentGPH.putExtra("ori", Photo.Orientation.SUD);
            intentGPH.putExtra("piece", this.pSelect);
            intentGPH.putExtra("list", this.listeModele);
            intentGPH.putExtra("pos", this.posM);
            startActivity(intentGPH);
        }
        if (v.getId() == R.id.imageButtonE) {
            Intent intentGPH = new Intent(this, GestionPhoto.class);
            intentGPH.putExtra("ori", Photo.Orientation.EST);
            intentGPH.putExtra("piece", this.pSelect);
            intentGPH.putExtra("list", this.listeModele);
            intentGPH.putExtra("pos", this.posM);
            startActivity(intentGPH);
        }
        if (v.getId() == R.id.imageButtonW) {
            Intent intentGPH = new Intent(this, GestionPhoto.class);
            intentGPH.putExtra("ori", Photo.Orientation.OUEST);
            intentGPH.putExtra("piece", this.pSelect);
            intentGPH.putExtra("list", this.listeModele);
            intentGPH.putExtra("pos", this.posM);
            startActivity(intentGPH);
        }
        if (v.getId() == R.id.btnBack) {
            Intent intentGP = new Intent(this, GestionPieces.class);
            this.modele.setPieceListePiece(pSelect.getPosListe(), pSelect);
            this.listeModele.set(this.posM, this.modele);
            intentGP.putExtra("list", this.listeModele);
            intentGP.putExtra("pos", this.posM);
            intentGP.putExtra("piece", this.pSelect);
            startActivity(intentGP);
        }
    }
}