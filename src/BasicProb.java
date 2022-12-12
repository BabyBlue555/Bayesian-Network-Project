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
    int count_mult = 0;


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

    // function that takes care of a list of values - hidden/ query
    // hidden - given a hidden node , calculate all the probs of its values
    // query - for normalization, i need to calculate all the probs of the values that aren't in the .txt
    public ArrayList<Double> calc_prob_values(BayesianNode _node, BayesianNode curr_node, int count_hidden, int count_value_hidden, String[][] cpt) {
        ArrayList<String> _values = new ArrayList<>();
        ArrayList<Integer> rowIndexes = new ArrayList<>();
        ArrayList<Double> rowProbs = new ArrayList<>();
        int index_wanted_row = 0;

        if (check_node(_node).equals("hidden")) {
            _values = getHiddenValues(_node.getVar().getName());
        } else if (check_node(_node).equals("query")) {
            for (String value : _node.getVar().getValues()) {
                if (!value.equals(queryValue)) { // add all the values that aren't the one in the .txt
                    _values.add(value);
                }
            }
        }
        // for both query or hidden nodes
        for (String value : _values) {
            // prob_var(_node,)
        }
//            for (int i = 1; i < cpt.length; i++) {
//                for (int j = cpt[i].length - 2; j >= 0; j--) {
//                    if (cpt[0][j].equals(_node.getVar().getName())) {
//
//                        if (!cpt[i][j].equals(value)) {
//                            continue;
//                        } else {
//                            index_wanted_row = i;
////                            rowIndexes.add(i);
//                        }
//                    }
//                    if (_node.getParents().size() != 0) {
//                        for (BayesianNode parent : _node.getParents()) {
//                            String parent_value = "";
//                            if (!check_node(parent).equals("hidden")) {
//                                parent_value = check_value_node(parent);
//
//                            } else {
//                                String[] parent_values;
//                                //  String parent_value=null;
//                                //  ArrayList<Double> child_prob = new ArrayList<>();
//                                parent_values = parent.getVar().getValues().toArray(new String[0]);
//                                if (count_hidden < parent_values.length && count_value_hidden < parent_values.length) {
//                                    if (check_parent(parent, hidden_node)) {
//                                        parent_value = parent_values[count_hidden % parent_values.length];
//                                    } else {
//                                        parent_value = parent_values[count_value_hidden % parent_values.length];
//                                    }
//                                }
//                            }
//
//                            if (cpt[0][j].equals(parent.getVar().getName())) { // if we are in the parent column (check made_cpt() function for better understanding)
//                                // does it matter if the parent is evidence/ hidden/query?
//                                // the answer is yes, since we need to know how much values we need to search and calculate
//                                //                if (!check_node(parent).equals("hidden")) {
//                                // it doesn't matter if the parent of the node is hidden ,
//                                // because a hidden node can have all its values
//
//                                if (!parent_value.equals(cpt[i][j])) {
//                                    index_wanted_row = -1; // if the value in this row is right for the node but not for one of its' parents
//                                    break; // keep checking the next rows
//                                } else if (index_wanted_row > 0 && i == index_wanted_row) {
//                                    rowIndexes.add(i);
//                                }
////                                    if(index_wanted_row >0) {
//                                //return Double.parseDouble(cpt[index_wanted_row][cpt[index_wanted_row].length - 1]); // col = the probabilities column is the last one
////                                    }
//                                else {
//                                    break;
//                                }
//                            }
//
//                        }
//                    }

        //      }
        //       }
        //    }
        for (Integer index : rowIndexes) {
            rowProbs.add(Double.valueOf(cpt[index][cpt[index].length - 1]));
        }
        return rowProbs;
    }

    public Double calcTotalProb() {

        double total_prob = 0;
        double query_prob = 0;
        double query_norm_prob = 0;
        double evidence_prob = 0;// will storage the multiplication of all the evidence probabilities
        double hidden_prob = 0;

        double numerator = 0;
        double denominator = 0;
//        int count_mult = 0;
        // 1. make a list of all the variables of the net.
        //2. make a node for each variable in order to make a cpt
        //3. make cpt's in order to calculate the probabilities


        //a. make cpt for query, and calculate its single probability

//        //BayesianNode query_node= queryNode;
//        String[][] query_cpt = net.make_CPT(queryNode);


//        //b. make cpt for all evidences, and calculate its single probability
//        for (String evidence : evidence_vars) {
//            BayesianNode evidence_node = (BayesianNode) net.get_nodes().get(evidence);
//            //  BayesianNode evidence_node= new BayesianNode(new Variable(evidence,evi))
//            String[][] evidence_cpt = net.make_CPT(evidence_node);
//            //evidence_prob *= prob_var(evidence_node, evidence_cpt); // multiply between each evidence prob
//            //  total_prob*=prob_evidence;

//            count_mult++; // pay attention - it should be less than num of probabitities

        //      }

        numerator = total_calc(null, "numerator");

        // for normalization:
        BayesianNode queryNode = (BayesianNode) net.get_nodes().get(query_var); // check if its right!
        denominator = numerator;
        ArrayList<Double> query_probs = new ArrayList<>();

//        for (double prob : query_probs) {
        for (String value : queryNode.getVar().getValues()) {
            if (!value.equals(queryValue)) {
                // create a new node that its value is only one of
                // the values of the query node, so we can call
                // the calc function for each one of them seperately.
                ArrayList<String> values = new ArrayList<>();
                values.add(value);
                Variable var = new Variable(query_var, values);
                BayesianNode denom_node = new BayesianNode(var);
                //calculates the given probs of all the other query values
                // that aren't the value in the .txt
                denominator += total_calc(denom_node, "denominator");
                addition_counter++;
            }
        }
//        }


        System.out.println("number of multiplications: " + count_mult);
        System.out.println("number of additions: " + addition_counter);
        return numerator / denominator;
    }


    public Double total_calc(BayesianNode denomNode, String flag) {
        //c. make cpt for all hiddens, and calculate its single probability
        double total_prob = 0;
        double query_prob = 0;
        double evidence_prob = 0;// will storage the multiplication of all the evidence probabilities
        double numerator = 0.0;
        int num_hidden = 0;
        BayesianNode queryNode = (BayesianNode) net.get_nodes().get(query_var); // check if its right!
        //BayesianNode query_node= queryNode;
        String[][] query_cpt = net.make_CPT(queryNode);
        for (String hidden : hidden_vars) {
            BayesianNode curr_node = (BayesianNode) net.get_nodes().get(hidden);
            String[][] hidden_cpt = net.make_CPT(curr_node);

            // turns to zero evertime we go to the next hidden var
            int count_value_hidden = 0; // in order to calculate the evidence prob
            int size_hidden_values = curr_node.getVar().getValues().size();
            // for (double prob : hidden_probs) {
            for (String value : curr_node.getVar().getValues()) {
                evidence_prob = 1;
                // calculte the total prob....
                count_value_hidden = count_value_hidden % size_hidden_values;
                if (flag.equals("numerator")) {
                    query_prob = prob_var(queryNode, curr_node, count_value_hidden, num_hidden, query_cpt);
                } else {
                    query_prob = prob_var(denomNode, curr_node, count_value_hidden, num_hidden, query_cpt);
                    ;
                }
                for (String evidence : evidence_vars) {
                    BayesianNode evidence_node = (BayesianNode) net.get_nodes().get(evidence);
                    String[][] evidence_cpt = net.make_CPT(evidence_node);
                    evidence_prob *= prob_var(evidence_node, curr_node, count_value_hidden, num_hidden, evidence_cpt);
                    count_mult++;
                    // the multiplication of all the evidence probs for a specific addition
                }

                numerator += query_prob * evidence_prob * hidden_prob(curr_node, num_hidden, count_value_hidden);
                count_value_hidden++;
                count_mult += 2;
                addition_counter++;
                //}
            }
            num_hidden++;
        }
//        System.out.println("number of multiplications: " + count_mult);
//        System.out.println("number of additions: " + addition_counter);
        return numerator;
    }

    public boolean check_parent(BayesianNode parent, BayesianNode curr_node) {
        // int index_parent=0;
        //  for (BayesianNode parent : child_node.getParents()) {
        if (curr_node.equals(parent)) {
            return true;
        }

        return false;
    }

    // helping function - is used in every iteration of the hidden_vars
    // when the numenator/ denominator is calculated.
    public double hidden_prob(BayesianNode curr_node, int num_hidden, int count_value_hidden) {
        double prob = 1;
        int index = 0;
        String[] arr_hidden = hidden_vars.toArray(new String[0]);
        for (int i = 0; i < arr_hidden.length; i++) {
            // the index indicates the specific value of the hidden var that we need
            if (arr_hidden[num_hidden].equals(arr_hidden[i])) {
                index = count_value_hidden;
            } else {
                index = num_hidden;
            }
            BayesianNode hidden_node = (BayesianNode) net.get_nodes().get(arr_hidden[i]);
            String[][] hidden_cpt = net.make_CPT(hidden_node);
            //   Double[] hidden_probs = calc_prob_values(hidden_node, hidden_cpt).toArray(new Double[0]);
            String[] arr_values = getHiddenValues(arr_hidden[i]).toArray(new String[0]);

            for (int j = 0; j < arr_values.length; j++) {
                if (j == index) {
                    // creating a new node for using prob_var()
                    ArrayList<String> values = new ArrayList<>();
                    // ArrayList<BayesianNode> parents = new ArrayList<>();
                    BayesianNode[] parents;
                    values.add(arr_values[j]);
                    Variable var = new Variable(arr_hidden[i], values);
                    BayesianNode new_hidden = new BayesianNode(var);
                    parents = hidden_node.getParents().toArray(new BayesianNode[0]);

                    //  for (BayesianNode parent : parents) {
                    // start from last parent from the xml , since it is backwards in the cpt.
                    for (int k = parents.length - 1; k >= 0; k--) {
                        new_hidden.addParents(parents[k]);
                    }
                    // }
                    prob *= prob_var(new_hidden, curr_node, count_value_hidden, num_hidden, hidden_cpt);
                    count_mult++;
                }
                // the goal is to calculate for each hidden var, only the prob
                //for the current value of the addition in the numerator/ denominator
                // prob *= hidden_probs[index % arr_values.length];
            }
        }

        return prob;
    }


