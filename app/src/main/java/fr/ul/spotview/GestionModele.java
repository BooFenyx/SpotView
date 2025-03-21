package fr.ul.spotview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

import fr.ul.spotview.donnees.Modele;

public class GestionModele extends AppCompatActivity implements View.OnClickListener{

    //Launcher du choix de l'image pour le modèle
    ActivityResultLauncher<Intent> imageLauncher;

    //Données lors de l'arrivée dans la page
    private ArrayList<Modele> listeModele = new ArrayList<>();

    private Modele modeleDejaChargeRetour = null;       //Modèle chargé de l'ancienne activité
    private int pos = -1;        //La positon du modèle
    private Class activiteRetour = null;    //La classe de l'ancienne activité

    private Uri uriImage;       //L'uri de l'image pour le sauvegarder dans le modèle
    private ImageView imageM;   //L'image du modèle de l'activité
    private TextView gm_desc;   //La description du modèle
    private TextView gm_name;   //Le nom du modèle


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_modele);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Mise en place des variables utiles à la page
        imageM = findViewById(R.id.gm_Image);
        gm_desc = findViewById(R.id.gm_descText);
        gm_name = findViewById(R.id.gm_nameText);

        //Mise en place des écouteurs:
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.gm_modifB).setOnClickListener(this);
        findViewById(R.id.gm_saveB).setOnClickListener(this);

        //Récupération de la liste de modèle
        Bundle extras = getIntent().getExtras();
        this.listeModele = (ArrayList<Modele>) extras.get("list");
        if(extras.get("retour") != null){
            this.activiteRetour = (Class) extras.get("retour");
        }
        if(extras.get("modele") !=null){
            this.modeleDejaChargeRetour = (Modele) extras.get("modele");
            this.pos = (int) extras.get("pos");
            imageM.setImageURI(Uri.parse(modeleDejaChargeRetour.getImagePath()));
            gm_desc.setText(modeleDejaChargeRetour.getDescriptif());
            gm_name.setText(modeleDejaChargeRetour.getNom());
            uriImage = Uri.parse(modeleDejaChargeRetour.getImagePath());
        }

        //Gestion des launchers
        imageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                int resCode = o.getResultCode();
                if(resCode == RESULT_OK) {
                    uriImage = o.getData().getData();
                    imageM.setImageURI(uriImage);
                }else{
                    Toast.makeText(GestionModele.this, "Erreur chargement image", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            if(activiteRetour == null){
                //Le bouton de retour qui renvoie vers la dernière activité (ici: MainActiviy)
                Intent intentAC = new Intent(this, MainActivity.class);
                intentAC.putExtra("list", this.listeModele);
                startActivity(intentAC);
            } else{
                //Ici: SelectionModele
                Intent intentAC = new Intent(this, activiteRetour);
                intentAC.putExtra("list", this.listeModele);
                if(modeleDejaChargeRetour != null){
                    intentAC.putExtra("modele", modeleDejaChargeRetour);
                    intentAC.putExtra("pos", pos);
                }
                startActivity(intentAC);
            }
        }
        if(v.getId() == R.id.gm_modifB){
            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            imageLauncher.launch(intent);
        }
        if(v.getId() == R.id.gm_saveB){
            TextView tvN = findViewById(R.id.gm_nameText);
            TextView tvD = findViewById(R.id.gm_descText);
            String nom = tvN.getText().toString();
            String desc = tvD.getText().toString();
            if(pos == -1){
                this.listeModele.add(new Modele(nom, uriImage.toString(), desc));
            } else{
                this.listeModele.set(pos, new Modele(nom, uriImage.toString(), desc));
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("list", this.listeModele);
            startActivity(intent);
        }
    }
}