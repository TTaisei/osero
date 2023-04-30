package myNet.network;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Scanner;

import myNet.layer.Layer;
import myNet.matrix.Matrix;
import myNet.nodes.Node;
import myNet.nodes.activationFunction.AF;
import myNet.version.Version;

/**
 * Class for network.
 */
public class Network implements Serializable {
    /** Information of version. */
    public final String version = Version.version;
    /** Number of input variables for this network. */
    public int input_num;
    /** Number of layers. */
    public int layers_num;
    /** Layer array for this network. */
    public Layer[] layers;

    /**
     * Constructor for this class.
     */
    private Network(){
        ;
    }

    /**
     * Constructor for this class.
     * @param input_num number of input.
     * @param layers Each layers.
     */
    public Network(int input_num, Layer ... layers){
        for (int i = 0; i < layers.length; i++){
            if (layers[i].nodes[0] != null){
                System.out.println("Wrong constractor.");
                System.exit(-1);
            }
        }

        this.input_num = input_num;
        this.layers = new Layer[layers.length];
        for (int i = 0; i < this.layers.length; i++){
            this.layers[i] = layers[i].clone();
        }

        int num = 0;
        for (int i = 0; i < this.layers.length; i++){
            AF type = this.layers[i].AF;
            for (int j = 0; j < this.layers[i].nodes.length; j++){
                this.layers[i].nodes[j] = new Node(input_num, type, num);
                num++;
            }
            input_num = this.layers[i].nodes_num;
        }

        this.layers_num = this.layers.length;
    }

    /**
     * Doing forward propagation.
     * @param in input matrix.
     * @return Matrix instance of output.
     */
    public Matrix forward(Matrix in){
        Matrix rtn = in.clone();
        for (int i = 0; i < this.layers.length; i++){
            rtn = this.layers[i].forward(rtn);
            // System.out.println(rtn);
        }

        return rtn;
    }

    /**
     * Save this network to one file.
     * @param name Name of save file.
     */
    public void save(String name){
        try (
            FileOutputStream fos = new FileOutputStream(name);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            oos.writeObject(this);
            oos.flush();
        }catch (IOException e){
            System.out.println("IOException");
            System.exit(-1);
        }
    }

    /**
     * Load a network from the file.
     * @param name Name of load file.
     */
    public void load(String name){
        try (
            FileInputStream fis = new FileInputStream(name);
            ObjectInputStream ois = new ObjectInputStream(fis);
        ){
            Network loadNet = (Network)ois.readObject();
            if (this.versionHasProblem(loadNet)){
                System.exit(-1);
            }else{
                ;
            }
            this.loadParameters(loadNet);
        }catch (IOException e){
            System.out.println("IOException");
            System.exit(-1);
        }catch (ClassNotFoundException e){
            System.out.println("ClassNotFoundException");
            System.exit(-1);
        }
    }

    /**
     * Check that the information of version has problem.
     * If the two networks have different versions, ask do you want to continue.
     * @param loaded Loaded network.
     * @return Do loaded network has problem?
     */
    protected boolean versionHasProblem(Network loaded){
        if (!this.version.equals(loaded.version)){
            System.out.println("The versions of the two Network classes do not match.");
            System.out.printf("The version of loaded is %s\n", loaded.version);
            System.out.printf("The version of this is %s\n", this.version);
            System.out.println("There is a risk of serious error.");
            System.out.print("Do you want to continue? [y/n] ");

            Scanner sc = new Scanner(System.in);
            String ans = this.nextLine(sc);

            while (!(ans.equals("y") || ans.equals("Y")
                     || ans.equals("n") || ans.equals("N"))){
                System.out.println("Choose 'y' or 'n'.");
                System.out.print("Do you want to continue? [y/n] ");
                ans = this.nextLine(sc);
            }

            if (ans.equals("y") || ans.equals("Y")){
                return false;
            }else{
                System.out.println("End.");
                return true;
            }
        }else{
            return false;
        }
    }

    /**
     * Load Network class's parameters.
     * @param loaded Loaded Network instance.
     */
    private void loadParameters(Network loaded){
        if (loaded.layers_num != this.layers.length
        || this.layers_num != loaded.layers.length){
            System.out.println("Layer's number error.");
            System.exit(-1);
        }else if (loaded.input_num != this.input_num){
            System.out.println("Input number error.");
            System.exit(-1);
        }
        this.input_num = loaded.input_num;
        this.layers_num = loaded.layers_num;
        this.layers = loaded.layers;
    }

    /**
     * Read next line.
     * If read String is Null, return "".
     * @param sc Scanner instance.
     * @return Readed String instance.
     */
    private String nextLine(Scanner sc) throws NoSuchElementException {
        String ans;
        try {
            ans = sc.nextLine();
        }catch (NoSuchElementException e){
            ans = "";
        }

        return ans;
    }

    /**
     * Call toString method.
     * @return Summary of this network.
     */
    public String summary(){
        return this.toString();
    }

    @Override
    public String toString(){
        String rtn = String.format("input: %d\n", this.layers[0].nodes[0].in);

        for (int i = 0; i < this.layers.length; i++){
            rtn += "--------------------------------\n";
            rtn += this.layers[i].toString();
            rtn += "\n";
        }
        rtn += "--------------------------------\n";

        return rtn;
    }

    @Override
    public Network clone(){
        Network rtn = new Network();

        rtn.input_num = this.input_num;
        rtn.layers = new Layer[this.layers.length];
        for (int i = 0; i < this.layers.length; i++){
            rtn.layers[i] = this.layers[i].clone();
        }

        int num = 0;
        for (int i = 0; i < this.layers.length; i++){
            AF type = this.layers[i].AF;
            for (int j = 0; j < this.layers[i].nodes.length; j++){
                this.layers[i].nodes[j] = new Node(this.input_num, type, num);
                num++;
            }
            this.input_num = this.layers[i].nodes_num;
        }

        rtn.layers_num = this.layers_num;

        return rtn;
    }
}
