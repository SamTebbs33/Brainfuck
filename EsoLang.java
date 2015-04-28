import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Test
 * 
 * @author samtebbs, 09:08:33 - 26 Apr 2015
 */
public class EsoLang {

    static long[] stack;
    static int stackPtr = 0;
    static long register = 0;

    static char[] bytes;
    static int bytePtr = 0;
    static int numBytes = 0;

    static Scanner in = new Scanner(System.in);

    public static void main(final String[] args) throws IOException {
	stack = new long[Integer.parseInt(args[0])];

	// Read in the bytes
	final File file = new File(args[1]);
	final FileReader r = new FileReader(file);
	bytes = new char[(int) file.length()];
	boolean add = true;
	for (int a = 0; a < bytes.length; a++) {
	    final char b = (char) r.read();
	    if (Character.isWhitespace(b)) {
		continue;
	    }
	    if (b == '{') {
		add = false;
	    } else if (b == '}') {
		add = true;
	    }
	    if (add) {
		bytes[numBytes++] = b;
	    }
	}
	r.close();

	while (bytePtr < numBytes) {
	    //System.out.printf("BytePtr: %d, StackPtr: %d, Value: %d, Instruction: %c%n",
	    //bytePtr, stackPtr, stack[stackPtr], bytes[bytePtr]);
	    switch (bytes[bytePtr]) {
		case '@':
		    System.out.println(Arrays.toString(stack) + ":" + stackPtr);
		    break;
		case '$':
		    register = stack[stackPtr];
		    break;
		case '=':
		    stack[stackPtr] = register;
		case '+': // Increment value at stack pointer
		    stack[stackPtr]++;
		    break;
		case '-': // Decrement value at stack pointer
		    stack[stackPtr]--;
		    break;
		case '>': // Increment stack pointer
		    stackPtr++;
		    break;
		case '<': // Decrement stack pointer
		    stackPtr--;
		    break;
		case '.': // Output value as character at stack pointer
		    System.out.print((char) stack[stackPtr]);
		    break;
		case ':': // Output value as integer at stack pointer
		    System.out.print(stack[stackPtr]);
		    break;
		case ';': // Input an integer at stack pointer
		    stack[stackPtr] = in.nextInt();
		    in.nextLine();
		    break;
		case ',': // Input a character at stack pointer
		    stack[stackPtr] = in.nextLine().charAt(0);
		    break;
		case '&': // Input a string
		    final char[] str = in.nextLine().toCharArray();
		    for (int i = 0; i < str.length; i++) {
			stack[stackPtr + i] = str[i];
		    }
		    break;
		case '[': // If value at stack pointer is 0, jump to the
			  // corresponding ], else move to next instruction
		    if (stack[stackPtr] == 0) {
			while (++bytePtr < numBytes) {
			    int expected = 0;
			    if (bytes[bytePtr] == ']') {
				if (expected == 0) {
				    break;
				} else {
				    expected--;
				}
			    } else if (bytes[bytePtr] == '[') {
				expected++;
			    }
			}
		    }
		    break;
		case ']': // Return to previous [
		    if (stack[stackPtr] != 0) {
			int expected = 0;
			while (--bytePtr >= 0) {
			    if (bytes[bytePtr] == '[') {
				if (expected == 0) {
				    break;
				} else {
				    expected--;
				}
			    } else if (bytes[bytePtr] == ']') {
				expected++;
			    }
			}
		    }
		    break;
		case '!': // Halt program
		    bytePtr = numBytes;
		    break;
	    }
	    bytePtr++;
	}

    }

}
