package fr.ul.spotview;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import androidx.documentfile.provider.DocumentFile;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import fr.ul.spotview.donnees.Modele;
import fr.ul.spotview.donnees.Photo;
import fr.ul.spotview.donnees.Piece;

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
    private ImageButton btnMenu;
    private Dialog dialogVisu;      //Dialog gérant le pop-up de pré-visualisation
    private ListView lvVisu;
    private ArrayAdapter<Piece> adapterVisu;
    private Piece pSelect;
    private Photo.Orientation oriVisu = null;
    private Button n, s, e, w;

    private static final int PICK_DIRECTORY_REQUEST_CODE = 1; // Code pour le résultat de récupération de modèle

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
        findViewById(R.id.sm_visuB).setOnClickListener(this);
        findViewById(R.id.sm_importB).setOnClickListener(this);

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

        //Gestion du pop-up: Visualiser

        dialogVisu = new Dialog(this);

        // Charge le layout
        dialogVisu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogVisu.setContentView(R.layout.popup_choose_visu);

        // Gérer les vues du popup
        dialogVisu.findViewById(R.id.popup_v_cancel).setOnClickListener(this);
        dialogVisu.findViewById(R.id.popup_v_confirm).setOnClickListener(this);
        n = dialogVisu.findViewById(R.id.popup_v_N);
        n.setOnClickListener(this);
        s = dialogVisu.findViewById(R.id.popup_v_S);
        s.setOnClickListener(this);
        e = dialogVisu.findViewById(R.id.popup_v_E);
        e.setOnClickListener(this);
        w = dialogVisu.findViewById(R.id.popup_v_W);
        w.setOnClickListener(this);

        //Gestion de la liste du popup
        if(mSelect != null){
            adapterVisu = new ArrayAdapter<>(this, R.layout.list_item_dark, mSelect.getListePiece());

            lvVisu = dialogVisu.findViewById(R.id.popup_v_list);
            lvVisu.setAdapter(adapterVisu);

            lvVisu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pSelect = mSelect.getListePiece().get(position);
                    TextView tv = dialogVisu.findViewById(R.id.popup_v_text);
                    tv.setText("La visualisation commencera dans : " + pSelect);
                }
            });
        }

        // Récupération du bouton menu
        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Création et affichage du PopupMenu
                PopupMenu popupMenu = new PopupMenu(SelectionModele.this, btnMenu);
                popupMenu.getMenuInflater().inflate(R.menu.app_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_item1){
                            Toast.makeText(SelectionModele.this, "Item 1 sélectionné", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if(item.getItemId() == R.id.action_item2){
                            Toast.makeText(SelectionModele.this, "Item 2 sélectionné", Toast.LENGTH_SHORT).show();
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
        if(v.getId() == R.id.sm_visuB){
            if(mSelect != null){
                if(verifAvantVisu(mSelect)){
                    dialogVisu.show();
                } else{
                    Toast.makeText(SelectionModele.this, "Le modèle n'est pas correct", Toast.LENGTH_LONG).show();
                }
            } else{
                Toast.makeText(SelectionModele.this, "Aucun modèle sélectionné", Toast.LENGTH_LONG).show();
            }
        }
        if(v.getId() == R.id.sm_importB){
            openFileManager();
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

        /* Boutons du Pop-up: Visu */
        if(v.getId() == R.id.popup_v_cancel){
            dialogVisu.dismiss();
        }
        if(v.getId() == R.id.popup_v_confirm){
            dialogVisu.dismiss();
        }
        if(v.getId() == R.id.popup_v_N){
            if(n.getAlpha() == 1f && s.getAlpha() == 0.5f){
                s.setAlpha(1f);
                e.setAlpha(1f);
                w.setAlpha(1f);
                oriVisu = null;
            } else{
                oriVisu = Photo.Orientation.NORD;
                n.setAlpha(1f);
                s.setAlpha(0.5f);
                e.setAlpha(0.5f);
                w.setAlpha(0.5f);
            }
            Toast.makeText(this, oriVisu + "", Toast.LENGTH_SHORT).show();
        }
        if(v.getId() == R.id.popup_v_S){
            if(s.getAlpha() == 1f && n.getAlpha() == 0.5f){
                n.setAlpha(1f);
                e.setAlpha(1f);
                w.setAlpha(1f);
                oriVisu = null;
            } else{
                oriVisu = Photo.Orientation.SUD;
                s.setAlpha(1f);
                n.setAlpha(0.5f);
                e.setAlpha(0.5f);
                w.setAlpha(0.5f);
            }
            Toast.makeText(this, oriVisu + "", Toast.LENGTH_SHORT).show();
        }
        if(v.getId() == R.id.popup_v_E){
            if(e.getAlpha() == 1f && w.getAlpha() == 0.5f){
                s.setAlpha(1f);
                n.setAlpha(1f);
                w.setAlpha(1f);
                oriVisu = null;
            } else{
                oriVisu = Photo.Orientation.EST;
                e.setAlpha(1f);
                s.setAlpha(0.5f);
                n.setAlpha(0.5f);
                w.setAlpha(0.5f);
            }
            Toast.makeText(this, oriVisu + "", Toast.LENGTH_SHORT).show();
        }
        if(v.getId() == R.id.popup_v_W){
            if(w.getAlpha() == 1f && e.getAlpha() == 0.5f){
                s.setAlpha(1f);
                e.setAlpha(1f);
                n.setAlpha(1f);
                oriVisu = null;
            } else{
                oriVisu = Photo.Orientation.OUEST;
                w.setAlpha(1f);
                s.setAlpha(0.5f);
                e.setAlpha(0.5f);
                n.setAlpha(0.5f);
            }
            Toast.makeText(this, oriVisu + "", Toast.LENGTH_SHORT).show();
        }
    }

    public void setModeleSelect(Modele m){
        if(m.getImagePath() != null){
            sm_image.setImageURI(Uri.parse(m.getImagePath()));
        }
        sm_name.setText(m.getNom());
    }

    public void setModeleImport(){
        if(mSelect.getImagePath() != null){
                sm_image.setImageURI(Uri.parse(mSelect.getImagePath()));
        }
        listeModele.add(mSelect);
        pos = listeModele.size() - 1;
        adapter.notifyDataSetChanged();
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

    /**
     * Fonction vérifiant si le modèle est éligible à la visualisation
     * @param modele Le modèle à vérifier
     * @return Vrai si c'est bon, faux sinon
     */
    public boolean verifAvantVisu(Modele modele){
        return false;
    }

    // Fonction pour ouvrir le gestionnaire de fichiers
    private void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        startActivityForResult(intent, PICK_DIRECTORY_REQUEST_CODE);
    }

    // Méthode pour gérer le résultat (sélection du dossier)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_DIRECTORY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri directoryUri = data.getData();
                // Lancer l'importation avec le fichier data.json
                importModel(directoryUri);
            }
        }
    }

    // Fonction pour importer le modèle depuis le fichier JSON
    private void importModel(Uri directoryUri) {
        // Tu peux utiliser DocumentFile pour accéder aux fichiers dans un dossier
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, directoryUri);

        if (pickedDir != null) {
            DocumentFile file = pickedDir.findFile("data.json");

            if (file != null && file.exists()) {
                // Lire le contenu du fichier data.json et traiter le modèle
                try {
                    InputStream inputStream = getContentResolver().openInputStream(file.getUri());
                    // Lire le fichier JSON et le convertir en objet Java
                    Gson gson = new Gson();
                    Modele modele = gson.fromJson(new InputStreamReader(inputStream), Modele.class);
                    this.mSelect = modele;
                    setModeleImport();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erreur lors de l'importation", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Fichier data.json non trouvé", Toast.LENGTH_SHORT).show();
            }
        }
    }
}