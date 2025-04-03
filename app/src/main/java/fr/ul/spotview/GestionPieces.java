package fr.ul.spotview;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.Objects;

import fr.ul.spotview.donnees.Modele;
import fr.ul.spotview.donnees.Piece;

public class GestionPieces extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Modele> listeModele = new ArrayList<>();
    private ArrayList<Piece> listePiece = new ArrayList<>();
    private int posM;        //La position du modèle
    private Modele modele;  //Le modèle
    private Piece pSelect;   //La pièce sélectionnée
    private TextView gp_nameM;   //Le nom du modèle sélectionné
    private TextView gp_nameSelect; //Le nom de la pièce sélectionnée
    private ListView gp_list;   //L'affichage de la liste des pièces du modèle
    private ArrayAdapter<Piece> adapter;   //Permet d'afficher les pièces dans la listeView
    private Dialog dialogCreate;     //Dialog gérant le pop-up de Création
    private Dialog dialogSuppr;     //Dialog gérant le pop-up de suppression
    private ImageButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_pieces);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Mise en place des variables utiles
        this.gp_list = findViewById(R.id.gp_list);
        this.gp_nameM = findViewById(R.id.gp_nameM);
        this.gp_nameSelect = findViewById(R.id.gp_nameSelect);

        //Gestion des écouteurs
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.gp_createB).setOnClickListener(this);
        findViewById(R.id.gp_supprB).setOnClickListener(this);
        findViewById(R.id.gp_ModifB).setOnClickListener(this);

        //Récupération de la liste de modèle et de la position
        Bundle extras = getIntent().getExtras();
        this.listeModele = (ArrayList<Modele>) extras.get("list");
        this.posM = (int) extras.get("pos");
        this.modele = this.listeModele.get(posM);
        this.listePiece = modele.getListePiece();

        if(extras.get("piece") != null){
            this.pSelect = (Piece) extras.get("piece");
            setSelectPiece(this.pSelect);
        }

        //Affichage du nom du modèle
        gp_nameM.setText(this.modele.getNom());

        //Remplissage de la liste des pièces
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listePiece);

        gp_list.setAdapter(adapter);

        gp_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pSelect = listePiece.get(position);
                pSelect.setPosListe(position);
                setSelectPiece(pSelect);
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

        // --- Créer ---
        dialogCreate = new Dialog(this);

        //Charge le layout
        dialogCreate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCreate.setContentView(R.layout.popup_gp_name);

        //Gérer les vues du popup
        dialogCreate.findViewById(R.id.popup_gp_cancel).setOnClickListener(this);
        dialogCreate.findViewById(R.id.popup_gp_confirm).setOnClickListener(this);

        // Récupération du bouton menu
        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Création et affichage du PopupMenu
                PopupMenu popupMenu = new PopupMenu(GestionPieces.this, btnMenu);
                popupMenu.getMenuInflater().inflate(R.menu.app_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_item1){
                            Toast.makeText(GestionPieces.this, "Item 1 sélectionné", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if(item.getItemId() == R.id.action_item2){
                            Toast.makeText(GestionPieces.this, "Item 2 sélectionné", Toast.LENGTH_SHORT).show();
                            return true;
                        } else{
                            return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            modele.setListePiece(this.listePiece);
            listeModele.set(posM, modele);
            Intent intentGM = new Intent(this, GestionModele.class);
            intentGM.putExtra("list", listeModele);
            intentGM.putExtra("modele", modele);
            intentGM.putExtra("pos", posM);
            startActivity(intentGM);
        }
        if(v.getId() == R.id.gp_createB){
            dialogCreate.show();
        }
        if(v.getId() == R.id.gp_supprB){
            if(pSelect != null){
                TextView popup_text = dialogSuppr.findViewById(R.id.popup_text);
                popup_text.setText("Voulez vous vraiment supprimer " + pSelect.getNom() + " ?");
                dialogSuppr.show();
            } else{
                Toast.makeText(GestionPieces.this, "Aucune pièce sélectionné", Toast.LENGTH_LONG).show();
            }
        }
        if(v.getId() == R.id.gp_ModifB){
            if(pSelect != null){
                modele.setListePiece(this.listePiece);
                Intent intentGPP = new Intent(this, GestionPhotosPiece.class);
                intentGPP.putExtra("list", listeModele);
                intentGPP.putExtra("pos", posM);
                intentGPP.putExtra("piece", pSelect);
                startActivity(intentGPP);
            } else{
                Toast.makeText(GestionPieces.this, "Aucune pièce sélectionné", Toast.LENGTH_LONG).show();
            }
        }

        /* Bontous du Pop-up Supprimer */
        if(v.getId() == R.id.popup_cancel){
            dialogSuppr.dismiss();
        }
        if(v.getId() == R.id.popup_confirm){
            this.listePiece.remove(pSelect.getPosListe());
            deleteConfirm();
            dialogSuppr.dismiss();
        }

        /* Bontous du Pop-up Créer*/
        if(v.getId() == R.id.popup_gp_cancel){
            dialogCreate.dismiss();
        }
        if(v.getId() == R.id.popup_gp_confirm){
            TextView popup_gp_name = dialogCreate.findViewById(R.id.popup_gp_name);
            String name = popup_gp_name.getText().toString();
            boolean sameName = false;
            for(Piece p: listePiece){
                if(Objects.equals(p.getNom(), name)){
                    sameName = true;
                }
            }
            if(!name.isEmpty() && !name.equals(modele.getNom()) && !sameName){
                this.listePiece.add(new Piece(name));
                adapter.notifyDataSetChanged();
                popup_gp_name.setText("");
                popup_gp_name.setHint(R.string.popup_gp_textField);
                dialogCreate.dismiss();
            } else{
                Toast.makeText(this, "Nom de pièce incorrect", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setSelectPiece(Piece p){
        this.gp_nameSelect.setText(p.getNom());
    }

    public void deleteConfirm(){
        //On mets à jour la listView
        adapter.notifyDataSetChanged();

        //On supprime et reset l'affichage de la piece
        this.pSelect = null;
        gp_nameSelect.setText(R.string.gp_nameSelect);
    }
}