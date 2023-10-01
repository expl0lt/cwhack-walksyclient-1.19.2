package net.walksy.client.misc.json;

public class JSONTokener {
    private final String in;

    private int pos;

    public JSONTokener(String in) {
        this.in = in;
    }

    public Object nextValue() throws JSONException {
        int c = nextCleanInternal();
        switch (c) {
            case -1:
                throw syntaxError("End of input");
            case 123:
                return readObject();
            case 91:
                return readArray();
            case 34:
            case 39:
                return nextString((char)c);
        }
        this.pos--;
        return readLiteral();
    }

    private int nextCleanInternal() throws JSONException {
        while (this.pos < this.in.length()) {
            char peek;
            int c = this.in.charAt(this.pos++);
            switch (c) {
                case 9:
                case 10:
                case 13:
                case 32:
                    continue;
                case 47:
                    if (this.pos == this.in.length())
                        return c;
                    peek = this.in.charAt(this.pos);
                    if (peek != '*' && peek != '/')
                        return c;
                    skipComment();
                    continue;
            }
            return c;
        }
        return -1;
    }

    private void skipComment() throws JSONException {
        if (this.in.charAt(this.pos++) == '*') {
            int commentEnd = this.in.indexOf("*/", this.pos);
            if (commentEnd == -1)
                throw syntaxError("Unterminated comment");
            this.pos = commentEnd + 2;
        } else {
            for (; this.pos < this.in.length(); this.pos++) {
                char c = this.in.charAt(this.pos);
                if (c == '\r' || c == '\n') {
                    this.pos++;
                    break;
                }
            }
        }
    }

    public String nextString(char quote) throws JSONException {
        StringBuilder builder = null;
        int start = this.pos;
        while (this.pos < this.in.length()) {
            int c = this.in.charAt(this.pos++);
            if (c == quote) {
                if (builder == null)
                    return new String(this.in.substring(start, this.pos - 1));
                builder.append(this.in, start, this.pos - 1);
                return builder.toString();
            }
            if (c == 92) {
                if (this.pos == this.in.length())
                    throw syntaxError("Unterminated escape sequence");
                if (builder == null)
                    builder = new StringBuilder();
                builder.append(this.in, start, this.pos - 1);
                builder.append(readEscapeCharacter());
                start = this.pos;
            }
        }
        throw syntaxError("Unterminated string");
    }

    private char readEscapeCharacter() throws JSONException {
        String hex;
        char escaped = this.in.charAt(this.pos++);
        switch (escaped) {
            case 'u':
                if (this.pos + 4 > this.in.length())
                    throw syntaxError("Unterminated escape sequence");
                hex = this.in.substring(this.pos, this.pos + 4);
                this.pos += 4;
                return (char)Integer.parseInt(hex, 16);
            case 't':
                return '\t';
            case 'b':
                return '\b';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 'f':
                return '\f';
        }
        return escaped;
    }

    private Object readLiteral() throws JSONException {
        String literal = nextToInternal("{}[]/\\:,=;# \t\f");
        if (literal.length() == 0)
            throw syntaxError("Expected literal value");
        if ("null".equalsIgnoreCase(literal))
            return JSONObject.NULL;
        if ("true".equalsIgnoreCase(literal))
            return Boolean.TRUE;
        if ("false".equalsIgnoreCase(literal))
            return Boolean.FALSE;
        if (literal.indexOf('.') == -1) {
            int base = 10;
            String number = literal;
            if (number.startsWith("0x") || number.startsWith("0X")) {
                number = number.substring(2);
                base = 16;
            } else if (number.startsWith("0") && number.length() > 1) {
                number = number.substring(1);
                base = 8;
            }
            try {
                long longValue = Long.parseLong(number, base);
                if (longValue <= 2147483647L && longValue >= -2147483648L)
                    return Integer.valueOf((int)longValue);
                return Long.valueOf(longValue);
            } catch (NumberFormatException numberFormatException) {}
        }
        try {
            return Double.valueOf(literal);
        } catch (NumberFormatException numberFormatException) {
            return new String(literal);
        }
    }

