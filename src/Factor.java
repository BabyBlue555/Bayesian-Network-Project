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

    public void setVar_values(){

//        int num_parents=
//            int num_parents = queryNode.getParents().size();
//            int num_col = num_parents + 2; // 2 = one query and probability column
//            int num_rows = queryNode.getVar().getValues().size();
//            for (BayesianNode parent : queryNode.getParents()) {
//                num_rows *= parent.getVar().getValues().size();
//            }
//            num_rows ++; // because we want to have a row for the variable names
//            String  [][] CPT_query=new String[num_rows][num_col];
//
//            // putting values in the matrix
//            //a. putting names of vars in the first row
//            for(int j=0; j< num_col;j++){
//                if(j==num_col-1) {
//                    continue;
//                }
//                else if (j== num_col-2){
//                    CPT_query[0][j] = queryNode.getVar().getName();
//                }
//                else{
//                    while (j<num_col-2) {
//                        for(BayesianNode parent: queryNode.getParents()){
//                            CPT_query[0][j]= parent.getVar().getName();
//                        }
//                        j++;
//                    }
//                }
//            }

            int value_index = 0;
            String[] values ;
            for(int j=0; j<num_col; j++){
                for(int i=1; i<num_rows;i++){
                    if(j==num_col-1){ // column of probabilities
                        for(Double prob: queryNode.getFactor().getProbabilities()) {
                            CPT_query[i][j] = String.valueOf(prob); // convert double to string
                        }
                    }
                    else if(j==num_col-2){ // column of query
                        CPT_query[i][j]= queryNode.getVar().
                    }
                }
            }


    }



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



}
