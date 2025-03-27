package models;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;

/**
 * tests outcome of request to become a reviewer
 */
public class Reviewerstatestest {
	
	/**
	 * makes sure the right status is sent
	 */
    @Test
    public void testRatingPositive() {
        if (ReviewerRequestStatus.valueOf("ACCEPTED") != ReviewerRequestStatus.ACCEPTED) {
            throw new RuntimeException(" mismatch");
        }
        if (ReviewerRequestStatus.valueOf("DENIED") != ReviewerRequestStatus.DENIED) {
            throw new RuntimeException(" mismatch");
        }
        if (ReviewerRequestStatus.valueOf("NO_REQUEST") != ReviewerRequestStatus.NO_REQUEST) {
            throw new RuntimeException("NO_REQUEST value mismatch");
        }
    }

    /**
     * tests neg outcome with wrong status
     */
    @Test
   public  void testRatingNeg() {
        if (ReviewerRequestStatus.values().length != 3) {
            throw new RuntimeException("not available");
        }
    }
}