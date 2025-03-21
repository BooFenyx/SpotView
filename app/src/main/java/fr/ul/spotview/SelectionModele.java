package fr.ul.spotview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import fr.ul.spotview.donnees.Modele;

public class SelectionModele extends AppCompatActivity implements View.OnClickListener{

    //Données lors de l'arrivée dans la page
    private ArrayList<Modele> listeModele = new ArrayList<>();
    private Modele mSelect;
    private int pos;
    private ImageView sm_image;
    private TextView sm_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selection_modele);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Mise en place des variables utiles à la page:
        sm_image = findViewById(R.id.sm_image);
        sm_name = findViewById(R.id.sm_name);

        //Mise en place des écouteurs:
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.sm_modifB).setOnClickListener(this);

        //Récupération de la liste de modèle et d'un potentiel modèle si choisi sur la page d'acceuil
        Bundle extras = getIntent().getExtras();
        this.listeModele = (ArrayList<Modele>) extras.get("list");
        if(extras.get("modele") != null){
            this.mSelect = (Modele) extras.get("modele");
            this.pos = (int) extras.get("pos");
            sm_image.setImageURI(Uri.parse(mSelect.getImagePath()));
            sm_name.setText(mSelect.getNom());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            Intent intentAC = new Intent(this, MainActivity.class);
            intentAC.putExtra("list", this.listeModele);
            startActivity(intentAC);
        }
        if(v.getId() == R.id.sm_modifB){
            Intent intentGM = new Intent(this, GestionModele.class);
            intentGM.putExtra("list", this.listeModele);
            intentGM.putExtra("modele", this.mSelect);
            intentGM.putExtra("retour", SelectionModele.class);
            intentGM.putExtra("pos", pos);
            startActivity(intentGM);
        }
    }
}