    private String nextToInternal(String excluded) {
        int start = this.pos;
        for (; this.pos < this.in.length(); this.pos++) {
            char c = this.in.charAt(this.pos);
            if (c == '\r' || c == '\n' || excluded.indexOf(c) != -1)
                return this.in.substring(start, this.pos);
        }
        return this.in.substring(start);
    }

    private JSONObject readObject() throws JSONException {
        JSONObject result = new JSONObject();
        int first = nextCleanInternal();
        if (first == 125)
            return result;
        if (first != -1)
            this.pos--;
        while (true) {
            Object name = nextValue();
            if (!(name instanceof String)) {
                if (name == null)
                    throw syntaxError("Names cannot be null");
                throw syntaxError("Names must be strings, but " + name + " is of type " + name
                        .getClass().getName());
            }
            int separator = nextCleanInternal();
            if (separator != 58 && separator != 61)
                throw syntaxError("Expected ':' after " + name);
            if (this.pos < this.in.length() && this.in.charAt(this.pos) == '>')
                this.pos++;
            result.put((String)name, nextValue());
            switch (nextCleanInternal()) {
                case 125:
                    return result;
                case 44:
                case 59:
                    continue;
            }
            break;
        }
        throw syntaxError("Unterminated object");
    }

    private JSONArray readArray() throws JSONException {
        JSONArray result = new JSONArray();
        boolean hasTrailingSeparator = false;
        while (true) {
            switch (nextCleanInternal()) {
                case -1:
                    throw syntaxError("Unterminated array");
                case 93:
                    if (hasTrailingSeparator)
                        result.put((Object)null);
                    return result;
                case 44:
                case 59:
                    result.put((Object)null);
                    hasTrailingSeparator = true;
                    continue;
            }
            this.pos--;
            result.put(nextValue());
            switch (nextCleanInternal()) {
                case 93:
                    return result;
                case 44:
                case 59:
                    hasTrailingSeparator = true;
                    continue;
            }
            break;
        }
        throw syntaxError("Unterminated array");
    }

    public JSONException syntaxError(String message) {
        return new JSONException(message + this);
    }

    public String toString() {
        return " at character " + this.pos + " of " + this.in;
    }

    public boolean more() {
        return (this.pos < this.in.length());
    }

    public char next() {
        return (this.pos < this.in.length()) ? this.in.charAt(this.pos++) : Character.MIN_VALUE;
    }

    public char next(char c) throws JSONException {
        char result = next();
        if (result != c)
            throw syntaxError("Expected " + c + " but was " + result);
        return result;
    }

    public char nextClean() throws JSONException {
        int nextCleanInt = nextCleanInternal();
        return (nextCleanInt == -1) ? Character.MIN_VALUE : (char)nextCleanInt;
    }

    public String next(int length) throws JSONException {
        if (this.pos + length > this.in.length())
            throw syntaxError(length + " is out of bounds");
        String result = this.in.substring(this.pos, this.pos + length);
        this.pos += length;
        return result;
    }

    public String nextTo(String excluded) {
        if (excluded == null)
            throw new NullPointerException();
        return nextToInternal(excluded).trim();
    }

    public String nextTo(char excluded) {
        return nextToInternal(String.valueOf(excluded)).trim();
    }

    public void skipPast(String thru) {
        int thruStart = this.in.indexOf(thru, this.pos);
        this.pos = (thruStart == -1) ? this.in.length() : (thruStart + thru.length());
    }

    public char skipTo(char to) {
        int index = this.in.indexOf(to, this.pos);
        if (index != -1) {
            this.pos = index;
            return to;
        }
        return Character.MIN_VALUE;
    }

    public void back() {
        if (--this.pos == -1)
            this.pos = 0;
    }

    public static int dehexchar(char hex) {
        if (hex >= '0' && hex <= '9')
            return hex - 48;
        if (hex >= 'A' && hex <= 'F')
            return hex - 65 + 10;
        if (hex >= 'a' && hex <= 'f')
            return hex - 97 + 10;
        return -1;
    }
}
