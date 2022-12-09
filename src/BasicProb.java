import com.sun.org.apache.bcel.internal.generic.RETURN;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.io.*;

public class BasicProb {
    //Instance Variables
    BayesianNetwork net;
 //   BayesianNode queryNode;
    String query_var;
    String queryValue;
    String[] evidence_vars;
    String[] evidence_values;
    ArrayList<String> hidden_vars;
    // ArrayList<String> hidden_values;
    ArrayList<Factor> factors;
    int addition_counter = 0;
  //  int mult_counter = 0;
    int count_mult=0;


    // constructor
    public BasicProb(BayesianNetwork net, String line) {
        this.net = net;
        this.query_var = getQuery(line);
        //this.queryNode= new BayesianNode(new Variable(query_var))
       // this.queryNode= (BayesianNode) net.get_nodes().get(query_var);
        this.queryValue = getQueryValue(line);
        this.evidence_vars = getEvidence(line);
        this.evidence_values = getEvidenceValues(line);
        this.hidden_vars = getHidden();
        //  this.hidden_values = getHiddenValues();

        this.factors = getFactors();

    }

    // get the query variable from the text input file
    public String getQuery(String line) {
        String query = line.substring(2); // take only "QUERY="
        query = query.split("=")[0];
        return query;

    }


    // get the value of the query variable from the text
    public String getQueryValue(String line) {
        String value = line.split("=")[1].split("\\|")[0];
        return value;
    }

    // return the current factors list
    public ArrayList<Factor> getFactors() {
        return factors;
    }

    // get the evidence variables from the text
    public String[] getEvidence(String line) {

        String[] evidences = line.split("\\|")[1].split("\\)")[0].split(","); // make a list that contains each evidence and its value
        String[] evidence_vars = new String[evidences.length];
        for (int i = 0; i < evidences.length; i++) {
            evidence_vars[i] = evidences[i].split("=")[0];
        }
        return evidence_vars;

    }

    // get the values of the evidence variables from the text
    public String[] getEvidenceValues(String line) {

        String[] evidences = line.split("\\|")[1].split("\\)")[0].split(","); // make a list that contains each evidence and its value
        String[] evidence_values = new String[evidences.length];
        for (int i = 0; i < evidences.length; i++) {
            evidence_values[i] = evidences[i].split("=")[1];
        }
        return evidence_values;

    }

    // get the hidden variables from the XML file

    public ArrayList<String> getHidden() {
        ArrayList<String> hidden_vars = new ArrayList<>();
        ArrayList<String> varNames = net.getVariablesNames();
        boolean flag_evidence;
        for (String var : varNames) {
            flag_evidence = false;
            if (var.equals(this.query_var)) {
                continue;
            }
            for (String evidence : this.evidence_vars) {

                if (var.equals(evidence)) {
                    flag_evidence = true;
                    break;
                }
            }
            if (!flag_evidence) {
                hidden_vars.add(var);
            }
        }


        return hidden_vars;
    }

//                    break; // if the variable is evidence don't add it to the list
//                } else if (var.equals(this.query_var)) {
//                    break;// if the variable is query don't add it to the list
//                } else {
//
//                }


    // for a given hidden variable, returns the values of this variable
    public ArrayList<String> getHiddenValues(String hidden_var) {

        ArrayList<String> hidden_values = new ArrayList<>();
        BayesianNode node = (BayesianNode) net.get_nodes().get(hidden_var);
        hidden_values = node.getVar().getValues();
        return hidden_values;
    }

//        //  ArrayList<String> hidden_list=getHidden(line);
//        ArrayList<String> hidden_values = null;
//        //  int addition_counter=1;
//        int permenant_counter = 0;
//        for (String hidden : hidden_vars) {
//            line = scanner.nextLine();
//            hidden_values = new ArrayList<>(); // each hidden var has hidden_values of its own.
//            if (Objects.equals(hidden, net.getData(line))) {  // if this is a hidden variable, and not other types.
//                //  String hidden_name = net.getData(line);
//                //   ArrayList<String> hidden_values = new ArrayList<>();
//
//                while (line.matches("(.*)<OUTCOME>(.*)</OUTCOME>(.*)")) {
//                    permenant_counter = 0;
//                    String value = net.getData(line); //check if the outcome of this is what i need
//                    hidden_values.add(value);
//                    line = scanner.nextLine();
//                    permenant_counter++;
//                }
//
//                this.addition_counter *= permenant_counter;
//
//            } else {
//                continue;
//            }
//
//        }
//        this.addition_counter--; // because we are counting the number of additions and not the number of probabilities.
//        return hidden_values;
//    }

