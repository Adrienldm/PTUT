package com.example.matches;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class StageCreationActivity extends AppCompatActivity {

    EditText stageTitreEditText;
    EditText dateStageDebutEditTextDate;
    EditText dateStageFinEditTextDate;
    EditText descStageEditText;
    EditText competencesEditTextMultiLine;
    Button stageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_creation);
    }
}