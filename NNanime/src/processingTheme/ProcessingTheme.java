package processingTheme;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.LMS;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ProcessingTheme {

    public ArrayList<ProcessingTheme.ThemeInfo> getThemeData(int year, String season) {
        String[] result = null;
        ArrayList<ProcessingTheme.ThemeInfo> scoreAll = new ArrayList();
        BufferedReader reader;
        double seas_temp=Double.valueOf(year);
        if(season.equalsIgnoreCase("spring")){
            seas_temp +=0.25;
        }
        else if (season.equalsIgnoreCase("summer")) {
            seas_temp += 0.5;
        }
        else if (season.equalsIgnoreCase("fall")) {
            seas_temp += 0.75;
        }
        else {
            seas_temp+=0;
        }
        try {
            reader = new BufferedReader(new FileReader("rawdata/"+year + season + ".csv"));
            String line = reader.readLine();
            while (line != null) {
                //System.out.println(line);
                result = line.split(";");
                scoreAll.add(new ProcessingTheme.ThemeInfo(Double.parseDouble(result[1]), result[6].toString(), seas_temp));
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scoreAll;
    }

    public void call(int year_end, int season, boolean final_forecasting) {
        String[] seasons = {"winter", "spring", "summer", "fall"};
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        String finalLine = "\"theme\":[";
        if(final_forecasting==true){
            try {
                fileWriter = new FileWriter("rawdata/predictionData.txt",true);//создание файла, в которые записываются данные
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            printWriter = new PrintWriter(fileWriter);
        }
        else{
            try {
                fileWriter = new FileWriter("rawdata/jsons/predictionData"+year_end+seasons[season]+".txt",true);//создание файла, в которые записываются данные
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            printWriter = new PrintWriter(fileWriter);
        }

        HashSet<String> themes = new HashSet<>();//куча объектов, но без повторений (разновидность collection)

        ArrayList<ProcessingTheme.ThemeInfo> themeSeason = new ArrayList();
        for (int i = 2015; i <= year_end; i++ ) {//просматриваем все тайтлы с периода 2015 по 2022 включительно
            for (int j = 0; j < 4; j++) {
                if(i==year_end&&j==season) break;
                themeSeason.addAll(getThemeData(i, seasons[j]));
            }
        }
        for(ProcessingTheme.ThemeInfo nc : themeSeason) {
            //System.out.println(nc.season+" "+nc.score+"  "+nc.name_theme);
            themes.add(nc.name_theme);
        }
        Iterator value = themes.iterator();
        double finalDispersion = 0;
        int allDispersionIteration = 0;
        while(value.hasNext()) {
            String vTheme = (String) value.next();
            System.out.println(vTheme);

            List<ProcessingTheme.ThemeInfo> oneTheme = themeSeason.stream()
                    .filter(e -> e.name_theme.equalsIgnoreCase(vTheme))
                    .collect(Collectors.toList());

            long count = oneTheme.stream().count();
            if (count > 1) {
                //System.out.println("One genre name "+vTheme" size "+count);
                double summ = 0;
                ArrayList<Double> themebyScore = new ArrayList<>();
                for (ProcessingTheme.ThemeInfo nc : oneTheme) {
                    //System.out.println("One genre " + nc.season + " " + nc.score + "  " + nc.name_theme);

                    if (count < 6) {
                        summ += nc.score;
                        themebyScore.add(nc.score);
                    } else {
                        themebyScore.add(nc.score);
                    }
                }
                double res = 0;
                if (count >= 6) {
                    System.out.println("oneGenre  " + themebyScore);
                    res = trainNeuralNetwork(themebyScore);
                    double disp = squareDispersion(res, themebyScore);
                    finalDispersion += disp;
                    allDispersionIteration++;
                    finalLine += "{\"thname\": \"" + vTheme + "\", \"thrait\": " + res + ", \"thdisp\": " + disp + "},\n";
                    // System.out.println("finalDispersion  " + finalDispersion + " allDispersionIteration " + allDispersionIteration);
                } else {
                    res = summ / count;
                    System.out.println("1<x<6 " + res + "\n" + themebyScore);
                    if (count > 1) {
                        double disp = squareDispersion(res, themebyScore);
                        finalDispersion += disp;
                        allDispersionIteration++;
                        finalLine += "{\"thname\": \"" + vTheme + "\", \"thrait\": " + res + ", \"thdisp\": " + disp + "},\n";
                    }
                    //System.out.println("finalDispersion  " + finalDispersion + " allDispersionIteration " + allDispersionIteration);
                }
            }
        }
        //проходим по жанрам, в которых есть только 1 произв ( а следственно только один рейтинг)
        //тогда дисперсия этого жанра (то есть его вес в конечной формуле) будет равен среднему дисперсий других жанров
        value = themes.iterator();
        while(value.hasNext()) {
            String vTheme = (String) value.next();

            List<ProcessingTheme.ThemeInfo> oneTheme = themeSeason.stream()
                    .filter(e -> e.name_theme.equalsIgnoreCase(vTheme))
                    .collect(Collectors.toList());

            long count = oneTheme.stream().count();
            if (count < 2 ) {
                //System.out.println("One theme name "+vTheme+" size "+count);
                double disp = 0;
                double res = 0;
                for (ProcessingTheme.ThemeInfo nc : oneTheme) {
                    //System.out.println("One theme " + nc.season + " " + nc.score + "  " + nc.name_theme);
                    res = nc.score;
                }
                disp = finalDispersion / allDispersionIteration;
                System.out.println("==1 disp " + disp + "  raiting  " + res);
                finalLine += "{\"thname\": \"" + vTheme + "\", \"thrait\": " + res + ", \"thdisp\": " + disp + "},\n";
            }
        }
        finalLine += "],";
        printWriter.print(finalLine);
        printWriter.flush();
    }

    public double trainNeuralNetwork(ArrayList<Double> themebyScore) {
        System.out.println("Time stamp N1:" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:MM").format(new Date()));

        int maxIterations = 10000;
//        NeuralNetwork neuralNet = new MultiLayerPerceptron(4, 8, 1);
        NeuralNetwork neuralNet = new MultiLayerPerceptron(4, 8, 1);
        ((LMS) neuralNet.getLearningRule()).setMaxError(0.001);//0-1
//        ((LMS) neuralNet.getLearningRule()).setLearningRate(0.7);//0-1
        ((LMS) neuralNet.getLearningRule()).setLearningRate(0.08);//0-1
        ((LMS) neuralNet.getLearningRule()).setMaxIterations(maxIterations);//0-1
        TrainingSet trainingSet = new TrainingSet();

        ArrayList<Double> coeff = themebyScore;

        int dent = 10;
        for (int i = 0; i < coeff.size()-4; i++) {
            trainingSet.addElement(new SupervisedTrainingElement(new double[]{coeff.get(i)/dent,coeff.get(i+1)/dent,coeff.get(i+2)/dent,coeff.get(i+3)/dent},new double[]{coeff.get(i+4)/dent}));
        }
        neuralNet.learnInSameThread(trainingSet);
        System.out.println("Time stamp N2:" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:MM").format(new Date()));

        TrainingSet testSet = new TrainingSet();
        testSet.addElement(new TrainingElement(new double[]{coeff.get(coeff.size()-4)/dent,coeff.get(coeff.size()-3)/dent,coeff.get(coeff.size()-2)/dent,coeff.get(coeff.size()-1)/dent}));
        double res = 0;
        for (TrainingElement testElement : testSet.trainingElements()) {
            neuralNet.setInput(testElement.getInput());
            neuralNet.calculate();
            Vector<Double> networkOutput = neuralNet.getOutput();
            System.out.print("Input: " + testElement.getInput());
            System.out.println(" Output: " + networkOutput.get(0)*10);
            res = networkOutput.get(0)*10;
        }
        System.out.println("Time stamp N3:" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:MM").format(new Date()));

        return res;
    }

    double squareDispersion(double a, ArrayList<Double> xArr) {
        double summY = 0;
        for (int i = 0; i < xArr.size(); i++) {
            double diff = (xArr.get(i)-a)*(xArr.get(i)-a);
            summY += diff;
        }
        summY /= xArr.size();
        System.out.println("dispersia = " + summY);
        return summY;
    }

    public class ThemeInfo{
        public double score;
        public String name_theme;
        public double season;
        public ThemeInfo(double score, String name_theme, double season){
            this.score = score;
            this.name_theme = name_theme;
            this.season = season;
        }

    }
}
