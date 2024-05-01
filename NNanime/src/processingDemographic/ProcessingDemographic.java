package processingDemographic;

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

public class ProcessingDemographic {
    public ArrayList<ProcessingDemographic.DemographicInfo> getDemographicData(int year, String season) {
        String[] result = null;
        ArrayList<ProcessingDemographic.DemographicInfo> scoreAll = new ArrayList();
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
                scoreAll.add(new ProcessingDemographic.DemographicInfo(Double.parseDouble(result[1]), result[8].toString(), seas_temp));
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scoreAll;
    }

    public void call(int year_end, int season, boolean final_forecastinh) {
        String[] seasons = {"winter", "spring", "summer", "fall"};
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        String finalLine = "\"demographic\":[";
        if(final_forecastinh==true){
            try {
                fileWriter = new FileWriter("rawdata/predictionData.txt",true);//создание файла, в которые записываются данные
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            printWriter = new PrintWriter(fileWriter);
        }
        else{
            try {
                File file = new File("rawdata/jsons/predictionData"+year_end+seasons[season]+".txt");
//            if(file.exists()) {
//                if(file.delete()){
//                    System.out.println("file.txt файл был удален с корневой папки проекта");
//                }else System.out.println("Файл file.txt не был найден в корневой папке проекта");
//            }
                fileWriter = new FileWriter(file,true);//создание файла, в которые записываются данные
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            printWriter = new PrintWriter(fileWriter);
        }

        HashSet<String> demographics = new HashSet<>();//куча объектов, но без повторений (разновидность collection)
//        String[] seasons = {"winter", "spring", "summer", "fall"};
        ArrayList<ProcessingDemographic.DemographicInfo> demographicSeason = new ArrayList();
        for (int i = 2015; i <= year_end; i++ ) {//просматриваем все тайтлы с периода 2015 по 2022 включительно
            for (int j = 0; j < 4; j++) {
                if(i==year_end&&j==season) break;
                demographicSeason.addAll(getDemographicData(i, seasons[j]));
            }
        }
        for(ProcessingDemographic.DemographicInfo nc : demographicSeason) {
            //System.out.println(nc.season+" "+nc.score+"  "+nc.name_rating);
            demographics.add(nc.name_demographic);
        }
        Iterator value = demographics.iterator();
        double finalDispersion = 0;
        int allDispersionIteration = 0;
        while(value.hasNext()) {
            String vDemographic = (String) value.next();
            System.out.println(vDemographic);

            List<ProcessingDemographic.DemographicInfo> oneDemographic = demographicSeason.stream()
                    .filter(e -> e.name_demographic.equalsIgnoreCase(vDemographic))
                    .collect(Collectors.toList());

            long count = oneDemographic.stream().count();
            if (count > 1) {
                //System.out.println("One genre name "+vTheme" size "+count);
                double summ = 0;
                ArrayList<Double> demographicbyScore = new ArrayList<>();
                for (ProcessingDemographic.DemographicInfo nc : oneDemographic) {
                    //System.out.println("One genre " + nc.season + " " + nc.score + "  " + nc.name_demographic);

                    if (count < 6) {
                        summ += nc.score;
                        demographicbyScore.add(nc.score);
                    } else {
                        demographicbyScore.add(nc.score);
                    }
                }
                double res = 0;
                if (count >= 6) {
                    System.out.println("oneDemographic  " + demographicbyScore);
                    res = trainNeuralNetwork(demographicbyScore);
                    double disp = squareDispersion(res, demographicbyScore);
                    finalDispersion += disp;
                    allDispersionIteration++;
                    finalLine += "{\"dmname\": \"" + vDemographic + "\", \"dmrait\": " + res + ", \"dmdisp\": " + disp + "},\n";
                    // System.out.println("finalDispersion  " + finalDispersion + " allDispersionIteration " + allDispersionIteration);
                } else {
                    res = summ / count;
                    System.out.println("1<x<6 " + res + "\n" + demographicbyScore);
                    if (count > 1) {
                        double disp = squareDispersion(res, demographicbyScore);
                        finalDispersion += disp;
                        allDispersionIteration++;
                        finalLine += "{\"dmname\": \"" + vDemographic + "\", \"dmrait\": " + res + ", \"dmdisp\": " + disp + "},\n";
                    }
                    //System.out.println("finalDispersion  " + finalDispersion + " allDispersionIteration " + allDispersionIteration);
                }
            }
        }
        //проходим по жанрам, в которых есть только 1 произв ( а следственно только один рейтинг)
        //тогда дисперсия этого жанра (то есть его вес в конечной формуле) будет равен среднему дисперсий других жанров
        value = demographics.iterator();
        while(value.hasNext()) {
            String vDemographic = (String) value.next();

            List<ProcessingDemographic.DemographicInfo> oneDemographic = demographicSeason.stream()
                    .filter(e -> e.name_demographic.equalsIgnoreCase(vDemographic))
                    .collect(Collectors.toList());

            long count = oneDemographic.stream().count();
            if (count < 2 ) {
                //System.out.println("One theme name "+vRating+" size "+count);
                double disp = 0;
                double res = 0;
                for (ProcessingDemographic.DemographicInfo nc : oneDemographic) {
                    //System.out.println("One theme " + nc.season + " " + nc.score + "  " + nc.name_rating);
                    res = nc.score;
                }
                disp = finalDispersion / allDispersionIteration;
                System.out.println("==1 disp " + disp + "  raiting  " + res);
                finalLine += "{\"dmname\": \"" + vDemographic + "\", \"dmrait\": " + res + ", \"dmdisp\": " + disp + "},\n";
            }
        }
        finalLine += "],";
        printWriter.print(finalLine);
        printWriter.flush();
    }

    public double trainNeuralNetwork(ArrayList<Double> demographicbyScore) {
        System.out.println("Time stamp N1:" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:MM").format(new Date()));

        int maxIterations = 10000;
//        NeuralNetwork neuralNet = new MultiLayerPerceptron(4, 8, 1);
        NeuralNetwork neuralNet = new MultiLayerPerceptron(4, 8, 1);
        ((LMS) neuralNet.getLearningRule()).setMaxError(0.001);//0-1
//        ((LMS) neuralNet.getLearningRule()).setLearningRate(0.006);//0-1
        ((LMS) neuralNet.getLearningRule()).setLearningRate(0.08);//0-1 0.7 prev
        ((LMS) neuralNet.getLearningRule()).setMaxIterations(maxIterations);//0-1
        TrainingSet trainingSet = new TrainingSet();

        ArrayList<Double> coeff = demographicbyScore;

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

    public class DemographicInfo {
        public double score;
        public String name_demographic;
        public double season;
        public DemographicInfo(double score, String name_demographic, double season){
            this.score = score;
            this.name_demographic = name_demographic;
            this.season = season;
        }

    }
}
