package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {

    String total_file = "totalAmount";
    String zoo_file = "remainingZoos";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        setStatusBar(context);
        TextView count = findViewById(R.id.zooNum);

        ActionManager undoList = new ActionManager(null);

        try {
            setBoxText(context, amountRead(context));
            count.setText(Integer.toString(zoosRead(context)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // /data/user/0/com.example.moneymanager/files

        //zoo
        Button zoo = findViewById(R.id.zoo);
        zoo.setOnClickListener(view -> {
            try {
                zoosDecrease(context);
                amountWrite(context,-3.75);
                Action action = new Action(3.75, 1);
                undoList.push(action);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //kombucha
        Button buch = findViewById(R.id.kombucha);
        buch.setOnClickListener(view -> {
            try {
                amountWrite(context, -3.89);
                Action action = new Action(3.89, 0);
                undoList.push(action);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //override
        Button override = findViewById(R.id.override);
        override.setOnClickListener(view -> {
            EditText textBox = findViewById(R.id.enterAmount);
            try {
                override(context, Double.parseDouble(String.valueOf(textBox.getText())));
                Action action = new Action(3.75, 0);
                undoList.push(action);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            textBox.setText("");
        });

        //spend
        Button spend = findViewById(R.id.spend);
        spend.setOnClickListener(view -> {
            EditText textBox = findViewById(R.id.enterAmount);
            try {
                double amount = Double.parseDouble(String.valueOf(textBox.getText()));
                amountWrite(context, -amount);
                Action action = new Action(amount, 0);
                undoList.push(action);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            textBox.setText("");
        });

        //reset
        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(view -> {
            try {
                double currentAmount = amountRead(context);
                int currentZoos = zoosRead(context);
                zoosReset(context);
                override(context, 78.75);
                Action action = new Action(78.75 - currentAmount, 14 - currentZoos);
                undoList.push(action);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //zoo skip
        Button skip = findViewById(R.id.zooSkip);
        skip.setOnClickListener(view -> {
            try {
                zoosDecrease(context);
                amountWrite(context, 0);
                Action action = new Action(0, 1);
                undoList.push(action);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //undo
        Button undo = findViewById(R.id.undo);
        undo.setOnClickListener(view -> {
            EditText textBox = findViewById(R.id.enterAmount);
            Action action = undoList.pop();
            if(action != null){
                try {
                    zoosIncrease(context, action.zoo);
                    amountWrite(context, action.amount);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            textBox.setText("");
        });
    }

    public void setBoxText(Context context, double amount) throws IOException {
        TextView total = findViewById(R.id.total);
        TextView free = findViewById(R.id.freeSpending);
        double zoo = zoosRead(context);
        double freeSpening = (amount - (zoo * 3.75));

        total.setText(formatText(amount));
        free.setText(formatText(freeSpening));
    }

    public String formatText(double amount){
        if(amount < 0){
            amount = 0;
        }
        String text = Double.toString(amount);
        //maniptulate
        int point = text.indexOf(".");
        if(text.substring(text.indexOf(".")+1).length() == 1){
            text += "0";
        }
        else if(text.substring(point).length() > 3){
            text = text.substring(0, point+3);
        }
        return text;
    }

    public void amountWrite(Context context, double write) throws IOException {
        double tot = amountRead(context);
        FileOutputStream out = context.openFileOutput(total_file, MODE_PRIVATE);
        tot += write;
        String new_tot = Double.toString(tot);
        out.write(new_tot.getBytes());
        out.close();
        setBoxText(context, tot);
    }

    public void override(Context context, double write) throws IOException{
        FileOutputStream out = context.openFileOutput(total_file, MODE_PRIVATE);
        String new_tot = Double.toString(write);
        out.write(new_tot.getBytes());
        out.close();
        setBoxText(context, write);
    }
    
    public double amountRead(Context context) throws IOException{
        FileInputStream in = context.openFileInput(total_file);
        InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(inputStreamReader);
        String string = br.readLine();
        br.close();
        return Double.parseDouble(string);
    }

    public int zoosRead(Context context) throws IOException{
        FileInputStream in = context.openFileInput(zoo_file);
        InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(inputStreamReader);
        String str = br.readLine();
        br.close();
        return Integer.parseInt(str);
    }

    public void zoosDecrease(Context context) throws IOException{
        int zoos = zoosRead(context)-1;
        FileOutputStream out = context.openFileOutput(zoo_file, MODE_PRIVATE);
        TextView count = findViewById(R.id.zooNum);
        if(zoos < 0){
            zoos=0;
        }
        String amount = Integer.toString(zoos);
        out.write(amount.getBytes());
        out.close();
        count.setText(amount);
    }
    public void zoosReset(Context context) throws IOException{
        FileOutputStream out = context.openFileOutput(zoo_file, MODE_PRIVATE);
        String amount = "14";
        out.write(amount.getBytes());
        TextView count = findViewById(R.id.zooNum);
        count.setText(amount);
        out.close();
    }
    public void zoosIncrease(Context context, int zoos) throws IOException{
        int zooAmount = zoosRead(context);
        FileOutputStream out = context.openFileOutput(zoo_file, MODE_PRIVATE);
        TextView count = findViewById(R.id.zooNum);
        String amount = Integer.toString(zooAmount + zoos);
        out.write(amount.getBytes());
        out.close();
        count.setText(amount);
    }

    //android
    public void setStatusBar(Context context){
        getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.uofmRed));
    }
}

