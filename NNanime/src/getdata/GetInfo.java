package getdata;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;

public class GetInfo {

    public String getAnimeListbySeason(int year, String season, int page){//вызываем и получаем http request
        HttpResponse<String> response;//ответ, содержащий строку, из котором будем извлекать json объекты
        try {
            HttpClient client = HttpClient.newBuilder()//создание HttpClient client
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()//создание запроса
                    .uri(new URI("https://api.jikan.moe/v4/seasons/"+year+"/"+season+"?page="+page))
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    public boolean parseAnime (String generaljson, int year, String season) {//парсим страницу, содержащую аниме в некотором сезоне некоторого года
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;

        Boolean pagenext = null;//для проверки существования след стр, так как миксимум 25 аниме на стр, а их обычно больше
        String manga_score = null;//рейтинг манги
        String id_manga = null;//id манги (нужно для получения рейтинга манги)
        String genre = null;//"главный" жанр
        String studioname = null;//название студии-создателя
        String anime_score = null;//рейтинг аниме
        String id_anime = null;//id аниме (нужно для получения рейтинга, жанра, названия студии)

        // Считываем json
        try {
            fileWriter = new FileWriter("rawdata/"+year + season + ".csv",true);//создание файла, в которые записываются данные
            printWriter = new PrintWriter(fileWriter);
            JSONObject obj = (JSONObject) new JSONParser().parse(generaljson);//парсим json объект из переданной строки(список всех аниме в сезоне)
            // Достаем массив номеров
            JSONArray animeArr = (JSONArray) obj.get("data");//создание массива, содержащие данные из data
            JSONObject paginationList = (JSONObject) obj.get("pagination");//объект, содержащий информацию о способе отображения произведений

    try {//проверяем, существует ли следующая страница в списке
        pagenext = (Boolean) paginationList.get("has_next_page");
    }catch(Exception ex){
        System.out.println("PAGELIST "+obj);
    }
            Iterator animeItr = animeArr.iterator();//итератор по массиву всех произведений в списке
            // Выводим в цикле данные массива
            while (animeItr.hasNext()) {//пока существует след элемент
                JSONObject test = (JSONObject) animeItr.next();//создаем json объект для конкретного произв, на котором находится итератор
                JSONArray studioArr = (JSONArray) test.get("studios");//список всех студий
                studioname = null;
                if(studioArr.size()==0){//если массив пуст, то студия неизвестна
                    studioname = "UNKNOWN";
                }else{//в противном случае, считаем за "главную" студию первую в списке
                    JSONObject st = (JSONObject) studioArr.get(0);//объект, содержащий всю инфо о студии
                    studioname = (String) st.get("name");//достаем название студии
                }
                JSONArray genreArr = (JSONArray) test.get("genres");//список всех жанров
                genre = null;
                if(genreArr.size()==0){//если список пуст, то жанр считается неизвестным
                    genre = "UNKNOWN";
                }else {//в противном случае, считаем за "главный" жанр первый в списке
                    JSONObject gn = (JSONObject) genreArr.get(0);//объект, содержащий всю инфо о жанре
                    genre = (String) gn.get("name");//достаем название жанра
                }//проверка, что у произв есть рейтинг
                if(test.get("score")!=null){
                    String className = test.get("score").getClass().getName();//узнаем тип данных, соотв полученному рейтингу
                    if(className.indexOf("Double")>=0) {//когда рейтинг не целочисленный
                        anime_score = Double.toString((Double) test.get("score"));
                    }else if(className.indexOf("Long")>=0){//т. к. возможна ситуация, когда рейтинг целочисленный
                        anime_score = Long.toString((Long) test.get("score"));
                    }else{
                        anime_score = null;
                    }
                }else {
                    anime_score = null;
                }
                id_anime = Long.toString((Long)test.get("mal_id"));//достаем id аниме из json объекта
                System.out.println("- id: " + id_anime + "  - score: " + anime_score +
                        "  - studio: " + studioname + "  - genre: " + genre);

                Long mal_id = (Long) test.get("mal_id");//преобразуем Object к int
                String allrelations = getMangaIdbyAnimeId(mal_id);//возвращаем все связанные произв по id аниме
                //System.out.println("id manga" + allrelations);
                while (allrelations.indexOf("TimeoutException")>=0) {//если сервер падает из-за частоты запросов, то продолжаем их посылать, пока не пройдем
                    allrelations = getMangaIdbyAnimeId(mal_id);//возвращаем все связанные произв по id аниме

                    try {//останавливаем поток запросов на 1 с, т.к. макс кол-во запросов 60 в минуту
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                try {//останавливаем поток запросов на 1 с, т.к. макс кол-во запросов 60 в минуту
                    Thread.sleep(1100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                id_manga = parseRelation(allrelations);//передаем список всех связанных произв и выделяем то произв, для которого аниме яв-ся адаптацией
                try {//останавливаем поток запросов на 1 с, т.к. макс кол-во запросов 60 в минуту
                    Thread.sleep(1100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (id_manga!=null) {//если существует манга, которую адаптировали, то достаем её рейтинг
                    String allInfoAboutManga = getMangabyId(id_manga);//выделяем всю инфо манги по её id
                    //System.out.println(" allInfoAboutManga: " +allInfoAboutManga);
                    while (allInfoAboutManga.indexOf("TimeoutException")>=0) {//если сервер падает из-за частоты запросов, то продолжаем их посылать, пока не пройдем
                        allInfoAboutManga = getMangabyId(id_manga);//выделяем всю инфо манги по её id
                        try {//останавливаем поток запросов на 1 с, т.к. макс кол-во запросов 60 в минуту
                            Thread.sleep(1100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    try {//останавливаем поток запросов на 1 с, т.к. макс кол-во запросов 60 в минуту
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    manga_score = parseManga(allInfoAboutManga);//передаем строку, которую парсим и выделяем рейтинг манги
                    try {//останавливаем поток запросов на 1 с, т.к. макс кол-во запросов 60 в минуту
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }//если все поля непусты и известны, то записываем данные в файл
                if ((anime_score != null) && (studioname.equalsIgnoreCase("UNKNOWN")==false) && (genre.equalsIgnoreCase("UNKNOWN")==false)
            && (id_manga != null) && (manga_score!=null)) {
                    String finalLine =  id_anime + ";" + anime_score + ";" + studioname + ";" + genre + ";" +id_manga + ";" + manga_score + "\n";
                    printWriter.print (finalLine);
                    printWriter.flush();
                    System.out.println(finalLine);
                    }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {//чтобы закрыть файл в любом случае, даже если он пустой
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
        }
        return pagenext;
    }

    public String getMangaIdbyAnimeId(Long id){//получаем все связанные произв по id аниме
        HttpResponse<String> response;//ответ, содержащий строку, из котором будем извлекать json объекты
        try {
            HttpClient client = HttpClient.newBuilder()//создание HttpClient client
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.jikan.moe/v4/anime/" + id + "/relations"))
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    public String parseRelation (String generaljson) {//из спика всех связанных произв достаем id манги, которую адаптируют
        String id_manga = null;
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(generaljson);//преобразуем строку в json объект
            JSONArray relationArr = (JSONArray) obj.get("data");//создаем массив из связанных произв
            if(relationArr == null){
                return id_manga;
            }
            Iterator entryItr = relationArr.iterator();//итератор по массиву всех произведений в списке

            while (entryItr.hasNext()) {//пока существует след элемент
                JSONObject test = (JSONObject) entryItr.next();//преобразуем элемент итератора в json объект
                if (test.get("relation").toString().equalsIgnoreCase("Adaptation")) {//если в списке связанных произв есть поле Адаптация
                    JSONArray entryArr = (JSONArray) test.get("entry");
                    JSONObject en = (JSONObject) entryArr.get(0);
                    id_manga = en.get("mal_id").toString();
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return id_manga;
    }

    public String getMangabyId(String id){
        HttpResponse<String> response;
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.jikan.moe/v4/manga/" + Integer.parseInt(id)))
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    public String parseManga (String generaljson) {//получаем рейтинг манги
        String manga_score = null;
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(generaljson);
            JSONObject data = (JSONObject) obj.get("data");
            if(data!=null) {
                if (data.containsKey("score")) {
                    System.out.println("score:  " + data.get("score"));
                    if (data.get("score") != null) {
                        String className = data.get("score").getClass().getName();
                        if (className.indexOf("Double")>=0) {
                            manga_score = Double.toString((Double) data.get("score"));
                        } else if (className.indexOf("Long")>=0) {
                            manga_score = Long.toString((Long) data.get("score"));
                        } else {
                            manga_score = null;
                        }
                    }
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return manga_score;
    }
}
