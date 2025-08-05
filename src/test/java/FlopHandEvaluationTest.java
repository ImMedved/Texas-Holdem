import com.kukharev.core.HeroProbabilityCalculator;
import com.kukharev.model.HandProbabilities;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class FlopHandEvaluationTest {
    @Test
    public void testHighCardOnFlop() {
        List<String> hole = List.of("2H", "7D");
        List<String> board = List.of("9S", "JC", "KD");
        HandProbabilities hp = HeroProbabilityCalculator.calculateFlop(hole, board, 1);
        assertTrue(hp.getHighCard() > 20.0);
    }

    @Test
    public void testOnePairOnFlop() {
        List<String> hole = List.of("2H", "7D");
        List<String> board = List.of("7S", "JC", "KD");
        HandProbabilities hp = HeroProbabilityCalculator.calculateFlop(hole, board, 1);
        assertEquals(100.0, hp.getOnePair(), 0.01);
    }

    @Test
    public void testTwoPairOnFlop() {
        List<String> hole = List.of("2H", "7D");
        List<String> board = List.of("2S", "7C", "KD");
        HandProbabilities hp = HeroProbabilityCalculator.calculateFlop(hole, board, 1);
        assertEquals(100.0, hp.getTwoPair(), 0.01);
    }

    @Test
    public void testThreeOfAKindOnFlop() {
        List<String> hole = List.of("2H", "2D");
        List<String> board = List.of("2S", "JC", "KD");
        HandProbabilities hp = HeroProbabilityCalculator.calculateFlop(hole, board, 1);
        assertEquals(100.0, hp.getSet(), 0.01);
    }

    @Test
    public void testStraightOnFlop() {
        List<String> hole = List.of("3H", "4D");
        List<String> board = List.of("2S", "5C", "6H");
        HandProbabilities hp = HeroProbabilityCalculator.calculateFlop(hole, board, 1);
        assertEquals(100.0, hp.getStraight(), 0.01);
    }

    @Test
    public void testFlushOnFlop() {
        List<String> hole = List.of("2H", "7H");
        List<String> board = List.of("KH", "9H", "TH");
        HandProbabilities hp = HeroProbabilityCalculator.calculateFlop(hole, board, 1);
        assertEquals(100.0, hp.getFlush(), 0.01);
    }

    @Test
    public void testFullHouseOnFlop() {
        List<String> hole = List.of("2H", "2D");
        List<String> board = List.of("2S", "KD", "KS");
        HandProbabilities hp = HeroProbabilityCalculator.calculateFlop(hole, board, 1);
        assertEquals(100.0, hp.getFullHouse(), 0.01);
    }

    @Test
    public void testFourOfAKindOnFlop() {
        List<String> hole = List.of("2H", "2D");
        List<String> board = List.of("2S", "2C", "KH");
        HandProbabilities hp = HeroProbabilityCalculator.calculateFlop(hole, board, 1);
        assertEquals(100.0, hp.getQuads(), 0.01);
    }

    @Test
    public void testStraightFlushOnFlop() {
        List<String> hole = List.of("6H", "7H");
        List<String> board = List.of("4H", "5H", "8H");
        HandProbabilities hp = HeroProbabilityCalculator.calculateFlop(hole, board, 1);
        assertEquals(100.0, hp.getStraightFlush(), 0.01);
    }

    @Test
    public void testRoyalFlushOnFlop() {
        List<String> hole = List.of("AH", "KH");
        List<String> board = List.of("QH", "JH", "TH");
        HandProbabilities hp = HeroProbabilityCalculator.calculateFlop(hole, board, 1);
        assertEquals(100.0, hp.getRoyalFlush(), 0.01);
    }

}