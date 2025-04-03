package fr.ul.spotview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import fr.ul.spotview.donnees.Modele;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Launcher de la page de Gestion et la page de Selection d'un modèle
    private ActivityResultLauncher<Intent> launcherGM, launcherSM;

    //Données lors de l'ouverture de l'application
    private ArrayList<Modele> listeModele = new ArrayList<>();
    private ImageButton btnMenu;
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Récupération de la liste de modèle si l'intent est non vide
        if(getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            this.listeModele = (ArrayList<Modele>) extras.get("list");
            int i = 0;
            while(i < this.listeModele.size() && i < 3){
                Modele m = this.listeModele.get(i);
                if(i == 0){
                    ImageButton ib = findViewById(R.id.imageButtonM1);
                    if(m.getImagePath() != null){
                        ib.setImageURI(Uri.parse(m.getImagePath()));
                    }
                    ib.setOnClickListener(view -> bouton1());
                    TextView tv = findViewById(R.id.textViewM1);
                    tv.setText(m.getNom());
                }
                if(i == 1){
                    ImageButton ib = findViewById(R.id.imageButtonM2);
                    if(m.getImagePath() != null){
                        ib.setImageURI(Uri.parse(m.getImagePath()));
                    }
                    ib.setOnClickListener(view -> bouton2());
                    TextView tv = findViewById(R.id.textViewM2);
                    tv.setText(m.getNom());
                }
                if(i == 2){
                    ImageButton ib = findViewById(R.id.imageButtonM3);
                    if(m.getImagePath() != null){
                        ib.setImageURI(Uri.parse(m.getImagePath()));
                    }
                    ib.setOnClickListener(view -> bouton3());
                    TextView tv = findViewById(R.id.textViewM3);
                    tv.setText(m.getNom());
                }
                i++;
            }
        }

        //Gestion des différents objets sur le layout
        findViewById(R.id.btnBack).setVisibility(View.INVISIBLE);
        
        //Mise en place des écouteurs :
        findViewById(R.id.imageButtonCreate).setOnClickListener(this);
        findViewById(R.id.ac_chargerB).setOnClickListener(this);

        // Récupération du bouton menu
        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Création et affichage du PopupMenu
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnMenu);
                popupMenu.getMenuInflater().inflate(R.menu.app_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_item1){
                            Toast.makeText(MainActivity.this, "Impossible d'accédez au dossier de sauvegarde", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if(item.getItemId() == R.id.action_item2){
                            Toast.makeText(MainActivity.this, "Item 2 sélectionné", Toast.LENGTH_SHORT).show();
                            return true;
                        } else{
                            return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        //Verification Systeme fichier
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    /**
     * Fonctions qui sélectionne directement le bon modèle de la page d'accueil
     */
    private void bouton1() {
        Intent intentSM = new Intent(this, SelectionModele.class);
        intentSM.putExtra("list", this.listeModele);
        intentSM.putExtra("modele", this.listeModele.get(0));
        intentSM.putExtra("pos", 0);
        startActivity(intentSM);
    }

    private void bouton2() {
        Intent intentSM = new Intent(this, SelectionModele.class);
        intentSM.putExtra("list", this.listeModele);
        intentSM.putExtra("modele", this.listeModele.get(1));
        intentSM.putExtra("pos", 1);
        startActivity(intentSM);
    }

    private void bouton3() {
        Intent intentSM = new Intent(this, SelectionModele.class);
        intentSM.putExtra("list", this.listeModele);
        intentSM.putExtra("modele", this.listeModele.get(2));
        intentSM.putExtra("pos", 2);
        startActivity(intentSM);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ac_chargerB){
            Intent intentSM = new Intent(this, SelectionModele.class);
            intentSM.putExtra("list", this.listeModele);
            startActivity(intentSM);
        }
        if(v.getId() == R.id.imageButtonCreate){
            Intent intentGM = new Intent(this, GestionModele.class);
            intentGM.putExtra("list", this.listeModele);
            startActivity(intentGM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
