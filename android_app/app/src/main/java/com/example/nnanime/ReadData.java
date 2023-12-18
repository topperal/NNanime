package com.example.nnanime;

import android.app.Activity;


import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReadData implements BroadcastData{
    Activity activity;
    String finalLine = "";
    //ListenerData predictFrag1;
    ArrayList<ListenerData> fragments;

    public void getJSON() {
        Request request = new Request.Builder()
                .url("http://37ofps-mchs.ru/predictionData.txt")
                .build();
        OkHttpClient client = new OkHttpClient();

        /*try (Response response = client.newCall(request).execute()) {
            parsingString( response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(true)return;
*/
      /*   try (Response response = client.newCall(request).execute()) {

            finalLine = response.body().string();
            finalLine = finalLine.replace("\n","");
            //System.out.println("Response "+finalLine);
            parsingString(finalLine);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
       Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response)
                    throws IOException {
                finalLine = response.body().string();
                finalLine = finalLine.replace("\n","");
                //System.out.println("Response "+finalLine);
                parsingString(finalLine);
            }

            public void onFailure(Call call, IOException e) {
                //System.out.println("onFailure "+e.getLocalizedMessage());
            }
        });
        //System.out.println(finalLine);
    }

    public void parsingString(String json) {

        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(json);//парсим json объект из переданной строки
            ((MainActivity)activity).coeffA = (Double) obj.get("koeffA");
            ((MainActivity)activity).coeffB = (Double) obj.get("koeffB");
            ((MainActivity)activity).mdisp = (Double) obj.get("mangdisp");
            //System.out.println("coeffA " + ((MainActivity)activity).coeffA + " coeffB " + ((MainActivity)activity).coeffB
             //       + " disp " + ((MainActivity)activity).mdisp);

            JSONArray genreArr = (JSONArray) obj.get("genre");
            Iterator genreItr = genreArr.iterator();
            ((MainActivity)activity).genres = new ArrayList<>();
            while(genreItr.hasNext()) {
                JSONObject test = (JSONObject) genreItr.next();
                String name = (String) test.get("gname");
                //System.out.println(name);
                double score = (Double) test.get("grait");
                double gdisp = (Double) test.get("gdisp");
                ((MainActivity)activity).genres.add(new StudioOrGenre(name, score, gdisp));
            }
            /*for(int i = 0; i < ((MainActivity)activity).genres.size(); i++) {
                System.out.println(((MainActivity)activity).genres.get(i).name + "score " + ((MainActivity)activity).genres.get(i).score
                        + " disp " + ((MainActivity)activity).genres.get(i).dispersia);
            }*/

            JSONArray studioArr = (JSONArray) obj.get("studio");
            Iterator studioItr = studioArr.iterator();
            ((MainActivity)activity).studios = new ArrayList<>();
            //for(int j = 0; j < studioArr.length(); j++) {
            while(studioItr.hasNext()) {
                JSONObject test = (JSONObject) studioItr.next();
                String name = (String) test.get("stname");
                //System.out.println(name);
                double score = (Double) test.get("strait");
                double gdisp = (Double) test.get("stdisp");
                ((MainActivity)activity).studios.add(new StudioOrGenre(name, score, gdisp));
            }

            JSONArray themeArr = (JSONArray) obj.get("theme");
            Iterator themeItr = themeArr.iterator();
            ((MainActivity)activity).themes = new ArrayList<>();
            //for(int j = 0; j < studioArr.length(); j++) {
            ((MainActivity)activity).themes.add(new AddParam(" input theme...", 0, 1));
            while(themeItr.hasNext()) {
                JSONObject test = (JSONObject) themeItr.next();
                String name = (String) test.get("thname");
                //System.out.println(name);
                double score = (Double) test.get("thrait");
                double gdisp = (Double) test.get("thdisp");
                ((MainActivity)activity).themes.add(new AddParam(name, score, gdisp));
            }

            JSONArray ratingArr = (JSONArray) obj.get("rating");
            Iterator ratingItr = ratingArr.iterator();
            ((MainActivity)activity).ratings = new ArrayList<>();
            ((MainActivity)activity).ratings.add(new AddParam(" input rating...", 0, 1));
            while(ratingItr.hasNext()) {
                JSONObject test = (JSONObject) ratingItr.next();
                String name = (String) test.get("rtname");
                double score = (Double) test.get("rtrait");
                double disp = (Double) test.get("rtdisp");
                ((MainActivity)activity).ratings.add(new AddParam(name, score, disp));
            }

            JSONArray demographicArr = (JSONArray) obj.get("demographic");
            Iterator demographicItr = demographicArr.iterator();
            ((MainActivity)activity).demographics = new ArrayList<>();
            ((MainActivity)activity).demographics.add(new AddParam(" input demographic...", 0, 1));
            while(demographicItr.hasNext()) {
                JSONObject test = (JSONObject) demographicItr.next();
                String name = (String) test.get("dmname");
                double score = (Double) test.get("dmrait");
                double disp = (Double) test.get("dmdisp");
                ((MainActivity)activity).demographics.add(new AddParam(name, score, disp));
            }
           /* for(int i = 0; i < ((MainActivity)activity).themes.size(); i++) {
                System.out.println(((MainActivity)activity).themes.get(i).name + "score "
                        + ((MainActivity)activity).themes.get(i).score + " disp "
                        + ((MainActivity)activity).themes.get(i).dispersia);
            }*/
//            Collections.sort(((MainActivity) activity).ratings);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        sendtoAdapter();
    }

    public ReadData(Activity activity){
        fragments = new ArrayList<>();
        this.activity = activity;
    }

    @Override
    public void registerFrag(ListenerData predictFrag) {
        fragments.add(predictFrag);
    }

    @Override
    public void sendtoAdapter() {

       for(ListenerData ld : fragments) {
           ld.getData(activity);
       }
    }
}
