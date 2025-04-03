package fr.ul.spotview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import fr.ul.spotview.donnees.Modele;
import fr.ul.spotview.donnees.Photo;
import fr.ul.spotview.donnees.Piece;

public class GestionModele extends AppCompatActivity implements View.OnClickListener{

    //Launcher du choix de l'image pour le modèle
    ActivityResultLauncher<Intent> imageLauncher;

    //Données lors de l'arrivée dans la page
    private ArrayList<Modele> listeModele = new ArrayList<>();

    private Modele modele = null;       //Modèle chargé de l'ancienne activité
    private int pos = -1;        //La positon du modèle
    private Class activiteRetour = null;    //La classe de l'ancienne activité

    private Uri uriImage = null;       //L'uri de l'image pour le sauvegarder dans le modèle
    private ImageView imageM;   //L'image du modèle de l'activité
    private TextView gm_desc;   //La description du modèle
    private TextView gm_name;   //Le nom du modèle
    private ImageButton btnMenu;


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
        findViewById(R.id.gm_gestB).setOnClickListener(this);

        //Récupération de la liste de modèle
        Bundle extras = getIntent().getExtras();
        this.listeModele = (ArrayList<Modele>) extras.get("list");
        if(extras.get("retour") != null){
            this.activiteRetour = (Class) extras.get("retour");
        }
        if(extras.get("modele") !=null){
            this.modele = (Modele) extras.get("modele");
            this.pos = (int) extras.get("pos");
            if(modele.getImagePath() != null){
                imageM.setImageURI(Uri.parse(modele.getImagePath()));
                uriImage = Uri.parse(modele.getImagePath());
            }
            gm_desc.setText(modele.getDescriptif());
            gm_name.setText(modele.getNom());
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

        // Récupération du bouton menu
        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Création et affichage du PopupMenu
                PopupMenu popupMenu = new PopupMenu(GestionModele.this, btnMenu);
                popupMenu.getMenuInflater().inflate(R.menu.app_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_item1){
                            if(modele != null){
                                SingletonOpenFolder.getInstance().openFolder(GestionModele.this, modele.getNom());
                            } else{
                                Toast.makeText(GestionModele.this, "Aucun modèle enregistré", Toast.LENGTH_LONG).show();
                            }
                            return true;
                        } else if(item.getItemId() == R.id.action_item2){
                            Toast.makeText(GestionModele.this, "Item 2 sélectionné", Toast.LENGTH_SHORT).show();
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
            if(activiteRetour == null){
                //Le bouton de retour qui renvoie vers la dernière activité (ici: MainActiviy)
                Intent intentAC = new Intent(this, MainActivity.class);
                intentAC.putExtra("list", this.listeModele);
                startActivity(intentAC);
            } else{
                //Ici: SelectionModele
                Intent intentAC = new Intent(this, activiteRetour);
                intentAC.putExtra("list", this.listeModele);
                if(modele != null){
                    intentAC.putExtra("modele", modele);
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
            if(!gm_name.getText().toString().isEmpty()) {
                sauvegarderModele();
                saveAndPutInList();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("list", this.listeModele);
                startActivity(intent);
            } else{
                Toast.makeText(this, "Veuillez choisir un nom de modèle", Toast.LENGTH_LONG).show();
            }
        }
        if(v.getId() == R.id.gm_gestB){
            if(!gm_name.getText().toString().isEmpty()){
                saveAndPutInList();
                Intent intentGP = new Intent(this, GestionPieces.class);
                intentGP.putExtra("list", this.listeModele);
                intentGP.putExtra("pos", pos);
                startActivity(intentGP);
            } else{
                Toast.makeText(this, "Veuillez choisir un nom de modèle", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void saveAndPutInList(){
        String uri;
        if(uriImage != null){
            uri = uriImage.toString();
        } else{
            uri = null;
        }
        TextView tvN = findViewById(R.id.gm_nameText);
        TextView tvD = findViewById(R.id.gm_descText);
        String nom = tvN.getText().toString();
        String desc = tvD.getText().toString();
        if(pos == -1){
            this.listeModele.add(new Modele(nom, uri, desc));
            this.pos = this.listeModele.size() - 1;
        } else{
            modele.setNom(nom);
            modele.setDescriptif(desc);
            modele.setImagePath(uri);
            this.listeModele.set(pos, modele);
        }
    }

    // GESTION DE LA SAUVEGARDE DANS UN DOSSIER DEIE

    // Copie un fichier image depuis une URI source dans un dossier dédié
    private Uri copyImageToFolder(Uri sourceUri, String modelName) {
        try {
            // Créer un dossier nommé "modelName_date" directement dans Documents
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), modelName + "_" + currentDate);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            // Définir un nom de fichier unique pour l'image
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            File destFile = new File(destDir, fileName);

            // Copier le contenu de l'image depuis l'URI source vers le nouveau fichier
            InputStream is = getContentResolver().openInputStream(sourceUri);
            OutputStream os = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.close();
            is.close();

            return Uri.fromFile(destFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Méthode pour générer et sauvegarder un fichier JSON dans le même dossier que l'image copiée
    private void writeModelDataToJson(String modelName, Uri imageUri, Modele modele) {
        try {
            // Recréer le dossier de destination dans Documents (sans sous-dossier pour l'application)
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), modelName + "_" + currentDate);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            // Sérialiser l'objet modèle en JSON
            Gson gson = new Gson();
            String json = gson.toJson(modele);

            File jsonFile = new File(destDir, "data.json");
            FileWriter writer = new FileWriter(jsonFile);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode appelée lors de la sauvegarde, qui gère la copie de l'image et la mise à jour de l'URI
    private void sauvegarderModele() {
        // Si une image principale a été sélectionnée, on la copie dans le dossier dédié
        if (uriImage != null) {
            // Copier l'image et mettre à jour la variable globale uriImage
            Uri newImageUri = copyImageToFolder(uriImage, gm_name.getText().toString());
            if (newImageUri != null) {
                modele.setImagePath(newImageUri.toString());
            } else {
                Toast.makeText(this, "Erreur lors de la copie de l'image", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //Sauvegarde des images vers le dossier
         for (Piece p : modele.getListePiece()) {
             HashMap<Photo.Orientation, Photo> listePhoto = p.getListePhoto();
             for(Photo.Orientation o: listePhoto.keySet()){
                 Photo ph = p.getListePhoto().get(o);
                 if(ph != null){
                     Uri newUri = copyImageToFolder(Uri.parse(ph.getCheminImage()), gm_name.getText().toString());
                     if(newUri != null) {
                         ph.setCheminImage(newUri.toString());
                     }
                 }
             }
        }

        // Créer le dossier et récupérer son URI
        Uri folderUri = createFolderAndGetUri(gm_name.getText().toString());

        if (folderUri == null) {
            Toast.makeText(this, "Erreur lors de la création du dossier", Toast.LENGTH_SHORT).show();
            return;
        } else{
            modele.setUriDossier(folderUri.toString());
        }

        writeModelDataToJson(gm_name.getText().toString(), uriImage, modele);
    }

    private Uri createFolderAndGetUri(String modelName) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String folderName = modelName + "_" + currentDate;
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Utiliser FileProvider pour obtenir un URI sécurisé
        return androidx.core.content.FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider", folder);
    }
}