package com.example.nnanime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudioAdapter extends RecyclerView.Adapter<StudioAdapter.ViewHolder> implements Filterable {
    Context context;
    LayoutInflater inflater;
    List<StudioOrGenre> titlesFull;
    int selectedPosition = -1;
    public StudioAdapter(Context ctx) {
        this.context = ctx;
        this.inflater = LayoutInflater.from(ctx);
        titlesFull = new ArrayList<>(((MainActivity)context).studios);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudioAdapter.ViewHolder holder, int position) {
        holder.title.setText(((MainActivity)context).studios.get(position).name);
        holder.position = holder.getAdapterPosition();
        holder.folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = holder.getAdapterPosition();
                System.out.println("selectedPosition  " + selectedPosition);

                ((MainActivity)context).currentstudioscore = ((MainActivity)context).studios.get(selectedPosition).score;
                ((MainActivity)context).currentstudiodisp = ((MainActivity)context).studios.get(selectedPosition).dispersia;
                System.out.println("studio score " + ((MainActivity) context).currentstudioscore);
                System.out.println("studio name " + ((MainActivity) context).studios.get(selectedPosition).name);
                ((MainActivity)context).st_name = ((MainActivity)context).studios.get(selectedPosition).name;

                notifyDataSetChanged();
            }
        });
        if (selectedPosition != position) {
//            holder.fileChecked.setChecked(false);
            // remove file from sending file set, so it can`t be more one selected files
            //System.out.println("false " );
            holder.choosing.setChecked(false);
        } else {
//            holder.fileChecked.setChecked(true);
            //System.out.println("true");
            holder.choosing.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        if(((MainActivity)context).studios == null){
            return 0;
        }else {
            return ((MainActivity)context).studios.size();
        }
    }

    //методы, отвечающие за фильтрацию RecycleView
    @Override
    public Filter getFilter() {
        return titlesFilter;
    }

    private Filter titlesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //createObjMess();//обновляем titles перед копирование
            titlesFull = new ArrayList<>(((MainActivity)context).studios);//создаем новый лист, копирующий исх, и проводим над ним операции
            List<StudioOrGenre> filteredList = new ArrayList<>();//создаем отфильтрованный список

            if (constraint == null || constraint.length() == 0) {//если поисковый запрос пуст, то в отфильтрованный список добавляется весь исх
                System.out.println("Studio adapt clear");
                ((MainActivity)context).readData.getJSON();
              try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                titlesFull = new ArrayList<>(((MainActivity)context).studios);
                filteredList.clear();
                filteredList.addAll(((MainActivity)context).studios);


            } else {
                //создаем строку запроса, с которой будем сранивать названия folder и фильтровать
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();
                //для названий папок из исх списка проверяем, включает ли в себя строку поискового запроса
                //если включает - то добавляем этот заголовок в отфильтрованный список
                for (StudioOrGenre header : titlesFull) {
                    if (header.name.toLowerCase(Locale.ROOT).contains(filterPattern)) {

                        //Log.d(TAG, "filter title " + header.title + " color " + header.bg_color);
                        filteredList.add(header);
                    }
                }
            }
            //создаем конечный отфильтрованный результат
            //где значения конечных результатов совпадают с отфильтрованным списком
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        //возвращает результирующий список после фильтрации
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ((MainActivity)context).studios.clear();
            ((MainActivity)context).studios.addAll((List) results.values);
            notifyDataSetChanged(); //дает адаптеру знать, что список элементов изменился и нужно  перерисовать элементы на экране

        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        int position;
        CardView folder;
        CheckBox choosing;
        CheckBox prevchoose;
        int count;
        //RadioButton v;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            folder = itemView.findViewById(R.id.folder);
            title = itemView.findViewById(R.id.textView2);
            choosing = itemView.findViewById(R.id.choosing);
            count = 0;

        /*itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((MainActivity)context).currentstudioscore = ((MainActivity)context).studios.get(position).score;
            ((MainActivity)context).currentstudiodisp = ((MainActivity)context).studios.get(position).dispersia;
            System.out.println("score " + ((MainActivity) context).currentstudioscore);
            ((MainActivity)context).st_name = ((MainActivity)context).studios.get(position).name;
        }
    });*/

        }
    }

}
