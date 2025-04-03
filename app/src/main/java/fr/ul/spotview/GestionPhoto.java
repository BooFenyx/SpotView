package fr.ul.spotview;

import android.Manifest;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.ul.spotview.DrawingView;
import fr.ul.spotview.donnees.Acces;
import fr.ul.spotview.donnees.Modele;
import fr.ul.spotview.donnees.Photo;
import fr.ul.spotview.donnees.Piece;

public class GestionPhoto extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private ArrayList<Modele> listeModele = new ArrayList<>();  //La liste des modèle
    private Photo.Orientation ori;      //L'orientation de la photo en cours
    private Modele modele;              //Le modèle
    private int posM;                   //La position du modèle
    private Piece pSelect;              //La pièce
    private Acces accesEnCours;         //L'accès en train d'être créé
    private ActivityResultLauncher<Intent> launcher;
    private Uri uriImage;
    private ImageView gph_image;
    private File imageFile;
    private Photo photoEnCours;
    private TextView gph_textAcces;
    private ImageButton btnMenu;

    /* Gestion de la bousolle */
    private ImageView gph_boussole;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    // Variables pour stocker les données des capteurs
    private float[] gravity;
    private float[] geomagnetic;
    private float currentDegree = 0f;
    private final float ALPHA = 0.2f; // Coefficient de lissage

    //Gestion des accès
    private ArrayList<Acces> listeAcces = new ArrayList<>();
    private Acces aSelect = null;
    private Piece pSelectAcces;
    private DrawingView drawingView;
    private ArrayAdapter<Acces> adapter;   //Permet d'afficher les accès dans la listeView
    private ArrayAdapter<Piece> adapterChoose; //Permet d'afficher les accès dans la listeView
    private Dialog dialogSuppr;     //Dialog gérant le pop-up de suppression
    private ListView lv;
    private Dialog dialogChoose;    //Dialog gérant le pop up de choix de pièce de destination
    private ListView lvChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_photo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Gestion des variables utiles
        gph_textAcces = findViewById(R.id.gph_textAcces);
        gph_textAcces.setVisibility(View.GONE);
        gph_image = findViewById(R.id.gph_image);
        drawingView = findViewById(R.id.drawingView);

        //Gestion des écouteurs
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.gph_takeB).setOnClickListener(this);
        findViewById(R.id.gph_addAcc).setOnClickListener(this);
        findViewById(R.id.gph_supprAcc).setOnClickListener(this);


        //Récupération de la liste de modèle et de la position
        Bundle extras = getIntent().getExtras();
        this.listeModele = (ArrayList<Modele>) extras.get("list");
        this.posM = (int) extras.get("pos");
        this.modele = this.listeModele.get(posM);
        this.pSelect = (Piece) extras.get("piece");
        this.ori = (Photo.Orientation) extras.get("ori");
        setTextOri(ori);

        if(pSelect.getListePhoto().get(ori) != null){
            photoEnCours = pSelect.getListePhoto().get(ori);
            if(!photoEnCours.getListeAcces().isEmpty()){
                listeAcces = photoEnCours.getListeAcces();
                drawingView.setAccesList(listeAcces);
            }
        } else{
            photoEnCours = null;
        }
        setImage();

        /* Gestion de la boussole */

        this.gph_boussole = findViewById(R.id.gph_boussole);

        // Obtenir le SensorManager et les capteurs
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        //Remplissage de la liste des pièces
        adapter = new ArrayAdapter<>(this, R.layout.list_item_dark, listeAcces);

        //Gestion des Pop-up: Supprimer
        dialogSuppr = new Dialog(this);

        // Charge le layout    private int posAcces = -1;
        dialogSuppr.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSuppr.setContentView(R.layout.popup_delete_acces);

        // Gérer les vues du popup
        dialogSuppr.findViewById(R.id.popup_cancel).setOnClickListener(this);
        dialogSuppr.findViewById(R.id.popup_confirm).setOnClickListener(this);

        this.lv = dialogSuppr.findViewById(R.id.popup_list);
        this.lv.setAdapter(adapter);

        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                aSelect = listeAcces.get(position);
                TextView tv = dialogSuppr.findViewById(R.id.popup_text);
                tv.setText("Voulez vous supprimer un accès menant à " + aSelect.toString() + " ?");
            }
        });

        //Pop-up Choix
        adapterChoose = new ArrayAdapter<>(this, R.layout.list_item_dark, modele.getListePiece());

        //Gestion des Pop-up: Supprimer
        dialogChoose = new Dialog(this);

        // Charge le layout
        dialogChoose.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogChoose.setContentView(R.layout.popup_choose_acces);

        // Gérer les vues du popup
        dialogChoose.findViewById(R.id.popup_c_cancel).setOnClickListener(this);
        dialogChoose.findViewById(R.id.popup_c_confirm).setOnClickListener(this);

        this.lvChoose = dialogChoose.findViewById(R.id.popup_c_list);
        this.lvChoose.setAdapter(adapterChoose);

        this.lvChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pSelectAcces = modele.getListePiece().get(position);
                TextView tv = dialogChoose.findViewById(R.id.popup_c_text);
                tv.setText("Pièce vers laquelle l'accès mène : " + pSelectAcces);
            }
        });

        // Récupération du bouton menu
        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Création et affichage du PopupMenu
                PopupMenu popupMenu = new PopupMenu(GestionPhoto.this, btnMenu);
                popupMenu.getMenuInflater().inflate(R.menu.app_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_item1){
                            Toast.makeText(GestionPhoto.this, "Item 1 sélectionné", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if(item.getItemId() == R.id.action_item2){
                            Toast.makeText(GestionPhoto.this, "Item 2 sélectionné", Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();
        // Enregistrer les écouteurs des capteurs
        if(accelerometer != null)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        if(magnetometer != null)
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Désenregistrer les écouteurs pour économiser la batterie
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            Intent intentGPP = new Intent(this, GestionPhotosPiece.class);
            this.listeModele.set(this.posM, this.modele);
            intentGPP.putExtra("list", this.listeModele);
            intentGPP.putExtra("pos", this.posM);

            //Ajout de la liste d'accès dans la photo avant de la mettre dans la pièce
            photoEnCours.setListeAcces(listeAcces);
            pSelect.putPhoto(ori, photoEnCours);

            intentGPP.putExtra("piece", this.pSelect);
            startActivity(intentGPP);
        }
        if(v.getId() == R.id.gph_takeB){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
        }
        if(v.getId() == R.id.gph_addAcc){
            if(this.photoEnCours != null){
                gph_boussole.setVisibility(View.GONE);
                gph_boussole.setImageResource(R.drawable.invisible);
                gph_textAcces.setVisibility(View.VISIBLE);
                activerModeDessin();
            } else{
                Toast.makeText(this, "Vous devez prendre une photo avant", Toast.LENGTH_LONG).show();
            }
        }

        if(v.getId() == R.id.gph_supprAcc){
            if(!listeAcces.isEmpty()){
                dialogSuppr.show();
            } else{
                Toast.makeText(this, "Aucun Accès dessiné", Toast.LENGTH_LONG).show();
            }
        }

        /* Bontous du Pop-up */
        if(v.getId() == R.id.popup_cancel){
            dialogSuppr.dismiss();
        }
        if(v.getId() == R.id.popup_confirm){
            if(aSelect != null){
                this.listeAcces.remove(aSelect);
                drawingView.setAccesList(listeAcces);
                deleteConfirm();
                dialogSuppr.dismiss();
                TextView tv = dialogSuppr.findViewById(R.id.popup_text);
                tv.setText("Veuillez choisir une pièce");
                adapter.notifyDataSetChanged();
            } else{
                Toast.makeText(this, "Aucun accès séléctionné", Toast.LENGTH_LONG).show();
            }
        }

        /* Boutons du pop_up */
        if(v.getId() == R.id.popup_c_cancel){
            accesEnCours = null;
            dialogChoose.dismiss();
        }
        if(v.getId() == R.id.popup_c_confirm){
            if(pSelectAcces != null && !pSelect.getNom().equals(pSelectAcces.getNom())){
                dialogChoose.dismiss();
                setPieceAcces();
                TextView tv = dialogChoose.findViewById(R.id.popup_c_text);
                tv.setText("Veuillez choisir une pièce");
            }
            else{
                Toast.makeText(this, "Veuillez choisir une pièce correct", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setTextOri(Photo.Orientation ori){
        TextView textViewOri = findViewById(R.id.textViewOri);
        switch (ori){
            case NORD:
                textViewOri.setText("N");
                break;
            case SUD:
                textViewOri.setText("S");
                break;
            case EST:
                textViewOri.setText("E");
                break;
            case OUEST:
                textViewOri.setText("W");
                break;
        }
    }

    private void setImage(){
        if(photoEnCours != null){
            gph_image.setImageURI(Uri.parse(photoEnCours.getCheminImage()));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = lowPassFilter(event.values.clone(), gravity);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = lowPassFilter(event.values.clone(), geomagnetic);
        }
        if (gravity != null && geomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // Orientation[0] correspond à l'azimut en radians
                float azimuthInRadians = orientation[0];
                float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
                azimuthInDegrees = (azimuthInDegrees + 360) % 360;

                // Animation de rotation pour l'imageView
                RotateAnimation ra = new RotateAnimation(
                        currentDegree,
                        -azimuthInDegrees,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                ra.setDuration(250);
                ra.setFillAfter(true);
                if(gph_boussole.getVisibility() != View.GONE){
                    gph_boussole.startAnimation(ra);
                }
                currentDegree = -azimuthInDegrees;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Méthode de filtre passe-bas pour lisser les données du capteur
    private float[] lowPassFilter(float[] input, float[] output) {
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                imageFile = createImageFile();
                if (imageFile != null) {
                    uriImage = FileProvider.getUriForFile(this, "com.example.cameracapture.provider", imageFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
                    cameraLauncher.launch(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return new File(storageDir, imageFileName);
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && uriImage != null) {
                    displayImage();
                }
            }
    );

    private void displayImage() {
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uriImage);
            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
            gph_image.setImageBitmap(bitmap);
            photoEnCours = new Photo(ori, uriImage.toString());
            listeAcces.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    private void activerModeDessin() {
        // Active le mode dessin en utilisant votre méthode setDraw(boolean)
        drawingView.setDraw(true);
        drawingView.setOnAccesDrawnListener(new DrawingView.OnAccesDrawnListener() {
            @Override
            public void onAccesDrawn(Acces acces) {
                accesEnCours = acces;
                // Désactive le mode dessin après le dessin
                drawingView.setDraw(false);
                gph_textAcces.setVisibility(View.GONE);
                gph_boussole.setVisibility(View.VISIBLE);
                gph_boussole.setImageResource(R.drawable.compass_white);
                dialogChoose.show();
            }
        });
    }

    private void setPieceAcces(){
        accesEnCours.setDestination(pSelectAcces);
        listeAcces.add(accesEnCours);
        drawingView.setAccesList(listeAcces);
        pSelectAcces = null;
    }

    public void deleteConfirm(){
        //On mets à jour la listView
        this.adapter.notifyDataSetChanged();

        //On reset
        this.aSelect = null;
    }
}