import java.io.*;
import java.util.*;

public class newTraining {
    public static void semiRoundRobin(BotBrain []  groupOfComp,BotBrain []  topPercent,int firstIndexToFill,int rounds,int passNumber){
        BotBrain temp;

        for (int robinRounds = 0,len = groupOfComp.length,swapIndex,i; robinRounds < rounds; robinRounds++) {

            ///////////////

            for (i = 64; i < len; i+=4) {

                swapIndex = i + (int)Math.round(Math.random() * 8 - 4);

                if (0 <= swapIndex && swapIndex < len) {
                    temp = groupOfComp[i];
                    groupOfComp[i] = groupOfComp[swapIndex];
                    groupOfComp[swapIndex] = temp;
                }
            }

            //////////////


            for (i = 0; i < len; i+=2) {

                singleMatch(groupOfComp[i],groupOfComp[i+1],7);
            }
            Arrays.sort(groupOfComp, Comparator.comparingDouble(b -> -b.elo));
        }

        for (int i = 0; i < passNumber; i++) {
            if (i + firstIndexToFill < topPercent.length) {
                topPercent[i+firstIndexToFill]=groupOfComp[i];
            }
        }

    }

    public static void singleMatch(BotBrain playerOne,BotBrain playerTwo,int numGames){
        int [] score = {0,0,0};
        for (int k = 0; k < numGames; k++) {
            DominosFaceOff.twoGamesFliped(playerOne,playerTwo,score);
        }

        playerOne.changeElo(Math.round(32*((score[1]-score[2]+0.5)/2.0 - 1.0/(1+Math.pow(10,(playerTwo.elo-playerOne.elo)/400.0)))));
        playerTwo.changeElo(Math.round(32*((score[2]-score[1]+0.5)/2.0 - 1.0/(1+Math.pow(10,(playerOne.elo-playerTwo.elo)/400.0)))));
    }

    public static void faceOffGoodBots(BotBrain [] strongBots,int a,int b,boolean extra){
        //(int)Math.round(Math.random()*parentBots.length-0.5)

        int [] score = {0,0,0};

        BotBrain testCase = new BotBrain(0);

        System.out.println("Show Matchs\nBot one: "+a+"\tBot two: "+b+"\tSize of file: "+strongBots.length);
        for (int i = 0; i < 5000; i++) {
            DominosFaceOff.twoGamesFliped(strongBots[a], strongBots[b], score);

        }
        System.out.println("Bot One: "+score[1]+"\tBot two: \t"+score[2]+"\nWin percentage: "+(score[1]/(10000.0))+"\n---------------\n");
        if (extra) {
            score[0] = 0;
            score[1] = 0;
            score[2] = 0;
            System.out.println("Show Matchs\nBot one: " + a + "\tBot two: Random");
            for (int i = 0; i < 5000; i++) {
                DominosFaceOff.twoGamesFliped(strongBots[a], testCase, score);
            }
            System.out.println("Bot One: " + score[1] + "\tBot two: \t" + score[2] + "\nWin percentage: "+(score[1]/(10000.0))+"\n---------------");
            //strongBots[0].printOffVal();
        }

    }

    public static void grind(BotBrain [] strongBots,BotBrain[] groupOfComp,BotBrain[] topPercent,String nfileName,int finalPass){
        Random rand = new Random();
        System.out.println("Start");

        for (int i = 0,len = groupOfComp.length; i < len; i++) {
            if (i<len*0.24){
                groupOfComp[i] = new BotBrain(strongBots[(int)Math.round(Math.random()*strongBots.length-0.5)],i,rand.nextInt(10));
            }else if (i<len*0.48){
                groupOfComp[i] = new BotBrain(strongBots[(int)Math.round(Math.random()*strongBots.length-0.5)],i,rand.nextInt(30)+120);
            }else if(i<len*0.72){
                groupOfComp[i] = new BotBrain(strongBots[(int)Math.round(Math.random()*strongBots.length-0.5)],i,rand.nextInt(100)+450);
            }else if(i<len*0.96){
                groupOfComp[i] = new BotBrain(strongBots[(int)Math.round(Math.random()*strongBots.length-0.5)],i,rand.nextInt(300)+2000);
            }else {
                groupOfComp[i] = new BotBrain(i);
            }
        }

        System.out.println("Loaded");

        testRBots.NewSystem(groupOfComp,topPercent,0,400,finalPass);

        /////////////////////
        for (int i = 0; i < 16; i++) {
            System.out.println("ELO: "+groupOfComp[i].elo+"\tID: "+groupOfComp[i].id+"\tGen: "+groupOfComp[i].generation);
        }
        System.out.println("\n");
        for (int i = groupOfComp.length-16; i < groupOfComp.length; i++) {
            System.out.println("ELO: "+groupOfComp[i].elo+"\tID: "+groupOfComp[i].id+"\tGen: "+groupOfComp[i].generation);
        }

        //////////////////

        System.out.println("\nDone\n");

        clearFile(nfileName);

        for (int i = 0; i < finalPass; i++) {
            if (topPercent[i]!=null){
                topPercent[i].appendWeightsToBinary(nfileName);
            }
        }
        System.out.println("Finished\n");
    }

    public static void clearFile(String fileName) {
        try {
            new FileOutputStream(fileName).close();
            System.out.println("File cleared: " + fileName);
        } catch (IOException e) {
            System.out.println("Failed to clear file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        int sizeOfTopPrecent = 512;

        String diffFile = "other.bin";

        BotBrain [] strongBots = BotBrain.loadBotsFromFile(diffFile);

        BotBrain[] topPercent = new BotBrain[sizeOfTopPrecent];
        BotBrain[] groupOfComp = new BotBrain[65536];


        for (int lk = 0; lk < 1; lk++) {
            System.out.println("Round: "+lk+"\n");

            grind(strongBots,groupOfComp,topPercent,diffFile,sizeOfTopPrecent);

            for (int i = 0; i < sizeOfTopPrecent; i++) {
                if (topPercent[i]!=null){
                    strongBots[i]=topPercent[i];
                }else{
                    strongBots[i] = new BotBrain(i);
                }

            }

            System.out.println();
            faceOffGoodBots(strongBots,0,(int)Math.round(Math.random()*strongBots.length-0.5),true);
            faceOffGoodBots(strongBots,1,(int)Math.round(Math.random()*strongBots.length-0.5),true);

        }
    }


}
