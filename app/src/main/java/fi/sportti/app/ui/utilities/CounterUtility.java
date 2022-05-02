package fi.sportti.app.ui.utilities;

/**
 * Counter utility for getting user input from our pop ups
 *
 * @author Lassi Bågman
 * @version 0.5
 */

public class CounterUtility {

    private int counter;
    private int min, max, step, start;
    private boolean roll;

    /**
     * Constructor if developer wants rolling counter with custom values
     * @param min Minimum value for counter
     * @param max Maximum value for counter
     * @param start Value where the counter will start
     * @param step Value of how big will the default step be
     * @param roll Boolean to determine if user wants rolling counter
     */
    public CounterUtility(int min, int max, int start, int step, boolean roll) {
        this(min, max, start, step);
        this.roll = roll;
    }

    /**
     * Constructor if developer wants counter with custom values
     * @param min Minimum value for counter
     * @param max Maximum value for counter
     * @param start Value where the counter will start
     * @param step Value of how big will the default step be
     */
    public CounterUtility(int min, int max, int start, int step) {
        this.counter = start;
        this.min = min;
        this.max = max;
        this.step = step;
        this.start = start;
    }

    /**
     * Constructor if developer wants counter with default values
     */
    public CounterUtility() {
        this.counter = 0;
        this.min = -100;
        this.max = 100;
        this.step = 1;
    }

    /**
     * Returns counter value in string
     * @return Counter value in string
     */
    public String returnCounter() {
        return Integer.toString(counter);
    }

    /**
     * Returns counter value in integer
     * @return Counter value in integer
     */
    public int returnCounterInt() {
        return counter;
    }

    /**
     * Adds step amount to counter
     */
    public void addToCounter() {
        addToCounter(this.step);
    }

    /**
     * Adds amount of inputted value to counter
     * @param i Value of how much to add
     */
    public void addToCounter(int i) {
        if (this.counter + i <= this.max) {
            this.counter += i;
        } else if (this.roll && this.counter == this.max) {
            this.counter = this.min;
        } else {
            this.counter = this.max;
        }
    }

    /**
     * Removes step amount to counter
     */
    public void minusToCounter() {
        minusToCounter(this.step);
    }


    /**
     * Removes amount of inputted value to counter
     * @param i Value of how much to remove
     */
    public void minusToCounter(int i) {
        if (this.counter - i >= this.min) {
            this.counter -= i;
        } else if (this.roll && this.counter == this.min) {
            this.counter = this.max;
        } else {
            this.counter = this.min;
        }
    }

    /**
     * Resets counter to defined starting value
     */
    public void resetCounter() {
        this.counter = start;
    }
}
