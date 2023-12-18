package com.example.nnanime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> implements Filterable {
    Context context;
    LayoutInflater inflater;
    List<StudioOrGenre> titlesFull;
    int selectedPosition = -1;
    List<Integer> checkPositions = new ArrayList<>();
    public GenreAdapter(Context ctx) {
        this.context = ctx;
        this.inflater = LayoutInflater.from(ctx);
        titlesFull = new ArrayList<>(((MainActivity)context).genres);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.ViewHolder holder, int position) {
        Integer alredyChecked = -1;
        selectedPosition = -1;

        holder.title.setText(((MainActivity)context).genres.get(position).name);

        holder.position = holder.getAdapterPosition();

        holder.folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedPosition = holder.getAdapterPosition();
                System.out.println("selectedPosition  " + selectedPosition);

                if (checkPositions.contains(selectedPosition)) {
                    // Если текущая позиция уже выбрана, снимаем выбор
                    checkPositions.remove(Integer.valueOf(selectedPosition));
                    ((MainActivity)context).currentgenrescore.remove(((MainActivity)context).genres.get(selectedPosition).score);
                    ((MainActivity)context).currentgenredisp.remove(((MainActivity)context).genres.get(selectedPosition).dispersia);
                } else {
                    // В противном случае добавляем позицию в выбранные
                    checkPositions.add(selectedPosition);
                    ((MainActivity)context).currentgenrescore.add(((MainActivity)context).genres.get(selectedPosition).score);
                    ((MainActivity)context).currentgenredisp.add(((MainActivity)context).genres.get(selectedPosition).dispersia);
                }


                System.out.println("genre score " + ((MainActivity) context).currentgenrescore);
                System.out.println("genre name " + ((MainActivity) context).genres.get(selectedPosition).name);
                ((MainActivity)context).dg_name="";
                for(int n: checkPositions) {
                    ((MainActivity)context).dg_name+=((MainActivity)context).genres.get(n).name+", ";
                }
                if(!((MainActivity)context).dg_name.equals("")){
                    ((MainActivity)context).dg_name = ((MainActivity)context).dg_name.substring(0,((MainActivity)context).dg_name.length()-2);
                }
//                ((MainActivity)context).dg_name = ((MainActivity)context).genres.get(selectedPosition).name;

                notifyDataSetChanged();

            }
        });

//        if(selectedPosition > -1) {
//            for (Integer checked : checkPositions) {
//
//                if (checked.equals(selectedPosition)) {
//                    alredyChecked = checked;
//                    break;
//                }
//            }
//
//
//            if (alredyChecked.equals(new Integer(-1))) {
//////            holder.fileChecked.setChecked(false);
////            // remove file from sending file set, so it can`t be more one selected files
////            //System.out.println("false " );
//                checkPositions.remove(alredyChecked);
//                holder.choosing.setChecked(true);
//            } else {
//////            holder.fileChecked.setChecked(true);
//                //System.out.println("true");
//                checkPositions.add(alredyChecked);
//                holder.choosing.setChecked(false);
//            }
//        }
        holder.choosing.setChecked(checkPositions.contains(position));
    }

    @Override
    public int getItemCount() {
        if(((MainActivity)context).genres == null){
            return 0;
        }else {
            return ((MainActivity)context).genres.size();
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
            titlesFull = new ArrayList<>(((MainActivity)context).genres);//создаем новый лист, копирующий исх, и проводим над ним операции
            List<StudioOrGenre> filteredList = new ArrayList<>();//создаем отфильтрованный список

            if (constraint == null || constraint.length() == 0) {//если поисковый запрос пуст, то в отфильтрованный список добавляется весь исх
                System.out.println("Genre adapt clear");
                ((MainActivity)context).readData.getJSON();
              try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                titlesFull = new ArrayList<>(((MainActivity)context).genres);
                filteredList.clear();
                filteredList.addAll(((MainActivity)context).genres);


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
            ((MainActivity)context).genres.clear();
            ((MainActivity)context).genres.addAll((List) results.values);
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
