import getdata.GetInfo;
import processingDemographic.ProcessingDemographic;
import processingGenre.ProcessingGenre;
import processingManga.MangaCoeff;
import processingManga.ProcessingManga;
import processingRating.ProcessingRating;
import processingStudio.ProcessingStudio;
import processingTheme.ProcessingTheme;

import java.util.ArrayList;

public class Main {

    static void createDataset() {
        boolean nextpage = true;//проверка существует ли следующая страница(всего на стр 25 элементов)
        int page = 1;//номер страницы, с которой парсируем эелементы
        String[] seasons = {"winter", "spring", "summer", "fall"};
        GetInfo getInfo = new GetInfo();

        for (int i = 2015; i < 2023; i++ ) {//просматриваем все тайтлы с периода 2015 по 2022 включительно
            for (int j = 0; j < 3; j++) {
                while (nextpage == true) {//пока существует след стр
                    String fullList = getInfo.getAnimeListbySeason(i, seasons[j], page);//получаем всю информацию об аниме по году и сезону
                    nextpage = getInfo.parseAnime(fullList, i, seasons[j]);//парсим информацию про аниме и получаем: id, score, name studio, main genre
                    page++;//возвращаем true/false в зависимости от существования след стр и переходим на след стр

                    try {//останавливаем поток запросов на 1 с, т.к. макс кол-во запросов 60 в минуту
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                nextpage = true;//сбрасываем страницу для каждого года
                page = 1;
            }
        }
    }

    public static void main(String[] args) {

//        createDataset();
//        ReadData readData = new ReadData();
//        String s = readData.fromJSONtoString();
//        System.out.println(s);
//        readData.parsingString();

//        if(true) return;
        ProcessingManga processingManga = new ProcessingManga();
        ArrayList<MangaCoeff> finalC = (ArrayList<MangaCoeff>) processingManga.relationScores();

        ProcessingGenre processingGenre = new ProcessingGenre();
        processingGenre.call();

        ProcessingTheme processingTheme = new ProcessingTheme();
        processingTheme.call();

        ProcessingRating processingRating = new ProcessingRating();
        processingRating.call();

        ProcessingDemographic processingDemographic = new ProcessingDemographic();
        processingDemographic.call();

        ProcessingStudio processingStudio = new ProcessingStudio();
        processingStudio.call();


    }
}
