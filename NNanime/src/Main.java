import finalFormula.FinalFormula;
import getdata.GetInfo;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import processingDemographic.ProcessingDemographic;
import processingGenre.ProcessingGenre;
import processingManga.MangaCoeff;
import processingManga.ProcessingManga;
import processingRating.ProcessingRating;
import processingStudio.ProcessingStudio;
import processingTheme.ProcessingTheme;

import java.io.*;
import java.util.ArrayList;

public class Main {

    public static String[] seasons = {"winter", "spring", "summer", "fall"};

    static void createDataset(int start, int end) {
        boolean nextpage = true;//проверка существует ли следующая страница(всего на стр 25 элементов)
        int page = 1;//номер страницы, с которой достаем эелементы
        String[] seasons = {"winter", "spring", "summer", "fall"};
        GetInfo getInfo = new GetInfo();

        for (int i = start; i <= end; i++ ) {//просматриваем все тайтлы с периода start по end включительно
            for (int j = 0; j < 4; j++) {
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

    public static void upload_file(){

        FTPClient ftp = new FTPClient();;
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        try {
            ftp.connect("185.253.219.219", 21);
            ftp.enterLocalPassiveMode();
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new IOException("Exception in connecting to FTP Server");
            }

            

            File file = new File("rawdata/predictionData.txt");
            ftp.storeFile("public_html/predictionData.txt", new FileInputStream(file));


            ftp.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void check_rmse(int target_year, int target_season){
        FinalFormula finalFormula = new FinalFormula();
        String tg_genre = "";
        String tg_studio = "";
        double manga_score = 0.0;
        String tg_theme = "";
        String tg_rating = "";
        String tg_demographic = "";
        String[] result = null;
        ArrayList<Double> predict_score = new ArrayList<>();
        ArrayList<Double> real_score = new ArrayList<>();
        ArrayList<Double> m_score = new ArrayList<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("rawdata/"+ target_year + seasons[target_season] + ".csv"));//путь к считываемому файлу
            String line = reader.readLine();//чтение по-строчно
            while (line != null) {//читаем файл по-строчно и выделяем нужные знач из столбцов
                result = line.split(";");
                tg_studio = result[2];
                tg_genre = result[3];
                manga_score = Double.parseDouble(result[5]);
                tg_theme = result[6];
                tg_rating = (result[7]);
                tg_demographic = (result[8]);
                real_score.add(Double.parseDouble(result[1]));
                m_score.add(manga_score);
                predict_score.add(finalFormula.formul(manga_score,tg_genre,tg_studio,tg_theme, tg_rating, tg_demographic, target_year, target_season));
                // read next line
                line = reader.readLine();
            }
            reader.close();//закрываем файл
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(m_score);
        System.out.println(real_score);
        System.out.println(predict_score);

        double rmse = 0.0;
        for(int i = 0; i < real_score.size(); i++) {
            rmse += (real_score.get(i)-predict_score.get(i))*(real_score.get(i)-predict_score.get(i));
        }
        double ans = Math.sqrt(rmse/real_score.size());
        System.out.println(ans);
    }

    public static void main(String[] args) {

        int start = 2024;
        int end = 2024;

//        createDataset(start, end);//создание датасета за указанный интервал
////        блок для создания прогноза и последующего сравнения
//        FinalFormula finalFormula = new FinalFormula();
//        finalFormula.create_json(start, end);//создание json, содержащего предсказанный значения, если обучаться на данных с года start по год end
//
        int target_year = 2024;
        int target_season = 0;

        check_rmse(target_year, target_season);//проверка модели - сравниваем предсказанные значения с известными
        if(true) return;

//        блок для создания json, чтобы отправить его на сервер
        int last_year = 2024;//последний известный год (т.е. текущий или предыдуший, если в данный момент зима)
        int last_season = 0;//последний известный сезон (если в данный момент зима, то последний известный сезон - осень)

        ProcessingManga processingManga = new ProcessingManga();
        ArrayList<MangaCoeff> finalC = (ArrayList<MangaCoeff>) processingManga.relationScores(last_year, last_season, true);

        ProcessingGenre processingGenre = new ProcessingGenre();
        processingGenre.call(last_year, last_season, true);

        ProcessingTheme processingTheme = new ProcessingTheme();
        processingTheme.call(last_year, last_season, true);

        ProcessingRating processingRating = new ProcessingRating();
        processingRating.call(last_year, last_season, true);

        ProcessingDemographic processingDemographic = new ProcessingDemographic();
        processingDemographic.call(last_year, last_season, true);

        ProcessingStudio processingStudio = new ProcessingStudio();
        processingStudio.call(last_year, last_season, true);

        upload_file();//загрузка получившегося json с предсказанниями на сервер

    }
}