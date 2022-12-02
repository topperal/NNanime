package processingManga;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.LMS;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.stream.Stream;

public class ProcessingManga {
    public ArrayList<MangaCoeff> relationScores() {//заполняем собств класс, содержащий коэффициенты а и в из у=ах+в и знач дисперсии
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        int k = 0;

        try {
            fileWriter = new FileWriter("rawdata/predictionData.txt",true);//создание файла, в которые записываются данные
            //fileWriter = new FileWriter("rawdata/hyperParametrs.txt",true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        printWriter = new PrintWriter(fileWriter);

        ArrayList<MangaCoeff> finalData = new ArrayList<>();
        ArrayList<Double> xA = new ArrayList<>();
        ArrayList<Double> yA = new ArrayList<>();
        String[] seasons = {"winter", "spring", "summer", "fall"};
        String finalLine = "";

        for (int i = 2015; i < 2023; i++ ) {//просматриваем все тайтлы с периода 2015 по 2022 включительно
            for (int j = 0; j < 4; j++) {//проходим по всем сезонам
                ArrayList[] result = new ArrayList[2];
                result = getMangaData(i, seasons[j]);//в массив записываем рейтинги аниме и манги
                xA = result[0];//вектор, содержащий рейтинги манги
                yA = result[1];//вектор, содержащий рейтинги аниме

                double[] res = least_square(xA, yA);//вектор, содержащий знач по методу наим квадратов

                double disp = squareDispersion(xA, yA, res[0], res[1]);//считаем дисперсию

                finalData.add(new MangaCoeff((i+0.25*j),res[0],res[1], disp));//заполняем собств класс
                //System.out.println("f(x) = " + res[0] + " * x + " + res[1] + "   dispersia: " + disp);

                if ((i == 2022) && (j == 2)) {

                    double[] predictManga = new double[3];
                    for (int h = 0; h < 3; h++) {
                        predictManga[h] = trainNeuralNetwork(h, finalData);
                    }
                    finalLine += "{\"koeffA\":" + predictManga[0] + ",\n\"koeffB\":" + predictManga[1] + ",\n\"mangdisp\":" + predictManga[2] +",\n" ;
                    break;
                }
            }
        }
        printWriter.print(finalLine);
        printWriter.flush();
        //finalData.remove(finalData.size()-1);

        return finalData;
    }

    public double trainNeuralNetwork(int k,ArrayList<MangaCoeff> finalC) {//нейронная сеть, обучение с учителем
        System.out.println("Time stamp N1:" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:MM").format(new Date()));

        int maxIterations = 10000;
        NeuralNetwork neuralNet = new MultiLayerPerceptron(4,8,1);
        ((LMS) neuralNet.getLearningRule()).setMaxError(0.001);//0-1
        ((LMS) neuralNet.getLearningRule()).setLearningRate(0.7);//0-1
        ((LMS) neuralNet.getLearningRule()).setMaxIterations(maxIterations);//0-1
        TrainingSet trainingSet = new TrainingSet();

        ProcessingManga processingManga = new ProcessingManga();
        ArrayList<Double> coeff = new ArrayList<>();
        //ArrayList<ProcessingManga.MangaCoeff> finalC = processingManga.relationScores();
        for(MangaCoeff nc : finalC) {
            if (k==0) {
                coeff.add(nc.coeffA);
                //System.out.println("coeffA");
            }else if(k==1) {
                coeff.add(nc.coeffB);
                //System.out.println("coeffB");
            }
            else {
                coeff.add(nc.dispersion);
                //System.out.println("coeffB");
            }
        }

        int dent = 10;
        if(k==2){dent = 1;}
        for (int i = 0; i < coeff.size()-4; i++) {
            trainingSet.addElement(new SupervisedTrainingElement(new double[]{coeff.get(i)/dent,coeff.get(i+1)/dent,coeff.get(i+2)/dent,coeff.get(i+3)/dent},new double[]{coeff.get(i+4)/dent}));
        }
        neuralNet.learnInSameThread(trainingSet);
        System.out.println("Time stamp N2:" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:MM").format(new Date()));

        TrainingSet testSet = new TrainingSet();
        testSet.addElement(new TrainingElement(new double[]{coeff.get(coeff.size()-4)/dent,coeff.get(coeff.size()-3)/dent,coeff.get(coeff.size()-2)/dent,coeff.get(coeff.size()-1)/dent}));
        double predictCoeff = 0;

        for (TrainingElement testElement : testSet.trainingElements()) {
            neuralNet.setInput(testElement.getInput());
            neuralNet.calculate();
            Vector<Double> networkOutput = neuralNet.getOutput();
            System.out.print("Input: " + testElement.getInput());
            System.out.println(" Output: " + networkOutput.get(0)*dent);
            predictCoeff = networkOutput.get(0)*dent;
        }
        System.out.println("Time stamp N3:" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:MM").format(new Date()));
        return predictCoeff;
    }

    public double squareDispersion(ArrayList<Double>xA, ArrayList<Double> yA, double a, double b) {//дисперсия
        double summY = 0;
        for (int i = 0; i < xA.size(); i++) {
            double calcY = xA.get(i)*a+b;//вычисленное значение
            double diff = (calcY-yA.get(i))*(calcY-yA.get(i));//разница между вычисленным знач и точным
            summY += diff;
        }
        summY /= xA.size();//конечная формула дисперсии
        return summY;
    }
    public double determinant(double[] xArr, double[] yArr) {//считаем определитель
        double[][] finalDet = new double[2][2];
        for (int i = 0; i < 2; i++) {
            finalDet[0][i]=xArr[i];
            finalDet[1][i]=yArr[i];
        }
        double determinant = finalDet[0][0]*finalDet[1][1]-finalDet[0][1]*finalDet[1][0];
        return determinant;
    }

    public double[] least_square(ArrayList<Double> xArr, ArrayList<Double> yArr) {//метод наименьших квадратов
        double[] result = new double[2];
        Stream<Double> streamFromX = xArr.stream();//потоковое чтение вектора Х (рейтинг манги)
        Stream<Double> streamFromY = yArr.stream();//потоковое чтение вектора У (рейтинг аниме)
        double sumX = streamFromX.reduce(0.0, Double::sum);//в потоке складываем все значения вектора Х
        double sumy = streamFromY.reduce(0.0, Double::sum);//в потоке складываем все значения вектора У
        streamFromX = xArr.stream();//заново открываем поток
        double sumSqX = streamFromX.mapToDouble(i -> i.doubleValue()*i.doubleValue()).sum();//в потоке возводим в квадрат знач вектора Х и суммируем их
        double mult = 0;
        for(int i = 0; i < xArr.size(); i++) {//сумма произведений Х на У
            mult += xArr.get(i)*yArr.get(i);
        }

        double[] aArray = new double[] {sumSqX,sumX};//вектор-столбец
        double[] bArray = new double[] {sumX, xArr.size()};
        double[] cArray = new double[] {mult, sumy};//сводобный вектор-столбец

        double mainDet = determinant(aArray,bArray);//главный определитель
        double aDet = determinant(cArray, bArray);
        double bDet = determinant(aArray,cArray);

        result[0]=aDet/mainDet;
        result[1]=bDet/mainDet;

        return result;
    }

    public ArrayList[] getMangaData(int year, String season) {//получаем рейтинг аниме и манги
        String[] result = null;
        ArrayList[] scoreAll = new ArrayList[2];
        scoreAll[0] = new ArrayList<>();//score manga
        scoreAll[1] = new ArrayList<>();//score anime
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("rawdata/"+year + season + ".csv"));//путь к считываемому файлу
            String line = reader.readLine();//чтение по-строчно
            while (line != null) {//читаем файл по-строчно и выделяем нужные знач из стобцов
                result = line.split(";");
                scoreAll[0] .add(Double.parseDouble(result[5]));//score manga
                scoreAll[1].add(Double.parseDouble(result[1]));//score anime
                // read next line
                line = reader.readLine();
            }
            reader.close();//закрываем файл
        } catch (IOException e) {
            e.printStackTrace();
        }
            return scoreAll;
    }

}
