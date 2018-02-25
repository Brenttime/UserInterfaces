/*
 * Brent Turner
 *
 * UserInterfaces
 * ExpectedException
 */
package doctorfx;

/**
 * This class is meant to handle exceptions we intend to happen if the user
 * attempts to do something "wrong." It interrupts the action rather than
 * crashing the program.
 * 
 * @author Brent Turner
 */
class ExpectedException extends Exception {
  ExpectedException(String message) {
    super(message);
  }
}
