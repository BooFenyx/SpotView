package fr.ul.spotview.donnees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Piece implements Serializable {

    private HashMap<Photo.Orientation, Photo> listePhoto;       //La liste des 4 photos de l'image de chaque cardinalité
    private String nom;                                         //Le nom de la pièce
    private int posListe;                                       //Sa position dans la liste

    // Constructeur par défaut pour Gson
    public Piece() {
        this.listePhoto = new HashMap<>();
        // Initialiser les 4 cardinalités avec null
        this.listePhoto.put(Photo.Orientation.NORD, null);
        this.listePhoto.put(Photo.Orientation.SUD, null);
        this.listePhoto.put(Photo.Orientation.EST, null);
        this.listePhoto.put(Photo.Orientation.OUEST, null);
        this.nom = "";
        this.posListe = -1;
    }

    /**
     * Constructeur créant une pièce pour le modèle associé.
     * @param nom Le nom de la pièce
     */
    public Piece(String nom) {
        this();
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


    /**
     * Fonction qui retourne la liste des photos de la pièce
     * @return La liste de photos
     */
    public HashMap<Photo.Orientation, Photo> getListePhoto(){
        return this.listePhoto;
    }

    /**
     * Fonction modifiant la liste photo
     * @param list
     */
    public void setListePhoto(HashMap<Photo.Orientation, Photo> list){
        this.listePhoto = list;
    }

    /**
     * Fonction qui permet de mettre une photo dans la liste des photos
     * @param o L'orientation
     * @param p La photo
     */
    public void putPhoto(Photo.Orientation o, Photo p){
        this.listePhoto.put(o, p);
    }

    /**
     * Fonction mettant en place la position dans la liste de la pièce
     * @param pl La position dans la liste
     */
    public void setPosListe(int pl){
        this.posListe = pl;
    }

    /**
     * Fonction retournant la position dans la liste de la pièce
     * @return La position dans la liste
     */
    public int getPosListe(){
        return posListe;
    }

    public String toString(){
        return this.nom;
    }
}
