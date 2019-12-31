package com.dandrzas.onlinemap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dandrzas.onlinemap.stmmap_client.R;
import com.google.android.material.snackbar.Snackbar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {
    private TextView UIResult;
    private Button UIGetMapButton, UIGetMapByPixelsButton;
    private ImageViewWithRectDraw UIMapImage;
    private EditText[] UIMapCoordParam;
    private ProgressBar UIDownloadProgressBar;
    private int MapMiniaturePixDim = 1000;
    private int[] MapPixelParams = {0, 0, 1000, 1000};  // start x, stary y, end x, end y
    private double[] MapCoordinatesParams = {18.64141, 54.34411, 18.66278, 54.35662}; // start long, start lat, end long, end lat
    private boolean touchMoveDeltaOK = false, imageViewdrawingEnable = false;
    private double startX, startY, xTouchCoordinate, yTouchCoordinate, xTouchDelta, yTouchDelta;
    private static final int SETTINGS_ACTIVITY_REQUEST_CODE = 1;
    public static final String SETTINGS_ACTIVITY_RESPONSE = "Response";
    private String WSServerIpAddress;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Pobranie ustawień
        int[] IpAdaress = new int[4];
        SharedPreferences prefs = getSharedPreferences("STMProjekt_Settings", MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        IpAdaress[0] = prefs.getInt("IPAddressOct1", 192);
        IpAdaress[1] = prefs.getInt("IPAddressOct2", 168);
        IpAdaress[2] = prefs.getInt("IPAddressOct3", 1);
        IpAdaress[3] = prefs.getInt("IPAddressOct4", 1);
        WSServerIpAddress = Integer.toString(IpAdaress[0]) + "." + Integer.toString(IpAdaress[1]) + "." + Integer.toString(IpAdaress[2]) + "." + Integer.toString(IpAdaress[3]);

        // Rysowanie UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bindowanie elementów UI
        UIGetMapByPixelsButton = (Button) findViewById(R.id.UIGetMapByPixelsButton);
        UIMapCoordParam = new EditText[4];
        UIMapCoordParam[0] = (EditText) findViewById(R.id.UIPixelStartX);
        UIMapCoordParam[1] = (EditText) findViewById(R.id.UIPixelStartY);
        UIMapCoordParam[2] = (EditText) findViewById(R.id.UIPixelEndX);
        UIMapCoordParam[3] = (EditText) findViewById(R.id.UIPixelEndY);
        UIResult = (TextView) findViewById(R.id.result);
        UIGetMapButton = (Button) findViewById(R.id.UIGetMapButton);
        UIMapImage = (ImageViewWithRectDraw) findViewById(R.id.UIMapImage);
        UIDownloadProgressBar = (ProgressBar) findViewById(R.id.UIDownloadProgressBar);

        // Ustawienie rozmiaru mapy (dopasowanie do szerokości wyświetlacza)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        UIMapImage.getLayoutParams().height = (int) (width * 0.7);
        UIMapImage.getLayoutParams().width = (int) (width * 0.7);
        UIMapImage.requestLayout();

        // Inicjalizacja UI wartościami startowymi
        UIMapCoordParam[0].setText(Double.toString(MapCoordinatesParams[0]));
        UIMapCoordParam[1].setText(Double.toString(MapCoordinatesParams[1]));
        UIMapCoordParam[2].setText(Double.toString(MapCoordinatesParams[2]));
        UIMapCoordParam[3].setText(Double.toString(MapCoordinatesParams[3]));

        new MapDownloader().execute("getMap"); // Pobranie mapy po uruchomieniu aplikacji

        // UIMapImage - Touch Event
        UIMapImage.setOnTouchListener(new View.OnTouchListener() {
            AsyncTask downloader = null;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (imageViewdrawingEnable) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {  // Pobranie pozycji punktu początkowego
                        startX = event.getX();
                        startY = event.getY();
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {

                        // Pobranie współrzędnych dotyku
                        xTouchCoordinate = event.getX();
                        xTouchDelta = xTouchCoordinate - startX; // sprawdzenie różnicy przesunięcia na X i dodanie tego samego przesunięcia na Y w celu uzyskania kwadratu
                        yTouchCoordinate = event.getY();
                        yTouchDelta = yTouchCoordinate - startY; // sprawdzenie różnicy przesunięcia na X i dodanie tego samego przesunięcia na Y w celu uzyskania kwadratu
                        touchMoveDeltaOK = (Math.abs(xTouchDelta) >= 50) && (Math.abs(yTouchDelta) >= 50);

                        // ograniczenie rysowanego prostokąta współrzędnych do wielkości mapy
                        if (xTouchCoordinate > v.getWidth()) {
                            xTouchCoordinate = v.getWidth();
                        }
                        if (xTouchCoordinate < 0) {
                            xTouchCoordinate = 0;
                        }
                        if (yTouchCoordinate > v.getHeight()) {
                            yTouchCoordinate = v.getHeight();
                        }
                        if (yTouchCoordinate < 0) {
                            yTouchCoordinate = 0;
                        }

                        // Ustawienie współrzędnych dotyku
                        UIMapImage.setxTouchCoordinate(xTouchCoordinate);
                        UIMapImage.setyTouchCoordinate(yTouchCoordinate);
                        UIMapImage.setStartX(startX);
                        UIMapImage.setStartY(startY);

                        // Ustawienie koloru rysowanego prostokąta
                        if (touchMoveDeltaOK) {
                            UIMapImage.setDrawColor(Color.BLACK);
                        } else UIMapImage.setDrawColor(Color.RED);

                        // Rysowanie prostokąta na mapie
                        UIMapImage.setDrawRect(true);
                        v.invalidate();
                    }

                    //  Pobranie wycinka mapy zgodnie z zaznaczonym obszarem
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        UIMapImage.setDrawRect(false);

                        if (touchMoveDeltaOK) {

                            //Obliczenie startowych punktów dotyku w odniesieniu do wycinka mapy widocznego aktualnie na ekranie
                            double TouchStartXPix = (startX / v.getWidth()) * MapMiniaturePixDim;
                            double TouchEndXPix = (xTouchCoordinate / v.getWidth()) * MapMiniaturePixDim;
                            double TouchStartYPix = (startY / v.getHeight()) * MapMiniaturePixDim;
                            double TouchEndYPix = (((yTouchCoordinate) / v.getHeight()) * MapMiniaturePixDim);

                            // Pobranie parametrów pixeli naryspwanego prostokąta w zależności od kierunku rysowania
                            if ((xTouchDelta > 0) && (yTouchDelta > 0)) {
                                MapPixelParams[0] = (int) (TouchStartXPix);
                                MapPixelParams[1] = (int) (TouchStartYPix);
                                MapPixelParams[2] = (int) (TouchEndXPix);
                                MapPixelParams[3] = (int) (TouchEndYPix);
                            } else if ((xTouchDelta < 0) && (yTouchDelta < 0)) {
                                MapPixelParams[0] = (int) (TouchEndXPix);
                                MapPixelParams[1] = (int) (TouchEndYPix);
                                MapPixelParams[2] = (int) (TouchStartXPix);
                                MapPixelParams[3] = (int) (TouchStartYPix);
                            } else if ((xTouchDelta > 0) && (yTouchDelta < 0)) {
                                MapPixelParams[0] = (int) (TouchStartXPix);
                                MapPixelParams[1] = (int) (TouchEndYPix);
                                MapPixelParams[2] = (int) (TouchEndXPix);
                                MapPixelParams[3] = (int) (TouchStartYPix);
                            } else if ((xTouchDelta < 0) && (yTouchDelta > 0)) {
                                MapPixelParams[0] = (int) (TouchEndXPix);
                                MapPixelParams[1] = (int) (TouchStartYPix);
                                MapPixelParams[2] = (int) (TouchStartXPix);
                                MapPixelParams[3] = (int) (TouchEndYPix);
                            }

                            if (downloader != null)
                                downloader.cancel(true); //Jeśli trwa poprzednie pobieranie to anulowanie i start nowego
                            if ((downloader == null) || (downloader.isCancelled()))
                                downloader = new MapDownloader().execute("getMapByPx"); // Pobranie zadanego wycinka mapy
                        }
                        v.invalidate();
                    }
                }

                return true;
            }
        });


        // UIButtonGetMap - obługa kliknięcia
        UIGetMapButton.setOnClickListener(new View.OnClickListener() {
            AsyncTask downloader = null;

            @Override
            public void onClick(View view) {
                if (downloader != null) downloader.cancel(true);
                if ((downloader == null) || (downloader.isCancelled()))
                    downloader = new MapDownloader().execute("getMap"); // Pobranie miniatury mapy
            }
        });

        // UIButtonGetMapMiniature - obługa kliknięcia
        UIGetMapByPixelsButton.setOnClickListener(new View.OnClickListener() {
            AsyncTask downloader = null;

            @Override
            public void onClick(View view) {

                boolean[] pixelParamsOK = new boolean[4];
                boolean allPixelParamsOK;

                // Pobranie parametrów wprowadzonych w UI
                MapCoordinatesParams[0] = Double.parseDouble(UIMapCoordParam[0].getText().toString());
                MapCoordinatesParams[1] = Double.parseDouble(UIMapCoordParam[1].getText().toString());
                MapCoordinatesParams[2] = Double.parseDouble(UIMapCoordParam[2].getText().toString());
                MapCoordinatesParams[3] = Double.parseDouble(UIMapCoordParam[3].getText().toString());

                // Walidacja wprowadzonych parametrów
                pixelParamsOK[0] = (MapCoordinatesParams[0] >= 18.64141) && (MapCoordinatesParams[0] < MapCoordinatesParams[2]);
                pixelParamsOK[1] = (MapCoordinatesParams[1] >= 54.34411) && (MapCoordinatesParams[1] < MapCoordinatesParams[3]);
                pixelParamsOK[2] = (MapCoordinatesParams[2] > MapCoordinatesParams[0]) && (MapCoordinatesParams[2] <= 18.66278);
                pixelParamsOK[3] = (MapCoordinatesParams[3] > MapCoordinatesParams[1]) && (MapCoordinatesParams[3] <= 54.35662);

                for (int i = 0; i <= 3; i++) {
                    if (pixelParamsOK[i]) {
                        UIMapCoordParam[i].setTextColor(Color.BLACK);
                    } else UIMapCoordParam[i].setTextColor(Color.RED);
                }

                allPixelParamsOK = pixelParamsOK[0] && pixelParamsOK[1] && pixelParamsOK[2] && pixelParamsOK[3];

                if (allPixelParamsOK) {
                    // Pobranie mapy z parametrami wprowadzonymi w UI
                    if (downloader != null) downloader.cancel(true);
                    if ((downloader == null) || (downloader.isCancelled()))
                        downloader = new MapDownloader().execute("getMapByCoord"); // Pobranie zadanego wycinka mapy
                } else {

                    Snackbar mySnackbar = Snackbar.make(view,
                            "Wprowadzono niepoprawne paramety", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_ACTIVITY_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SETTINGS_ACTIVITY_REQUEST_CODE == requestCode) {
            int[] ReadIpAdaress = data.getIntArrayExtra(SettingsActivity.SETTINGS_ACTIVITY_RESPONSE);
            WSServerIpAddress = Integer.toString(ReadIpAdaress[0]) + "." + Integer.toString(ReadIpAdaress[1]) + "." + Integer.toString(ReadIpAdaress[2]) + "." + Integer.toString(ReadIpAdaress[3]);
        }
    }

    public class MapDownloader extends AsyncTask<String, Void, String> {
        byte[] result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UIResult.setTextColor(Color.BLACK);
            UIResult.setText("Pobieranie...");
            UIDownloadProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... arg) {
            PropertyInfo parVal1, parVal2, parVal3, parVal4;

            try {
                String NAMESPACE = "http://onlinemap.dandrzas.com/";
                String SOAP_ACTION = NAMESPACE + arg[0];
                String URL = "http://" + WSServerIpAddress + ":8080/OnlineMapServer/MapWebService?WSDL";
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                SoapObject request = new SoapObject(NAMESPACE, arg[0]);

                switch (arg[0]) {
                    case "getMap":
                        break;
                    case "getMapByPx":
                        parVal1 = new PropertyInfo();
                        parVal1.name = "p1x";
                        parVal1.type = parVal1.INTEGER_CLASS;

                        parVal2 = new PropertyInfo();
                        parVal2.name = "p1y";
                        parVal2.type = parVal2.INTEGER_CLASS;

                        parVal3 = new PropertyInfo();
                        parVal3.name = "p2x";
                        parVal3.type = parVal3.INTEGER_CLASS;

                        parVal4 = new PropertyInfo();
                        parVal4.name = "p2y";
                        parVal4.type = parVal4.INTEGER_CLASS;

                        request.addProperty("p1x", MapPixelParams[0]);
                        request.addProperty("p1y", MapPixelParams[1]);
                        request.addProperty("p2x", MapPixelParams[2]);
                        request.addProperty("p2y", MapPixelParams[3]);
                        break;

                    case "getMapByCoord":

                        request.addProperty("p1long", MapCoordinatesParams[0]);
                        request.addProperty("p1lat", MapCoordinatesParams[1]);
                        request.addProperty("p2long", MapCoordinatesParams[2]);
                        request.addProperty("p2lat", MapCoordinatesParams[3]);
                        envelope.addMapping(NAMESPACE, "Double", Double.class);
                        MarshalDouble md = new MarshalDouble();
                        md.register(envelope);
                        break;
                }

                envelope.setOutputSoapObject(request);
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
                String[] response = new String[1];
                response[0] = resultsRequestSOAP.toString();
                result = Base64.decode(response[0].getBytes(), 0);
            } catch (Exception e) {
                result = null;
            }
            return arg[0];
        }

        @Override
        protected void onPostExecute(String arx) {
            if (result != null) {
                Bitmap bm = BitmapFactory.decodeByteArray(result, 0, result.length);
                UIMapImage.setImageBitmap(bm);
                if (arx == "getMap") {
                    imageViewdrawingEnable = true;
                    // Aktualizacja parametrów koordynat do aktualnego wycinka mapy
                    MapCoordinatesParams[0] = 18.64141;
                    MapCoordinatesParams[1] = 54.34411;
                    MapCoordinatesParams[2] = 18.66278;
                    MapCoordinatesParams[3] = 54.35662;
                    UIMapCoordParam[0].setText(Double.toString(MapCoordinatesParams[0]));
                    UIMapCoordParam[1].setText(Double.toString(MapCoordinatesParams[1]));
                    UIMapCoordParam[2].setText(Double.toString(MapCoordinatesParams[2]));
                    UIMapCoordParam[3].setText(Double.toString(MapCoordinatesParams[3]));

                    // Ustawienie rozmiaru mapy (dopasowanie do szerokości wyświetlacza)
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int width = displayMetrics.widthPixels;
                    UIMapImage.getLayoutParams().height = (int) (width * 0.7);
                    UIMapImage.getLayoutParams().width = (int) (width * 0.7);
                    UIMapImage.requestLayout();
                } else {
                    imageViewdrawingEnable = false;
                    // Ustawienie rozmiaru mapy (dopasowanie do szerokości wyświetlacza)
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int width = displayMetrics.widthPixels;
                    UIMapImage.getLayoutParams().height = width;
                    UIMapImage.getLayoutParams().width = width;
                    UIMapImage.requestLayout();
                }


                UIResult.setTextColor(getResources().getColor(R.color.colorGreen));
                UIResult.setText("Pobrano");
            } else {
                UIResult.setTextColor(Color.RED);
                UIResult.setText("Błąd pobierania");
            }
            UIDownloadProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
