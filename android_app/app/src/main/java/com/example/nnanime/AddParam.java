package com.example.nnanime;

public class AddParam implements Comparable<AddParam>{
    public String name;
    public double score;
    public double dispersia;

    public AddParam(String name, double score,  double dispersia){
        this.score = score;
        this.name = name;
        this.dispersia = dispersia;
    }
    @Override
    public String toString() {
        return name;
    }


    @Override
    public int compareTo(AddParam addParam) {
        return this.name.compareTo(addParam.name);
    }
}
