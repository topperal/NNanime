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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;

public class AdditionalParametrs extends Fragment implements ListenerData {

    private static final String TAG = "AddtionalFrag";

    FragmentTransaction fTrans;
    ArrayAdapter<AddParam> adapterTheme;
    ArrayAdapter<AddParam> adapterRating;
    ArrayAdapter<AddParam> adapterDemographic;
    Spinner spin_theme;
    Spinner spin_rating;
    Spinner spin_demographic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.additional_parametrs, null);//всегда нужно для фрагмента, указываем на каком layout работаем


        ((MainActivity)getActivity()).readData.getJSON();

        spin_theme = (Spinner) v.findViewById(R.id.spinner_theme);
        spin_rating = (Spinner) v.findViewById(R.id.spinner_rating);
        spin_demographic = (Spinner) v.findViewById(R.id.spinner_demographic);
        ImageButton btn_next = (ImageButton) v.findViewById(R.id.btn_next);
        ImageButton btn_prev = (ImageButton) v.findViewById(R.id.previos);
        TextView head = (TextView) v.findViewById(R.id.header_title);
        head.setText("Additional Parametrs");
        TextView rule = (TextView) v.findViewById(R.id.rules);
        rule.setText("If you have some of these parametrs then please entry them. If you don't have some or all of them - just pass this stage.");


        spin_theme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(getActivity(),((StudioOrGenre) genres.get(position)).name , Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).currentthemedisp = ((MainActivity)getActivity()).themes.get(position).dispersia;
                ((MainActivity)getActivity()).currentthemescore = ((MainActivity)getActivity()).themes.get(position).score;
                ((MainActivity)getActivity()).position_th = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });
        spin_theme.setAdapter(adapterTheme);

        spin_demographic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(getActivity(),((StudioOrGenre) genres.get(position)).name , Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).currentdemographicdisp = ((MainActivity)getActivity()).demographics.get(position).dispersia;
                ((MainActivity)getActivity()).currentdemographicscore = ((MainActivity)getActivity()).demographics.get(position).score;
                ((MainActivity)getActivity()).position_dm = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });
        spin_demographic.setAdapter(adapterDemographic);

        spin_rating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(getActivity(),((StudioOrGenre) genres.get(position)).name , Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).currentratingdisp = ((MainActivity)getActivity()).ratings.get(position).dispersia;
                ((MainActivity)getActivity()).currentratingscore = ((MainActivity)getActivity()).ratings.get(position).score;
                ((MainActivity)getActivity()).position_rt = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });
        spin_rating.setAdapter(adapterRating);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    ((MainActivity) getActivity()).manga_score = Double.parseDouble(manga_score.getText().toString());
//                } catch (NumberFormatException e) {
//                    ((MainActivity) getActivity()).manga_score = 0;
//                }
//                if((((MainActivity)getActivity()).manga_score == 0) || (manga_score == null)) {
//                    System.out.println("Print manga score, please");
//                    Toast.makeText(getActivity(),"Print manga score, please", Toast.LENGTH_LONG).show();
//                }
//                else {
                ResultFrag resultFrag;
                resultFrag = new ResultFrag();
                fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.frgmCont,resultFrag);
                fTrans.addToBackStack(null);
                fTrans.commit();
//                    fTrans = getFragmentManager().beginTransaction();
//                    fTrans.replace(R.id.frgmCont, ((MainActivity) getActivity()).choosingStudio);
//                    fTrans.addToBackStack(null);
//                    fTrans.commit();
//                }
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        return v;
    }

    public void getData(Activity activity) {
        Log.d(TAG,("getData size "+((MainActivity)activity)));

        for(int i = 0; i < ((MainActivity)activity).themes.size(); i++) {
            System.out.println(((MainActivity)activity).themes.get(i).name + "score "
                    + ((MainActivity)activity).themes.get(i).score + " disp "
                    + ((MainActivity)activity).themes.get(i).dispersia);
        }
        Collections.sort(((MainActivity)activity).themes);
        adapterTheme =
                new ArrayAdapter<AddParam>(activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        ((MainActivity)activity).themes);
        adapterTheme.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        for(int i = 0; i < ((MainActivity)activity).demographics.size(); i++) {
            System.out.println(((MainActivity)activity).demographics.get(i).name + "score "
                    + ((MainActivity)activity).demographics.get(i).score + " disp "
                    + ((MainActivity)activity).demographics.get(i).dispersia);
        }
       Collections.sort(((MainActivity)activity).demographics);
        adapterDemographic =
                new ArrayAdapter<AddParam>(activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        ((MainActivity)activity).demographics);
        adapterDemographic.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        for(int i = 0; i < ((MainActivity)activity).ratings.size(); i++) {
            System.out.println(((MainActivity)activity).ratings.get(i).name + "score "
                    + ((MainActivity)activity).ratings.get(i).score + " disp "
                    + ((MainActivity)activity).ratings.get(i).dispersia);
        }
       Collections.sort(((MainActivity)activity).ratings);
        adapterRating =
                new ArrayAdapter<AddParam>(activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        ((MainActivity)activity).ratings);
        adapterRating.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                spin_theme.setAdapter(adapterTheme);
//            }
//        });

    }

}
