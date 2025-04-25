import java.io.*;
import java.util.*;

public class BotBrain {

    int id;
    double elo;
    int generation;
    private double [][] outputWeigths;

    public BotBrain(int n){
        id = n;
        elo = 1000;
        generation = 0;
        outputWeigths = new double[49][36];

        for (int i = 0; i < 49; i++) {
            for (int j = 0; j < 36; j++) {
                outputWeigths[i][j]=Math.random()*2.5-1.25;
            }
        }
    }

    public BotBrain(BotBrain parent,int n,int changeNum){
        id = n;
        elo = 1000;
        generation = parent.generation+1;
        outputWeigths = new double[49][36];

        for (int i = 0; i < 49; i++) {
            for (int j = 0; j < 36; j++) {
                outputWeigths[i][j] = parent.outputWeigths[i][j]+(0.008*Math.random()-0.004);
            }
        }
        for (int i = 0; i < changeNum; i++) {
            outputWeigths[(int)Math.round(Math.random()*49-0.5)][(int)Math.round(Math.random()*36-0.5)] += Math.random()*0.2-0.1;
        }

    }

    /////////////////////
    public static boolean testLegal49b(int [] cleanedBoard,int index){
        int left = index/7;
        int rigth = index%7;

        return (cleanedBoard[(left+1)*left/2+rigth]==1 && (left==cleanedBoard[28] || left==cleanedBoard[29] || rigth==cleanedBoard[28] || rigth==cleanedBoard[29]));

    }

    public int[] ranker(int[] cleanedBoard) {

        int index = 0;
        double[] output = new double[49];
        int[] finalValues = new int[49];

        for (int i = 0; i < 49; i++) {
            if (testLegal49b(cleanedBoard, i)) {
                double score = 0;
                for (int j = 0; j < 36; j++) {
                    score += cleanedBoard[j] * outputWeigths[i][j];
                }
                output[index] = score;
                finalValues[index] = i;
                index++;
            }
        }

        double[] validOutput = Arrays.copyOf(output, index);
        int[] validFinalValues = Arrays.copyOf(finalValues, index);

        Integer[] sortedIndices = new Integer[index];
        for (int i = 0; i < index; i++) {
            sortedIndices[i] = i;
        }

        Arrays.sort(sortedIndices, Comparator.comparingDouble(i -> validOutput[i]));


        for (int i = 0; i < index; i++) {
            finalValues[i] = validFinalValues[sortedIndices[i]];
        }
        if (index<49){
            finalValues[index]=-1;
        }

        return finalValues;
    }

    public void printOffVal(){
        for (int i = 0; i < 49; i++) {
            for (int j = 0; j < 36; j++) {
                System.out.print(outputWeigths[i][j]+"\t");
            }
            System.out.println();
        }
        System.out.println(outputWeigths[0][1]);
    }

    public void changeElo(double change){
        elo = elo + change;
    }

    public void appendWeightsToBinary(String filename) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename, true))) {
            dos.writeInt(id);
            dos.writeDouble(elo);
            dos.writeInt(generation);
            for (int i = 0; i < 49; i++) {
                for (int j = 0; j < 36; j++) {
                    dos.writeDouble(outputWeigths[i][j]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BotBrain[] loadBotsFromFile(String fileName) {
        List<BotBrain> bots = new ArrayList<>();

        try (DataInputStream dis = new DataInputStream(new FileInputStream(fileName))) {
            while (true) {
                try {
                    int id = dis.readInt();
                    double eloFromFile = dis.readDouble();
                    int generation = dis.readInt();

                    BotBrain bot = new BotBrain(id);
                    bot.elo = 1000;
                    bot.generation = generation;
                    bot.outputWeigths = new double[49][36];

                    for (int i = 0; i < 49; i++) {
                        for (int j = 0; j < 36; j++) {
                            bot.outputWeigths[i][j] = dis.readDouble();
                        }
                    }

                    bots.add(bot);

                } catch (EOFException eof) {
                    break;  // Finished reading all bots
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bots.toArray(new BotBrain[0]);
    }


    public static void backUpFile(String ogFile, String backUp){
        BotBrain[] tempers = BotBrain.loadBotsFromFile(ogFile);
        for (int i = 0; i < tempers.length; i++) {
            tempers[i].appendWeightsToBinary(backUp);
        }
    }


}