package eirikhl.musicorganization;

import java.util.Collection;
import io.realm.RealmObject;

public class ImportedArtist extends RealmObject {
    private String name;
    private Collection<String> genre;
    private int heaviness;
    public void setName(String n){
        name = n;
    }
    public void setGenre(Collection<String> g){
        genre = g;
    }
    public void setHeaviness(int h){
        heaviness = h;
    }
}
