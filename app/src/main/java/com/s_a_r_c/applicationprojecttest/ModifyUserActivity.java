package com.s_a_r_c.applicationprojecttest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.s_a_r_c.applicationprojecttest.Helpers.UserDatabase;
import com.s_a_r_c.applicationprojecttest.dummy.AvatarContent;
import com.s_a_r_c.applicationprojecttest.Helpers.Avatars;
import com.s_a_r_c.applicationprojecttest.dummy.DummyContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;

public class ModifyUserActivity extends AppCompatActivity {
    String jsonSaved = "";
    String strMotDePasse = "";
    String strCourriel = "";
    String strId = "";
    String strAvatar = "";
    String strAlias = "";
    String strTicketID = "";
    String strCle = "";
    String MDstrMotDePasse = "";
    String MDstrCourriel = "";
    String MDstrId = "";
    String MDstrAvatar = "";
    String MDstrAlias = "";

    AvatarContent lastSelectedAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        getSupportActionBar().setTitle("Modification d'un utilisateur");

        Spinner spinnerEditUserAvatar = (Spinner) findViewById(R.id.spinnerEditUserAvatars);

        Collection<AvatarContent> vals = Avatars.getInstance().getListAvatars().values();
        HashMap<Integer, Integer> arrayMap = new HashMap<Integer, Integer>();
        AvatarContent[] array = vals.toArray(new AvatarContent[vals.size()]);
        for (int i = 0; i < array.length; i ++)
            arrayMap.put(array[i].getId(), i);
        ArrayAdapter<AvatarContent> adapter = new ArrayAdapter<AvatarContent>(this,
                android.R.layout.simple_spinner_item,
                array);
        spinnerEditUserAvatar.setAdapter(adapter);
        spinnerEditUserAvatar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
                displayAvatar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        Intent intent = getIntent();
        String strMessage = intent.getStringExtra(playListListActivity.EXTRA_MESSAGE);

