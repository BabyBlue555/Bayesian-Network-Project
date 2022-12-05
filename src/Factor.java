import java.text.DecimalFormat;
import java.util.ArrayList;

public class Factor {
    // variables, var_values , probabilities

    ArrayList<Variable> variables;
    ArrayList<String> var_values;
    ArrayList<Double> probabilities;

    public Factor(ArrayList<Variable> variables,ArrayList<Double> probabilities ){
        setVariables(variables);
        setProbabilities(probabilities);
        setVarValues();
    }


    public void setVariables(ArrayList<Variable> variables){
        this.variables=variables;
    }

    public void setProbabilities(ArrayList<Double> probabilities){
        this.probabilities=probabilities;
    }

    public void setVarValues(){ // ????????/
        this.var_values=var_values;
    };

//    public void setVar_values() {
        public String[][] make_CPT(BayesianNode node) {

            int num_parents = node.getParents().size();
            int num_col = num_parents + 2; // 2 = one query and probability column
            int num_rows = node.getVar().getValues().size();
            if (num_parents > 0) {
                for (BayesianNode parent : node.getParents()) {
                    num_rows *= parent.getVar().getValues().size();
                }
            }
            num_rows++; // because we want to have a row for the variable names
            String[][] CPT_query = new String[num_rows][num_col];

            // putting values in the matrix
            //a. putting names of vars in the first row
            BayesianNode [] arr = (BayesianNode[]) node.getParents().toArray();
            for (int j = num_col-1; j >=0; j--) {
                if (j == num_col - 1) {
                    continue;
                } else if (j == num_col - 2) {
                    CPT_query[0][j] = node.getVar().getName(); // the node column is the first column after the probabilities
                } else {
                    CPT_query[0][j] = arr[j].getVar().getName();

//                    while (j >= 0) {
//                        for (BayesianNode parent : node.getParents()) {
//                            CPT_query[0][j] = parent.getVar().getName();
//                        }
//                        j--;
//                    }
                }
            }

            int value_index = 0;
            String[] values = node.getVar().getValues().toArray(new String[0]);
            for (int j = num_col - 1; j >= num_col - 2; j--) {
                for (int i = 1; i < num_rows; i++) {
                    if (j == num_col - 1) { // column of probabilities
                        for (Double prob : node.getFactor().getProbabilities()) {
                            CPT_query[i][j] = String.valueOf(prob); // convert double to string
                        }
                    } else if (j == num_col - 2) { // column of query
                        CPT_query[i][j] = values[value_index];
                        value_index = (value_index + 1) % values.length; // in order to have a " TFTFTF..." sequence
                    }
//                else{
//                    if(num_parents>0){
//                        while(j>num_parents){
//                            String first_str=    CPT_query[1][num_col-2]; // the first value of query in the cpt
//                            for(BayesianNode parent: node.getParents()){
//                                value_index = 0; // for each parent node, start over
//                                values=parent.getVar().getValues().toArray(new String[0]);
//                                if(CPT_query[i][num_col-2].equals(first_str)){
//                                    CPT_query[i][j]=values[value_index];
//                                    value_index = (value_index+1) % values.length;
//                                }
//                            }
//                        }
//                    }
//                }
                }
            }
            int parent_index = num_parents;
            String first_str = CPT_query[1][num_col - 2];
            for (int j = num_col - 3; j >= 0; j--) {
                for (BayesianNode parent : node.getParents()) {
                    value_index = 0; // for each parent node, start over
                    values = parent.getVar().getValues().toArray(new String[0]);
                    for (int i = 1; i < num_rows; i++) {
                        if (CPT_query[i][num_col - 2].equals(first_str)) { // check the status in query column
                            // if we came back to v1 - change value of parent
                            // note - value index doesn't change in the "else" section
                            CPT_query[i][j] = values[value_index];
                            value_index = (value_index + 1) % values.length;
                        } else { // that means we need to keep putting the same value of parent
                            // since the the query gives us different values - i.e. , v1, v2, v3...
                            int previous_value_index = (value_index - 1) % values.length;
                            if (previous_value_index < 0) {
                                previous_value_index += values.length;
                            }
                            CPT_query[i][j] = values[previous_value_index];

                        }
                    }


                }
            }
            return CPT_query;


        } // closing of make_cpt function




    public ArrayList<Variable> getVariables(){
        return variables;
    }

    public ArrayList<Double> getProbabilities(){
        return probabilities;
    }

    public ArrayList<String> getVar_values() {
        return var_values;
    }

    // used only for output !
    public void roundProbability(ArrayList<Double> probabilities){
        DecimalFormat df = new DecimalFormat("###.######");
        for (Double prob: probabilities){
            String _prob=df.format(prob);
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



}
