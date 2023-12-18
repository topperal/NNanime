package com.example.nnanime;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

public class GenreFrag extends Fragment implements ListenerData  {

    FragmentTransaction fTrans;
    private static final String TAG = "GenreFrag";
    GenreAdapter adapter;//объявляем адаптер
    public RecyclerView listofgenre;
    SearchView find;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.choosing_genre, null);//всегда нужно для фрагмента, указываем на каком layout работаем

        ImageButton btn_prev = (ImageButton) v.findViewById(R.id.previos);
        ImageButton btn_next = (ImageButton) v.findViewById(R.id.next);
        TextView head = (TextView) v.findViewById(R.id.header_title);
        listofgenre = (RecyclerView) v.findViewById(R.id.listofgenre);
        find = (SearchView) v.findViewById(R.id.find);
        head.setText("Selection Genres");

        Collections.sort(((MainActivity) getActivity()).genres);
        adapter = new GenreAdapter(getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2,
                GridLayoutManager.VERTICAL, false);
        //listofstudio.setLayoutManager(new LinearLayoutManager(getActivity()));
        listofgenre.setLayoutManager(gridLayoutManager);
        listofgenre.setAdapter(adapter);

        //описываем работу поисковой строки
        find.clearFocus();//сбросить поисковик(чтобы он не искал предыдущий запрос)
        find.setOnQueryTextListener(new SearchView.OnQueryTextListener() {//работа с поисковым запросом
            @Override
            public boolean onQueryTextSubmit(String s) {//получаем конечный запрос
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {//обновляется при каждом изменении поискового запроса
                adapter.getFilter().filter(s);

                return true;
            }

        });



        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( ((MainActivity) getActivity()).currentgenredisp.isEmpty()){
                    Toast.makeText(getActivity(),"Select atleast one genre, please", Toast.LENGTH_LONG).show();
                }
                else {
                    fTrans = getFragmentManager().beginTransaction();
                    fTrans.replace(R.id.frgmCont, ((MainActivity) getActivity()).choosingStudio);
                    fTrans.addToBackStack(null);
                    fTrans.commit();
                }
            }
        });

        return v;
    }

    public void getData(Activity activity) {

//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });

    }


}
