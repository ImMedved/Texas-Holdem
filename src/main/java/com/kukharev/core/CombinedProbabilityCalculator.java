package com.kukharev.core;

import java.math.BigInteger;
import java.util.*;
import java.util.function.DoubleUnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CombinedProbabilityCalculator {
    private static final Logger logger = LoggerFactory.getLogger(CombinedProbabilityCalculator.class);

    public record ProbResult(
            String highCard, String pair, String twoPair, String set, String straight,
            String flush, String fullHouse, String quads, String straightFlush, String royal) {

        // Helper method to convert string percentage to double
        public double getValueAsDouble(String value) {
            if (value.equals("—")) return 0.0;
            if (value.equals("100%")) return 1.0;
            return Double.parseDouble(value.replace("%", "")) / 100.0;
        }

        // Getters that return double values
        public double highCardValue() { return getValueAsDouble(highCard); }
        public double pairValue() { return getValueAsDouble(pair); }
        public double twoPairValue() { return getValueAsDouble(twoPair); }
        public double setValue() { return getValueAsDouble(set); }
        public double straightValue() { return getValueAsDouble(straight); }
        public double flushValue() { return getValueAsDouble(flush); }
        public double fullHouseValue() { return getValueAsDouble(fullHouse); }
        public double quadsValue() { return getValueAsDouble(quads); }
        public double straightFlushValue() { return getValueAsDouble(straightFlush); }
        public double royalValue() { return getValueAsDouble(royal); }
    }

    public record OppResult(
            String pair, String twoPair, String set, String straight,
            String flush, String fullHouse, String quads,
            String straightFlush, String royal) {

        // Helper method to convert string percentage to double
        public double getValueAsDouble(String value) {
            if (value.equals("—")) return 0.0;
            if (value.equals("100%")) return 1.0;
            return Double.parseDouble(value.replace("%", "")) / 100.0;
        }

        // Getters that return double values
        public double pairValue() { return getValueAsDouble(pair); }
        public double twoPairValue() { return getValueAsDouble(twoPair); }
        public double setValue() { return getValueAsDouble(set); }
        public double straightValue() { return getValueAsDouble(straight); }
        public double flushValue() { return getValueAsDouble(flush); }
        public double fullHouseValue() { return getValueAsDouble(fullHouse); }
        public double quadsValue() { return getValueAsDouble(quads); }
        public double straightFlushValue() { return getValueAsDouble(straightFlush); }
        public double royalValue() { return getValueAsDouble(royal); }
    }

    private static boolean hasAnyHand(boolean[] hands) {
        for (int i = 1; i < hands.length; i++) {
            if (hands[i]) return true;
        }
        return false;
    }

    private CombinedProbabilityCalculator() {}

    public static ProbResult computeHero(List<String> hole, List<String> board, int activeOpp) {
        if (hole.size()!=2) throw new IllegalArgumentException("need exactly 2 hole cards");

        // Check current board state first
        boolean[] current = detect(hole, board);

        String highCardP = "—";  // Default to em dash if we have better hands
        if (!hasAnyHand(current)) {
            double p = highCardProb(hole, board, activeOpp);
            highCardP = String.format("%.2f", p * 100) + "%";
        }

        // If we have any combination, handle it properly with potential higher combinations
        if (current[9]) // Royal Flush
            return new ProbResult("—", "—", "—", "—", "—", "—", "—", "—", "—", "100%");

        if (current[8]) // Straight Flush
            return new ProbResult("—", "—", "—", "—", "—", "—", "—", "—", "100%", calculateProb(9, hole, board));

        if (current[7]) // Four of a Kind
            return new ProbResult("—", "—", "—", "—", "—", "—", "—", "100%",
                calculateProb(8, hole, board), calculateProb(9, hole, board));

        if (current[6]) // Full House
            return new ProbResult("—", "—", "—", "—", "—", "—", "100%",
                calculateProb(7, hole, board), calculateProb(8, hole, board), calculateProb(9, hole, board));

        if (current[5]) // Flush
            return new ProbResult("—", "—", "—", "—", "—", "100%",
                calculateProb(6, hole, board), calculateProb(7, hole, board),
                calculateProb(8, hole, board), calculateProb(9, hole, board));

        if (current[4]) // Straight
            return new ProbResult("—", "—", "—", "—", "100%",
                calculateProb(5, hole, board), calculateProb(6, hole, board),
                calculateProb(7, hole, board), calculateProb(8, hole, board),
                calculateProb(9, hole, board));

        if (current[3]) // Three of a Kind
            return new ProbResult("—", "—", "—", "100%",
                calculateProb(4, hole, board), calculateProb(5, hole, board),
                calculateProb(6, hole, board), calculateProb(7, hole, board),
                calculateProb(8, hole, board), calculateProb(9, hole, board));

        if (current[2]) // Two Pair
            return new ProbResult("—", "—", "100%",
                calculateProb(3, hole, board), calculateProb(4, hole, board),
                calculateProb(5, hole, board), calculateProb(6, hole, board),
                calculateProb(7, hole, board), calculateProb(8, hole, board),
                calculateProb(9, hole, board));

        if (current[1]) // Pair
            return new ProbResult("—", "100%",
                calculateProb(2, hole, board), calculateProb(3, hole, board),
                calculateProb(4, hole, board), calculateProb(5, hole, board),
                calculateProb(6, hole, board), calculateProb(7, hole, board),
                calculateProb(8, hole, board), calculateProb(9, hole, board));

        // If we have no combinations yet, calculate all probabilities
        return calculateAllProbs(hole, board, highCardP);
    }

    private static String calculateProb(int combinationIndex, List<String> hole, List<String> board) {
        // Return "0%" if board is complete
        if (board.size() == 5) return "0%";

        // Calculate probability for this specific combination
        List<String> deck = liveDeck(hole, board);
        int r = 5 - board.size();
        int n = deck.size();
        long total = 0, found = 0;
        int[] idx = new int[r];
        for(int i=0; i<r; i++) idx[i]=i;

        while(true) {
            List<String> add = new ArrayList<>(r);
            for(int i:idx) add.add(deck.get(i));
            total++;
            boolean[] d = detect(hole, merge(board, add));
            if(d[combinationIndex]) found++;

            int t=r-1; while(t>=0 && idx[t]==n-r+t) t--;
            if(t<0) break;
            idx[t]++;
            for(int i=t+1;i<r;i++) idx[i]=idx[i-1]+1;
        }

        return String.format("%.2f%%", (double)found/total * 100);
    }

    private static ProbResult calculateAllProbs(List<String> hole, List<String> board, String highCardP) {
        if (board.size() == 5) return new ProbResult(highCardP, "0%", "0%", "0%", "0%", "0%", "0%", "0%", "0%", "0%");

        List<String> deck = liveDeck(hole, board);
        int r = 5 - board.size();
        long total=0, pair=0,twoPair=0,set=0,straight=0,flush=0,full=0,quads=0,sf=0,royal=0;
        int n = deck.size(), idx[] = new int[r];
        for(int i=0;i<r;i++) idx[i]=i;

        while(true){
            List<String> add = new ArrayList<>(r);
            for(int i:idx) add.add(deck.get(i));
            total++;
            boolean[] d = detect(hole, merge(board, add));
            if(d[1]) pair++;
            if(d[2]) twoPair++;
            if(d[3]) set++;
            if(d[4]) straight++;
            if(d[5]) flush++;
            if(d[6]) full++;
            if(d[7]) quads++;
            if(d[8]) sf++;
            if(d[9]) royal++;

            int t=r-1; while(t>=0 && idx[t]==n-r+t) t--;
            if(t<0) break;
            idx[t]++;
            for(int i=t+1;i<r;i++) idx[i]=idx[i-1]+1;
        }

        return new ProbResult(
                highCardP,
                String.format("%.2f%%", (double)pair/total * 100),
                String.format("%.2f%%", (double)twoPair/total * 100),
                String.format("%.2f%%", (double)set/total * 100),
                String.format("%.2f%%", (double)straight/total * 100),
                String.format("%.2f%%", (double)flush/total * 100),
                String.format("%.2f%%", (double)full/total * 100),
                String.format("%.2f%%", (double)quads/total * 100),
                String.format("%.2f%%", (double)sf/total * 100),
                String.format("%.2f%%", (double)royal/total * 100));
    }

    public static OppResult computeOpponents(List<String> heroHole, List<String> board, int activeOpp) {
        if(activeOpp == 0) return new OppResult("—","—","—","—","—","—","—","—","—");

        List<String> deck = liveDeck(heroHole, board);
        int totalPairs = deck.size() * (deck.size()-1) / 2;
        logger.debug("Computing opponents with deck size: {}, totalPairs: {}, activeOpp: {}", deck.size(), totalPairs, activeOpp);

        long pair=0,twoPair=0,set=0,straight=0,flush=0,full=0,quads=0,sf=0,royal=0;

        // Calculate opponent possibilities
        for(int i=0; i<deck.size(); i++)
            for(int j=i+1; j<deck.size(); j++){
                List<String> oppHole = List.of(deck.get(i), deck.get(j));
                boolean[] d = detect(oppHole, board);
                if(d[1]) pair++;
                if(d[2]) twoPair++;
                if(d[3]) set++;
                if(d[4]) straight++;
                if(d[5]) flush++;
                if(d[6]) full++;
                if(d[7]) quads++;
                if(d[8]) sf++;
                if(d[9]) royal++;
            }

        logger.debug("Raw counts - pair: {}, twoPair: {}, set: {}, straight: {}, flush: {}, full: {}, quads: {}, sf: {}, royal: {}",
            pair, twoPair, set, straight, flush, full, quads, sf, royal);

        // Calculate probabilities for at least one opponent having each combination
        DoubleUnaryOperator atLeastOne = p -> 1 - Math.pow(1-(double)p/totalPairs, activeOpp);

        double pairProb = atLeastOne.applyAsDouble(pair);
        logger.debug("Probability calculation example for pair: raw={}, probability={}", pair, pairProb);

        return new OppResult(
                String.format("%.2f%%", atLeastOne.applyAsDouble(pair) * 100),
                String.format("%.2f%%", atLeastOne.applyAsDouble(twoPair) * 100),
                String.format("%.2f%%", atLeastOne.applyAsDouble(set) * 100),
                String.format("%.2f%%", atLeastOne.applyAsDouble(straight) * 100),
                String.format("%.2f%%", atLeastOne.applyAsDouble(flush) * 100),
                String.format("%.2f%%", atLeastOne.applyAsDouble(full) * 100),
                String.format("%.2f%%", atLeastOne.applyAsDouble(quads) * 100),
                String.format("%.2f%%", atLeastOne.applyAsDouble(sf) * 100),
                String.format("%.2f%%", atLeastOne.applyAsDouble(royal) * 100));
    }

    /** detect(): index-map<br>
     * 0 unused, 1 pair,2 twoPair,3 set,4 straight,5 flush,6 full,7 quads,8 sf,9 royal */
    public static boolean[] detect(List<String> hole, List<String> board) {
        boolean[] res = new boolean[10];
        List<String> all = new ArrayList<>(hole);
        all.addAll(board);

        // Count ranks and suits
        int[] rankCnt = new int[13], suitCnt = new int[4];
        for(String c : all) {
            rankCnt[rank(c)]++;
            suitCnt[suit(c)]++;
        }

        // 1. Royal Flush (T-J-Q-K-A same suit)
        for (int s = 0; s < 4; s++) {
            if (suitCnt[s] >= 5) {
                boolean hasRoyal = true;
                boolean usesHoleCard = false;
                for (int r = 8; r <= 12; r++) {
                    boolean found = false;
                    for (String c : all) {
                        if (rank(c) == r && suit(c) == s) {
                            found = true;
                            if (hole.contains(c)) usesHoleCard = true;
                            break;
                        }
                    }
                    if (!found) {
                        hasRoyal = false;
                        break;
                    }
                }
                if (hasRoyal && usesHoleCard) {
                    res[9] = true;  // Royal
                    res[8] = true;  // St. Flush
                    res[5] = true;  // Flush
                    res[4] = true;  // Straight
                    return res;
                }
            }
        }

        // 2. Straight Flush (any 5 consecutive same suit)
        for (int s = 0; s < 4; s++) {
            if (suitCnt[s] >= 5) {
                boolean[] suitRanks = new boolean[13];
                boolean[] suitHoleRanks = new boolean[13];
                for (String c : all) {
                    if (suit(c) == s) {
                        suitRanks[rank(c)] = true;
                        if (hole.contains(c)) suitHoleRanks[rank(c)] = true;
                    }
                }

                // Check each possible straight starting position
                for (int start = 0; start <= 8; start++) {
                    boolean consecutive = true;
                    boolean usesHole = false;
                    for (int i = 0; i < 5; i++) {
                        if (!suitRanks[start + i]) {
                            consecutive = false;
                            break;
                        }
                        if (suitHoleRanks[start + i]) usesHole = true;
                    }
                    if (consecutive && usesHole) {
                        res[8] = true;  // St. Flush
                        res[5] = true;  // Flush
                        res[4] = true;  // Straight
                        return res;
                    }
                }

                // Check wheel straight flush (A-2-3-4-5)
                if (suitRanks[12] && suitRanks[0] && suitRanks[1] && suitRanks[2] && suitRanks[3] &&
                    (suitHoleRanks[12] || suitHoleRanks[0] || suitHoleRanks[1] || suitHoleRanks[2] || suitHoleRanks[3])) {
                    res[8] = true;  // St. Flush
                    res[5] = true;  // Flush
                    res[4] = true;  // Straight
                    return res;
                }
            }
        }

        // 3. Four of a Kind
        for (int r = 0; r < 13; r++) {
            if (rankCnt[r] == 4) {
                for (String c : hole) {
                    if (rank(c) == r) {
                        res[7] = true;  // Quads
                        res[3] = true;  // Set
                        res[2] = true;  // Two Pair
                        res[1] = true;  // Pair
                        return res;
                    }
                }
            }
        }

        // 4. Full House
        int trip = -1;
        boolean hasPair = false;
        for (int r = 0; r < 13; r++) {
            if (rankCnt[r] == 3) {
                for (String c : hole) {
                    if (rank(c) == r) {
                        trip = r;
                        break;
                    }
                }
            }
            if (rankCnt[r] == 2) {
                for (String c : hole) {
                    if (rank(c) == r) {
                        hasPair = true;
                        break;
                    }
                }
            }
        }
        if (trip >= 0 && hasPair) {
            res[6] = true;  // Full House
            res[3] = true;  // Set
            res[1] = true;  // Pair
            return res;
        }

        // 5. Flush (5+ cards same suit)
        for (int s = 0; s < 4; s++) {
            if (suitCnt[s] >= 5) {
                for (String c : hole) {
                    if (suit(c) == s) {
                        res[5] = true;  // Flush
                        return res;
                    }
                }
            }
        }

        // 6. Straight (5 consecutive ranks)
        boolean[] rankPresent = new boolean[13];
        boolean[] rankHole = new boolean[13];
        for (String c : all) {
            rankPresent[rank(c)] = true;
            if (hole.contains(c)) rankHole[rank(c)] = true;
        }

        // Regular straight
        for (int start = 0; start <= 8; start++) {
            boolean consecutive = true;
            boolean usesHole = false;
            for (int i = 0; i < 5; i++) {
                if (!rankPresent[start + i]) {
                    consecutive = false;
                    break;
                }
                if (rankHole[start + i]) usesHole = true;
            }
            if (consecutive && usesHole) {
                res[4] = true;  // Straight
                return res;
            }
        }

        // Wheel straight (A-2-3-4-5)
        if (rankPresent[12] && rankPresent[0] && rankPresent[1] && rankPresent[2] && rankPresent[3] &&
            (rankHole[12] || rankHole[0] || rankHole[1] || rankHole[2] || rankHole[3])) {
            res[4] = true;  // Straight
            return res;
        }

        // 7. Three of a Kind
        for (int r = 0; r < 13; r++) {
            if (rankCnt[r] == 3) {
                for (String c : hole) {
                    if (rank(c) == r) {
                        res[3] = true;  // Set
                        res[1] = true;  // Pair
                        return res;
                    }
                }
            }
        }

        // 8. Two Pair
        int pairs = 0;
        boolean usesHole = false;
        for (int r = 0; r < 13; r++) {
            if (rankCnt[r] == 2) {
                pairs++;
                for (String c : hole) {
                    if (rank(c) == r) {
                        usesHole = true;
                        break;
                    }
                }
            }
        }
        if (pairs >= 2 && usesHole) {
            res[2] = true;  // Two Pair
            res[1] = true;  // Pair
            return res;
        }

        // 9. Pair
        for (int r = 0; r < 13; r++) {
            if (rankCnt[r] == 2) {
                for (String c : hole) {
                    if (rank(c) == r) {
                        res[1] = true;  // Pair
                        return res;
                    }
                }
            }
        }

        return res;  // High card (no combinations)
    }

    /* ---------- helpers ---------- */
    private static boolean involvesHoleRank(List<String> hole,int[] rankCnt,int needed){
        for(String c:hole) if(rankCnt[rank(c)]>=needed) return true;
        return false;
    }
    private static boolean involvesHoleRankSingle(List<String> hole,int r){
        for(String c:hole) if(rank(c)==r) return true;
        return false;
    }
    private static boolean holeStreamHasSuit(List<String> hole,int s){
        for(String c:hole) if(suit(c)==s) return true;
        return false;
    }

    /** probability that max(hole) > all opponent hands */
    private static double highCardProb(List<String> hole, List<String> board, int opp) {
        if (opp == 0) return 1.0;

        int myMax = Math.max(rank(hole.get(0)), rank(hole.get(1)));

        // Count available higher cards
        boolean[] seen = new boolean[52];
        hole.forEach(c->seen[index(c)]=true);
        board.forEach(c->seen[index(c)]=true);
        int higher = 0;
        for(int r=myMax+1; r<13; r++)
            for(char s:"cdhs".toCharArray())
                if(!seen[index(""+ "23456789TJQKA".charAt(r) + s)])
                    higher++;

        int live = 52 - 2 - board.size();
        int oppCards = opp * 2;
        if(oppCards > live) return 0;

        // Probability that NO opponent gets ANY higher card
        double probNoHigher = 1.0;
        for(int i = 0; i < oppCards; i++) {
            probNoHigher *= (double)(live - higher - i) / (live - i);
        }

        return probNoHigher;
    }

    /* card utils */
    private static int rank(String c){ return "23456789TJQKA".indexOf(c.charAt(0)); }
    private static int suit(String c){ return "cdhs".indexOf(c.charAt(1)); }
    private static int index(String c){ return rank(c)*4 + suit(c); }

    public static List<String> liveDeck(List<String> hole,List<String> board){
        List<String>d=new ArrayList<>();
        String rs="23456789TJQKA", ss="cdhs";
        for(char R:rs.toCharArray())
            for(char S:ss.toCharArray()){
                String card=""+R+S;
                if(!hole.contains(card)&&!board.contains(card)) d.add(card);
            }
        return d;
    }
    private static List<String> merge(List<String>a,List<String>b){
        List<String> r=new ArrayList<>(a); r.addAll(b); return r;
    }

    private static BigInteger comb(int n,int k){
        if(k<0||k>n) return BigInteger.ZERO;
        if(k==0||k==n) return BigInteger.ONE;
        k=Math.min(k,n-k);
        BigInteger num=BigInteger.ONE, den=BigInteger.ONE;
        for(int i=1;i<=k;i++){
            num=num.multiply(BigInteger.valueOf(n-i+1));
            den=den.multiply(BigInteger.valueOf(i));
        }
        return num.divide(den);
    }

    public static ProbResult computeAll(List<String> hole,
                                        List<String> board,
                                        int activeOpp) {
        return computeHero(hole, board, activeOpp);
    }
}
