package eirikhl.musicorganization;

import java.util.Collection;
import io.realm.RealmObject;

public class ImportedArtist extends RealmObject {
    private String name;
    private String genre;
    private int heaviness;
    public void setName(String n){
        name = n;
    }
    public void setGenre(String g){
        genre = g;
    }
    public void setHeaviness(int h){
        heaviness = h;
    }
}
