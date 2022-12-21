import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
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
    private ArrayList<Factor> original_factors; // make a copy of the list in order to save the original variables after the deleting of evidence
    private int count_addition;
    private int count_mult;
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
        this.original_factors = get_factors();

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

    public void algorythm() {
        // check if the query is already given in the cpt - if so , return it immietiadtly

        ArrayList<String[][]> new_list = find_evidence(factors);
        to_join(new_list);
    }


    // put the evidence values - terminate tables that have only one value
    // returns the new list of factors - after removing the evidence values
    public ArrayList<String[][]> find_evidence(ArrayList<Factor> lst_factors) {
        String[][] new_cpt;
        ArrayList<String[][]> new_list = new ArrayList<>();

        // fix to : for(Factor factor: lst_factors)
        // for (Factor factor : this.factors) {
        for (Factor factor : lst_factors) {
            // Factor _factor = new Factor(factor);
            //original_factors.add(factor);
            new_cpt = null; // to each factor, start over
            String[] vars_factor = get_name_vars(factor.cpt);
            for (String evidence_name : evidence_vars) {
                for (int i = 0; i < vars_factor.length; i++) {
                    if (evidence_name.equals(vars_factor[i])) {
                        BayesianNode node_evidence = (BayesianNode) net.get_nodes().get(evidence_name);

                        String value = check_value_evidence(node_evidence);
                        if (new_cpt != null) {
                            //_factor.setCpt(new_cpt);
                            factor.setCpt(new_cpt);
//                            new_cpt=delete_evidence(factor,evidence_name, value);
                        }
                        new_cpt = delete_evidence(factor, evidence_name, value);
                        if (new_cpt != null) {
                            if (new_cpt.length == 2) { // 2 rows- including the variable names - first row
                                // remove_factor(new_cpt);
                                factors.remove(new_cpt);
                            }
                        }

                    }
                }

            }
            if (new_cpt != null) {
                new_list.add(new_cpt); // after we checked we deleted all of the evidences from the factor
                //_factor.setCpt(new_cpt);
                factor.setCpt(new_cpt); // in order to have an access to the updated cpt through factor class
            } else {
                new_list.add(factor.cpt);
            }

        }

        return new_list;
    }


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


    // step 2- join - find all tables with the current hidden and join them from small to big
    // *if they have the same size - according to ASCII values of the vars

    // this function sends all the cpts that needs to be joined to join_cpts()
    // NOTE - WE DELETE THE EVIDENCE FROM THE CPT'S ONLY AFTER WE SORTED
