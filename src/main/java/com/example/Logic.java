package com.example;


/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 7/31/13
 * Time: 12:51 PM
 */
public class Logic {

	/**
	 * This method is modified to tell a number is prime or not, instead of handful of items in List
	 * to chose we have a broader range.
	 * @param number
	 * @return
	 */
	public boolean isPrime(long number) {

		if(number <= 1)
		   return false;

    	if(number == 2)
    		return true;

    	for(int j=2; j<= number; j=j+2){
    		if(j%number == 0)
    			return false;
    	}
    	return true;
    }

    public long nextPrimeFrom(long number) {
        int result = (int) number + 1;
        while(!isPrime(result)) result++;
        return result;
    }
}
