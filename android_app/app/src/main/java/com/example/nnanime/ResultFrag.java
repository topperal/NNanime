package com.example.nnanime;

import static java.lang.Math.abs;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ResultFrag extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.result, null);//всегда нужно для фрагмента, указываем на каком layout работаем

        ImageButton btn_home = (ImageButton) v.findViewById(R.id.to_home);
        TextView got_it = (TextView) v.findViewById(R.id.got_it);
        TextView main_genre = (TextView) v.findViewById(R.id.main_genre);
        TextView production_studio = (TextView) v.findViewById(R.id.production_studio);
        TextView res = (TextView) v.findViewById(R.id.res);
        TextView head = (TextView) v.findViewById(R.id.header_title);

        head.setText("Result");
        got_it.append(" "+((MainActivity)getActivity()).manga_score);
        main_genre.append(" "+((MainActivity)getActivity()).dg_name);
        production_studio.append(" " + ((MainActivity)getActivity()).st_name);

        for(int i = 0; i < ((MainActivity)getActivity()).currentgenredisp.size(); i++){
            if(((MainActivity)getActivity()).currentgenredisp.get(i)>=1.0) {
                ((MainActivity)getActivity()).currentgenredisp.set(i,0.0);//было 0 если 1 то все равно ничего не меняется
            }
        }

        if(((MainActivity)getActivity()).currentstudiodisp>=1) {
            ((MainActivity)getActivity()).currentstudiodisp = 0;//было 0
        }

        double add_denominator = 0.0;
        if(((MainActivity)getActivity()).themes.get(((MainActivity)getActivity()).position_th).name.charAt(0)!=' '){
            if(((MainActivity)getActivity()).currentthemedisp>=1) {
                ((MainActivity)getActivity()).currentthemedisp = 0;//было 0
            }
            add_denominator += abs(1-((MainActivity)getActivity()).currentthemedisp);
        }
        if(((MainActivity)getActivity()).ratings.get(((MainActivity)getActivity()).position_rt).name.charAt(0)!=' '){
            if(((MainActivity)getActivity()).currentratingdisp>=1) {
                ((MainActivity)getActivity()).currentratingdisp = 0;//было 0
            }
            add_denominator += abs(1-((MainActivity)getActivity()).currentratingdisp);
        }
        if(((MainActivity)getActivity()).demographics.get(((MainActivity)getActivity()).position_dm).name.charAt(0)!=' '){
            if(((MainActivity)getActivity()).currentdemographicdisp>=1) {
                ((MainActivity)getActivity()).currentdemographicdisp = 0;//было 0
            }
            add_denominator += abs(1-((MainActivity)getActivity()).currentdemographicdisp);
        }

        double denominator = abs(1-((MainActivity)getActivity()).mdisp) +  abs(1-((MainActivity)getActivity()).currentstudiodisp) + add_denominator;//abs(1-((MainActivity)getActivity()).currentgenredisp) +
        for(int i = 0; i < ((MainActivity)getActivity()).currentgenredisp.size(); i++){
            denominator += abs(1-((MainActivity)getActivity()).currentgenredisp.get(i));
        }
        //double denominator = abs(1-(0.3175144488989962)) + abs(1-((MainActivity)getActivity()).currentgenredisp) + abs(1-((MainActivity)getActivity()).currentstudiodisp);

        System.out.println("mdisp " + ((MainActivity)getActivity()).mdisp);
        System.out.println("currentgenredisp " + ((MainActivity)getActivity()).currentgenredisp);
        System.out.println("currentstudiodisp " + ((MainActivity)getActivity()).currentstudiodisp);
        System.out.println("denominator " + denominator);

        double manga = (((MainActivity)getActivity()).manga_score*((MainActivity)getActivity()).coeffA+((MainActivity)getActivity()).coeffB) * abs(1-((MainActivity)getActivity()).mdisp) / denominator;
        //double manga = (((MainActivity)getActivity()).manga_score*0.9522726021456265+(0.050872877340424685) * abs(1-(0.3175144488989962))) / denominator;
//        double genre = ((MainActivity)getActivity()).currentgenrescore * abs(1-((MainActivity)getActivity()).currentgenredisp) / denominator;
        double genre = 0.0;
        for(int i = 0; i < ((MainActivity)getActivity()).currentgenredisp.size(); i++){
            genre += ((MainActivity)getActivity()).currentgenrescore.get(i) * abs(1-((MainActivity)getActivity()).currentgenredisp.get(i)) / denominator;
        }
        double studio = ((MainActivity)getActivity()).currentstudioscore * abs(1-((MainActivity)getActivity()).currentstudiodisp) / denominator;
        double theme = ((MainActivity)getActivity()).currentthemescore * abs(1-((MainActivity)getActivity()).currentthemedisp) / denominator;
       double rating = ((MainActivity)getActivity()).currentratingscore * abs(1-((MainActivity)getActivity()).currentratingdisp) / denominator;
        double demographic = ((MainActivity)getActivity()).currentdemographicscore * abs(1-((MainActivity)getActivity()).currentdemographicdisp) / denominator;

        double result = 0;
        //if(((MainActivity)getActivity()).manga_score > 8.0) {
        //    result = 1.5*manga + genre + studio;
        //}
        //else {
            result = manga + genre + studio + theme + rating + demographic;//
        //}

        res.append(""+result);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
                ((MainActivity)getActivity()).st_name = "";
            }
        });

        return v;
    }
}
