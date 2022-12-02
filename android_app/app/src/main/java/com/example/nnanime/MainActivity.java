package com.example.nnanime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.StrictMode;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    PredictFrag prediction;//создаем фрагмент
    FragmentTransaction fTrans;
    public double coeffA = 0;
    public double coeffB = 0;
    public double mdisp = 0;

    public ArrayList<StudioOrGenre> studios;
    public ArrayList<StudioOrGenre> genres;

    public StudioFrag choosingStudio;//создаем фрагмент
    public ReadData readData;
    public double currentgenredisp = 0;
    public double currentgenrescore = 0;
    public double currentstudioscore = 0;
    public double currentstudiodisp = 0;
    public int position = -1;
    public String st_name = "";
    public double manga_score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     //  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
     //  StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        readData = new ReadData(this);

        choosingStudio = new StudioFrag();
        //открывает фрагмент при включении приложения
        prediction = new PredictFrag();
        readData.registerFrag(prediction);
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmCont, prediction);
        fTrans.addToBackStack(null);
        fTrans.commit();

    }

}