        try {

            JSONObject lireJSON     = new JSONObject(strMessage);
            strMotDePasse = lireJSON.get("motdepasse").toString();
            strCourriel = lireJSON.get("courriel").toString();
            strId = lireJSON.get("Id").toString();
            strAvatar = lireJSON.get("avatar").toString();
            strAlias = lireJSON.get("alias").toString();
            setTitle("Modification de "+strAlias);
            EditText editText = (EditText) findViewById(R.id.etEditUsername);
            editText.setText(strAlias);
            EditText editText2 = (EditText) findViewById(R.id.etEditEmail);
            editText2.setText(strCourriel);
            spinnerEditUserAvatar.setSelection(arrayMap.get(Integer.valueOf(lireJSON.get("avatar").toString())));


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("labo7",e.toString());
        }
    }

    public void sendModifyLogin(View view)
    {
        EditText EDalias = (EditText)findViewById(R.id.etEditUsername);
        EditText EDcourriel = (EditText)findViewById(R.id.etEditEmail);
        EditText EDpwd = (EditText)findViewById(R.id.etEditPassword);

        MDstrAlias=EDalias.getText().toString();
        MDstrCourriel=EDcourriel.getText().toString();
        MDstrMotDePasse=EDpwd.getText().toString();
        Spinner spinner = (Spinner)findViewById(R.id.spinnerEditUserAvatars);

        if (EDalias.getText().toString().trim().equals("")) {
            hideKeyboardShowToast("Alias invalide");
        } else if (EDcourriel.getText().toString().trim().equals("") || !Patterns.EMAIL_ADDRESS.matcher(EDcourriel.getText().toString().trim()).matches()) {
            hideKeyboardShowToast("Courriel invalide");
        } else if (EDpwd.getText().toString().trim().equals("")) {
            hideKeyboardShowToast("Mot de passe invalide");
        } else if (spinner.getSelectedItem() == null) {
            hideKeyboardShowToast("Avatar invalide");
        } else {
            new DownloadJsonModifyAttept(null).execute("Useless");
        }
    }

    public void receivedModifyResponse()
    {
        Log.e("JSON SAVED",jsonSaved+"Message");

        try {

            JSONObject lireJSON     = new JSONObject(jsonSaved);
            strTicketID =lireJSON.get("idTicket").toString();
            strCle=lireJSON.get("cle").toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("labo7",e.toString());
        }
        new DownloadJsonModifyComplete(null).execute("Useless");
    }

    private class DownloadJsonModifyAttept extends AsyncTask<String, Void, String> {
        String url;

        public DownloadJsonModifyAttept(String url) {

            this.url = url;
        }



        protected String doInBackground(String... url) {

            HttpURLConnection c = null;
            try {
                URL u = new URL("http://424t.cgodin.qc.ca:8180/ProjetFinalServices/service/utilisateur/getTicket/"+strCourriel);
                Log.e("URL","http://424t.cgodin.qc.ca:8180/ProjetFinalServices/service/utilisateur/getTicket/"+strCourriel);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.connect();
                int intStatusRetrieved = c.getResponseCode();
                String strString;
                switch (intStatusRetrieved) {
                    case 200:
                        InputStreamReader  inputStreamReader =new InputStreamReader(c.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((strString = bufferedReader.readLine()) != null){stringBuilder.append(strString+"\n");}
                        bufferedReader.close();
                        return stringBuilder.toString();
                    case 400:
                        strString = "";
                        InputStreamReader  inputStreamReader1 =new InputStreamReader(c.getErrorStream());
                        BufferedReader bufferedReader1 = new BufferedReader(inputStreamReader1);
                        StringBuilder stringBuilder1 = new StringBuilder();
                        while ((strString = bufferedReader1.readLine()) != null){stringBuilder1.append(strString);}
                        bufferedReader1.close();
                        Log.e("JsonRetrieveError",c.getResponseMessage());
                        return "{\"success\":\"false\",\"reason\":\"" + stringBuilder1.toString() + "\"}";
                    case 500:
                        return "{\"success\":\"false\",\"reason\":\"" + "Une erreur est survenue !" + "\"}";
                }}
            catch (Exception ex) {return ex.toString();} finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {Log.e("JsonRetrieveError","Error fielded");}
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            jsonSaved = result;
            //confirmLogin();
            receivedModifyResponse();
        }
    }


    private class DownloadJsonModifyComplete extends AsyncTask<String, Void, String> {
        String url;

        public DownloadJsonModifyComplete(String url) {

            this.url = url;
        }



        protected String doInBackground(String... url) {

            HttpURLConnection c = null;
            try {
                String strConfirmation = DummyContent.encryptMD5(UserDatabase.getInstance(getApplicationContext()).retournerInfosUser().get(UserDatabase.USER_MD5_PASSWORD)+strCle);
                URL u = new URL("http://424t.cgodin.qc.ca:8180/ProjetFinalServices/service/utilisateur/commande?idTicket="+strTicketID+"&confirmation="+strConfirmation+"&action=modifierUser&p1="+strId+"&p2="+MDstrCourriel+"&p3="+ UserDatabase.getInstance(getApplicationContext()).retournerInfosUser().get(UserDatabase.USER_MD5_PASSWORD)+"&p4="+MDstrAlias.trim().replace(" ", "%20")+"&p5="+String.valueOf(idAvatarSelected())+"&p6=true");
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("PUT");
                c.connect();
                int intStatusRetrieved = c.getResponseCode();
                String strString;
                switch (intStatusRetrieved) {
                    case 200:
                        InputStreamReader  inputStreamReader =new InputStreamReader(c.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((strString = bufferedReader.readLine()) != null){stringBuilder.append(strString+"\n");}
                        bufferedReader.close();
                        return stringBuilder.toString();
                    case 400:
                        strString = "";
                        InputStreamReader  inputStreamReader1 =new InputStreamReader(c.getErrorStream());
                        BufferedReader bufferedReader1 = new BufferedReader(inputStreamReader1);
                        StringBuilder stringBuilder1 = new StringBuilder();
                        while ((strString = bufferedReader1.readLine()) != null){stringBuilder1.append(strString);}
                        bufferedReader1.close();
                        Log.e("JsonRetrieveError",c.getResponseMessage());
                        return "{\"success\":\"false\",\"reason\":\"" + stringBuilder1.toString() + "\"}";
                    case 500:
                        return "{\"success\":\"false\",\"reason\":\"" + "Une erreur est survenue !" + "\"}";
                }}
            catch (Exception ex) {return ex.toString();} finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {Log.e("JsonRetrieveError","Error fielded");}
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            jsonSaved = result;

            Log.e("FinalResponse",jsonSaved);
            finalResponse();
        }
    }

    public void finalResponse()
    {
        try {
            JSONObject lireJSON     = new JSONObject(jsonSaved);
            String strSuccess =lireJSON.get("success").toString();
            if(strSuccess.equals("true"))
            {
                hideKeyboardShowToast(lireJSON.get("reason").toString());
                setDrawerInfos();
                UserDatabase.getInstance(getApplicationContext()).updateAfterEdit(
                        lireJSON.get("alias").toString(),
                        lireJSON.get("courriel").toString(),
                        lireJSON.get("motdepasse").toString(),
                        lireJSON.get("avatar").toString()
                        );
                finish();
            }
            else
            {
                hideKeyboardShowToast(lireJSON.get("reason").toString());
                Log.e("Final Response","Failure");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("labo7",e.toString());
        }
    }

    private void displayAvatar() {
        Spinner spinnerEditUserAvatar = (Spinner) findViewById(R.id.spinnerEditUserAvatars);

        if (spinnerEditUserAvatar.getSelectedItem() != null) {
            AvatarContent avatarSelected = Avatars.getInstance().getListAvatars().get(spinnerEditUserAvatar.getSelectedItem().toString());
            if (avatarSelected != null) {
                byte[] decodedString = Base64.decode(avatarSelected.getAvatarB64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ImageView img = (ImageView) findViewById(R.id.ivAvatar);
                img.setImageBitmap(decodedByte);
                img.requestLayout();
                img.getLayoutParams().height = 200;
                img.getLayoutParams().width = 200;
                img.requestLayout();
                lastSelectedAvatar = avatarSelected;
            }

        }

    }

    private int idAvatarSelected() {
        Spinner spinnerEditUserAvatar = (Spinner) findViewById(R.id.spinnerEditUserAvatars);

        if (spinnerEditUserAvatar.getSelectedItem() != null) {
            AvatarContent avatarSelected = Avatars.getInstance().getListAvatars().get(spinnerEditUserAvatar.getSelectedItem().toString());
            if (avatarSelected != null) {
                return avatarSelected.getId();
            } else {
                return 1;
            }

        } else {
            return 1;
        }
    }

    private void setDrawerInfos() {

        if (lastSelectedAvatar != null) {
            EditText etEmail = (EditText) findViewById(R.id.etEditEmail);
            UserDatabase.getInstance(getApplicationContext()).updateAvatar(lastSelectedAvatar.getAvatarB64());
            UserDatabase.getInstance(getApplicationContext()).updateEmail(etEmail.getText().toString().trim());
        } else {
            Log.v("ERREUR DRAWER AVAR", "WAIT WHAT");
        }

    }

    private void hideKeyboardShowToast(String strMessage) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Toast toast = Toast.makeText(this, strMessage, Toast.LENGTH_SHORT);
        toast.show();
    }
}
