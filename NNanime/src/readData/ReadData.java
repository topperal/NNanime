package readData;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ReadData {

    double coeffA = 0;
    double coeffB = 0;
    double mdisp = 0;

    ArrayList<StudioOrGenre> studios;
    ArrayList<StudioOrGenre> genres;
   public String fromJSONtoString() {
        BufferedReader reader;
        String finalLine = "";
       try {
           reader = new BufferedReader(new FileReader("rawdata/predictionData.txt"));//путь к считываемому файлу
           String line = reader.readLine();//чтение по-строчно
           while (line != null) {//читаем файл по-строчно и выделяем нужные знач из стобцов
               finalLine += line;
               // read next line
               line = reader.readLine();

           }
           reader.close();//закрываем файл
       } catch (IOException e) {
           e.printStackTrace();
       }
       return finalLine;
    }

    public void parsingString() {

       studios = new ArrayList<>();
       genres = new ArrayList<>();

       String json = fromJSONtoString();

        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(json);//парсим json объект из переданной строки
            coeffA = (Double) obj.get("koeffA");
            coeffB = (Double) obj.get("koeffB");
            mdisp = (Double) obj.get("mangdisp");
            System.out.println("coeffA " + coeffA + " coeffB " + coeffB + " disp " + mdisp);

            JSONArray genreArr = (JSONArray) obj.get("genre");
            Iterator genreItr = genreArr.iterator();
            while(genreItr.hasNext()) {
                JSONObject test = (JSONObject) genreItr.next();
                String name = (String) test.get("gname");
                System.out.println(name);
                double score = (Double) test.get("grait");
                double gdisp = (Double) test.get("gdisp");
                genres.add(new StudioOrGenre(name, score, gdisp));
            }
            for(int i = 0; i < genres.size(); i++) {
                System.out.println(genres.get(i).name + "score " + genres.get(i).score + " disp " + genres.get(i).dispersia);
            }

            JSONArray studioArr = (JSONArray) obj.get("studio");
            Iterator studioItr = studioArr.iterator();
            while(studioItr.hasNext()) {
                JSONObject test = (JSONObject) studioItr.next();
                String name = (String) test.get("stname");
                System.out.println(name);
                double score = (Double) test.get("strait");
                double gdisp = (Double) test.get("stdisp");
                studios.add(new StudioOrGenre(name, score, gdisp));
            }
            for(int i = 0; i < studios.size(); i++) {
                System.out.println(studios.get(i).name + "score " + studios.get(i).score + " disp " + studios.get(i).dispersia);
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    class StudioOrGenre{

        public String name;
        public double score;
        public double dispersia;

        public StudioOrGenre(String name, double score,  double dispersia){
            this.score = score;
            this.name = name;
            this.dispersia = dispersia;
        }

    }

}
