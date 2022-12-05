import java.io.FileNotFoundException;
import java.util.*;

public class BasicProb {
    //Instance Variables
    BayesianNetwork net;
    BayesianNode queryNode;
    String query_var;
    String queryValue;
    String[] evidence_vars;
    String[] evidence_values;
    ArrayList<String> hidden_vars;
    ArrayList<Factor> factors;
    int addition_counter = 1;
    int num_of_mult = 0;


    // constructor
    public BasicProb(BayesianNetwork net, String line) {
        this.net = net;
        this.query_var = getQuery(line);
        this.queryValue = getQueryValue(line);
        this.evidence_vars = getEvidence(line);
        this.evidence_values = getEvidenceValues(line);
        this.hidden_vars = getHidden(line);
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

    public ArrayList<String> getHidden(String line) {
        ArrayList<String> hidden_vars = new ArrayList<>();
        ArrayList<String> varNames = net.getVariablesNames();
        for (String var : varNames) {
            for (String evidence : getEvidence(line)) {
                if (var.equals(evidence)) {
                    continue; // if the variable is evidence don't add it to the list
                } else if (var.equals(getQuery(line))) {
                    continue;// if the variable is query don't add it to the list
                } else {
                    hidden_vars.add(var);
                }
            }
        }

        return hidden_vars;
    }

    // read the hidden variables values from the xml
    public ArrayList<String> getHiddenValues(Scanner scanner, String line) {
        //  ArrayList<String> hidden_list=getHidden(line);
        ArrayList<String> hidden_values = null;
        //  int addition_counter=1;
        int permenant_counter = 0;
        for (String hidden : hidden_vars) {
            line = scanner.nextLine();
            hidden_values = new ArrayList<>(); // each hidden var has hidden_values of its own.
            if (Objects.equals(hidden, net.getData(line))) {
                //  String hidden_name = net.getData(line);
                //   ArrayList<String> hidden_values = new ArrayList<>();

                while (line.matches("(.*)<OUTCOME>(.*)</OUTCOME>(.*)")) {
                    permenant_counter = 0;
                    String value = net.getData(line); //check if the outcome of this is what i need
                    hidden_values.add(value);
                    line = scanner.nextLine();
                    permenant_counter++;
                }

                this.addition_counter *= permenant_counter;

            } else {
                continue;
            }

        }
        this.addition_counter--; // because we are counting the number of additions and not the number of probabilities.
        return hidden_values;
    }

    public String [][] make_CPT(String line) {

        int num_parents = queryNode.getParents().size();
        int num_col = num_parents + 2; // 2 = one query and probability column
        int num_rows = queryNode.getVar().getValues().size();
        if(num_parents>0) {
            for (BayesianNode parent : queryNode.getParents()) {
                num_rows *= parent.getVar().getValues().size();
            }
        }
        num_rows ++; // because we want to have a row for the variable names
        String  [][] CPT_query=new String[num_rows][num_col];

        // putting values in the matrix
        //a. putting names of vars in the first row
        for(int j=0; j< num_col;j++){
            if(j==num_col-1) {
                continue;
            }
            else if (j== num_col-2){
                CPT_query[0][j] = queryNode.getVar().getName();
            }
            else{
                while (j<num_col-2) {
                    for(BayesianNode parent: queryNode.getParents()){
                        CPT_query[0][j]= parent.getVar().getName();
                    }
                    j++;
                }
            }
        }

        int value_index = 0;
        String[] values =queryNode.getVar().getValues().toArray(new String[0]);
        for(int j=num_col-1; j>=num_col-2; j--){
            for(int i=1; i<num_rows;i++){
                if(j==num_col-1){ // column of probabilities
                    for(Double prob: queryNode.getFactor().getProbabilities()) {
                        CPT_query[i][j] = String.valueOf(prob); // convert double to string
                    }
                }
                else if(j==num_col-2){ // column of query
                    CPT_query[i][j]= values[value_index];
                    value_index = (value_index+1) % values.length; // in order to have a " TFTFTF..." sequence
                }
//                else{
//                    if(num_parents>0){
//                        while(j>num_parents){
//                            String first_str=    CPT_query[1][num_col-2]; // the first value of query in the cpt
//                            for(BayesianNode parent: queryNode.getParents()){
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
        int parent_index=num_parents;
        String first_str= CPT_query[1][num_col-2];
        for(int j=num_col-3;j>=0;j--){
            for(BayesianNode parent: queryNode.getParents()){
              value_index = 0; // for each parent node, start over
              values=parent.getVar().getValues().toArray(new String[0]);
              for(int i=1; i<num_rows; i++){
                  if(CPT_query[i][num_col-2].equals(first_str)){ // check the status in query column
                    // if we came back to v1 - change value of parent
                      // note - value index doesn't change in the "else" section
                    CPT_query[i][j]=values[value_index];
                    value_index = (value_index+1) % values.length;
              }
                  else{ // that means we need to keep putting the same value of parent
                      // since the the query gives us different values - i.e. , v1, v2, v3...
                      int previous_value_index = (value_index-1)%values.length;
                      if (previous_value_index<0) {
                          previous_value_index+=values.length; }
                      CPT_query[i][j]= values[previous_value_index];

                  }
        }

    }


//    public ArrayList<Double> getProb(Scanner scanner , String line){
//        net.createNodes(net,scanner);
//        ArrayList<Double> probs= queryNode.getFactor().getProbabilities();
//
//
//    }

}







//    public Double probability(String fileName, Scanner scanner ) throws FileNotFoundException {
//        net=net.readXML(fileName);
////        for(String hidden:hidden_vars ){
////
////        }
//
//        for(int i=1; i<=addition_counter+1; i++){
//            //getQueryValue()
//        }
////        net.createNodes(net, scanner);
////        net.prob();
//        //num_of_sum=hidden_vars.size()*hidden_vars.size(); // number of hidden vars multiply in the number of each
//
//
//
//}

//
//public Double calc_query(String line){
//    String query=getQuery( line);
//  //  Double probability;
//    Hashtable NODES=net.get_nodes();
//
//    Set<String> setOfKeys = NODES.keySet();
//
//    for (String key : setOfKeys){
//        if(key.equals(query)) {
//            BayesianNode node = (BayesianNode) NODES.get(key);
////            if(node.getParents()!=null){
////
////            }
//        }
//
//
//
//            for(Factor factor : factors){
//                boolean flag=true;
//                BayesianNode node = (BayesianNode) NODES.get(key);
//                ArrayList<Variable> vars=factor.getVariables();
//
//                for(Variable var:vars) {
//                    while(flag){
//                    for(BayesianNode parent:  node.getParents())
//                        if (!query.equals(var.getName()) ||! parent.equals(var.getName())){
//                            flag=false;
//                            break;
//                        }
//
//                }
//                    break;
//            }
//                double calc=0;
//                for(String index:factor.getVar_values()){
//                    for(Double probability: factor.getProbabilities()){
//                    if(getQueryValue(line).equals(index)){
//                        calc= probability;
//
//                        }
//
//                    }
//                }
//                }
//
//
//
//
//    }
//    return 1.;
//}
//
//public void check_node(BayesianNode node){
//        ArrayList<Variable> vars= node.getFactor().getVariables();
//        if(node.getParents()!=null){
//            node.var.
//            node.getFactor().probabilities
////            ArrayList<BayesianNode> parents= node.getParents();
////            for (BayesianNode parent: parents){
////                String name=parent.getVar().getName();
////              //  if(name in vars)
////
////            }
//        }
//
//    }
//
//
//
//}
