import java.io.*;
import java.util.Scanner;
public class Ex1
{
        // open and read from input file
   //     static File file = new File("C:\\Users\\User\\Documents\\אריאל\\שנה ב\\סמסטר א\\אלגו בבינה מלאכותית\\מטלה\\input2.txt");
        static File file = new File("C:\\Users\\User\\IdeaProjects\\AI_try\\input.txt"); // for the cmd running
       //file =
        static Scanner scanner;
        static {
        try {
        scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
        System.out.println("An error occurred with file reading.");
        e.printStackTrace();
        }
        }



    public static void main(String[] args) throws IOException {

        File output = new File("output.txt");
        BufferedWriter out = new BufferedWriter(new FileWriter(output));
        String XML_name = scanner.nextLine();
        BayesianNetwork net = BayesianNetwork.readXML(XML_name); //null pointer exception beacuse factor is null
        //  System.out.println(net);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();


//            VariableElimination v_e = new VariableElimination(net, line);
//            String result = v_e.get_answer();
            BasicProb bs= new BasicProb(net,line);
            VariableElimination ve= new VariableElimination(net,line);
//            String result=bs.calcProb().toString();
            String result=null;
            if(line.split("\\),")[1].equals("1")) {
                 result = bs.calcTotalProb();

            }
            else if(line.split("\\),")[1].equals("2")){
                result =String.valueOf(ve); //
            }
            System.out.println();
            out.write(result);
            System.out.println();


        }
        if (scanner.hasNextLine()){
            out.newLine();
        }

        scanner.close();
        out.close();
    }
}

            // create an output text and write to it
        // the result of the queries in the input file.
//        try {
//            File myObj = new File("filename.txt");
//            if (myObj.createNewFile()) {
//                System.out.println("File created: " + myObj.getName());
//            } else {
//                System.out.println("File already exists.");
//            }
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }


