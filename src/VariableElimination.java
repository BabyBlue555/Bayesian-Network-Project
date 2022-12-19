import java.io.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class VariableElimination {

    private BayesianNetwork net;
    private String query_var;
    private String queryValue;
    private String[] evidence_vars;
    private String[] evidence_values;
    private ArrayList<String> hidden_vars;
    private String[] _relevant;
    private ArrayList<Factor> factors;
    private int _additionCounter;
    private int _multiplicationCounter;
    private boolean _is_immediate;
    private String _answer;

    public VariableElimination(BayesianNetwork net, String line) {

        this.net = net;
        this.query_var = getQuery(line);
        //this.queryNode= new BayesianNode(new Variable(query_var))
        // this.queryNode= (BayesianNode) net.get_nodes().get(query_var);
        this.queryValue = getQueryValue(line);
        this.evidence_vars = getEvidence(line);
        this.evidence_values = getEvidenceValues(line);
        this.hidden_vars = getHidden();
        //  this.hidden_values = getHiddenValues();
        this.factors = get_factors();

    }

    private ArrayList<Factor> get_factors() {
//        getCPTs_factor();
        ArrayList<Factor> _factors = new ArrayList<>();
        BayesianNode query_node = (BayesianNode) net.get_nodes().get(query_var);
        Factor query_factor = query_node.getFactor(); // check null pointer excepition
        _factors.add(query_factor);

        for (String str : evidence_vars) {
            BayesianNode evidence_node = (BayesianNode) net.get_nodes().get(str);
            Factor evidence_factor = evidence_node.getFactor();
            _factors.add(evidence_factor);
        }

        for (String str : hidden_vars) {
            BayesianNode hidden_node = (BayesianNode) net.get_nodes().get(str);
            Factor hidden_factor = hidden_node.getFactor();
            _factors.add(hidden_factor);
        }
        return _factors;
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
//    public ArrayList<Factor> getFactors() {
//        return factors;
//    }


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

    // BasicProb bs= new BasicProb(this.net,line)
    public String check_value_evidence(BayesianNode node) {
        String[] array = new String[0];
        String[] arr_values = new String[0];
        array = evidence_vars;
        arr_values = evidence_values;

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

//    public ArrayList<String[][]> getCPTs() throws NullPointerException {
//        ArrayList<String[][]> factors = new ArrayList<>();
//        //String[][] query_cpt=null;
//        try {
//            // query
//            BayesianNode query_node = (BayesianNode) net.get_nodes().get(query_var);
//            String[][] query_cpt = net.make_CPT(query_node);
//            factors.add(query_cpt);
//            // evidences
//            for (String str : evidence_vars) {
//                BayesianNode evidence_node = (BayesianNode) net.get_nodes().get(str);
//                String[][] evidence_cpt = net.make_CPT(evidence_node);
//                factors.add(evidence_cpt);
//            }
//            //hidden
//            for (String str : hidden_vars) {
//                BayesianNode hidden_node = (BayesianNode) net.get_nodes().get(str);
//                String[][] hidden_cpt = net.make_CPT(hidden_node);
//                factors.add(hidden_cpt);
//            }
//
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//        // return cpt;
//        return factors;
//    }


    // make cpt's for each variable
    public ArrayList<String[][]> getCPTs_factor() throws NullPointerException {
        ArrayList<String[][]> cpts = new ArrayList<>();
        //String[][] query_cpt=null;
        try {
            // query
            BayesianNode query_node = (BayesianNode) net.get_nodes().get(query_var);
            Factor query_factor = query_node.getFactor(); // check null pointer excepition
            String[][] query_cpt = query_factor.make_cpt(query_node);
            query_factor.cpt = query_cpt;
            cpts.add(query_cpt);
            // evidences
            for (String str : evidence_vars) {
                BayesianNode evidence_node = (BayesianNode) net.get_nodes().get(str);
                Factor evidence_factor = evidence_node.getFactor();
                //  factors.add(evidence_factor);
                String[][] evidence_cpt = evidence_factor.make_cpt(evidence_node);
                evidence_factor.cpt = evidence_cpt;
                cpts.add(evidence_cpt);
            }
            //hidden
            for (String str : hidden_vars) {
                BayesianNode hidden_node = (BayesianNode) net.get_nodes().get(str);
                Factor hidden_factor = hidden_node.getFactor();
                //factors.add(hidden_factor);
                String[][] hidden_cpt = hidden_factor.make_cpt(hidden_node);
                hidden_factor.cpt = hidden_cpt;
                cpts.add(hidden_cpt);

            }


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // return cpt;
        return cpts;
    }


    // put the evidence values - terminate tables that have only one value
    // returns the new list of factors - after removing the evidence values
    public ArrayList<String[][]> find_evidence() {
        String[][] new_cpt;
        ArrayList<String[][]> new_list = new ArrayList<>();
        for (Factor factor : this.factors) {
            new_cpt = null; // to each factor, start over
            String[] vars_factor = get_name_vars(factor.cpt);
            for (String evidence_name : evidence_vars) {
                for (int i = 0; i < vars_factor.length; i++) {
                    if (evidence_name.equals(vars_factor[i])) {
                        BayesianNode node_evidence = (BayesianNode) net.get_nodes().get(evidence_name);
                        ;
                        String value = check_value_evidence(node_evidence);
                        if (new_cpt != null) {
                            factor.setCpt(new_cpt);
//                            new_cpt=delete_evidence(factor,evidence_name, value);
                        }
                        new_cpt = delete_evidence(factor, evidence_name, value);
                        if (new_cpt.length == 2) { // 2 rows- including the variable names - first row
                            // remove_factor(new_cpt);
                            factors.remove(new_cpt);
                        }

                    }
                }

            }
            if (new_cpt != null) {
                new_list.add(new_cpt); // after we checked we deleted all of the evidences from the factor
                factor.setCpt(new_cpt); // in order to have an access to the updated cpt through factor class
            }
        }

        return new_list;
    }
//        ArrayList<String[][]> factors = getCPTs_factor();
//        for(String[][] factor: factors){
//            String [] vars_factor =get_name_vars(factor);
//            for(String evidence_name: evidence_vars){
//                for (int i=0; i< vars_factor.length;i++){
//                    if(evidence_name.equals(vars_factor[i])){
//                        delete_evidence( factor);
//                    }
//                }
//            }
    //System.out.println(arr_factors[i]);
    //  }
//        return null;
//    }

    // returns an array of names of the variables in the factor
    private String[] get_name_vars(String[][] factor) {
        String[] vars = new String[factor[0].length - 1]; // number of variables
        for (int j = 0; j < factor[0].length - 1; j++) {
            vars[j] = factor[0][j];
        }
        return vars;
    }

    private String[][] delete_evidence(Factor factor, String evidence_name, String value) {
        int count_rows = 1; // for the new cpt - include the values names-> first row
        int count_columns = 0; // for the new cpt
        int index_delete = 0;
        int index_arr_rows = 0;
        boolean evidence_val = false;
        ArrayList<String> new_cpt_vars = new ArrayList<>();
        String[][] new_cpt = new String[0][0];
        ArrayList<String> list_prob = new ArrayList<>();
        ArrayList<String[]> list_rows = new ArrayList<>(); // the new values for every row

        if (!evidence_name.equals(null)) {
            count_columns = get_name_vars(factor.cpt).length; // including the prob column
            for (int i = 0; i < factor.cpt.length; i++) {
                index_arr_rows = 0;
                evidence_val = false;
                String[] arr_row = new String[count_columns - 1];
                for (int j = factor.cpt[i].length - 2; j >= 0; j--) { // not including the last col of probs
                    if (i == 0) {
                        if (factor.cpt[0][j].equals(evidence_name)) {
                            index_delete = j; // the column we want to delete its rows
                        } else {
                            new_cpt_vars.add(factor.cpt[0][j]);
                        }
                    } else {
                        if (factor.cpt[0][j].equals(evidence_name)) {
                            if (!factor.cpt[i][j].equals(value)) {

                                continue;
                            } else {
                                evidence_val = true; // indicates that this row is not "deleted"
                                list_prob.add(factor.cpt[i][factor.cpt[i].length - 1]);
                                count_rows++;
                            }
                        } else {  //if we are not in the evidence column, take the values for the new cpt
                            // in case the evidence value is the right one in this row
                            // if(evidence_val ) { // check - indexoutofbounds

                            // pay attention to the index - it's in order to make the first variable be in the right and the others from the left
                            arr_row[count_columns - 2 - index_arr_rows] = factor.cpt[i][j];
                            index_arr_rows++;
                            //}
                        }
                    }
                }
                if (i != 0 && evidence_val) {
                    list_rows.add(arr_row); // adds the row we iterated, only if the evidence value is correct
                }
            }

            new_cpt = new String[count_rows][count_columns];
            //arr_rows = list_rows.toArray();
            String[] cpt_vars = new_cpt_vars.toArray(new String[0]);
            for (int i = 0; i < count_rows; i++) {
                for (int j = count_columns - 1; j >= 0; j--) {
                    // edge case - first row ->  put names of variables
                    if (i == 0 && j != count_columns - 1) {
                        new_cpt[i][j] = cpt_vars[count_columns - 2 - j]; // in order to put them it the correct order of the algorythm
                    }
                    // edge case - probability column
                    else if (j == count_columns - 1) {
                        if (i == 0) {
                            new_cpt[i][j] = "probs";
                        } else {
                            for (String prob : list_prob) {
                                if (i < count_rows) {
                                    new_cpt[i][j] = prob;
                                    i++;
                                }
                            }

                        }
                    } else {
                        i = 1;
                        for (String[] arr : list_rows) { // take the values for the current column
                            if (i < count_rows) {
                                new_cpt[i][j] = arr[j];
                                if (i == count_rows - 1) { //  to avoid indexoutofbounds in the next line
                                    break;
                                }
                                i++;
                            }
                        }
                    }
                }
                //       break; // check if its right
            }
            // creating a new cpt

        }
        return new_cpt;
    }


    // if there is only one row left after the delete - remove the factor from the factors' list
    public void remove_factor(String[][] cpt) {

    }


    // join method - find all tables with the current hidden and join them from small to big
    // *if they have the same size - according to ASCII values of the vars
//    public void join() {
//        ArrayList<String[][]> factors = put_evidence();
//        for(String[][] factor:factors){
//
//        }
////       String [] join_factors = new String[0];
//        ArrayList<String[][]> join_factors = new ArrayList<>();
//       // Factor [] join_factors= new Factor[0];
//
//        for (String hidden_name : hidden_vars) {
//            for (String[][] factor : factors) {
//                String[] vars_factor = get_name_vars(factor);
//                for (int i = 0; i < vars_factor.length; i++) {
//                    if (hidden_name.equals(vars_factor[i])) {
//                        join_factors.add(factor);
//                    }
//                }
//            }
//            ArrayList<String[][]> sorted_factors=sort_factors(join_factors);
//
//
//        }
//    }

    public ArrayList<String[][]> sort_factors(ArrayList<String[][]> join_factors) {
        // for each factor in the list, build a factor based on the first var name
        // we will use it to iterate through the list, and compre the lengths of the tables.
        for (String[][] factor : join_factors) {

        }
        return null;
    }
//      //  String [][] arr=  join_factors.toArray(new String[0][0]);
//        Factor [] arr = join_factors.toArray();
//        String [] temp;
//        for(int i=0; i< arr.length;i++) {
//            //.toArray(new String[][])){
//            for (int j = 1; j < arr.length; j++) {
//                if (arr[i].length > arr[j].length ){
//
//                }
//            }
//        }
//            return null;
//      }

    // eliminate method - eliminates the current hidden

    // normalize


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
//        System.out.println(net);
        while (scanner.hasNextLine()) {
            // make a condition that it will only do BasicProb if in the end of the line there is 1
            String line = scanner.nextLine();
            VariableElimination ve = new VariableElimination(net, line);
            String result = String.valueOf(1.0);
            //  ArrayList<String[][]> cpts=ve.getCPTs();
            ArrayList<String[][]> factors = ve.getCPTs_factor(); // index out of bounds and nullpointerexception in factor
            ve.find_evidence();

            //System.out.println(factors);
            out.write(result);
        }
        if (scanner.hasNextLine()) {
            out.newLine();
        }

        scanner.close();
        out.close();
    }
}