package enigma;

import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;

/** Class that represents a complete enigma machine.
 *  @author Won Shil Park
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _plugboard = new Permutation("", _alphabet);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _myRotor = new Rotor[numRotors()];
        HashMap<String, Rotor> myMap = new HashMap<>();
        for (Rotor r: _allRotors) {
            myMap.put(r.name().toUpperCase(), r);
        }
        for (int i = 0; i < _myRotor.length; i++) {
            try {
                _myRotor[i] = myMap.get(rotors[i].toUpperCase());
            } catch (EnigmaException e) {
                throw new EnigmaException("Invalid name.");
            }
        }
        if (_myRotor.length != rotors.length) {
            throw EnigmaException.error("Choose"
                    + " a better name.");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != (numRotors() - 1)) {
            throw new EnigmaException("Invalid setting string length.");
        }
        for (int i = 1; i < _myRotor.length; i++) {
            if (!_myRotor[i].reflecting()) {
                _myRotor[i].set(setting.charAt(i - 1));
            } else {
                throw new EnigmaException("Invalid placement"
                        + " for reflector.");
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        int count = 0;
        for (int i = 0; i < _myRotor.length; i++) {
            if (_myRotor[i].rotates()) {
                count++;
            }
        }
        if (count != numPawls()) {
            throw new EnigmaException("Invalid amount of rotors");
        }

        ArrayList<Rotor> advance = new ArrayList<>();
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        Rotor last = _myRotor[_myRotor.length - 1];
        advance.add(last);
        for (int i = 0; i < _myRotor.length - 1; i++) {
            Rotor curr = _myRotor[i];
            Rotor next = _myRotor[i + 1];
            if (next.atNotch() && curr.rotates()
                    && next.rotates()) {
                if (!advance.contains(_myRotor[i])) {
                    advance.add(_myRotor[i]);
                }
                if (!advance.contains(_myRotor[i + 1])) {
                    advance.add(_myRotor[i + 1]);
                }
            }
        }

        for (Rotor r: advance) {
            r.advance();
        }
        for (int j = _myRotor.length - 1; j >= 0; j--) {
            c = _myRotor[j].convertForward(c);
        }
        for (int j = 1; j < _myRotor.length; j++) {
            c = _myRotor[j].convertBackward(c);
        }
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        int count = 0;
        for (int i = 0; i < _myRotor.length; i++) {
            if (_myRotor[i].rotates()) {
                count++;
            }
        }
        if (count != numPawls()) {
            throw new EnigmaException("Invalid amount of rotors");
        }
        String finalMsg = "";
        msg = msg.toUpperCase();
        for (int i = 0; i < msg.length(); i++) {
            try {
                int in = _alphabet.toInt(msg.charAt(i));
                char out = _alphabet.toChar(convert(in));
                finalMsg += out;
            } catch (EnigmaException excp) {
                finalMsg += msg.charAt(i);
            }
        }
        return finalMsg;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Num of rotors. */
    private final int _numRotors;
    /** Num of pawls. */
    private final int _pawls;
    /** All rotors in alphabet. */
    protected Collection<Rotor> _allRotors;
    /** new array Rotor. */
    private Rotor[] _myRotor;
    /** Permutation. */
    private Permutation _plugboard;
}
