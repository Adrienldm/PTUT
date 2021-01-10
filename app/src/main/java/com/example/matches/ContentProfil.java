package com.example.matches;

/**
 * Class ContentProfil
 */
public class ContentProfil {
    private String nom;
    private String age;
    private String localisation;
    private String imageProfil;
    private String id;
    private String competence;
    private String description;

    /**
     * Constructeur pour le stage
     *
     * @param titre
     * @param dateDebut
     * @param dateFin
     * @param image_entreprise
     * @param compétenceStage
     * @param descriptionStage
     * @param idEntreprise
     */
    public ContentProfil(String titre, String dateDebut, String dateFin, String image_entreprise, String compétenceStage, String descriptionStage, String idEntreprise) {
        this.nom = titre;
        this.age = dateDebut;
        this.localisation = dateFin;
        this.imageProfil = image_entreprise;
        this.id = idEntreprise;
        this.competence = compétenceStage;
        this.description = descriptionStage;
    }

    /**
     * Constructeur pour un étudiant
     *
     * @param nom
     * @param age
     * @param localisation
     * @param imageProfil
     * @param id
     */
    public ContentProfil(String nom, String age, String localisation, String imageProfil, String id) {
        this.nom = nom;
        this.age = age;
        this.localisation = localisation;
        this.imageProfil = imageProfil;
        this.id = id;
    }

    /**
     * @return les compétences pour le stage
     */
    public String getCompetence() {
        return competence;
    }

    /**
     * @return la decritpion du stage
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return le nom de l'étudiant ou le titre de l'enterprise
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom nom de l'etudiant ou titre de l'entreprise
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return l'age de l'étudiant
     */
    public String getAge() {
        if (age == null)
            return "";
        else
            return age;
    }

    /**
     * @return l'adresse de létudiant
     */
    public String getLocalisation() {
        return localisation;
    }

    /**
     * @return le nom de l'image de l'étudiant ou de l'entreprise
     */
    public String getImageProfil() {
        return imageProfil;
    }

    /**
     * @return l'id de l'étudiant ou de l'entreprise
     */
    public String getId() {
        return id;
    }
}
