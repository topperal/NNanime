package finalFormula;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import processingDemographic.ProcessingDemographic;
import processingGenre.ProcessingGenre;
import processingManga.ProcessingManga;
import processingRating.ProcessingRating;
import processingStudio.ProcessingStudio;
import processingTheme.ProcessingTheme;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class FinalFormula {

    public  String[] seasons = new String[] {"winter", "spring", "summer", "fall"};

//    public void openFiles(int year, String season) {
//        BufferedReader reader;
//        try {
//            reader = new BufferedReader(new FileReader("rawdata/"+year + season + ".csv"));
//            String line = reader.readLine();
//            while (line != null) {
//                System.out.println(line);
////                result = line.split(";");
////                scoreAll.add(new ProcessingDemographic.DemographicInfo(Double.parseDouble(result[1]), result[8].toString(), seas_temp));
//                // read next line
//                line = reader.readLine();
//            }
//            reader.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public void create_json(int start, int end) {
//        String[] seasons = new String[] {"winter", "spring", "summer", "fall"};

        ProcessingManga processingManga = new ProcessingManga();
        ProcessingGenre processingGenre = new ProcessingGenre();
        ProcessingTheme processingTheme = new ProcessingTheme();
        ProcessingRating processingRating = new ProcessingRating();
        ProcessingDemographic processingDemographic = new ProcessingDemographic();
        ProcessingStudio processingStudio = new ProcessingStudio();

        for(int i = start; i <= end; i++) {
            for(int j = 0; j < 4; j++) {
//                openFiles(i, seasons[j]);
                processingManga.relationScores(i, j, false);
                processingGenre.call(i,j, false);
                processingTheme.call(i,j, false);
                processingRating.call(i,j, false);
                processingDemographic.call(i, j, false);
                processingStudio.call(i,j, false);
            }
        }
    }
    public double formul(double manga_score, String tg_genre, String tg_studio, String tg_theme, String tg_rating, String tg_demographic, int year, int season) {

        double coeff_a = 0.0;
        double coeff_b = 0.0;
        double disp_manga = 0.0;
        double disp_genre = 0.0;
        double disp_studio = 0.0;
        double disp_theme = 0.0;
        double disp_rating = 0.0;
        double disp_demographic = 0.0;

        double score_genre = 0.0;
        double score_studio = 0.0;
        double score_theme = 0.0;
        double score_rating = 0.0;
        double score_demographic = 0.0;

        String finalLine = "";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("rawdata/jsons/predictionData"+year+seasons[season]+".txt"));
            String line = reader.readLine();

            while (line != null) {
//                System.out.println(line);
                finalLine += line;
//                result = line.split(";");
//                scoreAll.add(new ProcessingDemographic.DemographicInfo(Double.parseDouble(result[1]), result[8].toString(), seas_temp));
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(finalLine);//парсим json объект из переданной строки
            coeff_a = (Double) obj.get("koeffA");
            coeff_b = (Double) obj.get("koeffB");
            disp_manga = (Double) obj.get("mangdisp");

            JSONArray genreArr = (JSONArray) obj.get("genre");
            Iterator genreItr = genreArr.iterator();
            while(genreItr.hasNext()) {
                JSONObject test = (JSONObject) genreItr.next();
                String name = (String) test.get("gname");
                if(name.equalsIgnoreCase(tg_genre)){
                    disp_genre = (Double) test.get("gdisp");
                    score_genre = (Double) test.get("grait");
                    break;
                }
            }
            JSONArray studioArr = (JSONArray) obj.get("studio");
            Iterator studioItr = studioArr.iterator();
            while(studioItr.hasNext()) {
                JSONObject test = (JSONObject) studioItr.next();
                String name = (String) test.get("stname");
                if(name.equalsIgnoreCase(tg_studio)) {
                    disp_studio = (Double) test.get("stdisp");
                    score_studio = (Double) test.get("strait");
                    break;
                }
            }

            JSONArray themeArr = (JSONArray) obj.get("theme");
            Iterator themeItr = themeArr.iterator();
            while(themeItr.hasNext()) {
                JSONObject test = (JSONObject) themeItr.next();
                String name = (String) test.get("thname");
                if(name.equalsIgnoreCase(tg_theme)) {
                    disp_theme = (Double) test.get("thdisp");
                    score_theme = (Double) test.get("thrait");
                    break;
                }
            }

            JSONArray ratingArr = (JSONArray) obj.get("rating");
            Iterator ratingItr = ratingArr.iterator();
            while(ratingItr.hasNext()) {
                JSONObject test = (JSONObject) ratingItr.next();
                String name = (String) test.get("rtname");
                if(name.equalsIgnoreCase(tg_rating)) {
                    disp_rating = (Double) test.get("rtdisp");
                    score_rating = (Double) test.get("rtrait");
                    break;
                }
            }

            JSONArray demographicArr = (JSONArray) obj.get("demographic");
            Iterator demographicItr = demographicArr.iterator();
            while(demographicItr.hasNext()) {
                JSONObject test = (JSONObject) demographicItr.next();
                String name = (String) test.get("dmname");
                if(name.equalsIgnoreCase(tg_demographic)) {
                    disp_demographic = (Double) test.get("dmdisp");
                    score_demographic = (Double) test.get("dmrait");
                    break;
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        System.out.println("coeffA: " + coeff_a + " coeffB: " + coeff_b + " MangaDisp: " + disp_manga + " GenreDisp: " + disp_genre +
                " StudioDisp: " + disp_studio + " ThemeDisp: " + disp_theme + " RatingDisp: " + disp_rating + " DemographicDisp: " + disp_demographic);
        System.out.println("GenreScore: " + score_genre + " StudioScore: " + score_studio + " ThemeScore: " + score_theme +
                " RatingScore: " + score_rating + " DemographicScore: " + score_demographic);

        if(disp_genre>=1.0) disp_genre = 1.0; //prev: disp_genre>=1.0
        if(disp_studio>=1.0) disp_studio = 1.0;
        if(disp_theme>=1.0) disp_theme = 1.0;
        if(disp_rating>=1.0) disp_rating = 1.0;
        if(disp_demographic>=1.0) disp_demographic = 1.0;
//
        if(disp_genre==0.0) disp_genre = 1.0; //prev: disp_genre>=1.0
        if(disp_studio==0.0) disp_studio = 1.0;
        if(disp_theme==0.0) disp_theme = 1.0;
        if(disp_rating==0.0) disp_rating = 1.0;
        if(disp_demographic==0.0) disp_demographic = 1.0;


        double denominator = Math.abs(1-disp_manga)+Math.abs(1-disp_genre)+Math.abs(1-disp_studio)+Math.abs(1-disp_theme)+Math.abs(1-disp_rating)+Math.abs(1-disp_demographic);
        double manga = (manga_score*coeff_a+coeff_b)*Math.abs(1-disp_manga)/denominator;
        double genre = score_genre*Math.abs(1-disp_genre)/denominator;
        double studio = score_studio*Math.abs(1-disp_studio)/denominator;
        double theme = score_theme*Math.abs(1-disp_theme)/denominator;
        double rating = score_rating*Math.abs(1-disp_rating)/denominator;
        double demographic = score_demographic*Math.abs(1-disp_demographic)/denominator;

        double result = 0;
        result = (manga + genre + studio + theme + rating + demographic); //+ theme + rating + demographic
        if(manga_score < 6.4) result = 0.57*manga + genre + studio + theme + rating + demographic;//1 можно
        else if(manga_score>=6.4&manga_score < 6.85) result = 0.725*manga + genre + studio + theme + rating + demographic;//1 можно
        else if (manga_score>=6.85&&manga_score < 7.15) result = 0.775*manga + genre + studio + theme + rating + demographic;
        else if (manga_score>=7.15&&manga_score < 7.5) result = 0.8825*manga + genre + studio + theme + rating + demographic;//1 можно
//        else if (manga_score>=7.5&&manga_score < 7.75) result = 1.0*manga + genre + studio + theme + rating + demographic;
        else if (manga_score>=7.75&&manga_score < 7.85) result = 1.6*manga + genre + studio + theme + rating + demographic;//1 можно
        else if (manga_score>=7.85&&manga_score < 7.95) result = 1.225*manga + genre + studio + theme + rating + demographic;//1 можно
        else if(manga_score >= 8.40 && manga_score < 8.55) result = 1.285*manga + genre + studio + theme + rating + demographic;
        else if(manga_score >= 8.57) result = 1.3*manga + genre + studio + theme + rating + demographic;


        return result;
    }
}