//    public void to_join() {
//        boolean found_factor;
//        ArrayList<Factor> lst_factors;
//        ArrayList<String> sorted_hiddens = sort_by_abc(hidden_vars);
//        for (String curr_hidden : sorted_hiddens) {
//            lst_factors = new ArrayList<>(); // for each hidden , make a new list of factors
//            for (Factor factor : original_factors) {
//                found_factor = false;// for each cpt, we need this flag to find the factor that matches it
//                String[] vars_factor = get_name_vars(factor.cpt);
//                for (int i = 0; i < vars_factor.length; i++) {
//                    if (vars_factor[i].equals(curr_hidden)) {
//                        lst_factors.add(factor); // we need this in order to use sort_factors(...)
////                            // optional - to save time, create a flag = true when we found the right factor,
////                                // and then break if flag= true cause we dont need to keep searching
//                        found_factor = true;
//                    } else {
//                        if (found_factor) {
//                            break;
//                        }
//                    }
//                }
//
//            }
//            sort_factors(lst_factors); // for each hidden , sort the cpts that contains it
//        }
//    }


    public void to_join(ArrayList<String[][]> new_list) {

        //arr_cpt= new_list.toArray
        boolean found_factor;
        ArrayList<String[][]> to_join = new ArrayList<>();
        // Factor [] arr_factors= new Factor[0];
        ArrayList<Factor> lst_factors = new ArrayList<>();
        ArrayList<String> sorted_hiddens = sort_by_abc(hidden_vars);
        for (String curr_hidden : sorted_hiddens) {
            lst_factors = new ArrayList<>(); // for each hidden , make a new list of factors
            for (String[][] cpt : new_list) {
                found_factor = false;// for each cpt, we need this flag to find the factor that matches it
                String[] vars_factor = get_name_vars(cpt);
                for (int i = 0; i < vars_factor.length; i++) {
                    if (vars_factor[i].equals(curr_hidden)) {
                        //    to_join.add(cpt); // if the cpt has hidden in it, add it to the list
                        for (Factor factor : factors) {
                            if (cpt.equals(factor.cpt)) {
                                lst_factors.add(factor); // we need this in order to use sort_factors(...)
                                // optional - to save time, create a flag = true when we found the right factor,
                                // and then break if flag= true cause we dont need to keep searching
                                found_factor = true;
                            } else {
                                if (found_factor) {
                                    break;
                                }
                            }

                            // sort_factors(to_join,factor);
                        }
                    }


                    // join_cpts(to_join);
                    // sort_factors(to_join,curr_hidden);
                }
            }
            sort_factors(lst_factors); // for each hidden , sort the cpts that contains it
            Factor[] arr_factors = lst_factors.toArray(new Factor[0]);
//            for(Factor factor: lst_factors){
            for (int i = 0; i < arr_factors.length - 1; i++) {
                union_cpts(arr_factors[i], arr_factors[i + 1]);
            }

        }

    }


    // given an array list of strings , the function returns a sorted list in an alphabetic order
    // used before iterating on the hiddens in the join algorythm
    private ArrayList<String> sort_by_abc(ArrayList<String> hidden_vars) {
        Collections.sort(hidden_vars);
        return hidden_vars;

    }


    // for each hidden variable, sort the cpts that contains it from small to large (according to number of rows)
    public ArrayList<String[][]> sort_factors(ArrayList<Factor> lst_factors) {
        // ArrayList<String[][]>to_join
        //String curr_hidden
        ArrayList<String[][]> new_to_join = new ArrayList<>();
        Factor[] arr_factors = lst_factors.toArray(new Factor[0]);
        // bubble sort
        for (int i = 0; i < arr_factors.length - 1; i++) {
            // sorting only the factors with the curr_hidden
            // if(Arrays.asList(get_name_vars(arr_factors[i].cpt)).contains(curr_hidden)) {
            //   if(to_join.contains(arr_factors[i])) { // unneccessary, since factors contains the updated cpts
            for (int j = 0; j < (arr_factors.length) - 1 - i; j++) {
                if (arr_factors[j].cpt.length > arr_factors[j + 1].cpt.length) {
                    String[][] swap = arr_factors[j].cpt;
                    arr_factors[j].cpt = arr_factors[j + 1].cpt;
                    arr_factors[j + 1].cpt = swap;

                } else if (arr_factors[j].cpt.length == arr_factors[j + 1].cpt.length) {
                    if (sort_by_ASCII(arr_factors[j].cpt, arr_factors[j + 1].cpt)) { //if the function returns true - we need to switch between the cpt's.
                        String[][] swap = arr_factors[j].cpt;
                        arr_factors[j].cpt = arr_factors[j + 1].cpt;
                        arr_factors[j + 1].cpt = swap;
                    }
                }
            }
        }

        for (int j = 0; j < arr_factors.length; j++) {
            new_to_join.add(arr_factors[j].cpt);
        }
        return new_to_join;
    }

    // return true , if the sum of variables of the first cpt is bigger than the sum of variables of the second cpt.
    // indicates that we need to switch places in the factors list in sort_factors(..).
    private boolean sort_by_ASCII(String[][] cpt1, String[][] cpt2) {
        String[] vars_cpt1 = get_name_vars(cpt1);
        String[] vars_cpt2 = get_name_vars(cpt2);

        int sum_ASCII_1 = 0;
        int sum_ASCII_2 = 0;

        for (int i = 0; i < vars_cpt1.length; i++) {
            for (int j = 0; j < vars_cpt1[i].length(); j++) {
                sum_ASCII_1 += vars_cpt1[i].charAt(j);
            }
        }

        for (int i = 0; i < vars_cpt2.length; i++) {
            for (int j = 0; j < vars_cpt2[i].length(); j++) {
                sum_ASCII_2 += vars_cpt2[i].charAt(j);
            }
        }

        if (sum_ASCII_1 > sum_ASCII_2) {
            return true;
        }
        return false;

        //Arrays.sort(arr_string);
    }

    // this function unites two cpts and multiflies their probabilities
    // it returns the joined cpt
    public String[][] union_cpts(Factor factor1, Factor factor2) {
        // cpt1 - the smaller cpt / right cpt of the join
        String[][] cpt1= factor1.cpt;
        // cpt2 - the larger cpt/ left cpt of the join
        String[][] cpt2= factor2.cpt;

        String[] vars_factor1 = get_name_vars(cpt1);
        String[] vars_factor2 = get_name_vars(cpt2);
        boolean evidence_flag = false;
//        ArrayList<Variable> vars1= new ArrayList<>();
//        ArrayList<Variable> vars2= new ArrayList<>();

//        factor1.setVariables();

        // list of variables that are in both of the cpts
        ArrayList<String> both_cpt = new ArrayList<>();
        int num_rows = 1; // represent num of rows of the union cpt
        int num_col = 1; // starts with 1 for the probability column

        for (int i = 0; i < vars_factor1.length; i++) {
            for (int j = 0; j < vars_factor2.length; j++) {
                if (vars_factor2[j].equals(vars_factor1[i])) {
                    both_cpt.add(vars_factor1[i]);
                }
            }
        }


        // initialize num or rows and columns in union cpt
        for (Variable var : factor1.variables) {
            evidence_flag = false;
            for (String evidence : evidence_vars) {
                if (var.getName().equals(evidence)) {
                    evidence_flag = true;
                    break;
                }
            }
            if (!evidence_flag) {
                num_rows *= var.getValues().size();
                num_col++;
            }

        }
        for (Variable var : factor2.variables) {
            evidence_flag = false;
            if (!both_cpt.contains(var.getName())) { // if the variable is not in both cpt's - to avoid redundant rows
                for (String evidence : evidence_vars) {
                    if (var.getName().equals(evidence)) {
                        evidence_flag = true;
                        break;
                    }
                }
                if (!evidence_flag) {
                    num_rows *= var.getValues().size();
                    num_col++;
                }
            }
        }
        num_rows++; // add 1 for the variable names row - first row


        ///////////////////////////////////////////////////////////
        // union of cpts:
        //a. initialize data structures or variables
        String[][] union_cpt = new String[num_rows][num_col]; // the new cpt, after joining the cpts
        String[] union_row = new String[num_col]; // for each row in cpt1,
        // except from the last one, its  the values of each var
        Double[] union_prob = new Double[num_rows]; // the new probs of the joined cpt
        int prob = 0; // temp prob
        boolean not_same_value; // if true - it means that we didn't find the same values that are in cpt1, in cpt2.

        //b. define parameter to check in which row the values should be inserted to the union_cpt, in case we have
        // multiple rows with the same values for the variables that are in both of the cpt's
        int ratio= (union_cpt.length-1) / (cpt1.length-1); // the ratio between the new cpt to the left cpt

        // c. iterating through the cpts
        for (int row_1 = 1; row_1 < cpt1.length; row_1++) {
            not_same_value=false;
            union_prob=new Double[num_rows];
            int count_row_ratio=0;

            //  for(int col_1=0; col_1<cpt1[row_1].length;col_1++){
            // iterating through the second/right cpt
            for (int row_2 = 1; row_2 < cpt2.length; row_2++) {
              // reinitialize:
             //   count_row_ratio=0;
                union_prob=new Double[num_rows];
                not_same_value=false;

                // storing the values for each row in cpt1
                for (int col = 0; col < cpt1[row_1].length; col++) {
                    if(col==cpt1[row_1].length-1) { // edge case - if we are in the prob column
                        union_prob[row_1] = Double.valueOf(cpt1[row_1][col]);
                    }
                    else{
                        union_row[col] = cpt1[row_1][col];
                    }
                }



                for (int col_2 = 0; col_2 < cpt2[row_2].length-1; col_2++) {
                    // note - we don't iterate through the prob column
                    String name_var_cpt2=cpt2[0][col_2]; // name of the column's variable

                    if(both_cpt.contains(name_var_cpt2)) {
                        // checking if the values of the cpt's are the same
                       String val_cpt1= get_value_row(cpt1, row_1,name_var_cpt2);
                       if(!val_cpt1.equals(cpt2[row_2][col_2])){
                           not_same_value=true;
                           break;
                       }
                       else{
                           union_row[col_2] = cpt2[row_2][col_2];
                       }
                    }
                    else { // if the variable is only in the second/right cpt,
                        //we need to save its value, because the row might be correct and should be in the union
                        union_row[col_2] = cpt2[row_2][col_2];
                    }


                }

                // after we checked all the columns in the row , except probs
                if(!not_same_value){
                    String last_col= cpt2[row_2][cpt2[row_2].length-1]; // the probability in row of cpt2
                    union_prob[row_1]*=Double.valueOf(last_col);
                    //  union_cpt[row_1+(cpt1.length-1)*count_row_ratio]=
                    int index_row=0;
                    if(count_row_ratio<ratio) {
                        index_row = row_1 + ((cpt1.length - 1) * count_row_ratio);
                        count_row_ratio++;
                    }
                    for(int j=0;j<num_col;j++){

                        //edge case
                        if(j==num_col-1){
                            union_cpt[index_row][j]=Double.toString(union_prob[row_1]);
                        }
                        else{
                            union_cpt[index_row][j]=union_row[j];
                        }
                    }
                }


            }
            //}
        }

        return union_cpt;

    }


    // returns the value of a given variable in a given row in the cpt
    // input: the left cpt, index of left cpt and name of the current column in right cpt (cpt2)
    public String get_value_row(String [][] cpt1,int row, String var_name){
        String value=null;
        for(int j=0;j< cpt1[row].length-1;j++){
            if(cpt1[0][j].equals(var_name)){
                value= cpt1[row][j];
            }
        }
        return value;
    }


    // and then do the same as downwards, but now we only sort the cpts that
    // have the curr_hidden in them as we should

