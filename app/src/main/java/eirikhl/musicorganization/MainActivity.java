package eirikhl.musicorganization;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Period;
import de.umass.lastfm.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {
    // Prepare Realm for use
    private Realm realm;

    // Variables used for connecting to Last.fm
    private String user;
    private String key;
    /*
    private String password;
    private String secret;
    */
    Collection<Artist> chart;

    // Variables for certain interactive objects
    TextView txtOutput;
    private AlertDialog.Builder builder;
    private EditText input;

    // String for general usage, basically a placeholder
    private String general;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enable Wi-Fi
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);

        // Set initial variables to use
        general = "";

        // Standard Android creation stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Set-up the buttons and the text field used in the app
        Button btnBrowse = (Button) findViewById(R.id.btnBrowse);
        Button btnSuggestion = (Button) findViewById(R.id.btnSuggestion);
        Button btnImport = (Button) findViewById(R.id.btnImport);
        txtOutput = (TextView) findViewById(R.id.txtOutput);


        //Set up Realm configuration
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);


        // On click event handlers for the buttons
        btnBrowse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setContentView(R.layout.activity_browse);
            }
        });

        btnSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Get the username to import from, connect to Last.fm, and get the most played artists
        btnImport.setOnClickListener(new View.OnClickListener() {
            // Get the username to import from, connect to Last.fm, and get the most played artists
            @Override
            public void onClick(View view) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader("resources/username.txt"));
                    user = br.readLine();

                    br = new BufferedReader(new FileReader("resources/apikey.txt"));
                    key = br.readLine();

                    br.close();
                } catch (IOException e){
                    e.printStackTrace();
                }

                // Connect to Last.fm, send a query for the most played artists for the last week
                Caller.getInstance().setUserAgent(user);
                Collection<Artist> artists = User.getTopArtists(user, Period.WEEK, key);
                for(Artist artist : artists){
                    createArtist(artist);
                }
            }
        });
    }


    // Standard Android stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Create a new artist record
    private void createArtist(final Artist artist){
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                ImportedArtist ia = realm.createObject(ImportedArtist.class);
                ia.setName(artist.getName());
                ia.setGenre(artist.getTags().iterator().next());
            }
        });
    }
}
