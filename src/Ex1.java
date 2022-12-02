import java.io.*;
import java.util.Scanner;
public class Ex1
{
        // open and read from input file
        static File file = new File("C:\\Users\\User\\Documents\\אריאל\\שנה ב\\סמסטר א\\אלגו בבינה מלאכותית\\מטלה\\input.txt");
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
    }
}
