package processingStudio;

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

public class ProcessingStudio {

    public ArrayList<StudioInfo> getStudioData(int year, String season) {
        String[] result = null;
        ArrayList<StudioInfo> scoreAll = new ArrayList();
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
                scoreAll.add(new StudioInfo(Double.parseDouble(result[1]), result[2].toString(), seas_temp));
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
        String finalLine = "\"studio\":[";
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

        HashSet<String> studios = new HashSet<>();//куча объектов, но без повторений (разновидность collection)

        ArrayList<StudioInfo> studioSeason = new ArrayList();
        for (int i = 2015; i <= year_end; i++ ) {//просматриваем все тайтлы с периода 2015 по 2022 включительно
            for (int j = 0; j < 4; j++) {
                if(i==year_end&&j==season) break;
                studioSeason.addAll(getStudioData(i, seasons[j]));
            }
        }
        for(StudioInfo nc : studioSeason) {
            //System.out.println(nc.season+" "+nc.score+"  "+nc.name_studio);
            studios.add(nc.name_studio);
        }
        Iterator value = studios.iterator();
        double finalDispersion = 0;
        int allDispersionIteration = 0;
        while(value.hasNext()) {
            String vStudio = (String) value.next();
            System.out.println(vStudio);

            List<StudioInfo> oneStudio = studioSeason.stream()
                    .filter(e -> e.name_studio.equalsIgnoreCase(vStudio))
                    .collect(Collectors.toList());

            long count = oneStudio.stream().count();
            if(count > 1) {
                System.out.println("One studio name " + vStudio + " size " + count);

                double summ = 0;
                ArrayList<Double> studiobyScore = new ArrayList<>();
                for (StudioInfo nc : oneStudio) {
                    System.out.println("One studio " + nc.season + " " + nc.score + "  " + nc.name_studio);

                    if (count < 6) {
                        summ += nc.score;
                        studiobyScore.add(nc.score);
                    } else {
                        studiobyScore.add(nc.score);
                    }
                }
                double res = 0;
                if (count >= 6) {
                    res = trainNeuralNetwork(studiobyScore);
                    double disp = squareDispersion(res, studiobyScore);
                    finalDispersion += disp;
                    System.out.println("oneStudio  " + studiobyScore);
                    allDispersionIteration++;
                    finalLine += "{\"stname\": \"" + vStudio + "\", \"strait\": " + res + ", \"stdisp\": " + disp + "},\n";
                    System.out.println("finalDispersion  " + finalDispersion + " allDispersionIteration " + allDispersionIteration + " disp " + disp);
                } else {
                    res = summ / count;
                    System.out.println("<6 " + res + "\n" + studiobyScore);
                    double disp = squareDispersion(res, studiobyScore);
                    finalDispersion += disp;
                    allDispersionIteration++;
                    finalLine += "{\"stname\": \"" + vStudio + "\", \"strait\": " + res + ", \"stdisp\": " + disp + "},\n";
                    System.out.println("finalDispersion  " + finalDispersion + " allDispersionIteration " + allDispersionIteration+ " disp " + disp);
                }
            }
        }
        //проходим по студиям, в которых есть только 1 произв (а следственно только один рейтинг)
        //тогда дисперсия этой студии (то есть её вес в конечной формуле) будет равен среднему дисперсий других студий
        value = studios.iterator();
        while(value.hasNext()) {
            String vStudio = (String) value.next();
            List<StudioInfo> oneStudio = studioSeason.stream()
                    .filter(e -> e.name_studio.equalsIgnoreCase(vStudio))
                    .collect(Collectors.toList());

            long count = oneStudio.stream().count();
            if(count < 2) {
                System.out.println("One studio name " + vStudio + " size " + count);
                double res = 0;
                for (StudioInfo nc : oneStudio) {
                    //System.out.println("One studio " + nc.season + " " + nc.score + "  " + nc.name_studio);
                    res = nc.score;
                }
                double disp = 0;
                disp = finalDispersion / allDispersionIteration;
                if (value.hasNext() == false) {
                    finalLine += "{\"stname\": \"" + vStudio + "\", \"strait\": " + res + ", \"stdisp\": " + disp + "}\n";
                }
                else {
                    finalLine += "{\"stname\": \"" + vStudio + "\", \"strait\": " + res + ", \"stdisp\": " + disp + "},\n";
                }
                //System.out.println("==1 disp " + disp + " raiting " + res);
            }
        }
        finalLine += "]}";
        printWriter.print(finalLine);
        printWriter.flush();
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

    public double trainNeuralNetwork(ArrayList<Double> studiobyScore) {
        System.out.println("Time stamp N1:" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:MM").format(new Date()));

        int maxIterations = 10000;
//        NeuralNetwork neuralNet = new MultiLayerPerceptron(4, 8, 1);
        NeuralNetwork neuralNet = new MultiLayerPerceptron(4, 8, 1);
        ((LMS) neuralNet.getLearningRule()).setMaxError(0.001);//0-1
//        ((LMS) neuralNet.getLearningRule()).setLearningRate(0.7);//0-1
        ((LMS) neuralNet.getLearningRule()).setLearningRate(0.08);//0-1
        ((LMS) neuralNet.getLearningRule()).setMaxIterations(maxIterations);//0-1
        TrainingSet trainingSet = new TrainingSet();

        ArrayList<Double> coeff = studiobyScore;
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
            System.out.println(" Output: " + networkOutput);
            res = networkOutput.get(0)*10;
        }
        System.out.println("Time stamp N3:" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:MM").format(new Date()));
        //System.exit(0);
    return res;
    }

    public class StudioInfo{
        public double score;
        public String name_studio;
        public double season;
        public StudioInfo(double score, String name_studio, double season){
            this.score = score;
            this.name_studio = name_studio;
            this.season = season;
        }

        public String getStudio(){
            return name_studio;
        }
    }

}
