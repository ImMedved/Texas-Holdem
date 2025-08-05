import com.kukharev.core.HeroProbabilityCalculator;
import com.kukharev.model.HandProbabilities;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TurnCombinationTests {

    @Test
    public void testTurnHand1() {
        List<String> hole = List.of("AH", "KH");
        List<String> board = List.of("QH", "JH", "TH", "2D");
        HandProbabilities hp = HeroProbabilityCalculator.calculateTurn(hole, board, 1);
        assertEquals(100.0, hp.getRoyalFlush(), 0.01);
        assertEquals(100.0, hp.getStraightFlush(), 0.01);
        assertEquals(100.0, hp.getFlush(), 0.01);
        assertEquals(100.0, hp.getStraight(), 0.01);
        assertEquals(100.0, hp.getHighCard(), 0.01);
    }

    @Test
    public void testTurnHand2() {
        List<String> hole = List.of("9C", "9D");
        List<String> board = List.of("9H", "KS", "KD", "2S");
        HandProbabilities hp = HeroProbabilityCalculator.calculateTurn(hole, board, 1);
        assertEquals(2.17, hp.getQuads(), 1.0);
        assertEquals(100.0, hp.getFullHouse(), 5.0);
        assertEquals(97.83, hp.getSet(), 1.0);
        assertEquals(100.0, hp.getTwoPair(), 0.01);
        assertEquals(100.0, hp.getOnePair(), 0.01);
    }

    @Test
    public void testTurnHand3() {
        List<String> hole = List.of("4H", "4D");
        List<String> board = List.of("4S", "8C", "8D", "2S");
        HandProbabilities hp = HeroProbabilityCalculator.calculateTurn(hole, board, 1);
        assertEquals(100.0, hp.getFullHouse(), 5.0);
        assertEquals(100.0, hp.getSet(), 5.0);
        assertEquals(100.0, hp.getTwoPair(), 5);
        assertEquals(100.0, hp.getOnePair(), 5);
        assertTrue(hp.getHighCard() > 3);
    }

    @Test
    public void testTurnHand4() {
        List<String> hole = List.of("6H", "7H");
        List<String> board = List.of("8H", "9H", "TH", "2D");
        HandProbabilities hp = HeroProbabilityCalculator.calculateTurn(hole, board, 1);
        assertEquals(100.0, hp.getStraightFlush(), 0.01);
        assertEquals(100.0, hp.getFlush(), 0.01);
        assertEquals(100.0, hp.getStraight(), 0.01);
        assertEquals(21.3, hp.getHighCard(), 1.0);
        assertEquals(39.13, hp.getOnePair(), 1.0);
    }

    @Test
    public void testTurnHand5() {
        List<String> hole = List.of("2H", "7D");
        List<String> board = List.of("2S", "7C", "KD", "4H");
        HandProbabilities hp = HeroProbabilityCalculator.calculateTurn(hole, board, 1);
        assertEquals(100.0, hp.getTwoPair(), 0.01);
        assertEquals(100.0, hp.getOnePair(), 0.01);
        assertTrue(hp.getHighCard() > 15);
        assertEquals(0.0, hp.getStraight(), 0.01);
        assertEquals(0.0, hp.getFlush(), 0.01);
    }

    @Test
    public void testTurnHand6() {
        List<String> hole = List.of("5H", "5D");
        List<String> board = List.of("5S", "8C", "TD", "KD");
        HandProbabilities hp = HeroProbabilityCalculator.calculateTurn(hole, board, 1);
        assertEquals(100.0, hp.getSet(), 5.0);
        assertEquals(100.0, hp.getOnePair(), 0.01);
        assertTrue(hp.getHighCard() > 5);
        assertEquals(19.57, hp.getFullHouse(), 1.0);
        assertEquals(2.17, hp.getQuads(), 1.0);
    }

    @Test
    public void testTurnHand7() {
        List<String> hole = List.of("8H", "9H");
        List<String> board = List.of("TH", "JH", "QD", "2C");
        HandProbabilities hp = HeroProbabilityCalculator.calculateTurn(hole, board, 1);
        assertEquals(100.0, hp.getStraight(), 0.01);
        assertEquals(19.57, hp.getFlush(), 1.0);
        assertEquals(4.35, hp.getStraightFlush(), 1.0);
        assertEquals(40, hp.getHighCard(), 2);
        assertEquals(0.0, hp.getSet(), 0.01);
    }

    @Test
    public void testTurnHand8() {
        List<String> hole = List.of("KH", "3D");
        List<String> board = List.of("2H", "4C", "5S", "6D");
        HandProbabilities hp = HeroProbabilityCalculator.calculateTurn(hole, board, 1);
        assertEquals(100.0, hp.getStraight(), 0.01);
        assertEquals(39.13, hp.getOnePair(), 1.0);
        assertTrue(hp.getHighCard() > 80);
        assertEquals(0.0, hp.getFlush(), 0.01);
        assertEquals(0.0, hp.getSet(), 0.01);
    }

    @Test
    public void testTurnHand9() {
        List<String> hole = List.of("3H", "6C");
        List<String> board = List.of("4H", "5C", "7S", "QD");
        HandProbabilities hp = HeroProbabilityCalculator.calculateTurn(hole, board, 1);
        assertEquals(100.0, hp.getStraight(), 0.01);
        assertEquals(12, hp.getHighCard(), 1);
        assertEquals(0.0, hp.getSet(), 0.01);
        assertEquals(0.0, hp.getFlush(), 0.01);
        assertEquals(0.0, hp.getTwoPair(), 0.01);
    }

    @Test
    public void testTurnHand10() {
        List<String> hole = List.of("AH", "9D");
        List<String> board = List.of("2S", "4C", "7H", "JD");
        HandProbabilities hp = HeroProbabilityCalculator.calculateTurn(hole, board, 1);
        assertEquals(100.0, hp.getHighCard(), 1.0);
        assertEquals(39.13, hp.getOnePair(), 1.0);
        assertEquals(0.0, hp.getFlush(), 0.01);
        assertEquals(0.0, hp.getStraight(), 0.01);
        assertEquals(0.0, hp.getSet(), 0.01);
    }
}
