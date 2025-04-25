import java.util.Scanner;

public class FrontFacingInterFace {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String diffFile = "other.bin";

        BotBrain [] strongBots = BotBrain.loadBotsFromFile(diffFile);

        System.out.println("Input model Num: ");
        int inputAlpha = scanner.nextInt();
        BotBrain oppPlayer1 = strongBots[inputAlpha];



        int [] gameBoard = new int[36];

        for (int i = 0; i < 36; i++) {
            gameBoard[i]=-1;
        }

        gameBoard[28]=-1;//        int left=-1;//[28]
        gameBoard[29]=-1;//        int rigth=-1;//[29]
        gameBoard[30]=0;//        int p1hand=7;//[30]
        gameBoard[31]=7;//        int p2hand=7;//[31]
        gameBoard[32]=-1;
        gameBoard[33]=-1;
        gameBoard[34]=0;//        int sizeDraw=14;//[34]
        gameBoard[35]=0;//        int playedPool=0;//[35]

        int action;
        int left,right,temp;


        while(true){
            System.out.println("\n0 = bot makes a move\n1 = set a domino as played\n2 = draw domino\n3 = state of the world\n4 = # in opp hand");

            System.out.println("Action: ");
            action = scanner.nextInt();

            if (action==0){//play what you want

                DominosFaceOff.DoingHighestLegalMove(gameBoard,1,oppPlayer1.ranker(gameBoard),true);
                System.out.println("\nLeft side: "+gameBoard[28]+"\tRight side: "+gameBoard[29]);
            }else if (action ==1) {//set value to played

                System.out.println("Opponent move? 0 = yes");
                if (scanner.nextInt() == 0) {
                    gameBoard[31]--;
                }

                System.out.println("Left matched first then right.");

                System.out.println("Left num: ");
                left = scanner.nextInt();
                System.out.println("Right num: ");
                right = scanner.nextInt();

                temp = left;
                left = Math.max(left, right);
                right = Math.min(temp, right);

                gameBoard[left * (left + 1) / 2 + right] = 0;
                if (gameBoard[28]==gameBoard[29] && gameBoard[28] == -1){
                    gameBoard[28]=left;
                    gameBoard[29]=right;
                }else if (gameBoard[28]==gameBoard[29] || left==right){//both the same
                    if (left==gameBoard[28]){
                        gameBoard[28]=right;
                    }else if (right==gameBoard[28]){
                        gameBoard[28]=left;
                    }
                }else if ((gameBoard[28] == left && gameBoard[29] == right)){
                    System.out.println("Options:\n0 = "+gameBoard[28]+" : "+left+"\t1 = "+right+" : "+gameBoard[29]);
                    temp= scanner.nextInt();
                    if (temp==0){
                        gameBoard[29]=left;
                    }else if (temp==1){
                        gameBoard[28]=right;
                    }

                }else if (gameBoard[28] == right && gameBoard[29] == left){
                    System.out.println("Options:\n0 = "+gameBoard[28]+" : "+right+"\t1 = "+left+" : "+gameBoard[29]);
                    temp= scanner.nextInt();
                    if (temp==0){
                        gameBoard[29]=right;
                    }else if (temp==1){
                        gameBoard[28]=left;
                    }
                }else{
                    if (gameBoard[28] == left){
                        gameBoard[28]=right;
                    }else if (gameBoard[29] == left){
                        gameBoard[29]=right;
                    }else if (gameBoard[28] == right){
                        gameBoard[28]=left;
                    }else if (gameBoard[29] == right){
                        gameBoard[29]=left;
                    }
                }

                System.out.println("\nLeft side: "+gameBoard[28]+"\tRight side: "+gameBoard[29]);


            }else if (action == 2){//draw

                System.out.println("Left num: ");
                left = scanner.nextInt();
                System.out.println("Right num: ");
                right = scanner.nextInt();

                temp = left;
                left = Math.max(left,right);
                right = Math.min(temp,right);

                gameBoard[left*(left+1)/2+right]=1;

            }else if (action == 3) {//prints off state of the world
                System.out.println("\nBot's hand:");
                for (int i = 0; i < 28; i++) {
                    if (gameBoard[i] == 1) {
                        int[] domToNums = DominosFaceOff.leftRigthVal28b(i);
                        System.out.println(domToNums[0] + " : " + domToNums[1]);
                    }
                }
                System.out.println("\nUnkown hand:");
                for (int i = 0; i < 28; i++) {
                    if (gameBoard[i] == -1) {
                        int[] domToNums = DominosFaceOff.leftRigthVal28b(i);
                        System.out.println(domToNums[0] + " : " + domToNums[1]);
                    }
                }
                System.out.println("\nPlayed hand:");
                for (int i = 0; i < 28; i++) {
                    if (gameBoard[i] == 0) {
                        int[] domToNums = DominosFaceOff.leftRigthVal28b(i);
                        System.out.println(domToNums[0] + " : " + domToNums[1]);
                    }
                }
                System.out.println("\nLeft side: "+gameBoard[28]+"\tRight side: "+gameBoard[29]);
            }else if (action == 4){
                System.out.println("Num in enemy hands: ");
                gameBoard[31] = scanner.nextInt();
            }
        }

        //actions are
        // 0 = what to play
        // 1 = set domino to played
        // 2 = you drew + what you drew
        // 3 = end game

    }
}
