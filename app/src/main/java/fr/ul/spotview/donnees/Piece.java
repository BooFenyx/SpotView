package fr.ul.spotview.donnees;

import java.util.ArrayList;
import java.util.HashMap;

public class Piece {

    private HashMap<Photo.Orientation, Photo> listePhoto;       //La liste des 4 photos de l'image de chaque cardinalité
    private Modele modele;                                      //Le modèle
    private String nom;                                         //Le nom de la pièce

    /**
     * Fonction créant une pièce du modèle associé.
     * @param modele Le modèle
     * @param nom Le nom
     */
    public Piece(Modele modele, String nom){
        this.listePhoto = new HashMap<>();
        this.modele = modele;
        this.nom = nom;
    }

    /**
     * Fonction retournant le nom de la pièce
     * @return Le nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Fonction modifiant le nom de la pièce
     * @param nom Le nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
}
