package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Won Shil Park
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */

    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        String str, changed;
        int count = 0;
        Machine m = readConfig();

        while (_input.hasNextLine()) {
            str = _input.nextLine();
            if (str.length() > 0 && str.charAt(0) == '*') {
                setUp(m, str.substring(2));
                count = 1;
            } else {
                if (count == 0) {
                    throw new EnigmaException("Message"
                            + " can't be converted.");
                }
                count++;
                changed = m.convert(str);
                printMessageLine(changed);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphabet, str;
            int numRotors, numPawls;
            ArrayList<Rotor> rotors = new ArrayList<>();
            alphabet = _config.nextLine();

            if (alphabet.length() == 0 || alphabet.indexOf(' ') != -1) {
                throw new EnigmaException("Invalid input");
            }
            _alphabet = new Alphabet(alphabet);

            if (!_config.hasNextInt()) {
                throw new EnigmaException("No numRotors");
            }
            numRotors = _config.nextInt();

            if (!_config.hasNextInt()) {
                throw new EnigmaException("No numPawls");
            }
            numPawls = _config.nextInt();

            if (numRotors <= numPawls) {
                throw new EnigmaException("insufficient rotors");
            }
            _config.nextLine();
            while (_config.hasNextLine()) {
                str = _config.nextLine();
                if (str.charAt(1) == ' ') {
                    rotors.get(rotors.size() - 1).permutation().addCycles(str);
                } else {
                    Rotor adding = readRotor(str.substring(1));
                    for (Rotor r: rotors) {
                        if (adding.name().equals(r.name())) {
                            throw new EnigmaException("duplicate rotor");
                        }
                    }
                    rotors.add(adding);
                }
            }
            return new Machine(new Alphabet(alphabet),
                    numRotors, numPawls, rotors);
        } catch (NoSuchElementException excp) {
            throw error("shortened configuration file");
        }
    }

    /** Return a rotor, reading its description from _config.
     * @param rotorConfig - string containing name,
     *                              type, notches, and cycles.*/
    private Rotor readRotor(String rotorConfig) {
        try {
            String myName, myNotches, myCycles;
            int elem = rotorConfig.indexOf(' ');
            Rotor rotor;

            myName = rotorConfig.substring(0, elem);

            rotorConfig = rotorConfig.substring(elem + 1);
            elem = rotorConfig.indexOf(' ');
            myNotches = rotorConfig.substring(0, elem);
            elem = rotorConfig.indexOf('(');
            myCycles = rotorConfig.substring(elem);

            Permutation holder = new Permutation(myCycles, _alphabet);

            if (myNotches.charAt(0) == 'M') {
                if (myNotches.length() < 2) {
                    throw new EnigmaException("invalid rotor format");
                }
                rotor = new MovingRotor(myName, holder,
                        myNotches.substring(1));
            } else if (myNotches.charAt(0) == 'N') {
                rotor = new FixedRotor(myName, holder);
            } else if (myNotches.charAt(0) == 'R') {
                rotor = new Reflector(myName, holder);
            } else {
                throw new EnigmaException("Invalid rotor");
            }
            return rotor;
        } catch (NoSuchElementException excp) {
            throw error("invalid description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        int numRotors = M.numRotors(), elem;
        String[] myRotors = new String[numRotors];
        String mySettings;

        for (int i = 0; i < numRotors; i++) {
            elem = settings.indexOf(' ');
            if (elem != -1) {
                myRotors[i] = settings.substring(0, elem);
                settings = settings.substring(elem + 1);
            } else {
                myRotors[i] = settings;
            }
        }
        elem = settings.indexOf(' ');
        if (elem == -1) {
            mySettings = settings;
            M.setPlugboard(new Permutation("", _alphabet));
        } else {
            M.setPlugboard(new Permutation(settings.substring(elem + 1),
                    _alphabet));
            mySettings = settings.substring(0, elem);
        }
        M.insertRotors(myRotors);
        M.setRotors(mySettings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String finalMsg = "";
        int count = 0;

        for (int i = 0; i < msg.length(); i++) {
            char ch = msg.charAt(i);
            if (ch != ' ') {
                finalMsg += ch;
                count++;
            }
            if (count == 5) {
                count = 0;
                finalMsg += ' ';
            }
        }
        _output.println(finalMsg);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
