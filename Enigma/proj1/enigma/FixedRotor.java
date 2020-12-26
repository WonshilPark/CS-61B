package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Won Shil Park
 */
class FixedRotor extends Rotor {
    /** Initialize setting. */
    private int setting;

    /**
     * A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM.
     */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
    }

    @Override
    void advance() {
    }
}
