package fr.umontpellier.iut.rails.vues.layout;

import javafx.scene.media.AudioClip;

import java.io.File;

public class SoundEffect {

    private final AudioClip buttonSoundEffect;
    private final AudioClip MenuMusic;
    private final AudioClip piocherCartePile;
    private final AudioClip piocherCarteVisible;
    private final AudioClip placerCarte;
    private final AudioClip sonVictoire;
    private final AudioClip startGame;
    private final AudioClip errorSound;
    private final AudioClip winnerSound;

    private final static String PATH = "src/main/resources/music/";

    public SoundEffect() {
        buttonSoundEffect = new AudioClip(new File(PATH + "peutEtreAppuyeButton.mp3").toURI().toString());
        MenuMusic = new AudioClip(new File(PATH + "MenuMusic.mp3").toURI().toString());
        piocherCartePile = new AudioClip(new File(PATH + "piocherCartePile.mp3").toURI().toString());
        piocherCarteVisible = new AudioClip(new File(PATH + "piocherCarteVisible.mp3").toURI().toString());
        placerCarte = new AudioClip(new File(PATH + "placerCarte.mp3").toURI().toString());
        sonVictoire = new AudioClip(new File(PATH + "sonVictoire.mp3").toURI().toString());
        startGame = new AudioClip(new File(PATH + "startGame.mp3").toURI().toString());
        errorSound = new AudioClip(new File(PATH + "buzz4.mp3").toURI().toString());
        winnerSound = new AudioClip(new File(PATH + "sonVictoire.mp3").toURI().toString());
    }

    public AudioClip getButtonSoundEffect() {
        return buttonSoundEffect;
    }

    public AudioClip getWinnerSound() {
        return winnerSound;
    }

    public AudioClip getMenuMusic() {
        return MenuMusic;
    }

    public AudioClip getPiocherCartePile() {
        return piocherCartePile;
    }

    public AudioClip getPiocherCarteVisible() {
        return piocherCarteVisible;
    }


    public AudioClip getErrorSound() {
        return errorSound;
    }

    public AudioClip getPlacerCarte() {
        return placerCarte;
    }

    public AudioClip getSonVictoire() {
        return sonVictoire;
    }

    public AudioClip getStartGame() {
        return startGame;
    }
}
