package com.s_a_r_c.applicationprojecttest;

import android.content.Context;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

public class createUserActivity extends AppCompatActivity {

String jsonSaved = "";
    String strTicketID = "";
    String strAlias = "";
    String strPassword = "";
    String strEmail = "";
    String strCaptcha = "";
    String strSuccess = "";

    String[] avatarArray;
    HashMap<Integer, AvatarContent> avatarMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        getSupportActionBar().setTitle("Création d'un utilisateur");

        Spinner spinnerNewUserAvatar = (Spinner) findViewById(R.id.spinnerNewUserAvatars);

        Collection<AvatarContent> vals = Avatars.getInstance().getListAvatars().values();
        AvatarContent[] array = vals.toArray(new AvatarContent[vals.size()]);
        ArrayAdapter<AvatarContent> adapter = new ArrayAdapter<AvatarContent>(this,
                android.R.layout.simple_spinner_item,
                array);
        spinnerNewUserAvatar.setAdapter(adapter);
        spinnerNewUserAvatar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
                displayAvatar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        //hideKeyboardShowToast(String.valueOf(Avatars.getInstance().getListAvatars().size()));
    }

    public void confirmSuccesffulLoginAttempt()
    {
        Log.e("confirmSuccesffulLogin",jsonSaved+"Message");
        try {
            JSONObject lireJSON     = new JSONObject(jsonSaved);
            strSuccess =lireJSON.get("success").toString();
            if(strSuccess.equals("true"))
            {
                hideKeyboardShowToast(lireJSON.get("reason").toString());
                finish();
            }
            else
            {
                TextView mTextView = (TextView) findViewById(R.id.textView11);
                mTextView.setText(lireJSON.get("reason").toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("labo7",e.toString());
        }

    }

    public void sendCreateUserRequest(View view)
    {
        EditText editText1 = (EditText)findViewById(R.id.etNewUsername);
        EditText editText2 = (EditText)findViewById(R.id.etNewEmail);
        EditText editText3 = (EditText)findViewById(R.id.etNewPassword);
        strAlias = editText1.getText().toString();
        strEmail= editText2.getText().toString();
        strPassword = editText3.getText().toString();

        Spinner spinner = (Spinner)findViewById(R.id.spinnerNewUserAvatars);

        if (strAlias.trim().equals("")) {
            hideKeyboardShowToast("Alias invalide");
        } else if (strEmail.trim().equals("") || !Patterns.EMAIL_ADDRESS.matcher(strEmail.trim()).matches()) {
            hideKeyboardShowToast("Courriel invalide");
        } else if (strPassword.trim().equals("")) {
            hideKeyboardShowToast("Mot de passe invalide");
        } else if (spinner.getSelectedItem() == null) {
            hideKeyboardShowToast("Avatar invalide");
        } else {
            new DownloadJsonCreatenAttempt(null).execute("Useless");
        }


    }
    public void confirmRequest() {
        try {


            JSONObject lireJSON = new JSONObject(jsonSaved);
            //JSONObject jsonMovie = new JSONObject();
            String strCaptcha = lireJSON.get("captcha").toString();
            strTicketID = lireJSON.get("idTicket").toString();
            byte[] decodedString = Base64.decode(strCaptcha, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView img = (ImageView) findViewById(R.id.imageView3);
            img.setImageBitmap(decodedByte);
            img.requestLayout();
            img.getLayoutParams().height = 200;
            img.getLayoutParams().width = 200;
            img.requestLayout();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void confirmUserCreation(View view)
    {
        EditText editText4 = (EditText)findViewById(R.id.editText10);
        strCaptcha = editText4.getText().toString();
        new DownloadJsonCaptchaAttempt(null).execute("Useless");
    }


    private class DownloadJsonCaptchaAttempt extends AsyncTask<String, Void, String> {
        String url;

        public DownloadJsonCaptchaAttempt(String url) {

            this.url = url;
        }

        protected String doInBackground(String... url) {

            HttpURLConnection c = null;
            try {

                URL u = new URL("http://424t.cgodin.qc.ca:8180/ProjetFinalServices/service/utilisateur/verifCaptcha?idTicket="+strTicketID+"&captcha="+strCaptcha);
                Log.e("strCaptchaAttempt","http://424t.cgodin.qc.ca:8180/ProjetFinalServices/service/utilisateur/verifCaptcha?idTicket="+strTicketID+"&captcha="+strCaptcha);
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
            Log.e("onPostExecute",result+"Message");
            jsonSaved = result;
            confirmSuccesffulLoginAttempt();
        }
    }

    private class DownloadJsonCreatenAttempt extends AsyncTask<String, Void, String> {
        String url;

        public DownloadJsonCreatenAttempt(String url) {

            this.url = url;
        }



        protected String doInBackground(String... url) {

            HttpURLConnection c = null;
            try {
                //Log.d("WAIT", "http://424t.cgodin.qc.ca:8180/ProjetFinalServices/service/utilisateur/creer?alias="+strAlias+"&courriel="+strEmail+"&mdp="+getMd5Hash(strPassword)+"&actif=true&date=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()).replace(" ", "%20") + "&idAvatar=" + String.valueOf(displayAvatar()));
                URL u = new URL("http://424t.cgodin.qc.ca:8180/ProjetFinalServices/service/utilisateur/creer?alias="+strAlias.trim().replace(" ", "%20")+"&courriel="+strEmail+"&mdp="+ DummyContent.encryptMD5(strPassword)+"&actif=true&date=" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()).replace(" ", "%20") + "&idAvatar=" + String.valueOf(idAvatarSelected()));
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

            confirmRequest();

        }
    }

    private void displayAvatar() {
        Spinner spinnerNewUserAvatar = (Spinner) findViewById(R.id.spinnerNewUserAvatars);

        if (spinnerNewUserAvatar.getSelectedItem() != null) {
            AvatarContent avatarSelected = Avatars.getInstance().getListAvatars().get(spinnerNewUserAvatar.getSelectedItem().toString());
            if (avatarSelected != null) {
                byte[] decodedString = Base64.decode(avatarSelected.getAvatarB64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ImageView img = (ImageView) findViewById(R.id.ivAvatar);
                img.setImageBitmap(decodedByte);
                img.requestLayout();
                img.getLayoutParams().height = 200;
                img.getLayoutParams().width = 200;
                img.requestLayout();
            }

        }

    }

    private int idAvatarSelected() {
        Spinner spinnerNewUserAvatar = (Spinner) findViewById(R.id.spinnerNewUserAvatars);

        if (spinnerNewUserAvatar.getSelectedItem() != null) {
            AvatarContent avatarSelected = Avatars.getInstance().getListAvatars().get(spinnerNewUserAvatar.getSelectedItem().toString());
            if (avatarSelected != null) {
                return avatarSelected.getId();
            } else {
                return 1;
            }

        } else {
            return 1;
        }
    }

    private void hideKeyboardShowToast(String strMessage) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Toast toast = Toast.makeText(this, strMessage, Toast.LENGTH_SHORT);
        toast.show();
    }
}
