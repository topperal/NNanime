package com.example.nnanime;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class StudioFrag extends Fragment implements ListenerData  {

    FragmentTransaction fTrans;
    ResultFrag resultFrag;
    private static final String TAG = "StudioFrag";
    com.example.nnanime.StudioAdapter adapter;//объявляем адаптер
    public RecyclerView listofstudio;
    SearchView find;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.choosing_studio, null);//всегда нужно для фрагмента, указываем на каком layout работаем

        ImageButton btn_prev = (ImageButton) v.findViewById(R.id.previos);
        ImageButton btn_next = (ImageButton) v.findViewById(R.id.next);
        listofstudio = (RecyclerView) v.findViewById(R.id.listofstudio);
        find = (SearchView) v.findViewById(R.id.find);

        Collections.sort(((MainActivity) getActivity()).studios);
        adapter = new com.example.nnanime.StudioAdapter(getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2,
                GridLayoutManager.VERTICAL, false);
        //listofstudio.setLayoutManager(new LinearLayoutManager(getActivity()));
        listofstudio.setLayoutManager(gridLayoutManager);
        listofstudio.setAdapter(adapter);

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
                resultFrag = new ResultFrag();
                fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.frgmCont,resultFrag);
                fTrans.addToBackStack(null);
                fTrans.commit();
            }
        });

        return v;
    }

    public void getData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });

    }


}
