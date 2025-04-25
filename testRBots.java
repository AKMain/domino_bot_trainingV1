import java.util.Arrays;
import java.util.Comparator;

public class testRBots {
    public static void main(String[] args) {

        BotBrain [] strongBots = BotBrain.loadBotsFromFile("other.bin");


        testMatchVR(strongBots,0,10000);

//        strongBots[1].printOffVal();
    }

    public static void NewSystem(BotBrain []  groupOfComp,BotBrain []  topPercent,int firstIndexToFill,int rounds,int passNumber){
        BotBrain temp;

        int [][] randomShuffels = new int[rounds][28];

        shuffelSetup(randomShuffels,rounds);

        for (int robinRounds = 0,len = groupOfComp.length,swapIndex,i; robinRounds < rounds; robinRounds++) {

            ///////////////

            for (i = 8; i < len; i+=4) {

                swapIndex = i + (int)Math.round(Math.random() * 10 - 5);

                if (0 <= swapIndex && swapIndex < len) {
                    temp = groupOfComp[i];
                    groupOfComp[i] = groupOfComp[swapIndex];
                    groupOfComp[swapIndex] = temp;
                }
            }

            //////////////


            for (i = 0; i < len; i+=2) {

                int [] score = {0,0,0};

                V2twoGamesFliped(groupOfComp[i],groupOfComp[i+1],score,randomShuffels[robinRounds]);

                groupOfComp[i].changeElo((score[1]-score[2]+score[0]));
                groupOfComp[i+1].changeElo((score[2]-score[1]+score[0]));

            }

            Arrays.sort(groupOfComp, Comparator.comparingDouble(b -> -b.elo));
        }

        for (int i = 0; i < passNumber; i++) {
            if (i + firstIndexToFill < topPercent.length) {
                topPercent[i+firstIndexToFill]=groupOfComp[i];
            }
        }

    }

    public static void shuffelSetup(int[][] randomShuffels, int rounds) {
        for (int i = 0; i < rounds; i++) {
            randomShuffels[i] = new int[28];
            for (int j = 0; j < 28; j++) {
                randomShuffels[i][j] = j;
            }

            for (int k = 27; k > 0; k--) {
                int r = (int) Math.round(Math.random() * k);
                int temp = randomShuffels[i][k];
                randomShuffels[i][k] = randomShuffels[i][r];
                randomShuffels[i][r] = temp;
            }
        }
    }

    public static void V2twoGamesFliped(BotBrain playerOne,BotBrain playerTwo,int [] score,int [] randomShuffels){

        int [] gameBoard = new int[36];

        int player_turn;
        int moves;
        int numberOfPasses;
        boolean hasLegalBool;


        int[] ranking;

        for (int ko = 0; ko < 2; ko++) {
            for (int i = 0; i < 28; i++) {
                gameBoard[i]=-1;
            }
            moves = 0;
            gameBoard[28]=-1;//        int left=-1;//[28]
            gameBoard[29]=-1;//        int rigth=-1;//[29]
            gameBoard[30]=0;//        int p1hand=7;//[30]
            gameBoard[31]=0;//        int p2hand=7;//[31]
            gameBoard[32]=-1;
            gameBoard[33]=-1;
            gameBoard[34]=0;//        int sizeDraw=14;//[34]
            gameBoard[35]=0;//        int playedPool=0;//[35]

            if (ko==0){
                DominosFaceOff.drawNDominos(gameBoard,randomShuffels,7,1,false);
                DominosFaceOff.drawNDominos(gameBoard,randomShuffels,7,2,false);
            }else{
                DominosFaceOff.drawNDominos(gameBoard,randomShuffels,7,2,false);
                DominosFaceOff.drawNDominos(gameBoard,randomShuffels,7,1,false);
            }


            player_turn = DominosFaceOff.firstMove(gameBoard, false);

            numberOfPasses = 0;

            while (gameBoard[30] > 0 && gameBoard[31] > 0 && moves <= 28 && numberOfPasses < 2) {

                hasLegalBool=DominosFaceOff.hasLegalMove(gameBoard, player_turn);

                while (gameBoard[34] < 28 && !hasLegalBool) {

                    DominosFaceOff.drawNDominos(gameBoard, randomShuffels, 1, player_turn, false);
                    hasLegalBool=DominosFaceOff.hasLegalMove(gameBoard, player_turn);

                }

                if (hasLegalBool) {
                    numberOfPasses = 0;

                    if (player_turn == 1) {
                        ranking = playerOne.ranker(DominosFaceOff.normilize(gameBoard, player_turn));
                    } else {
                        ranking = playerTwo.ranker(DominosFaceOff.normilize(gameBoard, player_turn));
                    }

                    DominosFaceOff.DoingHighestLegalMove(gameBoard, player_turn, ranking, false);

                } else {
                    numberOfPasses++;
                }

                moves++;
                player_turn = player_turn % 2 + 1;


            }

            if (gameBoard[30] <= 0) {//player 1 won
                score[1]++;
            } else if (gameBoard[31] <= 0) {//player 2 won
                score[2]++;
            } else {
                score[DominosFaceOff.SumHigher(gameBoard)]++;
            }

        }

    }



