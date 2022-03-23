import java.util.function.BiConsumer;
import java.util.ArrayList;
import java.util.Random;

public class OseroData extends Osero {
    public ArrayList<ArrayList<Double>> history = new ArrayList<ArrayList<Double>>();
    public ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();

    public OseroData(){
        this.setup();
    }

    public OseroData(ArrayList<BiConsumer<long[], Boolean>> playMethod){
        this.setup();
        this.playMethod = playMethod;
    }

    public OseroData(BiConsumer<long[], Boolean> black, BiConsumer<long[], Boolean> white){
        this.setup();
        this.playMethod.add(black);
        this.playMethod.add(white);
    }

    public void addData(int num){
        boolean can, oldCan;
        double blackScore, whiteScore;
        ArrayList<Boolean> turnHistory = new ArrayList<Boolean>();

        for (int i = 0; i < num; i++){
            this.turn = false;
            can = true; oldCan = true;
            this.setup();
            turnHistory.clear();
            this.rand = new Random(i);

            while ((can = this.checkAll()) || oldCan){
                if (can){
                    this.playMethod.get(this.turn ? 1:0).accept(this.bw, this.turn);
                    turnHistory.add(this.turn);

                    ArrayList<Double> element = new ArrayList<Double>();
                    this.writeHistory(this.turn, element);
                    this.history.add(element);
                }

                this.turn = !this.turn;
                oldCan = can;
            }

            blackScore = (double)this.popCount(this.bw[0]);
            whiteScore = (double)this.popCount(this.bw[1]);

            for (Boolean turn: turnHistory){
                ArrayList<Double> element = new ArrayList<Double>();
                element.add(turn ? whiteScore : blackScore);
                this.result.add(element);
            }
        }
    }

    private void writeHistory(boolean turn, ArrayList<Double> ele){
        long place = 1;
        int my, opp;
        boolean myStone, oppStone;

        if (turn){
            my = 1; opp = 0;
        }else{
            my = 0; opp = 1;
        }

        while (place != 0){
            ele.add((myStone = ((this.bw[my] & place) != 0)) ? 1.0 : 0.0);
            ele.add((oppStone = ((this.bw[opp] & place) != 0)) ? 1.0 : 0.0);
            ele.add(myStone || oppStone ? 0.0 : 1.0);
            place = place << 1;
        }
    }

    public void dataClear(){
        this.history.clear();
        this.result.clear();
    }
}