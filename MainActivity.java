package com.example.sens;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    EditText IP_entrada;
    TextView textView;
    TextView OutputText;
    SensorManager sensorManager;
    Sensor sensor;

    String datos;
    String Salida = "CONSOLE OUTPUT";

    boolean Cerrar;
    boolean Conectado;

    Socket sk;
    PrintWriter TCPOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IP_entrada = findViewById(R.id.IP_text);
        textView = findViewById(R.id.text_accelerometer);
        OutputText = findViewById(R.id.Output);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this,sensor,sensorManager.SENSOR_DELAY_FASTEST);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        Cerrar = false;
        Conectado = false;

    }

    private void Send(String _DATA) {
        TCPOutput.println(_DATA);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        OutputText.setText(Salida);
        datos = sensorEvent.values[0]+","+sensorEvent.values[1]+","+sensorEvent.values[2];
        textView.setText(datos);
        if(Conectado){
            Send(datos);
        }
        if(Cerrar){
            try {
                sk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Conectado = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void SetDirection(View view) throws IOException {
        log("Modo Conectar = true");
        try {
            log(" Socket: " + IP_entrada.getText().toString() + ":" + 8080);
            sk = new Socket(IP_entrada.getText().toString(), 8080);
            log("Conectado a IP:"+IP_entrada.getText().toString());
            TCPOutput = new PrintWriter(new OutputStreamWriter(sk.getOutputStream()), true);
            log("Conectado con exito");
            Conectado = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //Conectar

    public void DisconnectClient(View view){
        log("Modo Cerrar = true");
        Cerrar = true;
    } //Desconectar

    private void log(String string) {
        OutputText.append(string + "\n");
    }
}