    public static void testMatchVR(BotBrain [] parentBots,int a,int num) {
        //(int)Math.round(Math.random()*parentBots.length-0.5)

        int[] score = {0, 0, 0};

        BotBrain testCase = new BotBrain(0);

        System.out.println("Show Matchs\nBot one: " + a + "\tBot two: Random");
        for (int i = 0; i < num; i++) {
            DominosFaceOff.twoGamesFliped(parentBots[a], testCase, score);
        }
        System.out.println("Bot One: " + score[1] + "\tBot two: \t" + score[2] + "\nWin percentage: "+(score[1]/(2.0*num))+"\n---------------");


    }

    public static void LookinMatch(BotBrain [] strongBots,int a,int b){

        System.out.println("Show Matchs\nBot one: "+a+"\tBot two: "+b+"\tSize of file: "+strongBots.length);

        BotBrain playerOne=strongBots[a];
        BotBrain playerTwo=strongBots[b];

        int [] gameBoard = new int[36];

        for (int i = 0; i < 28; i++) {
            gameBoard[i]=-1;
        }

        //-1 = unkown/undrawn
        //0 = played
        //1 = player one
        //2 = player two

        gameBoard[28]=-1;//        int left=-1;//[28]
        gameBoard[29]=-1;//        int rigth=-1;//[29]
        gameBoard[30]=0;//        int p1hand=7;//[30]
        gameBoard[31]=0;//        int p2hand=7;//[31]
        gameBoard[32]=-1;
        gameBoard[33]=-1;
        gameBoard[34]=0;//        int sizeDraw=14;//[34]
        gameBoard[35]=0;//        int playedPool=0;//[35]

        int player_turn;
        int moves = 0;

        int [] randDomToPul = new int[28];
        DominosFaceOff.randMizAndSet(randDomToPul);

        //draw domino's

        DominosFaceOff.drawNDominos(gameBoard,randDomToPul,7,1,true);
        if (true){
            System.out.println();
        }

        DominosFaceOff.drawNDominos(gameBoard,randDomToPul,7,2,true);

        System.out.println();

        debugprintoff(gameBoard,randDomToPul);

        player_turn=DominosFaceOff.firstMove(gameBoard,true);

        debugprintoff(gameBoard,randDomToPul);

        int numberOfPasses = 0;
        int [] ranking;

        boolean legalBool;

        while(gameBoard[30]>0 && gameBoard[31]>0 && moves<=28 && numberOfPasses<2){

            legalBool = DominosFaceOff.hasLegalMove(gameBoard,player_turn);

            while(gameBoard[34]<28 && !legalBool){

                DominosFaceOff.drawNDominos(gameBoard,randDomToPul,1,player_turn,true);
                legalBool = DominosFaceOff.hasLegalMove(gameBoard,player_turn);
            }

            if (legalBool){
                numberOfPasses=0;

                //normilize

                if (player_turn==1){
                    ranking = playerOne.ranker(DominosFaceOff.normilize(gameBoard,player_turn));
                }else{
                    ranking = playerTwo.ranker(DominosFaceOff.normilize(gameBoard,player_turn));
                }

                DominosFaceOff.DoingHighestLegalMove(gameBoard,player_turn,ranking,true);

            }else{
                numberOfPasses++;
            }


            debugprintoff(gameBoard,randDomToPul);

            moves++;
            player_turn = player_turn%2+1;



        }

        if (gameBoard[30]<=0){//player 1 won
            System.out.println("\tPlayer one won");
        }else if (gameBoard[31]<=0){//player 2 won
            System.out.println("\tPlayer two won");
        }

    }

    public static void debugprintoff(int [] gameBoard,int [] randDomToPul){
        for (int i = 0; i < 36; i++) {
            System.out.print(gameBoard[i]+"\t");
            if (i == 27 || i ==31 || i == 33 ){
                System.out.print("|\t");
            }
        }
        System.out.println();
        for (int i = 0; i < 28; i++) {
            System.out.print(randDomToPul[i]+"\t");
        }
        System.out.println("\n");
    }




}

