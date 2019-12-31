package com.dandrzas.onlinemap;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dandrzas.onlinemap.stmmap_client.R;
import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity {
    public static final String SETTINGS_ACTIVITY_RESPONSE = "Response";
    private int[] IpAdaress;
    private EditText[] UIIpOct;
    private String ServerIpAddress;
    private boolean[] IPOctetOK;

    @Override
    protected void
    onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IpAdaress = new int[4];
        UIIpOct = new EditText[4];
        IPOctetOK = new boolean[4];

        SharedPreferences prefs = getSharedPreferences("STMProjekt_Settings", MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        IpAdaress[0] = prefs.getInt("IPAddressOct1", 192);
        IpAdaress[1] = prefs.getInt("IPAddressOct2", 168);
        IpAdaress[2] = prefs.getInt("IPAddressOct3", 1);
        IpAdaress[3] = prefs.getInt("IPAddressOct4", 1);

        setContentView(R.layout.activity_settings);
        UIIpOct[0] = (EditText) findViewById(R.id.UIipOct1);
        UIIpOct[1] = (EditText) findViewById(R.id.UIipOct2);
        UIIpOct[2] = (EditText) findViewById(R.id.UIipOct3);
        UIIpOct[3] = (EditText) findViewById(R.id.UIipOct4);
        ServerIpAddress = Integer.toString(IpAdaress[0]) + "." + Integer.toString(IpAdaress[1]) + "." + Integer.toString(IpAdaress[2]) + "." + Integer.toString(IpAdaress[3]);
        for (int i = 0; i <= 3; i++) UIIpOct[i].setText(Integer.toString(IpAdaress[i]));
    }

    public void saveButtonClick(View v) {

        for (int i = 0; i <= 3; i++) {
            IPOctetOK[i] = Integer.parseInt(UIIpOct[i].getText().toString()) >= 0 && Integer.parseInt(UIIpOct[i].getText().toString()) <= 255;
        }

        if (IPOctetOK[0] && IPOctetOK[1] && IPOctetOK[2] && IPOctetOK[3]) {

            // Pobranie wprowadzonych wartości
            for (int i = 0; i <= 3; i++) {
                IpAdaress[i] = Integer.parseInt(UIIpOct[i].getText().toString());
            }

            // Zapis ustawień do listy preferencji
            SharedPreferences prefs = getSharedPreferences("STMProjekt_Settings", MODE_PRIVATE);
            SharedPreferences.Editor prefsEdit = prefs.edit();
            prefsEdit.putInt("IPAddressOct1", IpAdaress[0]);
            prefsEdit.putInt("IPAddressOct2", IpAdaress[1]);
            prefsEdit.putInt("IPAddressOct3", IpAdaress[2]);
            prefsEdit.putInt("IPAddressOct4", IpAdaress[3]);
            prefsEdit.apply();

            // Przekazanie danych do głównej aktywności
            Intent resultIntent = new Intent();
            resultIntent.putExtra(SETTINGS_ACTIVITY_RESPONSE, IpAdaress);
            setResult(RESULT_OK, resultIntent);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Log.d("zapis ip", Integer.toString(IpAdaress[0]) + "." + Integer.toString(IpAdaress[1]) + "." + Integer.toString(IpAdaress[2]) + "." + Integer.toString(IpAdaress[3]));
            finish();

        } else {
            for (int i = 0; i <= 3; i++) {
                if (IPOctetOK[i]){
                    UIIpOct[i].setTextColor(Color.BLACK);
                }
                else{
                    UIIpOct[i].setTextColor(Color.RED);
                }
            }
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.UIipOct1),
                    "Wprowadzono niepoprawny adres", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }

    }
}
