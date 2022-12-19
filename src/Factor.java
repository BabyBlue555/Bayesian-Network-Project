import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Factor {
    // variables, var_values , probabilities

    String var_name;
    ArrayList<Variable> variables;
    ArrayList<String> var_values;
    ArrayList<Double> probabilities;
    String [][] cpt;

    public Factor() {
        this.var_name= new String(); ///// delete or fix
        this.variables = new ArrayList<>();
        this.var_values = new ArrayList<String>();
        this.probabilities = new ArrayList<>();
        this.cpt= new String[0][0];
    }

    public Factor(String var_name,ArrayList<Variable> variables, ArrayList<Double> probabilities , String [][] cpt) {
        this.variables = variables;
        this.var_values = new ArrayList<>();
        this.probabilities = probabilities;
        setVarValues(this.var_values);
        //this.cpt=cpt;
        setCpt(cpt);
//        setCpt(new Variable(var_name,new ArrayList<>()));
//        for(Variable v :variables) {
//            setVarValues(v);
//        }
//        for(Variable v :variables) {
//            make_CPT(new BayesianNode(v));
//        }
    }


    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }

    public void setProbabilities(ArrayList<Double> probabilities) {
        this.probabilities = probabilities;
    }

    public void setVarValues(ArrayList<String> var_values) { // ????????/
        for(Variable var: this.variables ){
            for(String value: var.getValues())
            var_values.add(value);
        }
    }

    public void setCpt(String[][] cpt){
        this.cpt=cpt;

    }
//    public void setCpt(Variable var){
//        BayesianNode node = new BayesianNode(var);
//        cpt=make_cpt(node);
//    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public ArrayList<Double> getProbabilities() {
        return probabilities;
    }

    public ArrayList<String> getVar_values() {
        return var_values;
    }

    public String[][] make_cpt(BayesianNode node) {

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


    // used only for output !
    public void roundProbability(ArrayList<Double> probabilities) {
        DecimalFormat df = new DecimalFormat("###.######");
        for (Double prob : probabilities) {
            String _prob = df.format(prob);
        }
    }
    //similar function:
    // // this method get a number and return it rounded after specific places after the point.
    //    private static double round(double value, int places) {
    //        if (places < 0) throw new IllegalArgumentException();
    //
    //        BigDecimal bd = new BigDecimal(Double.toString(value));
    //        bd = bd.setScale(places, RoundingMode.HALF_UP);
    //        return bd.doubleValue();
    //    }


    @Override
    public String toString() {
        String str = "Factor:\n" +
                "_variables=" + variables.toString() + "\n";
        for (int i = 0; i < probabilities.size(); i++)
            str += var_values.get(i) + " | " + probabilities.get(i) + "\n";

        return str;
    }



    // checking null exceptions
    public static void main(String[] args) throws IOException {
        try {
            Factor f = new Factor();
            System.out.println(f.var_values);
            String value= "t";
            f.var_values.add(value);
            System.out.println(f.var_values);
            System.out.println(f);
            System.out.println(f.getVar_values());
            ArrayList<String> new_vals= new ArrayList<>();
            f.setVarValues(new_vals);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("null error ");
        }


    }


}