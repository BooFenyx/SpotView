package fr.ul.spotview;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Visualisation extends AppCompatActivity {

    private ImageButton btnMenu;

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

        //Gestion des variables utiles
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Assignation des écouteurs

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Création et affichage du PopupMenu
                PopupMenu popupMenu = new PopupMenu(Visualisation.this, btnMenu);
                popupMenu.getMenuInflater().inflate(R.menu.app_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_item1){
                            Toast.makeText(Visualisation.this, "Item 1 sélectionné", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if(item.getItemId() == R.id.action_item2){
                            Toast.makeText(Visualisation.this, "Item 2 sélectionné", Toast.LENGTH_SHORT).show();
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
}