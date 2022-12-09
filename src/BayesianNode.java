import java.util.ArrayList;

public class BayesianNode {
    private Variable var;
    private Factor factor;

    private ArrayList<BayesianNode> parents;
    private ArrayList<BayesianNode> children;

    public BayesianNode(Variable var) {
       // setVar(var);
        this.var = var;
        this.parents = new ArrayList<>(); // because it is a complexed type
        this.children = new ArrayList<>();
        this.factor=new Factor();
        this.factor.var_values=var.getValues();




    }

    public void setVar(Variable var) {
        this.var = var;
    }

    public void setFactor(ArrayList<Variable> vars, ArrayList<Double> probs) {
        this.factor= new Factor(vars,probs);
        //this.factor= new Factor(vars,probabilities);
        this.factor.var_values=var.getValues();

    }
    public void setFactor(Factor factor) {
        this.factor = factor;
    }

    public void addParents(BayesianNode parent) {
        this.parents.add(parent);
    }

    public void addChildren(BayesianNode child) {
        this.children.add(child);
    }

    public Variable getVar() {
        return var;
    }

    public Factor getFactor() {
        return factor;
    }

    public ArrayList<BayesianNode> getParents() {
        return parents;
    }

    public ArrayList<BayesianNode> getChildren() {
        return children;
    }
    //variable - name and values
    // factor





    // parents - optional
    // kids - optional
    @Override
    public String toString() {
        String _parents = "";
        String _children = "";
        String _factor = "";

        for (BayesianNode parent : this.parents) {
            _parents += parent.getVar().getName() + ",";

        }

        for (BayesianNode child : this.children) {
            _children += child.getVar().getName() + ",";
        }

        for (Variable v : this.factor.getVariables()) {
            _factor += v.getName() + ",";
        }
        _factor += ": " + this.factor.getProbabilities().toString();


        return "BayesianNode{" +
                "variable:" + var.toString()
                + ",factor:" + _factor
                + ",parents:" + _parents
                + ",children:" + _children
                + '}' + "\n\n";


    }

}




