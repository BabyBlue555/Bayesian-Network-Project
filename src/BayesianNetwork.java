import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors


/*
    this class's main goal is to read from the xml file all the data
     about the variables and their cpt's and to add them to the bayesian network
 */

public class BayesianNetwork {
    private Hashtable _nodes; // explain why i chose hashtable

    public BayesianNetwork(){ // constructor
        this._nodes = new Hashtable();
    }

    public Hashtable get_nodes() {
        return _nodes;
    }

    public void addNode(String name, BayesianNode node){
        this._nodes.put(name, node);
    }

    // helping function for createVariables and createNodes
    // returns the value between tags in the xml file
    public static String getData(String line){
        String value = line.split(">")[1].split("<")[0]; // get the data between >data< from the xml
        return value;
    }

    // helping function for createVariables
    public ArrayList<String> getVariablesNames(){
        ArrayList<String> names = new ArrayList<String>(this.get_nodes().keySet());
        return names;
    }


    // reads the xml file and call to function in order to create the variables and nodes of the network
    // input: string name of the xml file in the input txt, output: bayesian network
    public BayesianNetwork readXML(String fileName) throws FileNotFoundException {
        BayesianNetwork net= new BayesianNetwork();
        try {

            File xmlFile = new File(fileName);
            Scanner scan = new Scanner(xmlFile);
            while(scan.hasNextLine()){
                String line= scan.nextLine();
                if(line.matches("(.*<VAR.*)")){
                    createVariables(net,scan);
                }
                else{
                    if(line.matches("(.*<DEF.*)")){
                        createNodes(net,scan);
                    }
                }
            }
            scan.close();
        }
        catch(FileNotFoundException ex){
            System.out.println("exception has found:");
            ex.printStackTrace();
        }
        return net;
    }

    // from each part of Variable in the xml file,
    // this method create bayesian node and append it to the Bayesian Network
    private static void createVariables(BayesianNetwork net, Scanner scanner){
        String line= scanner.nextLine();
        String var_name=getData(line);
        ArrayList<String> var_values=new ArrayList<>();
        line=scanner.nextLine(); // move to next line in order to find the var values.
        while(line.matches("(.*)<OUTCOME>(.*)</OUTCOME>(.*)")){
            String value = getData(line);
            var_values.add(value);
            line=scanner.nextLine();
        }
        Variable var= new Variable(var_name,var_values);
        BayesianNode node =new BayesianNode(var);
        net.addNode(var_name,node);
    }

    public void createNodes(BayesianNetwork net, Scanner scanner){
        String name_var;
        ArrayList<String> parents= new ArrayList<>();
        ArrayList<Double> probs = new ArrayList<>();

        String line= scanner.nextLine();
        name_var=getData(line);
        // find parents in text
        line=scanner.nextLine();
        while(line.matches("(.*)<GIVEN>(.*)</GIVEN>(.*)")){
            parents.add(getData(line));
            line= scanner.nextLine();
        }

        //initialize parents

        BayesianNode cur_node = (BayesianNode) net.get_nodes().get(name_var); // get the current node from the hashtable by its variable name
        for(String parent: parents){
                BayesianNode parent_node=(BayesianNode) net.get_nodes().get(parent);
                cur_node.addParents(parent_node); // add the parent to the child - current node list of parents
        }

        // find probabilities in xml
        String[] probs_str = line.split(">")[1].split("<")[0].split(" ");
        for (String str : probs_str){
            probs.add(Double.parseDouble(str));
        }
       // String[] ST=prob(probs_str);
    }

//    public String[] prob(String[] probs_str){
//        return probs_str;
//    }



}
