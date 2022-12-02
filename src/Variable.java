import java.util.ArrayList;

public class Variable {
    private String var_name;
    private ArrayList<String> var_values;

    public Variable(String name, ArrayList<String> values) {
        setName(name);
        setValues(values);

    }


    public void setValues(ArrayList<String> var_values){
        this.var_values= var_values;
    }

    public void setName(String var_name){
        this.var_name=var_name;
    }

    public String getName(){
        return var_name;
    }

    public ArrayList<String> getValues(){
        return var_values;
    }


    // do a copy function?

    @Override
    public String toString() {
        return "Variable{" +
                "var_name='" + var_name + '\'' +
                ", var_values=" + var_values +
                '}';
    }
}
