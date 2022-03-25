import java.util.function.BiConsumer;
import java.util.ArrayList;

import org.MyNet.network.*;
import org.MyNet.nodes.activationFunction.*;
import org.MyNet.layer.*;
import org.MyNet.matrix.*;

public class OseroAI extends Osero {
    protected static Network net = null;

    public OseroAI(ArrayList<BiConsumer<long[], Boolean>> playMethod, String netName){
        this.setup();
        this.playMethod = playMethod;
        this.loadNet(netName);
    }

    public OseroAI(BiConsumer<long[], Boolean> black, BiConsumer<long[], Boolean> white, String netName){
        this.setup();
        this.playMethod.add(black);
        this.playMethod.add(white);
        this.loadNet(netName);
    }

    public int[] playAndGetResult(){
        boolean can = true, oldCan = true;

        while ((can = this.checkAll()) || oldCan){
            if (can){
                this.playMethod.get(this.turn ? 1:0).accept(this.bw, this.turn);
            }

            this.turn = !this.turn;
            oldCan = can;
        }

        int[] rtn = {this.popCount(this.bw[0]), this.popCount(this.bw[1])};
        return rtn;
    }

    protected void loadNet(String netName){
        net = new Network(
            192,
            new Input(100, AF.RELU),
            new Dense(50, AF.SIGMOID),
            new Output(1, AF.RELU)
        );
        net.load(netName);
    }

    public static void oseroAI(long board[], boolean turn){
        Osero.exploreAssist(board, turn, OseroAI::exploreAI);
    }

    protected static double exploreAI(long[] board, boolean nowTurn, boolean turn, int num){
        if (num >= Osero.readGoal[(nowTurn ? 1:0)]) return OseroAI.predict(board, nowTurn);

        double score = 0, placeNum = 0;
        int row = -1, col = 0;
        long[] boardLeaf = new long[2];
        for (int place = 0; place < OseroBase.SIZE << OseroBase.SHIFTNUM; place++){
            row++;
            if (row >= OseroBase.SIZE){
                row = 0;
                col++;
            }
            if (!OseroBase.check(row, col, board, turn)) continue;
            placeNum += 1;
            boardLeaf[0] = board[0]; boardLeaf[1] = board[1];
            OseroBase.put(row, col, boardLeaf, turn);
            score += OseroAI.exploreAI(
                boardLeaf,
                nowTurn,
                !turn,
                num + 1    
            );
        }
        
        if (placeNum > 0) return score / placeNum;
        else              return OseroAI.predict(board, nowTurn);
    }

    protected static double predict(long board[], boolean turn){
        Matrix x;
        ArrayList<ArrayList<Double>> x_list = new ArrayList<ArrayList<Double>>();
        x_list.add(new ArrayList<Double>());

        long place = 1;
        int my, opp;
        boolean myStone, oppStone;

        if (turn){
            my = 1; opp = 0;
        }else{
            my = 0; opp = 1;
        }

        while (place != 0){
            x_list.get(0).add((myStone = ((board[my] & place) != 0)) ? 1.0 : 0.0);
            x_list.get(0).add((oppStone = ((board[opp] & place) != 0)) ? 1.0 : 0.0);
            x_list.get(0).add(myStone || oppStone ? 0.0 : 1.0);
            place = place << 1;
        }

        x = new Matrix(x_list);

        return OseroAI.net.forward(x).matrix[0][0];
    }
}