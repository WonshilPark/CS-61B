package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Won Shil Park
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;

    }

    /** Add the cycles (c0->c1->...->cm->c0) to the permutation, where CYCLEs is
     *  c0c1...cm.
     *  @param cycles - string of cycles */
    void addCycles(String cycles) {
        _cycles += cycles;
    }

    /** Helper function to split the cycles.
     * @param cycles - cycle1, cycle2, cycle3
     * @return split */
    static String[] helper(String cycles) {
        /** declaring replace cases */
        String cycles1 = cycles.replaceAll(" ", "");
        String cycles2 = cycles.replaceAll("\\(", "");
        String cycles3 = cycles.replaceAll("\\)", "");

        if (cycles.contains(" ")) {
            cycles = cycles1;
            String[] mySplit = cycles.split("\\)\\(");
            mySplit[0] = mySplit[0].substring(1);
            int last = mySplit.length - 1;
            mySplit[last] = mySplit[last].substring(0,
                    mySplit[last].length() - 1);
            return mySplit;
        } else {
            cycles = cycles2;
            cycles = cycles3;
            String[] split1 = {cycles};
            return split1;
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int i = wrap(p);
        char apply = _alphabet.toChar(i);
        char output = permute(apply);
        int finalOutput = _alphabet.toInt(output);
        return finalOutput;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int i = wrap(c);
        char apply = _alphabet.toChar(i);
        char output = invert(apply);
        int finalOutput = _alphabet.toInt(output);
        return finalOutput;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        char myAnswer = p;
        if (!_cycles.equals("")) {
            String [] newCycles = helper(_cycles);
            for (int i = 0; i < newCycles.length; i++) {
                if (newCycles[i].contains(String.valueOf(p))) {
                    String newCycles1 = newCycles[i];
                    int index = newCycles1.indexOf(String.valueOf(p));
                    if (index != newCycles1.length() - 1) {
                        myAnswer = newCycles1.charAt(index + 1);
                    } else {
                        myAnswer = newCycles1.charAt(0);
                    }
                }
            }
            return myAnswer;
        } else {
            return myAnswer;
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        char myAnswer = c;

        if (!_cycles.equals("")) {
            String [] newCycles = helper(_cycles);
            for (int i = 0; i < newCycles.length; i++) {
                if (newCycles[i].contains(String.valueOf(c))) {
                    String theCycle = newCycles[i];
                    int index = theCycle.indexOf(String.valueOf(c));
                    if (index != 0) {
                        myAnswer = theCycle.charAt(index - 1);
                    } else {
                        myAnswer = theCycle.charAt(theCycle.length() - 1);
                    }
                }
            }
            return myAnswer;
        } else {
            return myAnswer;
        }
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        String [] newCycles = helper(_cycles);
        for (int i = 0; i < newCycles.length; i++) {
            if (newCycles[i].length() == 1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Collection of cycles. */
    private String _cycles;
}
