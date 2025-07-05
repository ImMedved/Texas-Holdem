package com.kukharev.core;

import java.math.BigInteger;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

public final class CombinedProbabilityCalculator {

    /* ───── public API structures ───── */
    public record ProbResult(
            double highCard, double pair, double twoPair, double set, double straight,
            double flush, double fullHouse, double quads, double straightFlush, double royal) {}

    public record OppResult(
            double pair,double twoPair,double set,double straight,
            double flush,double fullHouse,double quads,
            double straightFlush,double royal){}

    private CombinedProbabilityCalculator() {}

    /* ────────────────────────────────────────────────────────────────
       1.  HERO: The exact probability of any combo using at least one of the player's hole cards.
    ──────────────────────────────────────────────────────────────── */
    public static ProbResult computeHero(List<String> hole, List<String> board, int activeOpp) {
        if (hole.size()!=2) throw new IllegalArgumentException("need exactly 2 hole cards");

        double highCardP = highCardProb(hole, board, activeOpp); // O(1)

        int r = 5 - board.size();                 // missing cards
        if (r == 0) {                             // showdown
            boolean[] made = detect(hole, board);
            return new ProbResult(highCardP,
                    made[1]?1:0, made[2]?1:0, made[3]?1:0, made[4]?1:0,
                    made[5]?1:0, made[6]?1:0, made[7]?1:0, made[8]?1:0, made[9]?1:0);
        }

        List<String> deck = liveDeck(hole, board);
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
                (double)pair/total, (double)twoPair/total, (double)set/total,
                (double)straight/total, (double)flush/total, (double)full/total,
                (double)quads/total, (double)sf/total, (double)royal/total);
    }

    /* ────────────────────────────────────────────────────────────────
       2.  OPPONENTS: the probability that >=1 active opponent will make the combination, given the current board.
    ──────────────────────────────────────────────────────────────── */
    public static OppResult computeOpponents(List<String> heroHole, List<String> board, int activeOpp){
        if(activeOpp==0) return new OppResult(0,0,0,0,0,0,0,0,0);

        List<String> deck = liveDeck(heroHole, board);        // карты, доступные чужим рукам
        int totalPairs = deck.size() * (deck.size()-1) / 2;
        long pair=0,twoPair=0,set=0,straight=0,flush=0,full=0,quads=0,sf=0,royal=0;

        for(int i=0;i<deck.size();i++)
            for(int j=i+1;j<deck.size();j++){
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

        double pPair =(double)pair/totalPairs,
                pTwo =(double)twoPair/totalPairs,
                pSet =(double)set/totalPairs,
                pStr =(double)straight/totalPairs,
                pFlu =(double)flush/totalPairs,
                pFull=(double)full/totalPairs,
                pQuad=(double)quads/totalPairs,
                pSf  =(double)sf/totalPairs,
                pRoy =(double)royal/totalPairs;

        DoubleUnaryOperator atLeastOne = p -> 1 - Math.pow(1-p, activeOpp);

        return new OppResult(
                atLeastOne.applyAsDouble(pPair),
                atLeastOne.applyAsDouble(pTwo),
                atLeastOne.applyAsDouble(pSet),
                atLeastOne.applyAsDouble(pStr),
                atLeastOne.applyAsDouble(pFlu),
                atLeastOne.applyAsDouble(pFull),
                atLeastOne.applyAsDouble(pQuad),
                atLeastOne.applyAsDouble(pSf),
                atLeastOne.applyAsDouble(pRoy));
    }

    /* ────────────────────────────────────────────────────────────────
       3.  AUXILIARY METHODS (detect, combinatorics, utils)
    ──────────────────────────────────────────────────────────────── */
    /** detect(): index-map<br>
     * 0 unused, 1 pair,2 twoPair,3 set,4 straight,5 flush,6 full,7 quads,8 sf,9 royal */
    public static boolean[] detect(List<String> hole, List<String> board){
        boolean[] res=new boolean[10];
        List<String> all = new ArrayList<>(hole); all.addAll(board);
        int[] rankCnt = new int[13], suitCnt=new int[4];
        for(String c:all){ rankCnt[rank(c)]++; suitCnt[suit(c)]++; }

        int pairs=0, trip=-1, quad=-1;
        for(int r=0;r<13;r++){
            if(rankCnt[r]==4) quad=r;
            if(rankCnt[r]==3) trip=r;
            if(rankCnt[r]==2) pairs++;
        }

        if(pairs>0 && involvesHoleRank(hole,rankCnt,2)) res[1]=true;
        if(pairs>=2&&involvesHoleRank(hole,rankCnt,2)) res[2]=true;
        if(trip!=-1&&involvesHoleRankSingle(hole,trip)) res[3]=true;
        if(trip!=-1&&pairs>0&&(involvesHoleRankSingle(hole,trip)||involvesHoleRank(hole,rankCnt,2))) res[6]=true;
        if(quad!=-1&&involvesHoleRankSingle(hole,quad)) res[7]=true;

        for(int s=0;s<4;s++) if(suitCnt[s]>=5 && holeStreamHasSuit(hole,s)) res[5]=true;

        boolean[] rankPresent=new boolean[13], rankHole=new boolean[13];
        for(String c:all) rankPresent[rank(c)]=true;
        for(String c:hole) rankHole[rank(c)]=true;

        for(int st=0;st<=8;st++){
            boolean ok=true, hasHole=false;
            for(int off=0;off<5;off++){
                int r=st+off;
                if(!rankPresent[r]){ok=false;break;}
                if(rankHole[r]) hasHole=true;
            }
            if(ok&&hasHole) res[4]=true;
        }
        // wheel straight
        if(rankPresent[12]&&rankPresent[0]&&rankPresent[1]&&rankPresent[2]&&rankPresent[3]
                && (rankHole[12]||rankHole[0]||rankHole[1]||rankHole[2]||rankHole[3])) res[4]=true;

        boolean[][] suitRank=new boolean[4][13], suitHole=new boolean[4][13];
        for(String c:all) suitRank[suit(c)][rank(c)]=true;
        for(String c:hole) suitHole[suit(c)][rank(c)]=true;

        int[] royalR={8,9,10,11,12};
        for(int s=0;s<4;s++){
            boolean ok=true, hasHole=false;
            for(int r:royalR){ if(!suitRank[s][r]){ok=false;break;} if(suitHole[s][r]) hasHole=true;}
            if(ok&&hasHole){ res[8]=true; res[9]=true; }

            for(int st=0;st<=8;st++){
                ok=true; hasHole=false;
                for(int off=0;off<5;off++){
                    int r=st+off;
                    if(!suitRank[s][r]){ok=false;break;}
                    if(suitHole[s][r]) hasHole=true;
                }
                if(ok&&hasHole) res[8]=true;
            }
            // wheel sf
            if(suitRank[s][12]&&suitRank[s][0]&&suitRank[s][1]&&suitRank[s][2]&&suitRank[s][3]
                    && (suitHole[s][12]||suitHole[s][0]||suitHole[s][1]||suitHole[s][2]||suitHole[s][3])) res[8]=true;
        }
        return res;
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

    /** probability that max(hole) > any pocket hand of opponents */
    private static double highCardProb(List<String> hole, List<String> board, int opp){
        int myMax = Math.max(rank(hole.get(0)), rank(hole.get(1)));
        boolean[] seen=new boolean[52];
        hole.forEach(c->seen[index(c)]=true);
        board.forEach(c->seen[index(c)]=true);
        int live = 52 - 2 - board.size();
        int higher=0;
        for(int r=myMax+1;r<13;r++)
            for(char s:"cdhs".toCharArray())
                if(!seen[index(""+ "23456789TJQKA".charAt(r) + s)]) higher++;

        int oppCards = opp*2;
        if(oppCards>live) return 0;

        BigInteger fav = comb(live - higher, oppCards);
        BigInteger all = comb(live, oppCards);
        return fav.doubleValue()/all.doubleValue();
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