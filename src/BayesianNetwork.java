import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors

import static java.util.Objects.isNull;


/*
    this class's main goal is to read from the xml file all the data
     about the variables and their cpt's and to add them to the bayesian network
 */

public class BayesianNetwork {
    private Hashtable _nodes; // explain why i chose hashtable
    // private int size;


    public BayesianNetwork() { // constructor

        this._nodes = new Hashtable();

    }

    public Hashtable get_nodes() {
        return _nodes;
    }

    public void addNode(String name, BayesianNode node) {
        this._nodes.put(name, node);
    }

    // helping function for createVariables and createNodes
    // returns the value between tags in the xml file
    public static String getData(String line) {
        String value = line.split(">")[1].split("<")[0]; // get the data between >data< from the xml
        return value;
    }

    // helping function for createVariables
    public ArrayList<String> getVariablesNames() {
        ArrayList<String> names = new ArrayList<String>(this.get_nodes().keySet());
        return names;
    }


    // reads the xml file and call to function in order to create the variables and nodes of the network
    // input: string name of the xml file in the input txt, output: bayesian network
    public static BayesianNetwork readXML(String fileName) throws FileNotFoundException {
        BayesianNetwork net = new BayesianNetwork();
        try {

            File xmlFile = new File(fileName);
            Scanner scan = new Scanner(xmlFile);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.matches("(.*<VAR.*)")) {
                    createVariables(net, scan);
                } else {
                    if (line.matches("(.*<DEF.*)")) {
                        createNodes(net, scan); // also creates a factor
                    }
                }
            }
            scan.close();
        } catch (FileNotFoundException ex) {
            System.out.println("exception has found:");
            ex.printStackTrace();
        }
        return net;
    }

    // from each part of Variable in the xml file,
    // this method create bayesian node and append it to the Bayesian Network
    private static void createVariables(BayesianNetwork net, Scanner scanner) {
        String line = scanner.nextLine();
        String var_name = getData(line);
        ArrayList<String> var_values = new ArrayList<>();
        line = scanner.nextLine(); // move to next line in order to find the var values.
        while (line.matches("(.*)<OUTCOME>(.*)</OUTCOME>(.*)")) {
            String value = getData(line);
            var_values.add(value);
            line = scanner.nextLine();
        }
        Variable var = new Variable(var_name, var_values);
        try {
            BayesianNode node = new BayesianNode(var);
            net.addNode(var_name, node);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("null error");
        }
    }

    public static void createNodes(BayesianNetwork net, Scanner scanner) {
        String name_var;
        ArrayList<String> parents = new ArrayList<>();
        ArrayList<Double> probs = new ArrayList<>();
        ArrayList<String> children = new ArrayList<>();

        String line = scanner.nextLine();
        name_var = getData(line);
        // find parents in text
        line = scanner.nextLine();
        while (line.matches("(.*)<GIVEN>(.*)</GIVEN>(.*)")) {
            parents.add(getData(line));
            line = scanner.nextLine();
        }

        //initialize parents

        Hashtable nodes = net.get_nodes();

        BayesianNode cur_node = (BayesianNode) nodes.get(name_var); // get the current node from the hashtable by its variable name
        if (isNull(cur_node)) {
            return;
        }

        if (!isNull(cur_node)) {
            for (String parent : parents) {
                BayesianNode parent_node = (BayesianNode) net.get_nodes().get(parent);
                cur_node.addParents(parent_node); // add the parent to the child - current node list of parents
            }
        }

        // for all the parents of the current node , add the current node as a child to them
        for (String parent : parents) {
            BayesianNode parent_node = (BayesianNode) net.get_nodes().get(parent);
            parent_node.addChildren(cur_node);
        }
        // find probabilities in xml
        String[] probs_str = line.split(">")[1].split("<")[0].split(" ");
        for (String str : probs_str) {
            probs.add(Double.parseDouble(str));
        }
        // String[] ST=prob(probs_str);

        //make factor:
        ArrayList<Variable> varsOfFactor = new ArrayList<>();
        for (String parent : parents) {
            varsOfFactor.add(((BayesianNode) net.get_nodes().get(parent)).getVar());
        }


        if (!isNull(cur_node)) {
            Variable var = cur_node.getVar();
            varsOfFactor.add(var);
            ArrayList<String> var_values= new ArrayList<>();
            String [][]cpt= new String[0][0];
            Factor f = new Factor(varsOfFactor, probs,cpt);
            cur_node.setFactor(f);
        }
    }


    public String[][] make_CPT(BayesianNode node) {

        // null pointer exception found!
        String[][] cpt=null;
        if (node != null) {

            int num_parents = node.getParents().size();
            int num_col = num_parents + 2; // 2 = one query and probability column
            int num_rows = node.getVar().getValues().size();
            if (num_parents > 0) {
                for (BayesianNode parent : node.getParents()) {
                    num_rows *= parent.getVar().getValues().size();
                }
            }
            num_rows++; // because we want to have a row for the variable names
             cpt = new String[num_rows][num_col];

            // putting values in the matrix
            //a. putting names of vars in the first row
            BayesianNode[] arr = node.getParents().toArray(new BayesianNode[0]);
            for (int j = num_col - 1; j >= 0; j--) {
                if (j == num_col - 1) {
                    continue; // no name for the probability column
                } else if (j == num_col - 2) {
                    cpt[0][j] = node.getVar().getName(); // the node column is the first column after the probabilities
                } else {
                    cpt[0][j] = arr[j].getVar().getName(); // otherwise, it's the parents' column

//                    while (j >= 0) {
//                        for (BayesianNode parent : node.getParents()) {
//                            cpt[0][j] = parent.getVar().getName();
//                        }
//                        j--;
//                    }
                }
            }

            // fill the last column of the matrix with probabilities
            int value_index = 0;
            String[] values = node.getVar().getValues().toArray(new String[0]);
            for (int j = num_col - 1; j >= num_col - 2; j--) {
                for (int i = 1; i < num_rows; i++) {
                    if (j == num_col - 1) { // column of probabilities
                        for (Double prob : node.getFactor().getProbabilities()) {
                            if (i < num_rows) {
                                cpt[i][j] = String.valueOf(prob); // convert double to string
                                i++;
                            }
                        }
                        // fill the columns of node with its values
                    } else if (j == num_col - 2) { // column of query
                        cpt[i][j] = values[value_index];
                        value_index = (value_index + 1) % values.length; // in order to have a " TFTFTF..." sequence
                    }
                }
            }

            String first_str = cpt[1][num_col - 2]; // first cell of the node column - for example, of node A = "T"
            int index_par=node.getParents().size()-1;
            for (int j = num_col - 3; j >= 0; j--) {
                //for (BayesianNode parent : node.getParents()) {
                BayesianNode [] arr_par= node.getParents().toArray(new BayesianNode[0]);
            //    for(int par=0;par<= arr_par.length;par++)
                    value_index = 0; // for each parent node, start over
                    if(index_par>=0) {
                        values = arr_par[index_par].getVar().getValues().toArray(new String[0]);
                    }
                    for (int i = 1; i < num_rows; i++) {

                        // edge case - the nearest column to the node column : j= num_col-3
                        if (j == num_col - 3) {

                            if (cpt[i][num_col - 2].equals(first_str)) {

                                cpt[i][j] = values[value_index];
                                value_index = (value_index + 1) % values.length;
                            } else {

                                int previous_value_index = (value_index - 1) % values.length;
                                if (previous_value_index < 0) {
                                    previous_value_index += values.length;
                                }
                                cpt[i][j] = values[previous_value_index];
                            }
                        } else {

                       //     if (cpt[1][j + 1].equals(cpt[i][j + 1]) && cpt[1][j + 2].equals(cpt[i][j + 2])) {
                            if(cpt_value_rec(cpt,i,j, num_col)) {
                                cpt[i][j] = values[value_index];
                                value_index = (value_index + 1) % values.length;

                                //  }
                            }
                        else {
                                int previous_value_index = (value_index - 1) % values.length;
                                if (previous_value_index < 0) {
                                    previous_value_index += values.length;
                                }
                                cpt[i][j] = values[previous_value_index];
                            }

                        }


                    }
              //  }
                index_par--;
            }
        }

        return cpt;


    } // end of make_cpt function

    private boolean cpt_value_rec(String[][] cpt, int i, int j, int num_col) {


            if(j == num_col - 3){
                return true;
            }
            else if(!cpt[1][j + 1].equals(cpt[i][j + 1]) || !cpt[1][j + 2].equals(cpt[i][j + 2])){

                return false;
            }
            else{
                return cpt_value_rec(cpt,i, j+1, num_col);
            }
        }





    public void printCpt(String[][] cpt) {
        for (int i = 0; i < cpt.length; i++) {
            for (int j = 0; j < cpt[i].length; j++) {
                System.out.print(cpt[i][j] + "|");
            }
            System.out.println("\n");
        }
    }

    @Override
    public String toString() {
        return "BayesianNetwork{" +
                "_nodes=\n" + _nodes.toString() +
                "}";


    }

}