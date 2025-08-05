import com.kukharev.core.HeroProbabilityCalculator;
import com.kukharev.model.HandProbabilities;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RiverHandEvaluationTest {

    @Test
    public void testOnePairOnly() {
        List<String> hole = List.of("9H", "2C");
        List<String> board = List.of("9D", "6S", "4C", "7H", "QD");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100, hp.getOnePair(), 0.001);
        assertEquals(0, hp.getTwoPair(), 0.001);
        assertEquals(0, hp.getSet(), 0.001);
    }

    @Test
    public void testTwoPair() {
        List<String> hole = List.of("9H", "QH");
        List<String> board = List.of("9D", "QD", "6S", "3C", "7S");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100, hp.getTwoPair(), 0.001);
        assertEquals(0, hp.getSet(), 0.001);
        assertEquals(0, hp.getFullHouse(), 0.001);
    }

    @Test
    public void testSetOnly() {
        List<String> hole = List.of("7H", "7D");
        List<String> board = List.of("7S", "9C", "2H", "5D", "KC");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100, hp.getSet(), 0.001);
        assertEquals(0, hp.getFullHouse(), 0.001);
        assertEquals(0, hp.getQuads(), 0.001);
    }

    @Test
    public void testFullHouse() {
        List<String> hole = List.of("7H", "7D");
        List<String> board = List.of("7S", "9C", "9D", "5D", "KC");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100, hp.getFullHouse(), 0.001);
        assertEquals(0, hp.getQuads(), 0.001);
    }

    @Test
    public void testQuads() {
        List<String> hole = List.of("7H", "7D");
        List<String> board = List.of("7S", "7C", "9D", "5D", "KC");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100, hp.getQuads(), 0.001);
    }

    @Test
    public void testFlush() {
        List<String> hole = List.of("2H", "9H");
        List<String> board = List.of("KH", "4H", "6H", "7H", "QD");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100, hp.getFlush(), 0.001);
        assertEquals(0, hp.getStraight(), 0.001);
        assertEquals(0, hp.getFullHouse(), 0.001);
    }

    @Test
    public void testStraightOnly() {
        List<String> hole = List.of("9H", "8D");
        List<String> board = List.of("7C", "6S", "5H", "2D", "KC");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100, hp.getStraight(), 0.001);
        assertEquals(0, hp.getFlush(), 0.001);
        assertEquals(0, hp.getStraightFlush(), 0.001);
    }

    @Test
    public void testStraightFlush() {
        List<String> hole = List.of("9H", "8H");
        List<String> board = List.of("7H", "6H", "5H", "2D", "KC");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100, hp.getStraightFlush(), 0.001);
        assertEquals(100, hp.getFlush(), 0.001);
        assertEquals(100, hp.getStraight(), 0.001);
    }

    @Test
    public void testRoyalFlush() {
        List<String> hole = List.of("TH", "JH");
        List<String> board = List.of("QH", "KH", "AH", "3C", "9D");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100, hp.getRoyalFlush(), 0.001);
        assertEquals(100, hp.getStraightFlush(), 0.001);
    }

    @Test
    public void testNothing() {
        List<String> hole = List.of("2H", "7D");
        List<String> board = List.of("9C", "QS", "5S", "8D", "KC");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(0, hp.getOnePair(), 0.001);
        assertEquals(0, hp.getTwoPair(), 0.001);
        assertEquals(0, hp.getSet(), 0.001);
        assertEquals(0, hp.getStraight(), 0.001);
        assertEquals(0, hp.getFlush(), 0.001);
        assertEquals(0, hp.getFullHouse(), 0.001);
        assertEquals(0, hp.getQuads(), 0.001);
        assertEquals(0, hp.getStraightFlush(), 0.001);
        assertEquals(0, hp.getRoyalFlush(), 0.001);
    }
    @Test
    public void testFullHouseFromBoardOnly() {
        List<String> hole = List.of("2H", "3D");
        List<String> board = List.of("9S", "9D", "9C", "2S", "2D"); // board = full house
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(0, hp.getFullHouse(), 0.001);
    }

    @Test
    public void testQuadsOnBoard() {
        List<String> hole = List.of("AH", "3C");
        List<String> board = List.of("8S", "8D", "8C", "8H", "2S");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100.0, hp.getQuads(), 0.001);
    }

    @Test
    public void testFullHouseFromHandOverBoardTrips() {
        List<String> hole = List.of("2H", "2D");
        List<String> board = List.of("9S", "9D", "9C", "8H", "4S");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100.0, hp.getFullHouse(), 0.001);
        assertEquals(100.0, hp.getSet(), 0.001);
    }

    @Test
    public void testHighCardOnly() {
        List<String> hole = List.of("2H", "8D");
        List<String> board = List.of("9S", "JD", "5C", "QH", "4S");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(0.0, hp.getOnePair(), 0.001);
        assertEquals(0.0, hp.getTwoPair(), 0.001);
        assertEquals(0.0, hp.getSet(), 0.001);
        assertEquals(0.0, hp.getStraight(), 0.001);
        assertEquals(0.0, hp.getFlush(), 0.001);
    }

    @Test
    public void testWheelStraight() {
        List<String> hole = List.of("3D", "4C");
        List<String> board = List.of("2S", "5H", "AH", "QD", "9C");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100.0, hp.getStraight(), 0.001);
    }

    @Test
    public void testFakeRoyalFlushWrongSuit() {
        List<String> hole = List.of("AH", "KH");
        List<String> board = List.of("QH", "JH", "TC", "9D", "2S");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100.0, hp.getStraight(), 0.001);
        assertEquals(0, hp.getFlush(), 0.001); // 4 same suit — NOT enough
        assertEquals(0.0, hp.getStraightFlush(), 0.001);
        assertEquals(0.0, hp.getRoyalFlush(), 0.001);
    }

    @Test
    public void testBackdoorFlushOnRiver() {
        List<String> hole = List.of("AH", "5H");
        List<String> board = List.of("2H", "7H", "KH", "9C", "4D");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100.0, hp.getFlush(), 0.001);
    }

    @Test
    public void testTwoPairFromBoardAndHand() {
        List<String> hole = List.of("5S", "9C");
        List<String> board = List.of("5C", "9D", "KH", "3H", "7S");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100.0, hp.getTwoPair(), 0.001);
    }

    @Test
    public void testStraightFlushButNotRoyal() {
        List<String> hole = List.of("5H", "6H");
        List<String> board = List.of("7H", "8H", "9H", "QD", "KS");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100.0, hp.getStraightFlush(), 0.001);
        assertEquals(0.0, hp.getRoyalFlush(), 0.001);
    }

    @Test
    public void testMultipleTripsButNoFullHouse() {
        List<String> hole = List.of("9H", "9D");
        List<String> board = List.of("8S", "8D", "9C", "2H", "3D");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100.0, hp.getFullHouse(), 0.001);
        assertEquals(100.0, hp.getSet(), 0.001);
    }
    @Test
    public void testWheelStraightFlush() {
        List<String> hole = List.of("2H", "3H");
        List<String> board = List.of("AH", "4H", "5H", "9D", "KC");
        HandProbabilities hp = HeroProbabilityCalculator.calculateRiver(hole, board, 1);
        assertEquals(100.0, hp.getStraightFlush(), 0.001);
        assertEquals(100.0, hp.getFlush(), 0.001);
        assertEquals(100.0, hp.getStraight(), 0.001);
        assertEquals(0.0, hp.getRoyalFlush(), 0.001);
    }
}