    public ArrayList<Double> calc_prob_hidden(BayesianNode hidden_node, String [][] cpt)
    {
        ArrayList<String> hidden_values=getHiddenValues(hidden_node.getVar().getName());
        ArrayList<Integer> rowIndexes= new ArrayList<>();
        ArrayList <Double> rowProbs= new ArrayList<>();
        for( String value: hidden_values){
        for(int i=1; i<cpt.length; i++){
            for(int j=cpt[i].length-2; j>=0; j--){
                if (cpt[0][j].equals(hidden_node.getVar().getName())){

                        if(! cpt[i][j].equals(value)){
                            continue;
                        }
                        else{
                            rowIndexes.add(i);
                        }
                    }
                }

            }
        }
        for(Integer index: rowIndexes){
            rowProbs.add(Double.valueOf(cpt[index][cpt[index].length-1]));
        }
            return rowProbs;
    }

    public Double calcTotalProb() {

        double total_probability = 0;
        double query_prob = 0;
        double evidence_prob = 0;
//        ArrayList<Double> evidence_prob = new ArrayList<>();
//        ArrayList<Double> hidden_prob = new ArrayList<>();
        double total_prob = 0;
//        int count_mult = 0;
        // 1. make a list of all the variables of the net.
        //2. make a node for each variable in order to make a cpt
        //3. make cpt's in order to calculate the probabilities


        //a. make cpt for query, and calculate its single probability
        BayesianNode queryNode = (BayesianNode) net.get_nodes().get(query_var); // check if its right!
        //BayesianNode query_node= queryNode;
        String[][] query_cpt = net.make_CPT(queryNode);
        query_prob = prob_var(queryNode, query_cpt);
        // total_prob=query_prob;

        //b. make cpt for all evidences, and calculate its single probability
        for (String evidence : evidence_vars) {
            BayesianNode evidence_node = (BayesianNode) net.get_nodes().get(evidence);
            //  BayesianNode evidence_node= new BayesianNode(new Variable(evidence,evi))
            String[][] evidence_cpt = net.make_CPT(evidence_node);
            evidence_prob *= prob_var(evidence_node, evidence_cpt); // multiply between each prob
            //  total_prob*=prob_evidence;

            count_mult++; // pay attention - it should be less than num of probabitities

        }


        //c. make cpt for all hiddens, and calculate its single probability
        for(String hidden: hidden_vars) {
            BayesianNode hidden_node = (BayesianNode) net.get_nodes().get(hidden);
            String[][] hidden_cpt  = net.make_CPT(hidden_node);
            ArrayList<Double> hidden_probs = calc_prob_hidden(hidden_node,hidden_cpt);
            for(double hidden_prob: hidden_probs) {
                // calculte the total prob....
                total_prob += query_prob * evidence_prob * hidden_prob;
            }
            count_mult+=2;
            addition_counter++;
        }




        //    return 1.0; // for now
        //return 1.0;
        return total_prob;
    }

    // helping function
    public double addition_prob(double query, double evidence, double hidden) {
        double sum = query * evidence * hidden;
        return sum;
    }

//    public Double prob_var_hidden(BayesianNode node, String value, String[][] cpt) {
//
//
//
//    }

