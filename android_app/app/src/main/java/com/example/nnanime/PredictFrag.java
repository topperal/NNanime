package com.example.nnanime;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PredictFrag extends Fragment implements ListenerData {

    private static final String TAG = "PredictFrag";

    FragmentTransaction fTrans;

    //String[] genre = { "India", "USA", "China", "Japan", "Other"};
   // ArrayList<StudioOrGenre> genres;
   // ArrayList<StudioOrGenre> studios;
   Spinner spin_genre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.prediction, null);//всегда нужно для фрагмента, указываем на каком layout работаем


        ((MainActivity)getActivity()).readData.getJSON();

        //System.out.println("finalLine:  "+ finalLine);
        spin_genre = (Spinner) v.findViewById(R.id.spinner_genre);
       // Spinner spin_studio = (Spinner) v.findViewById(R.id.spinner_studio);
        ImageButton but_next = (ImageButton) v.findViewById(R.id.next_to_studio);
        ImageButton but_refresh = (ImageButton) v.findViewById(R.id.udpate);
        EditText manga_score = (EditText) v.findViewById(R.id.manga_score);

        //((MainActivity)getActivity()).manga_score = Double.parseDouble(manga_score.getText().toString());
        //System.out.println("manga score " + manga_score);

        spin_genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(getActivity(),((StudioOrGenre) genres.get(position)).name , Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).currentgenredisp = ((MainActivity)getActivity()).genres.get(position).dispersia;
                ((MainActivity)getActivity()).currentgenrescore = ((MainActivity)getActivity()).genres.get(position).score;
                ((MainActivity)getActivity()).position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((MainActivity) getActivity()).manga_score = Double.parseDouble(manga_score.getText().toString());
                } catch (NumberFormatException e) {
                    ((MainActivity) getActivity()).manga_score = 0;
                }
                if((((MainActivity)getActivity()).manga_score == 0) || (manga_score == null)) {
                    System.out.println("Print manga score, please");
                    Toast.makeText(getActivity(),"Print manga score, please", Toast.LENGTH_LONG).show();
                }
                else {
                    fTrans = getFragmentManager().beginTransaction();
                    fTrans.replace(R.id.frgmCont, ((MainActivity) getActivity()).choosingStudio);
                    fTrans.addToBackStack(null);
                    fTrans.commit();
                }
            }
        });

        but_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).readData.getJSON();
            }
        });

        return v;
    }

    public void getData() {
        //Log.d(TAG,("genrelistener"));
        Collections.sort(((MainActivity)getActivity()).genres);
        ArrayAdapter<StudioOrGenre> adapterGenre =
                new ArrayAdapter<StudioOrGenre>(getActivity(),  android.R.layout.simple_spinner_dropdown_item, ((MainActivity)getActivity()).genres);
        adapterGenre.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spin_genre.setAdapter(adapterGenre);
            }
        });

    }

}
