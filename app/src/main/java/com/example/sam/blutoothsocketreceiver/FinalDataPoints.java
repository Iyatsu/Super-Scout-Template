package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class FinalDataPoints extends ActionBarActivity {
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String alliance;
    String dataBaseUrl;
    String allianceScoreData;
    TextView finalScore;
    EditText allianceScore;
    JSONObject superExternalData;
    ArrayList<String> teamOneDataName;
    ArrayList<String> teamOneDataScore;
    ArrayList<String> teamTwoDataName;
    ArrayList<String> teamTwoDataScore;
    ArrayList<String> teamThreeDataName;
    ArrayList<String> teamThreeDataScore;
    Boolean isMute;
    File dir;
    PrintWriter file;
    DatabaseReference firebaseRef;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finaldatapoints);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        intent = getIntent();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getExtrasForFinalData();
        firebaseRef = FirebaseDatabase.getInstance().getReference();

        allianceScore = (EditText) findViewById(R.id.finalScoreEditText);
        finalScore = (TextView)findViewById(R.id.finalScoreTextView);
        superExternalData = new JSONObject();
        allianceScore.setCursorVisible(false);

        if(alliance.equals("Blue Alliance")){
            finalScore.setTextColor(Color.BLUE);
        }else if(alliance.equals("Red Alliance")){
            finalScore.setTextColor(Color.RED);
        }

        allianceScore.setText(allianceScoreData);
        dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data");
    }
    @Override
    public void onBackPressed(){
        final Activity activity = this;
        new AlertDialog.Builder(this)
                .setTitle("WARNING!")
                .setMessage("GOING BACK WILL CAUSE LOSS OF DATA")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.submit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.forgotAllianceScore){
            Toast.makeText(this, "Not Available right now.", Toast.LENGTH_LONG).show();
            /*Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://www.thebluealliance.com/match/2016casj_qm" + numberOfMatch));
            startActivity(intent);*/

        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.Submit) {
            final Activity context = this;
            int score;
            try {
                score = Integer.parseInt(allianceScore.getText().toString());
            } catch (NumberFormatException nfe) {
                Toast.makeText(this, "Invalid score", Toast.LENGTH_LONG).show();
                return false;
            } catch (NullPointerException npe) {
                Toast.makeText(this, "Enter a score", Toast.LENGTH_LONG).show();
                return false;
            }
            final int allianceScoreNum = score;

            //Send the data of the super scout on a separate thread
            new Thread() {
                @Override
                public void run() {
                    try {
                        file = null;
                        //make the directory of the file
                        dir.mkdir();
                        //can delete when doing the actual thing
                        file = new PrintWriter(new FileOutputStream(new File(dir, ("Q" + numberOfMatch + "_"  + new SimpleDateFormat("MM-dd-yyyy-H:mm:ss").format(new Date())))));
                    } catch (IOException IOE) {
                        Log.e("File error", "Failed to open File");
                        return;
                    }
                    try {
                        String JsonStringTeamOne = "{";
                        String JsonStringTeamTwo = "{";
                        String JsonStringTeamThree = "{";
                        Log.i("JSON", String.valueOf(teamOneDataScore.size()));
                        for(int a = 0; a <= teamOneDataScore.size() - 1; a++){
                            JsonStringTeamOne = JsonStringTeamOne + ("\"" + reformatDataNames(teamOneDataName.get(a)) + "\": " + teamOneDataScore.get(a));
                            if(a != teamOneDataScore.size() - 1){
                                JsonStringTeamOne = JsonStringTeamOne + ",";
                            } else {
                                JsonStringTeamOne = JsonStringTeamOne + "}";
                            }
                        }
                        for(int a = 0; a <= teamTwoDataScore.size() - 1; a++){
                            JsonStringTeamTwo = JsonStringTeamTwo + ("\"" + reformatDataNames(teamTwoDataName.get(a)) + "\": " + teamTwoDataScore.get(a));
                            if(a != teamTwoDataScore.size() - 1){
                                JsonStringTeamTwo = JsonStringTeamTwo + ",";
                            } else {
                                JsonStringTeamTwo = JsonStringTeamTwo + "}";
                            }
                        }
                        for(int a = 0; a <= teamThreeDataScore.size() - 1; a++){
                            JsonStringTeamThree = JsonStringTeamThree + ("\"" + reformatDataNames(teamThreeDataName.get(a)) + "\": " + teamThreeDataScore.get(a));
                            if(a != teamThreeDataScore.size() - 1){
                                JsonStringTeamThree = JsonStringTeamThree + ",";
                            } else {
                                JsonStringTeamThree = JsonStringTeamThree + "}";
                            }
                        }
                        JSONObject JsonTeamOne = new JSONObject(JsonStringTeamOne);
                        JSONObject JsonTeamTwo = new JSONObject(JsonStringTeamTwo);
                        JSONObject JsonTeamThree = new JSONObject(JsonStringTeamThree);

                        superExternalData.put("matchNumber", numberOfMatch);
                        superExternalData.put("alliance", alliance);
                        superExternalData.put(alliance + " Score", allianceScoreNum);
                        superExternalData.put(teamNumberOne, JsonTeamOne);
                        superExternalData.put(teamNumberTwo, JsonTeamTwo);
                        superExternalData.put(teamNumberThree, JsonTeamThree);
                        superExternalData.put("teamOne", teamNumberOne);
                        superExternalData.put("teamTwo", teamNumberTwo);
                        superExternalData.put("teamThree", teamNumberThree);
                        //ArrayList<String> rankNames = new ArrayList<>(Arrays.asList("numTimesBeached", "numTimesSlowed", "numTimesUnaffected"));
                    }catch(JSONException JE){
                        Log.e("JSON Error", "couldn't put keys and values in json object");
                    }
                    ArrayList<String> teamNumbers = new ArrayList<>(Arrays.asList(teamNumberOne, teamNumberTwo, teamNumberThree));

                    for (int i = 0; i < teamNumbers.size(); i++){
                        firebaseRef.child("TeamInMatchDatas").child(teamNumbers.get(i) + "Q" + numberOfMatch).child("teamNumber").setValue(Integer.parseInt(teamNumbers.get(i)));
                        firebaseRef.child("TeamInMatchDatas").child(teamNumbers.get(i) + "Q" + numberOfMatch).child("matchNumber").setValue(Integer.parseInt(numberOfMatch));
                    }
                    sendAfterMatchData();

                    System.out.println(superExternalData.toString());

                    file.println(superExternalData.toString());
                    file.close();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Sent Match Data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }.start();
            Intent backToHome = new Intent(context, MainActivity.class);
            if(alliance.equals("Red Alliance")){
                backToHome.putExtra("shouldBeRed", true);
            }else {
                backToHome.putExtra("shouldBeRed", false);
            }
            Log.e("final data alliance", alliance);
            backToHome.putExtra("number", numberOfMatch);
            backToHome.putExtra("mute", isMute);
            startActivity(backToHome);
        }
        return super.onOptionsItemSelected(item);
    }

    public String reformatDataNames(String dataName){
        return ("rank" + dataName.replace(" ", ""));
    }

    public void getExtrasForFinalData(){

        numberOfMatch = intent.getExtras().getString("matchNumber");
        teamNumberOne = intent.getExtras().getString("teamNumberOne");
        teamNumberTwo = intent.getExtras().getString("teamNumberTwo");
        teamNumberThree = intent.getExtras().getString("teamNumberThree");
        alliance = intent.getExtras().getString("alliance");

        teamOneDataName = intent.getStringArrayListExtra("dataNameOne");
        teamOneDataScore = intent.getStringArrayListExtra("ranksOfOne");
        teamTwoDataName = intent.getStringArrayListExtra("dataNameTwo");
        teamTwoDataScore = intent.getStringArrayListExtra("ranksOfTwo");
        teamThreeDataName = intent.getStringArrayListExtra("dataNameThree");
        teamThreeDataScore = intent.getStringArrayListExtra("ranksOfThree");

        dataBaseUrl = intent.getExtras().getString("dataBaseUrl");
        allianceScoreData = intent.getExtras().getString("allianceScore");
        isMute = intent.getExtras().getBoolean("mute");
    }

    public void sendAfterMatchData(){
        if (alliance.equals("Blue Alliance")) {
            firebaseRef.child("/Matches").child(numberOfMatch).child("blueScore").setValue(Integer.parseInt(allianceScore.getText().toString()));

        } else if (alliance.equals("Red Alliance")) {
            firebaseRef.child("/Matches").child(numberOfMatch).child("redScore").setValue(Integer.parseInt(allianceScore.getText().toString()));
        }
    }
}