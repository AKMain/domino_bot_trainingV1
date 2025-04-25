public class DominosFaceOff {

    public static void twoGamesFliped(BotBrain playerOne,BotBrain playerTwo,int [] score){

        int [] gameBoard = new int[36];

        int player_turn;
        int moves;
        int numberOfPasses;
        boolean hasLegalBool;

        int [] randDomToPul = new int[28];
        randMizAndSet(randDomToPul);

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
                drawNDominos(gameBoard,randDomToPul,7,1,false);
                drawNDominos(gameBoard,randDomToPul,7,2,false);
            }else{
                drawNDominos(gameBoard,randDomToPul,7,2,false);
                drawNDominos(gameBoard,randDomToPul,7,1,false);
            }


            player_turn = firstMove(gameBoard, false);

            numberOfPasses = 0;

            while (gameBoard[30] > 0 && gameBoard[31] > 0 && moves <= 28 && numberOfPasses < 2) {

                hasLegalBool=hasLegalMove(gameBoard, player_turn);

                while (gameBoard[34] < 28 && !hasLegalBool) {

                    drawNDominos(gameBoard, randDomToPul, 1, player_turn, false);
                    hasLegalBool=hasLegalMove(gameBoard, player_turn);

                }

                if (hasLegalBool) {
                    numberOfPasses = 0;

                    if (player_turn == 1) {
                        ranking = playerOne.ranker(normilize(gameBoard, player_turn));
                    } else {
                        ranking = playerTwo.ranker(normilize(gameBoard, player_turn));
                    }

                    DoingHighestLegalMove(gameBoard, player_turn, ranking, false);

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
                score[SumHigher(gameBoard)]++;
            }

        }

    }
    public static int [] leftRigthVal28b (int base28Num){
        int[] values = {(int) ((-1 + Math.sqrt(1 + 8 * base28Num)) / 2), 0};
        values[1] = base28Num - values[0] * (values[0] + 1) / 2;
        return values;
    }//optimized
    public static void randMizAndSet(int [] randDomToPul){

        for (int i = 0; i < 28; i++) {
            randDomToPul[i]=i;
        }

        for (int i = 0,temp,r; i < 28; i++) {
            r = (int)Math.round(Math.random()*28-0.5);
            temp = randDomToPul[r];
            randDomToPul[r]=randDomToPul[i%28];
            randDomToPul[i%28]=temp;

        }

    }
    public static void drawNDominos(int [] gameBoard,int [] randDomToPul,int n,int player_turn,boolean showing){
        int a =0;

        for (int i = gameBoard[34]; i < gameBoard[34]+n && i<28; i++,a++) {
            gameBoard[randDomToPul[i]]=player_turn;

            if (showing){
                int [] temp = leftRigthVal28b(randDomToPul[i]);
                System.out.println("Draw: \t"+temp[0]+"\t"+temp[1]);
            }

        }
        gameBoard[29+player_turn]+=a;

        if (n+gameBoard[34]>=28){
            gameBoard[34]=28;
        }else{
            gameBoard[34]+=n;
        }



    }
    public static int firstMove(int [] gameBoard,boolean showing){

        int player_turn;
        int [] temp;

        for (int i = 0,j=-1; i < 7; i++) {

            j += i+1;

            if (gameBoard[j]>0){

                player_turn=gameBoard[j];
                gameBoard[j]=0;
                gameBoard[29+player_turn]--;

                temp = leftRigthVal28b(j);

                gameBoard[28]=temp[0];
                gameBoard[29]=temp[1];
                gameBoard[35]=1;

                if (showing){
                    System.out.println(gameBoard[28]+"\t"+gameBoard[29]);
                }

                return player_turn%2+1;
            }
        }
        for (int i = 0; i < 28; i++) {
            if (gameBoard[i]>0){

                player_turn=gameBoard[i];
                gameBoard[i]=0;
                gameBoard[29+player_turn]--;

                temp = leftRigthVal28b(i);

                gameBoard[28]=temp[0];
                gameBoard[29]=temp[1];
                gameBoard[35]=1;
                if (showing) {
                    System.out.println(gameBoard[28] + "\t" + gameBoard[29]);
                }
                return player_turn%2+1;
            }
        }

        return -1;
    }
    public static boolean thislegalMove(int [] gameBoard,int player_turn,int index){

        int [] temp = leftRigthVal28b(index);
        return (gameBoard[index]==player_turn && (temp[0]==gameBoard[28] || temp[0]==gameBoard[29] || temp[1]==gameBoard[28] || temp[1]==gameBoard[29]));

    }
    public static boolean hasLegalMove(int [] gameBoard,int player_turn){
        for (int i = 0; i < 28; i++) {
            if(thislegalMove(gameBoard,player_turn,i)){
                return true;
            }
        }
        return false;
    }
    public static int [] normilize(int [] gameBoard,int player_turn){
        int [] cleanedBoard = gameBoard.clone();
        for (int i = 0; i < 28; i++) {
            if (cleanedBoard[i] == player_turn){
                cleanedBoard[i]=1;
            }else if (cleanedBoard[i]>0){
                cleanedBoard[i]=-1;
            }
        }
        return cleanedBoard;
    }
    public static void DoingHighestLegalMove(int [] gameBoard,int player_turn,int [] ranking,boolean showing){
        int bestMovein35b=-1;
        int [] ler={-1,-1};
        int [] Aog={-1,-1};
        for (int i = 0; i < 49; i++) {

            Aog[0]=ranking[i]/7;
            Aog[1]=ranking[i]%7;

            if (Aog[0]<Aog[1]){
                ler[0]=Aog[1];
                ler[1]=Aog[0];
            }else{
                ler[0]=Aog[0];
                ler[1]=Aog[1];
            }

            bestMovein35b = ler[0]*(ler[0]+1)/2+ler[1];


            if(thislegalMove(gameBoard,player_turn,bestMovein35b)){
                break;
            }
        }
        if (showing) {
            System.out.println(Aog[0] + "\t" + Aog[1]);
        }

        if(Aog[0]==gameBoard[28]){
            gameBoard[28]=Aog[1];
        }else if(Aog[0]==gameBoard[29]){
            gameBoard[29]=Aog[1];
        }else if(Aog[1]==gameBoard[28]){
            gameBoard[28]=Aog[0];
        }else if(Aog[1]==gameBoard[29]){
            gameBoard[29]=Aog[0];
        }

        gameBoard[29+player_turn]--;
        gameBoard[bestMovein35b]=0;
        gameBoard[35]++;

    }
    public static int SumHigher(int [] gameBoard){
        int [] temp;
        int SumOne=0,SumTwo=0;

        for (int i = 0; i < 28; i++) {
            temp = leftRigthVal28b(i);
            if (gameBoard[i]==1){
                SumOne +=temp[0]+temp[1];
            }else if (gameBoard[i]==2){
                SumTwo += temp[0]+temp[1];
            }
        }

        if (SumOne>SumTwo){
            return 2;
        }else if(SumOne<SumTwo){
            return 1;
        }else{
            return 0;
        }
    }

}