//    public Double prob_var_hidden(BayesianNode node, String value, String[][] cpt) {
//
//
//
//    }

    // find the probability of a specific variable with a given value (given its parents)
    public Double prob_var(BayesianNode node, BayesianNode curr_node, int count_value_hidden, int count_hidden, String[][] cpt) {
        double node_prob = 0;
        boolean flag_parents = false;
        String node_value = check_value_node(node); // returns the value
        if (node_value.equals("-1")) {
            // i.e, hidden case
            node_value = node.getVar().getValues().toString().split("\\[")[1].split("]")[0];
        }
        //  int count_right_values=0;
        int index_wanted_row = -1;
        if (node_value != null) {

            if (node.getParents().size() != 0) {
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
                                //     flag= true;
                            }
                        }

                        for (BayesianNode parent : node.getParents()) {
                            flag_parents=false;
                            String parent_value = "";
                            if (!check_node(parent).equals("hidden")) {
                                parent_value = check_value_node(parent);

                            } else {
                                String[] parent_values;
                                //  String parent_value=null;
                                //  ArrayList<Double> child_prob = new ArrayList<>();
                                parent_values = parent.getVar().getValues().toArray(new String[0]);
                                if (count_hidden < parent_values.length && count_value_hidden < parent_values.length) {
                                  // check if its true for all cases!
                                    // if not , fix it according to hidden_prob()
                                    if (check_parent(parent, curr_node)) {
                                        parent_value = parent_values[count_value_hidden % parent_values.length];
                                    } else {
                                        parent_value = parent_values[count_hidden % parent_values.length];
                                    }
                                }
                            }

                            if (cpt[0][j].equals(parent.getVar().getName())) { // if we are in the parent column (check made_cpt() function for better understanding)
                                // does it matter if the parent is evidence/ hidden/query?
                                // the answer is yes, since we need to know how much values we need to search and calculate
                                //                if (!check_node(parent).equals("hidden")) {
                                // it doesn't matter if the parent of the node is hidden ,
                                // because a hidden node can have all its values

                                if (!parent_value.equals(cpt[i][j])) {
                                    index_wanted_row = -1; // if the value in this row is right for the node but not for one of its' parents
                                    break; // keep checking the next rows
                                } else if (!(index_wanted_row > 0 && i == index_wanted_row)) {
                                    break;
                                } else {
                                    flag_parents = true;
                                }

                            }
                        }

                    }
                    if (flag_parents) {
                        return Double.parseDouble(cpt[index_wanted_row][cpt[index_wanted_row].length - 1]); // col = the probabilities column is the last one
                    }

                }

            }

