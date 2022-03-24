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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    EditText IP_entrada;
    TextView textView;
    TextView OutputText;
    SensorManager sensorManager;
    Sensor Accelerometer,AccelerometerGN,Gyroscope;

    String Sdata;

    boolean Cerrar;
    boolean Conectado;

    Socket sk;
    PrintWriter TCPOutput;

    float[] data = new float[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IP_entrada = findViewById(R.id.IP_text);
        textView = findViewById(R.id.text_accelerometer);
        OutputText = findViewById(R.id.Output);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        AccelerometerGN = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(MainActivity.this, Accelerometer,sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(MainActivity.this, AccelerometerGN,sensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(MainActivity.this, Gyroscope,sensorManager.SENSOR_DELAY_FASTEST);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        Cerrar = false;
        Conectado = false;

    }

    private void Send(String _DATA) {
        TCPOutput.println(_DATA);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            data[0] = sensorEvent.values[0];
            data[1] = sensorEvent.values[1];
            data[2] = sensorEvent.values[2];
        }else if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            data[3] = sensorEvent.values[0];
            data[4] = sensorEvent.values[1];
            data[5] = sensorEvent.values[2];
        }else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
            data[6] = sensorEvent.values[0];
            data[7] = sensorEvent.values[1];
            data[8] = sensorEvent.values[2];
        }

        Sdata = data[0]+","+data[1]+","+data[2]+","+data[3]+","+data[4]+","+data[5]+","+data[6]+","+data[7]+","+data[8];
        textView.setText((data[0]+"   "+data[1]+"   "+data[2]+"\n"+data[3]+"   "+data[4]+"   "+data[5]+"\n"+data[6]+"   "+data[7]+"   "+data[8]));

        if(Conectado){
            Send(Sdata);
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