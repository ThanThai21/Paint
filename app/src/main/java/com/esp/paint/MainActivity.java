package com.esp.paint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DrawView drawView;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        drawView = (DrawView) findViewById(R.id.draw_view);
        clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == clearButton) {
            Toast.makeText(this, "Clear", Toast.LENGTH_SHORT).show();
            drawView.clear();
        }
    }
}
