import com.kukharev.core.HeroProbabilityCalculator;
import com.kukharev.model.HandProbabilities;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class RandomCombinationTests {

    private static final String[] RANKS = {"2","3","4","5","6","7","8","9","T","J","Q","K","A"};
    private static final char[] SUITS = {'H','D','C','S'};
    private static final int SIMS = 100000;
    private static final double TOL = 4.0;

    private void runRandomTest(long seed) throws Exception {
        Random rnd = new Random(seed);
        List<String> deck = new ArrayList<>();
        for (String r : RANKS) for (char s : SUITS) deck.add(r + s);
        Collections.shuffle(deck, rnd);
        List<String> hole = new ArrayList<>();
        hole.add(deck.remove(0));
        hole.add(deck.remove(0));
        int[] sizes = {0,3,4,5};
        int boardSize = sizes[rnd.nextInt(sizes.length)];
        List<String> board = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) board.add(deck.remove(0));

        Map<String,Integer> simCounts = new HashMap<>();
        simCounts.put("high",0);
        simCounts.put("pair",0);
        simCounts.put("two",0);
        simCounts.put("set",0);
        simCounts.put("straight",0);
        simCounts.put("flush",0);
        simCounts.put("full",0);
        simCounts.put("quads",0);
        simCounts.put("sflush",0);
        simCounts.put("royal",0);

        for (int i = 0; i < SIMS; i++) {
            List<String> tmpDeck = new ArrayList<>(deck);
            Collections.shuffle(tmpDeck, rnd);
            List<String> extra = new ArrayList<>();
            for (int k = 0; k < 5 - boardSize; k++) extra.add(tmpDeck.remove(k));
            List<String> fullBoard = new ArrayList<>(board);
            fullBoard.addAll(extra);
            categorize(hole, fullBoard, simCounts);
        }

        double onePair = pct(simCounts.get("pair"));
        double twoPair = pct(simCounts.get("two"));
        double set = pct(simCounts.get("set"));
        double straight = pct(simCounts.get("straight"));
        double flush = pct(simCounts.get("flush"));
        double fullHouse = pct(simCounts.get("full"));
        double quads = pct(simCounts.get("quads"));
        double straightFlush = pct(simCounts.get("sflush"));
        double royal = pct(simCounts.get("royal"));
        double high = pct(simCounts.get("high"));

        String methodName = boardSize == 0 ? "calculatePreflop" :
                boardSize == 3 ? "calculateFlop" :
                        boardSize == 4 ? "calculateTurn" : "calculateRiver";
        Method m = HeroProbabilityCalculator.class.getMethod(methodName, List.class, List.class, int.class);
        HandProbabilities hp = (HandProbabilities) m.invoke(null, hole, board, 1);

        assertEquals(onePair, hp.getOnePair(), TOL);
        assertEquals(twoPair, hp.getTwoPair(), TOL);
        assertEquals(set, hp.getSet(), TOL);
        assertEquals(straight, hp.getStraight(), TOL);
        assertEquals(flush, hp.getFlush(), TOL);
        assertEquals(fullHouse, hp.getFullHouse(), TOL);
        assertEquals(quads, hp.getQuads(), TOL);
        assertEquals(straightFlush, hp.getStraightFlush(), TOL);
        assertEquals(royal, hp.getRoyalFlush(), TOL);
        assertEquals(high, hp.getHighCard(), TOL);
    }

    private double pct(int hits) { return 100.0 * hits / SIMS; }

    private void categorize(List<String> hole, List<String> board, Map<String,Integer> cnt) {
        List<String> all = new ArrayList<>();
        all.addAll(hole); all.addAll(board);
        Map<Character,Integer> ranks = new HashMap<>();
        Map<Character,Integer> suits = new HashMap<>();
        for (String c : all) {
            char r = c.charAt(0), s = c.charAt(1);
            ranks.put(r, ranks.getOrDefault(r,0)+1);
            suits.put(s, suits.getOrDefault(s,0)+1);
        }
        boolean flush = false; char flushSuit = 0;
        for (Map.Entry<Character,Integer> e : suits.entrySet()) if (e.getValue() >=5) { flush=true; flushSuit=e.getKey(); break; }
        boolean straight = false, straightFlush=false, royal=false;
        int[] rankMask = new int[15];
        for (char r : ranks.keySet()) rankMask[rankIndex(r)] = 1;
        rankMask[14] = rankMask[1];
        for (int i=1;i<=10;i++) if (rankMask[i]==1 && rankMask[i+1]==1 && rankMask[i+2]==1 && rankMask[i+3]==1 && rankMask[i+4]==1) straight=true;
        if (flush) {
            List<Integer> flranks = new ArrayList<>();
            for (String c:all) if (c.charAt(1)==flushSuit) flranks.add(rankIndex(c.charAt(0)));
            int[] fm = new int[15];
            for (int v:flranks) fm[v]=1; fm[14]=fm[1];
            for(int i=1;i<=10;i++) if(fm[i]==1&&fm[i+1]==1&&fm[i+2]==1&&fm[i+3]==1&&fm[i+4]==1){straightFlush=true; if(i==10) royal=true; break;}
        }
        int pairs=0, triple=0, quad=0;
        for(int v:ranks.values()){ if(v==2) pairs++; else if(v==3) triple++; else if(v==4) quad++; }
        boolean full = (triple>=1 && (pairs>=1 || triple>1));
        boolean set = triple>=1 || quad>=1;
        boolean two = pairs>=2 || full;
        boolean pair = pairs>=1 || set || two;
        if(royal)   inc(cnt,"royal");
        if(straightFlush) inc(cnt,"sflush");
        if(flush)   inc(cnt,"flush");
        if(straight)inc(cnt,"straight");
        if(quad==1) inc(cnt,"quads");
        if(full)    inc(cnt,"full");
        if(set)     inc(cnt,"set");
        if(two)     inc(cnt,"two");
        if(pair)    inc(cnt,"pair");
        if(!pair && !straight && !flush) inc(cnt,"high");
    }

    private int rankIndex(char r) {
        switch (r) {case '2':return 2;case '3':return 3;case '4':return 4;case '5':return 5;case '6':return 6;case '7':return 7;case '8':return 8;case '9':return 9;case 'T':return 10;case 'J':return 11;case 'Q':return 12;case 'K':return 13;default:return 14;}
    }

    private void inc(Map<String,Integer> m,String k){m.put(k,m.get(k)+1);}

    @Test public void randomTest1() throws Exception { runRandomTest(1001); }
    @Test public void randomTest2() throws Exception { runRandomTest(1002); }
    @Test public void randomTest3() throws Exception { runRandomTest(1003); }
    @Test public void randomTest4() throws Exception { runRandomTest(1004); }
    @Test public void randomTest5() throws Exception { runRandomTest(1005); }
    @Test public void randomTest6() throws Exception { runRandomTest(1006); }
    @Test public void randomTest7() throws Exception { runRandomTest(1007); }
    @Test public void randomTest8() throws Exception { runRandomTest(1008); }
    @Test public void randomTest9() throws Exception { runRandomTest(1009); }
    @Test public void randomTest10() throws Exception { runRandomTest(1010); }
    @Test public void randomTest11() throws Exception { runRandomTest(1011); }
    @Test public void randomTest12() throws Exception { runRandomTest(1012); }
    @Test public void randomTest13() throws Exception { runRandomTest(1013); }
    @Test public void randomTest14() throws Exception { runRandomTest(1014); }
    @Test public void randomTest15() throws Exception { runRandomTest(1015); }
    @Test public void randomTest16() throws Exception { runRandomTest(1016); }
    @Test public void randomTest17() throws Exception { runRandomTest(1017); }
    @Test public void randomTest18() throws Exception { runRandomTest(1018); }
    @Test public void randomTest19() throws Exception { runRandomTest(1019); }
    @Test public void randomTest20() throws Exception { runRandomTest(1020); }
    @Test public void randomTest21() throws Exception { runRandomTest(1021); }
    @Test public void randomTest22() throws Exception { runRandomTest(1022); }
    @Test public void randomTest23() throws Exception { runRandomTest(1023); }
    @Test public void randomTest24() throws Exception { runRandomTest(1024); }
    @Test public void randomTest25() throws Exception { runRandomTest(1025); }
}
