/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.imd.jv.Memory;

import br.imd.jv.Simulator.Configuration;

/**
 *
 * @author João Victor
 */
public class Line {

    private int tag;
    private int hits;
    private Configuration config;
    private Block block;

    public Line(Configuration config) {
        this.block = new Block(config);
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

}
