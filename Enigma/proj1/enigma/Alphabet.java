package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Won Shil Park
 */
class Alphabet {
    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
        charList = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            charList[i] = _chars.charAt(i);
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _chars.contains(String.valueOf(ch));
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index >= size()) {
            throw EnigmaException.error("Alphabet"
                    + " index is out of bounds.");
        } else {
            return charList[index];
        }
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        boolean insideAlpha = false;
        int index = 0;
        for (int i = 0; i < charList.length; i++) {
            if (charList[i] == ch) {
                insideAlpha = true;
                index = i;
            }
        }
        if (!insideAlpha) {
            throw EnigmaException.error("Char"
                    + ch + "not in alphabet");
        }
        return index;
    }
    /** Characters of alphabet. */
    private String _chars;
    /** List of characters. */
    private char[] charList;
}
