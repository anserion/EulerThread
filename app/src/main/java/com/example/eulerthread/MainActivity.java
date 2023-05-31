package com.example.eulerthread;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView txtPercent, txtAnswer;
    EditText editN;
    Button btnStart, btnAbort;
    Handler handler;
    Thread thread;
    boolean readyFlag = true;
    boolean abortFlag = false;
    String strN;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPercent = findViewById(R.id.txtPercent);
        txtAnswer = findViewById(R.id.txtAnswer);
        editN = findViewById(R.id.editN);
        btnStart = findViewById(R.id.btnStart);
        btnAbort = findViewById(R.id.btnAbort);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (abortFlag) abortFlag=false;
                strN = editN.getText().toString();
                if (readyFlag && checkN(strN)) {
                    thread = new NewThread();
                    thread.start();
                } else {
                    txtAnswer.setText(getResources().getText(R.string.answer_text));
                    txtPercent.setText(getResources().getText(R.string.ready_percent_text));
                }
            }
        });

        btnAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abortFlag = true;
                txtAnswer.setText(getResources().getText(R.string.answer_text));
                txtPercent.setText(getResources().getText(R.string.ready_percent_text));
                readyFlag = true;
            }
        });

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                    if (msg.what > 0) {
                        txtAnswer.setText("" + msg.what);
                        txtPercent.setText("100");
                    } else {
                        txtPercent.setText("" + (-msg.what));
                    }
                }
        };
    }

    public boolean checkN(String s) {
        boolean res = true;
        double x = 0;
        if (s.isEmpty()) return false;
        try {
            x = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        if (x < 1) return false;
        if (x != Math.floor(x)) return false;
        return true;
    }

    public int gcd(int a, int b) {
        while ((a != 0) && (b != 0))
            if (a > b) a = a % b;
            else b = b % a;
        return a + b;
    }

    public int phi(int N) {
        if (N == 1) return 1;
        int cnt = 0;
        double percent;
        for (int i = 1; i < N; i++) {
            if (abortFlag) return 0;
            percent = (i * 100.0) / N + 1;
            if (percent == Math.floor(percent)) handler.sendEmptyMessage((int) (-percent));
            if (gcd(i, N) == 1) cnt++;
        }
        return cnt;
    }

    public class NewThread extends Thread {
        @Override
        public void run() {
            if (readyFlag) {
                readyFlag = false;
                int N = (int) Math.floor(Double.parseDouble(strN));
                int res = phi(N);
                handler.sendEmptyMessage(res);
                readyFlag = true;
            }
        }
    }
}
