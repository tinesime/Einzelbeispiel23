package com.example.einzelbeispiel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    int port = 53212;
    String ip = "se2-isys.aau.at";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        send();
                    }
                };

                Thread thread = new Thread(runnable);
                thread.start();
            }
        });
    }

    private void send() {
        EditText numberInput = (EditText) findViewById(R.id.numer_input);
        TextView resultText = (TextView) findViewById(R.id.result_text);

        try {
            Socket socket = new Socket(ip, port);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            String outputMessage = numberInput.getText().toString();
            output.println(outputMessage);

            String inputMessage = input.readLine();
            resultText.setText(printOnlyPrimeNumbers(inputMessage));

            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String printOnlyPrimeNumbers(String text) {
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        ArrayList<Integer> primeIntegerArrayList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder("Primzahlen: ");

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String match = matcher.group();
            integerArrayList.add(Integer.parseInt(match));
        }

        for (int i = 0; i < integerArrayList.size(); i++)
            if (isPrimeNumber(integerArrayList.get(i))) primeIntegerArrayList.add(integerArrayList.get(i));

        if (primeIntegerArrayList.isEmpty()) return "No prime numbers in response!";

        for (int i = 0; i < primeIntegerArrayList.size(); i++) {
            if (primeIntegerArrayList.size() != 1) stringBuilder.append(", ");
            stringBuilder.append(primeIntegerArrayList.get(i));
        }

        return stringBuilder.toString();
    }

    private boolean isPrimeNumber(Integer number) {
        if (number < 2) return false;
        for (int i = 2; i <= number / 2; i++)
            if (number % i == 0) return false;
        return true;
    }

}