    // find the probability of a specific variable with a given value (given its parents)
    public Double prob_var(BayesianNode node, String[][] cpt) {
        double node_prob = 0;
        String node_value = check_value_node(node); // returns the value
        //  int count_right_values=0;
        int index_wanted_row = -1;
        if (node_value != null) {
        if (!check_node(node).equals("hidden")) {
            if (node.getParents() != null) {
                for (int i = 1; i < cpt.length; i++) { // start from row 1 since row 0 contains the names of each variable.
                    for (int j = cpt[i].length - 2; j >= 0; j--) { // we start from the node  -second  column from the right
                        if (cpt[0][j].equals(node.getVar().getName())) {

                            // to check if we are on the specific var column
                            // in order to take the row we need from the matrix, we need to check each var values
                            // edge case - if the variable is evidence we will get number of options for values, with help from check_value_node(node) method

                                if (!node_value.equals(cpt[i][j])) {
                                    // index_wanted_row=-1;
                                    break; // keep checking the next rows
                                } else {
                                    index_wanted_row = i;
                                }

                                for (BayesianNode parent : node.getParents()) {
                                    if (cpt[0][j].equals(parent.getVar().getName())) { // if we are in the parent column (check made_cpt() function for better understanding)
                                        // does it matter if the parent is evidence/ hidden/query?
                                        // the answer is yes, since we need to know how much values we need to search and calculate
                                        //                if (!check_node(parent).equals("hidden")) {
                                        // it doesn't matter if the parent of the node is hidden ,
                                        // because a hidden node can have all its values
                                        if (!check_value_node(parent).equals(cpt[i][j])) {
                                            index_wanted_row = -1; // if the value in this row is right for the node but not for one of its' parents
                                            break; // keep checking the next rows
                                        } else {
                                            index_wanted_row = i;
                                        }
                                    }
//                                else{
//
//                                }

                                }

                            }
                            return Double.parseDouble(cpt[index_wanted_row][cpt[i].length - 1]); // col = the probabilities column is the last one

                        }

                    }
                }
//                if(count_right_values==node.getParents().size() +1) {
//                    return cpt[index_wanted_row][0]
//                }


        else{ // if node has no parents
                    for (int i = 1; i < cpt.length; i++) {
                        if (node_value == cpt[i][1]) {
                            node_prob = Double.parseDouble(cpt[i][cpt[i].length - 1]);
                        }
                    }
                }

                return node_prob;
            }
        }

        //}//
        return -1.0;
        // taking care of hidden var scenario
//            else {
////            String [] arr_node_values = (String[]) node.getVar().getValues().toArray(); // array of the var values
////            if(node.getParents()!=null) {
////            }
////            else {
////                for(int i=0; i< arr_node_values.length; i++){
//                return -1.0; // this function doesn't fit to hidden variables.
//
//            }


    }


    // return the value of  certein node, given its type
    public String check_value_node(BayesianNode node) {
        String[] array = new String[0];
        String[] arr_values = new String[0];
        if (check_node(node).equals("evidence")) {
            array = evidence_vars;
            arr_values = evidence_values;
        } else if (check_node(node).equals("hidden")) {
//            array= (String[]) hidden_vars.toArray();
//            arr_values= (String[]) hidden_values.toArray();
        }

//        String [] arr=evidence_vars;
        int index = 0;
        String node_value = null;
        for (int i = 0; i < array.length; i++) {
            if (node.getVar().getName().equals(array[i])) {
                index = i;
                break;
            }
        }
//        String [] arr_values =evidence_values;
        for (int j = 0; j < arr_values.length; j++) {
            if (j == index) { // find the same index as the index in the vars array
                node_value = arr_values[j];
                break;
            }
        }
        return node_value;
    }

    // check which type of variable a certein node is - evidence, hidden or query
    public String check_node(BayesianNode node) {
        //  String value_parent=null;
        for (String evidence : evidence_vars) {
            if (evidence.equals(node.getVar().getName())) {

                return "evidence";
            }
        }
        for (String hidden : hidden_vars) {
            if (hidden.equals(node.getVar().getName())) {
                return "hidden";
            }
        }
        return "query";
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //static File file = new File("C:\\Users\\User\\Documents\\אריאל\\שנה ב\\סמסטר א\\אלגו בבינה מלאכותית\\מטלה\\input.txt");
    static File file = new File("C:\\Users\\User\\IdeaProjects\\AI_try\\input_bignet.txt");
    //static File file = new File("input.txt"); // for the cmd running
    //file =
    static Scanner scanner;

    static {
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred with file reading.");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        File output = new File("output.txt");
        BufferedWriter out = new BufferedWriter(new FileWriter(output));
        String XML_name = scanner.nextLine();
        BayesianNetwork net = BayesianNetwork.readXML(XML_name); //null pointer exception beacuse factor is null
        System.out.println(net);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            BasicProb bs = new BasicProb(net, line);
            System.out.println(bs.hidden_vars);
            BayesianNode queryNode= (BayesianNode) net.get_nodes().get(bs.query_var); // returns the node in key query var - i.e, the query node
            String[][] cpt=net.make_CPT(queryNode);
            net.printCpt(cpt);
//            for(String var_name: bs.hidden_vars) {
//                System.out.println(bs.getHiddenValues(var_name));
//            }
        }
    }

}