//                if(index_wanted_row >0) {
//                    return Double.parseDouble(cpt[index_wanted_row][cpt[index_wanted_row].length - 1]); // col = the probabilities column is the last one
//                }
//                else{
//                    return 1.0;
//                }
         else { // if node has no parents
             // index out of bounds exception - index_wanted_row = -1 - invalid array index
                // fix it.
            for (int i = 1; i < cpt.length; i++) {
                if (node_value.equals(cpt[i][0])) {
                    node_prob = Double.parseDouble(cpt[i][cpt[i].length - 1]);
                }
            }
        }

        return node_prob;
    }


        return-1.0; // if node_value is null
}


    // return the value of  certein node, given its type
    public String check_value_node(BayesianNode node) {
        String[] array = new String[0];
        String[] arr_values = new String[0];
        if (check_node(node).equals("evidence")) {
            array = evidence_vars;
            arr_values = evidence_values;
        } else if (check_node(node).equals("hidden")) {
            return "-1"; // this method is called by the function prob_ver, for evidence and query only
        } else {
            return queryValue;
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
    static File file = new File("C:\\Users\\User\\IdeaProjects\\AI_try\\input.txt");
    //static File file = new File("input.txt"); // for the cmd running
    //file =
    static Scanner scanner;

static {
        try{
        scanner=new Scanner(file);
        }catch(FileNotFoundException e){
        System.out.println("An error occurred with file reading.");
        e.printStackTrace();
        }
        }


public static void main(String[]args)throws IOException{
        File output=new File("output.txt");
        BufferedWriter out=new BufferedWriter(new FileWriter(output));
        String XML_name=scanner.nextLine();
        BayesianNetwork net=BayesianNetwork.readXML(XML_name); //null pointer exception beacuse factor is null
        System.out.println(net);
        while(scanner.hasNextLine()){
            // make a condition that it will only do BasicProb if in the end of the line there is 1
        String line=scanner.nextLine();
        BasicProb bs=new BasicProb(net,line);
       // System.out.println(bs.hidden_vars);
        BayesianNode queryNode=(BayesianNode)net.get_nodes().get(bs.query_var); // returns the node in key query var - i.e, the query node
        String[][]cpt=net.make_CPT(queryNode);
       // net.printCpt(cpt);
        double result=bs.calcTotalProb();
        System.out.println(result);
//            for(String var_name: bs.hidden_vars) {
//                System.out.println(bs.getHiddenValues(var_name));
//            }
        }
        }

        }


