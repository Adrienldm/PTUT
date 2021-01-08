package com.example.matches;

public class ContentProfil {
    private String nom;
    private String age;
    private String localisation;
    private String imageProfil;
    private String id;


    public String getNom() {
        return nom;
    }

    public String getAge() {
        if (age == null)
            return "";
        else
            return age;
    }

    public String getLocalisation() {
        return localisation;
    }

    public String getImageProfil(){
        return imageProfil;
    }

    public ContentProfil(String nom, String age, String localisation, String imageProfil, String id) {
        this.nom = nom;
        this.age = age;
        this.localisation = localisation;
        this.imageProfil = imageProfil;
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public ContentProfil(String nom, String localisation, String imageProfil, String id) {
        this.nom = nom;
        this.localisation = localisation;
        this.imageProfil = imageProfil;
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
