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

        got_it.append(" "+((MainActivity)getActivity()).manga_score);
        main_genre.append(" "+((MainActivity)getActivity()).genres.get(((MainActivity)getActivity()).position).name);
        production_studio.append(" " + ((MainActivity)getActivity()).st_name);

        if(((MainActivity)getActivity()).currentgenredisp>=1) {
            ((MainActivity)getActivity()).currentgenredisp = 0;
        }
        if(((MainActivity)getActivity()).currentstudiodisp>=1) {
            ((MainActivity)getActivity()).currentstudiodisp = 0;
        }

        double denominator = abs(1-((MainActivity)getActivity()).mdisp) + abs(1-((MainActivity)getActivity()).currentgenredisp) + abs(1-((MainActivity)getActivity()).currentstudiodisp);
        //double denominator = abs(1-(0.3175144488989962)) + abs(1-((MainActivity)getActivity()).currentgenredisp) + abs(1-((MainActivity)getActivity()).currentstudiodisp);

        System.out.println("mdisp " + ((MainActivity)getActivity()).mdisp);
        System.out.println("currentgenredisp " + ((MainActivity)getActivity()).currentgenredisp);
        System.out.println("currentstudiodisp " + ((MainActivity)getActivity()).currentstudiodisp);
        System.out.println("denominator " + denominator);
        double manga = (((MainActivity)getActivity()).manga_score*((MainActivity)getActivity()).coeffA+((MainActivity)getActivity()).coeffB) * abs(1-((MainActivity)getActivity()).mdisp) / denominator;
        //double manga = (((MainActivity)getActivity()).manga_score*0.9522726021456265+(0.050872877340424685) * abs(1-(0.3175144488989962))) / denominator;

        double genre = ((MainActivity)getActivity()).currentgenrescore * abs(1-((MainActivity)getActivity()).currentgenredisp) / denominator;
        double studio = ((MainActivity)getActivity()).currentstudioscore * abs(1-((MainActivity)getActivity()).currentstudiodisp) / denominator;
        double result = 0;
        //if(((MainActivity)getActivity()).manga_score > 8.0) {
        //    result = 1.5*manga + genre + studio;
        //}
        //else {
            result = manga + genre + studio;
        //}

        res.append(""+result);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
                ((MainActivity)getActivity()).st_name = "";
            }
        });

        return v;
    }
}
