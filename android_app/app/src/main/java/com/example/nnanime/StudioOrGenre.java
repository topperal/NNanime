package com.example.nnanime;

public class StudioOrGenre implements Comparable<StudioOrGenre>{

    public String name;
    public double score;
    public double dispersia;

    public StudioOrGenre(String name, double score,  double dispersia){
        this.score = score;
        this.name = name;
        this.dispersia = dispersia;
    }
    @Override
    public String toString() {
        return name;
    }



    @Override
    public int compareTo(StudioOrGenre studioOrGenre) {
        return this.name.compareTo(studioOrGenre.name);
    }
}