//        for(String[][]cpt : to_join){
//            arr_factors
//        }

    // for each factor in the list, build a factor based on the first var name
    // we will use it to iterate through the list, and compare the lengths of the tables.
    // based on bubble sort algoeythm
//        String[][]arr_cpts= to_join.toArray(new String[0][0]);
    // ArrayList<String[][]> to_join= new ArrayList<>();
//        Factor [] arr_factors= factors.toArray(new Factor[0]);
//        for(int i=0; i<arr_factors.length-1;i++){
//            // sorting only the factors with the curr_hidden
//            if(Arrays.asList(get_name_vars(arr_factors[i].cpt)).contains(curr_hidden)){
//         //   if(to_join.contains(arr_factors[i])) { // unneccessary, since factors contains the updated cpts
//                for (int j = 0; j < (arr_factors.length) - 1 - i; j++) {
//                    if (arr_factors[j].cpt.length > arr_factors[j + 1].cpt.length) {
//                        String[][] swap = arr_factors[j].cpt;
//                        arr_factors[j].cpt = arr_factors[j + 1].cpt;
//                        arr_factors[j + 1].cpt = swap;
//
//                    }
//                }
//           }
//        }

    //  ArrayList<String[][]> new_to_join=new ArrayList<>();
    // turn back to ArrayList<String[][]>


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
    // before elimination, check the order of the variables - ABC
    // if the factors are in the same size- ascii

    // normalize


    static File file = new File("C:\\Users\\User\\IdeaProjects\\AI_try\\input.txt");
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
            // ve.find_evidence();

            //sort by ascii
//            ArrayList<String> strings= new ArrayList<>();
//            strings.add("A3");
//            strings.add("A2");
//            strings.add("A1");
//            String [] arr_string=strings.toArray(new String[0]);


            // Sort the string S
//            Arrays.sort(arr_string);
//            String ans=Arrays.toString(arr_string);

            //  String ans=String.valueOf(arr_string);
//            System.out.println(ans);


            ve.algorythm();

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