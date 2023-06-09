package fr.umontpellier.iut.rails.vues.layout;

import javafx.scene.media.AudioClip;

import java.io.File;

public class SoundEffect {

    private AudioClip buttonSoundEffect;
    private AudioClip MenuMusic;
    private AudioClip piocherCartePile;
    private AudioClip piocherCarteVisible;
    private AudioClip placerCarte;
    private AudioClip sonVictoire;
    private AudioClip startGame;

    public SoundEffect() {
        buttonSoundEffect = new AudioClip(new File("src/main/resources/music/peutEtreAppuyeButton.mp3").toURI().toString());
        MenuMusic = new AudioClip(new File("src/main/resources/music/MenuMusic.mp3").toURI().toString());
        piocherCartePile = new AudioClip(new File("src/main/resources/music/piocherCartePile.mp3").toURI().toString());
        piocherCarteVisible = new AudioClip(new File("src/main/resources/music/piocherCarteVisible.mp3").toURI().toString());
        placerCarte = new AudioClip(new File("src/main/resources/music/placerCarte.mp3").toURI().toString());
        sonVictoire = new AudioClip(new File("src/main/resources/music/sonVictoire.mp3").toURI().toString());
        startGame = new AudioClip(new File("src/main/resources/music/startGame.mp3").toURI().toString());
    }

    public AudioClip getButtonSoundEffect() {
        return buttonSoundEffect;
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
