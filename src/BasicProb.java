import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class BasicProb {
    //Instance Variables
    BayesianNetwork net;
    String query_var;
    String queryValue;
    String[] evidence_vars;
    String[] evidence_values;
    ArrayList<String> hidden_vars;
    ArrayList<Factor> factors;
    int addition_counter=1;
    int num_of_mult=0;

    // constructor
    public BasicProb(BayesianNetwork net, String line){
        this.net= net;
        this.query_var=getQuery(line);
        this.queryValue=getQueryValue(line);
        this.evidence_vars=getEvidence(line);
        this.evidence_values=getEvidenceValues(line);
        this.hidden_vars=getHidden(line);
        this.factors=getFactors();

    }

    // get the query variable from the text input file
    public String getQuery(String line){
        String query=line.substring(2); // take only "QUERY="
        query=query.split("=")[0];
        return query;

    }

    // get the value of the query variable from the text
    public String getQueryValue(String line)
    {
        String value = line.split("=")[1].split("\\|")[0];
        return value;
    }

    // return the current factors list
    public ArrayList<Factor> getFactors(){
        return factors;
    }

    // get the evidence variables from the text
    public String[] getEvidence(String line){

        String[] evidences = line.split("\\|")[1].split("\\)")[0].split(","); // make a list that contains each evidence and its value
        String[] evidence_vars= new String[evidences.length];
        for(int i=0; i< evidences.length; i++){
            evidence_vars[i]= evidences[i].split("=")[0];
        }
        return evidence_vars;

    }

    // get the values of the evidence variables from the text
    public String[] getEvidenceValues(String line){

        String[] evidences = line.split("\\|")[1].split("\\)")[0].split(","); // make a list that contains each evidence and its value
        String[] evidence_values= new String[evidences.length];
        for(int i=0; i< evidences.length; i++){
            evidence_values[i]= evidences[i].split("=")[1];
        }
        return evidence_values;

    }

    // get the hidden variables from the XML file

    public ArrayList<String> getHidden(String line){
        ArrayList<String> hidden_vars= new ArrayList<>();
        ArrayList<String> varNames=net.getVariablesNames();
        for (String var: varNames){
            for(String evidence :getEvidence(line)) {
                if (var.equals(evidence)) {
                    continue; // if the variable is evidence don't add it to the list
                }
                else if (var.equals(getQuery(line))) {
                    continue;// if the variable is query don't add it to the list
                }
                else{
                    hidden_vars.add(var);
            }
            }
        }

        return hidden_vars;
    }

    // read the hidden variables values from the xml
    public ArrayList<String> getHiddenValues(Scanner scanner,String line){
          //  ArrayList<String> hidden_list=getHidden(line);
        ArrayList<String> hidden_values=null;
      //  int addition_counter=1;
        int permenant_counter=0;
            for(String hidden: hidden_vars) {
                line = scanner.nextLine();
                hidden_values=new ArrayList<>(); // each hidden var has hidden_values of its own.
                if (Objects.equals(hidden, net.getData(line))) {
                  //  String hidden_name = net.getData(line);
                    //   ArrayList<String> hidden_values = new ArrayList<>();

                    while (line.matches("(.*)<OUTCOME>(.*)</OUTCOME>(.*)")) {
                        permenant_counter=0;
                        String value = net.getData(line);
                        hidden_values.add(value);
                        line = scanner.nextLine();
                        permenant_counter++;
                    }

                    this.addition_counter*=permenant_counter;

                }
                else{
                    continue;
                }

            }
            this.addition_counter--; // because we are counting the number of additions and not the number of probabilities.
            return hidden_values;
    }



    public Double probability(String fileName, Scanner scanner ) throws FileNotFoundException {
        net=net.readXML(fileName);
//        for(String hidden:hidden_vars ){
//
//        }

        for(int i=1; i<=addition_counter+1; i++){
            //getQueryValue()
        }
//        net.createNodes(net, scanner);
//        net.prob();
        //num_of_sum=hidden_vars.size()*hidden_vars.size(); // number of hidden vars multiply in the number of each

        public String[] prob(){
            String[] probs_str = line.split(">")[1].split("<")[0].split(" ");
            for (String str : probs_str) {
                probs.add(Double.parseDouble(str));
            }

        }

}
