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

    // for a given hidden variable, returns the values of this variable
    public ArrayList<String> getHiddenValues(String hidden_var) {

        ArrayList<String> hidden_values = new ArrayList<>();
        BayesianNode node = (BayesianNode) net.get_nodes().get(hidden_var);
        hidden_values = node.getVar().getValues();
        return hidden_values;
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
//        //b. make cpt for all evidences, and calculate its single probability


        numerator = total_calc(null, "numerator", -1);

        // for normalization:
        BayesianNode queryNode = (BayesianNode) net.get_nodes().get(query_var); // check if its right!
        denominator = numerator;
        ArrayList<Double> query_probs = new ArrayList<>();

//        for (double prob : query_probs) {
        int index_denom = 0;
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
                denominator += total_calc(denom_node, "denominator", index_denom);
                addition_counter++;

            }
            index_denom++;
        }


        System.out.println("number of multiplications: " + count_mult);
        System.out.println("number of additions: " + addition_counter);
        return numerator / denominator;
    }


    public Double total_calc(BayesianNode denomNode, String flag, int index_denom) {
        //c. make cpt for all hiddens, and calculate its single probability
        double total_prob = 0;
        double query_prob = 0;
        double evidence_prob = 1;// will storage the multiplication of all the evidence probabilities
        double hidden_prob = 1;
        double numerator = 0.0;
        // int num_hidden = 0;
        BayesianNode queryNode = (BayesianNode) net.get_nodes().get(query_var); // check if its right!
        String[][] query_cpt = net.make_CPT(queryNode);
//        for (String hidden : hidden_vars) {
        String[][] hidden_values_table = hidden_prob_table();
        for (int i = 1; i < hidden_values_table.length; i++) {
            evidence_prob = 1;
            hidden_prob = 1;
            query_prob = 0;

            if (flag.equals("numerator")) {
                query_prob = prob_var(queryNode, query_cpt, hidden_values_table, i, index_denom);
            } else {
                if (index_denom != -1)
                    query_prob = prob_var(denomNode, query_cpt, hidden_values_table, i, index_denom);

            }
            for (String evidence : evidence_vars) {
                BayesianNode evidence_node = (BayesianNode) net.get_nodes().get(evidence);
                String[][] evidence_cpt = net.make_CPT(evidence_node);
                evidence_prob *= prob_var(evidence_node, evidence_cpt, hidden_values_table, i, index_denom);
                count_mult++;
                // the multiplication of all the evidence probs for a specific addition
            }

            for (int j = 0; j < hidden_values_table[i].length; j++) {
                BayesianNode hidden_node = (BayesianNode) net.get_nodes().get(hidden_values_table[0][j]);
                String[][] hidden_cpt = net.make_CPT(hidden_node);
                ArrayList<String> values = new ArrayList<>();
                // ArrayList<BayesianNode> parents = new ArrayList<>();
                BayesianNode[] parents;
                values.add(hidden_values_table[i][j]);
                Variable var = new Variable(hidden_values_table[0][j], values);
                BayesianNode new_hidden = new BayesianNode(var);
                parents = hidden_node.getParents().toArray(new BayesianNode[0]);

                //  for (BayesianNode parent : parents) {
                // start from last parent from the xml , since it is backwards in the cpt. - uncessesary , its in prob_var
                for (int k = 0; k < parents.length ; k++) {
                    new_hidden.addParents(parents[k]);
                }

                hidden_prob *= prob_var(new_hidden, hidden_cpt, hidden_values_table, i, index_denom);
            }

            numerator += query_prob * evidence_prob * hidden_prob;
        }
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

    public String[][] hidden_prob_table() {

        String[] arr_hidden = hidden_vars.toArray(new String[0]);
        int mult_hiddens = 1;
        String[] arr_values = new String[0];
        int value_index = 0;
        String first_str = null;


        for (int j = 0; j < arr_hidden.length; j++) {
            arr_values = getHiddenValues(arr_hidden[j]).toArray(new String[0]);
            mult_hiddens *= arr_values.length;

        }
        String[][] arr_new_values = new String[mult_hiddens + 1][arr_hidden.length];
        for (int j = 0; j < arr_hidden.length; j++) // "columns" = number of hidden variables
        {
            value_index = 0;
            arr_values= getHiddenValues(arr_hidden[j]).toArray(new String[0]);
            for (int i = 0; i <= mult_hiddens; i++) // number of "rows" = mult of values of all the hiddens
            {
                // i==0 - put the hidden vars names to identify on prob_var(..)
                if (i == 0) {
                    arr_new_values[0][j] = arr_hidden[j]; // name of the variable

                } else {
                    if (j == 0 ) { // first hidden variable
                     //   break;
                        arr_new_values[i][j] = arr_values[value_index];
                        value_index = (value_index + 1) % arr_values.length;
                        first_str = arr_new_values[1][0]; // not necessary

                    } else if (j == 1 ) {
                     //   break;
                        if (arr_new_values[i][j - 1].equals(arr_new_values[1][0])) {
                            arr_new_values[i][j] = arr_values[value_index];
                            value_index = (value_index + 1) % arr_values.length;
                        } else {

                                int previous_value_index = (value_index - 1) % arr_values.length;
                                if (previous_value_index < 0) {
                                    previous_value_index += arr_values.length;
                                }
                                arr_new_values[i][j] = arr_values[previous_value_index];

                            }
                        }

                    else { // all the other columns

                            // if (arr_new_values[1][j - 1].equals(arr_new_values[i][j - 1]) && arr_new_values[1][j - 2].equals(arr_new_values[i][j - 2])) {
                            if (table_value_rec(arr_new_values, i, j)) {
                                arr_new_values[i][j] = arr_values[value_index];
                                value_index = (value_index + 1) % arr_values.length;
                            } else {
                                int previous_value_index = (value_index - 1) % arr_values.length;
                                if (previous_value_index < 0) {
                                    previous_value_index += arr_values.length;
                                }
                                arr_new_values[i][j] = arr_values[previous_value_index];



                        }

                    }
                }

            }
        }
        return arr_new_values;  // meantime
    }

    // helping function for hidden_prob(). checks if we should change the value of the current var,
    //or should we keep putting the same value, according to previous values of vars in the table
    public boolean table_value_rec(String[][] arr_new_values, int i , int j){

        if(j==1){
            return true;
        }
        else if(!arr_new_values[1][j-1].equals(arr_new_values[i][j-1]) || !arr_new_values[1][j-2].equals(arr_new_values[i][j-2])){

            return false;
        }
        else{
            return table_value_rec(arr_new_values,i, j-1);
        }
    }


    // find the probability of a specific variable with a given value (given its parents)
    public Double prob_var(BayesianNode node, String[][] cpt, String[][] hidden_table, int index_hidden, int index_denom) {
        double node_prob = 0;
        boolean flag_parents = false;
        String node_value = check_value_node(node); // returns the value
        if (node_value.equals("-1") || (node.getVar().getValues().size() == 1)) {
            // i.e, hidden case
            node_value = node.getVar().getValues().toString().split("\\[")[1].split("]")[0];
        } else {
            // if we are in the denominator and the node is query
            //check_node(parent).equals("query")
            if (index_denom != -1 && node.getVar().getName().equals(query_var)) {
                String[] arr_val = node.getVar().getValues().toArray(new String[0]);
                node_value = arr_val[index_denom];
            }
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

                        BayesianNode [] parents= node.getParents().toArray(new BayesianNode[0]);
                        for (int par=parents.length-1;par>=0;par--) {
                            flag_parents = false;
                            String parent_value = "";

                            // edge case - if we calculate the denominator and the parent is query
                            if (index_denom != -1 && check_node(parents[par]).equals("query")) {
                                String[] arr_val = parents[par].getVar().getValues().toArray(new String[0]);
                                parent_value = arr_val[index_denom];

                            }


                                //the parent is not hidden and also we are in the numenator calc
                                if (!check_node(parents[par]).equals("hidden")) {
                                    parent_value = check_value_node(parents[par]);

                                }

                                // edge case - if the parent is hidden
                                else {
                                    // String[] parent_values;
                                    //  parent_values = parent.getVar().getValues().toArray(new String[0]);
                                    for (int k = 0; k < hidden_table[index_hidden].length; k++) {
                                        if (hidden_table[0][k].equals(parents[par].getVar().getName())) {

                                            parent_value = hidden_table[index_hidden][k];

                                        }
                                    }

                                }


                            if (cpt[0][j].equals(parents[par].getVar().getName())) { // if we are in the parent column (check made_cpt() function for better understanding)
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

            } else { // if node has no parents
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


        return -1.0; // if node_value is null
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
    static File file = new File("C:\\Users\\User\\IdeaProjects\\AI_try\\input2.txt");
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
            // make a condition that it will only do BasicProb if in the end of the line there is 1
            String line = scanner.nextLine();
            BasicProb bs = new BasicProb(net, line);
            // System.out.println(bs.hidden_vars);
            BayesianNode queryNode=new BayesianNode(new Variable(bs.query_var,null));
            if(net.get_nodes()!=null) {
                queryNode = (BayesianNode) net.get_nodes().get(bs.query_var); // returns the node in key query var - i.e, the query node
            }
            String[][] cpt = net.make_CPT(queryNode);
            // net.printCpt(cpt);
            double result = bs.calcTotalProb();
            System.out.println(result);
//            for(String var_name: bs.hidden_vars) {
//                System.out.println(bs.getHiddenValues(var_name));
//            }
        }
    }

}


