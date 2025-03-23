package fr.ul.spotview;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private Modele mSelect;     //Le modèle sélectionné
    private int pos;            //La position dans la liste du modèle sélectionné
    private ImageView sm_image; //L'image du modèle sélectionné
    private TextView sm_name;   //Le nom du modèle sélectionné
    private ListView sm_list;   //L'affichage de la liste des modèles
    private ArrayAdapter<Modele> adapter;   //Permet d'afficher les modèles dans la listeView
    private Dialog dialogSuppr;     //Dialog gérant le pop-up de suppression

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
        sm_list = findViewById(R.id.gp_list);

        //Mise en place des écouteurs:
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.sm_modifB).setOnClickListener(this);
        findViewById(R.id.sm_supprB).setOnClickListener(this);

        //Récupération de la liste de modèle et d'un potentiel modèle si choisi sur la page d'acceuil
        Bundle extras = getIntent().getExtras();
        this.listeModele = (ArrayList<Modele>) extras.get("list");
        if(extras.get("modele") != null){
            this.mSelect = (Modele) extras.get("modele");
            this.pos = (int) extras.get("pos");
            setModeleSelect(mSelect);
        }

        //Remplissage de la liste de modèle
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listeModele);

        sm_list.setAdapter(adapter);

        sm_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelect = listeModele.get(position);
                pos = position;
                setModeleSelect(mSelect);
            }
        });

        //Gestion des Pop-up: Supprimer

        dialogSuppr = new Dialog(this);

        // Charge le layout
        dialogSuppr.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSuppr.setContentView(R.layout.popup_delete);

        // Gérer les vues du popup
        dialogSuppr.findViewById(R.id.popup_cancel).setOnClickListener(this);
        dialogSuppr.findViewById(R.id.popup_confirm).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            Intent intentAC = new Intent(this, MainActivity.class);
            intentAC.putExtra("list", this.listeModele);
            startActivity(intentAC);
        }
        if(v.getId() == R.id.sm_modifB){
            if(mSelect != null){
                Intent intentGM = new Intent(this, GestionModele.class);
                intentGM.putExtra("list", this.listeModele);
                intentGM.putExtra("modele", this.mSelect);
                intentGM.putExtra("retour", SelectionModele.class);
                intentGM.putExtra("pos", pos);
                startActivity(intentGM);
            } else{
                Toast.makeText(SelectionModele.this, "Aucun modèle sélectionné", Toast.LENGTH_LONG).show();
            }
        }
        if(v.getId() == R.id.sm_supprB){
            if(mSelect != null){
                TextView popup_sm_text = dialogSuppr.findViewById(R.id.popup_text);
                popup_sm_text.setText("Voulez vous vraiment supprimer " + mSelect.getNom() + " ?");
                dialogSuppr.show();
            } else{
                Toast.makeText(SelectionModele.this, "Aucun modèle sélectionné", Toast.LENGTH_LONG).show();
            }
        }

        /* Bontous du Pop-up */
        if(v.getId() == R.id.popup_cancel){
            dialogSuppr.dismiss();
        }
        if(v.getId() == R.id.popup_confirm){
            this.listeModele.remove(pos);
            deleteConfirm();
            dialogSuppr.dismiss();
        }
    }

    public void setModeleSelect(Modele m){
        if(m.getImagePath() != null){
            sm_image.setImageURI(Uri.parse(m.getImagePath()));
        }
        sm_name.setText(m.getNom());
    }

    public void deleteConfirm(){
        //On mets à jour la listView
        adapter.notifyDataSetChanged();

        //On supprime et reset l'affichage du modèle
        this.mSelect = null;
        this.pos = -1;
        sm_name.setText(R.string.sm_nameM);
        sm_image.setImageResource(R.drawable.invisible);
    }
}