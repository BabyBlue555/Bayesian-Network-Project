import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
public class VariableElimination {

    private BayesianNetwork _net;
    private String _query;
    private String _queryValue;
    private String[] _evidence;
    private String[] _evidenceValues;
    private ArrayList<String> _hidden;
    private String[] _relevant;
    private ArrayList<Factor> _factors;
    private int _additionCounter;
    private int _multiplicationCounter;
    private boolean _is_immediate;
    private String _answer;

    // initializing the data and run the algorithm
}
