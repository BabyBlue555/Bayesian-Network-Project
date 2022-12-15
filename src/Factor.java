import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Factor {
    // variables, var_values , probabilities

    ArrayList<Variable> variables;
    ArrayList<String> var_values;
    ArrayList<Double> probabilities;

    public Factor() {
        this.variables = new ArrayList<>();
        this.var_values = new ArrayList<String>();
        this.probabilities = new ArrayList<>();

    }

    public Factor(ArrayList<Variable> variables, ArrayList<Double> probabilities,ArrayList<String> var_values) {
        this.variables = variables;
        this.var_values = new ArrayList<>();
        this.probabilities = probabilities;
        setVarValues(var_values);
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
        this.var_values = var_values;
//        for (Variable _var : variables) {
//            for (String value : _var.getValues()) {
//                this.var_values.add(value);
//            }
//             BayesianNode node=new BayesianNode(_var);
//             make_CPT(node);
//            setVarValues(variables);
    }


    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public ArrayList<Double> getProbabilities() {
        return probabilities;
    }

    public ArrayList<String> getVar_values() {
        return var_values;
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