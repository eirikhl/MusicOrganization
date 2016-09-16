package eirikhl.musicorganization;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Collection;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Period;
import de.umass.lastfm.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

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
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter username");
        input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Standard Android creation stuf
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

            }
        });
        btnSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnImport.setOnClickListener(new View.OnClickListener() {
            // Get the username to import from, connect to Last.fm, and get the most played artists
            @Override
            public void onClick(View view) {
                general = "Importing...";
                txtOutput.setText(general);
                // Pop-up dialog
                builder.show();
                if(!general.equals("")){
                    user = general;
                    // Connect to Last.fm
                    Caller.getInstance().setUserAgent(user);

                    // Get the API key
                    builder.setTitle("Enter API key");
                    builder.show();

                    chart = User.getTopArtists(user, Period.WEEK, key);
                    for(Artist artist : chart){
                        createArtist(artist);
                    }
                    txtOutput.setText(user);
                }
                else{
                    txtOutput.setText(general);
                }
            }
        });

        // On click event handlers for the pop-up dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ask the user for their username
                general = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                general = "";
                dialog.cancel();
            }
        });
    }

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
                ia.setGenre(artist.getTags());
            }
        });
    }
}
