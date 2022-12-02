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



    public ArrayList<Variable> getVariables(){
        return variables;
    }

    public ArrayList<Double> getProbabilities(){
        return probabilities;
    }

    // used only for output !
    public void roundProbability(ArrayList<Double> probabilities){
        DecimalFormat df = new DecimalFormat("###.######");
        for (Double prob: probabilities){
            String _prob=df.format(prob);
        }
    }



}
