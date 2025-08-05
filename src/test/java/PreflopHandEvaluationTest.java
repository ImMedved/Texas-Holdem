import com.kukharev.core.HeroProbabilityCalculator;
import com.kukharev.model.HandProbabilities;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class PreflopHandEvaluationTest {

    @Test
    public void testPocketPair() {
        List<String> hole = List.of("9H", "9D");
        List<String> board = List.of();
        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, board, 1);

        assertEquals(100.0, hp.getOnePair(), 0.001); // one pair always assumed present
        assertTrue(hp.getSet() > 10.0);           // ≈11.8% шанс добора сета
        assertTrue(hp.getFullHouse() > 0.0);
        assertTrue(hp.getQuads() > 0.0);
        System.out.println(hp.getStraight());
    }

    @Test
    public void testSuitedConnectors() {
        List<String> hole = List.of("9H", "8H");
        List<String> board = List.of();

        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, board,1);

        assertTrue(hp.getFlush() > 5.0);
        assertTrue(hp.getStraight() > 9.0);
        System.out.println(hp.getStraight());
    }

    @Test
    public void testOffsuitConnectors() {
        List<String> hole = List.of("9H", "8C");
        List<String> board = List.of();

        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, board,1);

        assertTrue(hp.getStraight() > 1.0);
        System.out.println(hp.getStraight());
    }

    @Test
    public void testSuitedBroadway() {
        List<String> hole = List.of("AH", "KH");
        List<String> board = List.of();

        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, board,1);

        assertTrue(hp.getFlush() > 6.0);
        assertTrue(hp.getRoyalFlush() > 0.0);
        System.out.println(hp.getStraight());
    }

    @Test
    public void testOffsuitBroadway() {
        List<String> hole = List.of("AH", "KD");
        List<String> board = List.of();

        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, board,1);

        assertTrue(hp.getFlush() > 0.8);
        System.out.println(hp.getStraight());
    }

    @Test
    public void testLowSuited() {
        List<String> hole = List.of("3S", "5S");
        List<String> board = List.of();

        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, board,1);

        assertTrue(hp.getFlush() > 5.0);
        assertTrue(hp.getStraight() > 6.0);
        System.out.println(hp.getStraight());
    }

    @Test
    public void testUnconnectedUnsuited() {
        List<String> hole = List.of("2H", "9D");
        List<String> board = List.of();

        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, board,1);

        assertTrue(hp.getFlush() > 0.8);
        assertTrue(hp.getStraight() < 30.0);
        System.out.println(hp.getStraight());
    }

    @Test
    public void testAceLowSuited() {
        List<String> hole = List.of("AH", "4H");
        List<String> board = List.of();

        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, board,1);

        assertTrue(hp.getFlush() > 6.0);
        assertTrue(hp.getStraight() > 3.0);
        System.out.println(hp.getStraight());
    }

    @Test
    public void testMidSuitedNonConnectors() {
        List<String> hole = List.of("7C", "TC");
        List<String> board = List.of();

        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, board, 1);

        assertTrue(hp.getFlush() > 5.0);
        assertTrue(hp.getStraight() > 3.0);
        System.out.println(hp.getStraight());
    }
    @Test
    public void testRandomHand1() {
        List<String> hole = List.of("3S", "QH");
        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, List.of(), 1);
        System.out.println("HighCard: " + hp.getHighCard());
        assertTrue(hp.getHighCard() > 50.0);
        assertTrue(hp.getFlush() < 3.0);
    }

    @Test
    public void testRandomHand2() {
        List<String> hole = List.of("2D", "KS");
        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, List.of(), 1);
        assertTrue(hp.getStraight() < 10.0);
        assertTrue(hp.getFlush() < 2.0);
    }

    @Test
    public void testRandomHand3() {
        List<String> hole = List.of("5S", "6D");
        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, List.of(), 1);
        assertTrue(hp.getStraight() > 8.0);
        assertTrue(hp.getFlush() < 3.0);
    }

    @Test
    public void testRandomHand4() {
        List<String> hole = List.of("4H", "5C");
        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, List.of(), 1);
        assertTrue(hp.getStraight() > 8.0);
        assertTrue(hp.getSet() < 6.0);
    }

    @Test
    public void testRandomHand5() {
        List<String> hole = List.of("3C", "KS");
        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, List.of(), 1);
        System.out.println(hp.getHighCard());
        assertTrue(hp.getHighCard() > 60.0);
        assertTrue(hp.getFlush() < 2.0);
    }

    @Test
    public void testRandomHand6() {
        List<String> hole = List.of("KS", "QS");
        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, List.of(), 1);
        System.out.println(hp.getStraight());
        assertTrue(hp.getFlush() > 5.0);
        assertTrue(hp.getStraight() > 4.0);
    }

    @Test
    public void testRandomHand7() {
        List<String> hole = List.of("3D", "TC");
        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, List.of(), 1);
        assertTrue(hp.getStraight() < 8.0);
        assertTrue(hp.getFlush() < 3.0);
    }

    @Test
    public void testRandomHand8() {
        List<String> hole = List.of("8S", "JD");
        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, List.of(), 1);
        System.out.println(hp.getHighCard());

        assertTrue(hp.getHighCard() > 55.0);
        assertTrue(hp.getFlush() < 3.0);
    }

    @Test
    public void testRandomHand9() {
        List<String> hole = List.of("3D", "5D");
        HandProbabilities hp = HeroProbabilityCalculator.calculatePreflop(hole, List.of(), 1);
        System.out.println(hp.getStraight());

        assertTrue(hp.getFlush() > 5.0);
        assertTrue(hp.getStraight() > 7.0);
    }

}
