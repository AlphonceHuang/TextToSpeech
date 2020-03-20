package com.example.texttospeech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech t1;
    private EditText ed1;
    private RadioGroup rg;
    private RadioButton rb1, rb2;
    SharedPreferences mem_Language;
    private final String TAG="Alan";

    private final int LANGUAGE_ENGLIST=0;
    private final int LANGUAGE_CHINESE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed1=findViewById(R.id.editText);
        Button b1 = findViewById(R.id.button);
        Button b2 = findViewById(R.id.button2);
        Button b3 = findViewById(R.id.button3);
        rb1 = findViewById(R.id.RadioBT_US);
        rb2 = findViewById(R.id.RadioBT_Chinese);
        rg = findViewById(R.id.LanguageRatioGroup);
        rg.setOnCheckedChangeListener(mRadioButton);

        ed1.requestFocus(); // 直接focus至此處

        mem_Language = getSharedPreferences("SpeakLanguage", MODE_PRIVATE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = ed1.getText().toString();
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        b2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String reportTime = "Now is "+currentTime;

                if (rg.getCheckedRadioButtonId() == R.id.RadioBT_Chinese)
                    reportTime = "現在是"+currentTime;

                t1.speak(reportTime, TextToSpeech.QUEUE_FLUSH, null, null);
                Toast.makeText(getApplicationContext(), reportTime,Toast.LENGTH_SHORT).show();
            }
        });

        b3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String reportDate = "Today is "+currentDate;
                if (rg.getCheckedRadioButtonId() == R.id.RadioBT_Chinese)
                    reportDate = "今天是"+currentDate;

                //t1.speak(reportDate, TextToSpeech.QUEUE_FLUSH, null);
                t1.speak(reportDate,TextToSpeech.QUEUE_FLUSH,null,null);
                Toast.makeText(getApplicationContext(), reportDate,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        Log.w(TAG, "onResume");

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {

                    int ttsLang = t1.setLanguage(Locale.US);
                    int currentLang=SpeakLanguageGet();

                    if (currentLang>1){
                        SpeakLanguageSet(LANGUAGE_ENGLIST);    // default US
                    }

                    switch(currentLang){
                        case LANGUAGE_ENGLIST:
                            ttsLang = t1.setLanguage(Locale.US);
                            rb1.setChecked(true);
                            break;
                        case LANGUAGE_CHINESE:
                            ttsLang = t1.setLanguage(Locale.CHINESE);
                            rb2.setChecked(true);
                            break;
                    }

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "The Language is not supported!");
                    } else {
                        Log.i(TAG, "Language Supported.");
                    }
                    Log.i(TAG, "Initialization success.");
                }
            }
        });
        super.onResume();
    }

    @Override
    protected void onPause(){
        Log.w(TAG, "onPause");
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    //---------------------------------------------------------------------
    // Radio button event
    //---------------------------------------------------------------------
    private RadioGroup.OnCheckedChangeListener mRadioButton = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int ttsLang=0;

            switch (i) {
                case R.id.RadioBT_US:
                    ttsLang = t1.setLanguage(Locale.US);
                    SpeakLanguageSet(LANGUAGE_ENGLIST);
                    break;
                case R.id.RadioBT_Chinese:
                    ttsLang = t1.setLanguage(Locale.TRADITIONAL_CHINESE);
                    SpeakLanguageSet(LANGUAGE_CHINESE);
                    break;
            }
            if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                    || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "The Language is not supported!");
            } else {
                Log.i(TAG, "Language Supported.");
            }
            Log.i(TAG, "Initialization success.");
        }
    };

    //=================================================================
    // Language index 儲存/讀取
    //=================================================================
    private int SpeakLanguageGet(){
        return mem_Language.getInt("SpeakLanguage", LANGUAGE_ENGLIST);
    }

    private void SpeakLanguageSet(int aa){
        SharedPreferences.Editor editor = mem_Language.edit(); //獲取編輯器
        editor.putInt("SpeakLanguage", aa);
        editor.apply();
        editor.commit();    //提交
    }
}
