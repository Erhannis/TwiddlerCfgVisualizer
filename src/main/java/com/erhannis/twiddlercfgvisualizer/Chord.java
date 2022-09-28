/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erhannis.twiddlercfgvisualizer;

import java.util.ArrayList;

/**
 *
 * @author erhannis
 */
public class Chord {
    // <editor-fold desc="Twiddler buttons">
    public boolean tbNum;
    public boolean tbA;
    public boolean tbE;
    public boolean tbSp;
    public boolean tbAlt;
    public boolean tbB;
    public boolean tbF;
    public boolean tbDel;
    public boolean tbCtrl;
    public boolean tbC;
    public boolean tbG;
    public boolean tbBs;
    public boolean tbShift;
    public boolean tbD;
    public boolean tbH;
    public boolean tbEnt;
    // </editor-fold>
    
    // <editor-fold desc="Send buttons">
    /** If this is set, the sb modifier flags are irrelevant. */
    public boolean sbIsStr;
    /** sbIsStr ? (the string index) : (the scan code) */
    public int     sbRemainder;
    
    public boolean sbLCtrl;
    public boolean sbLShift;
    public boolean sbLAlt;
    public boolean sbLGui;
    public boolean sbRCtrl;
    public boolean sbRShift;
    public boolean sbRAlt;
    public boolean sbRGui;
    // </editor-fold>
    
    public Chord() {
    }
    
    public static Chord fromInt(int tb, int sbMod, int sbMain) {
        Chord c = new Chord();
        
        c.tbNum       = ((tb & 0b0000000000000001) != 0);
        c.tbA         = ((tb & 0b0000000000000010) != 0);
        c.tbE         = ((tb & 0b0000000000000100) != 0);
        c.tbSp        = ((tb & 0b0000000000001000) != 0);
        c.tbAlt       = ((tb & 0b0000000000010000) != 0);
        c.tbB         = ((tb & 0b0000000000100000) != 0);
        c.tbF         = ((tb & 0b0000000001000000) != 0);
        c.tbDel       = ((tb & 0b0000000010000000) != 0);
        c.tbCtrl      = ((tb & 0b0000000100000000) != 0);
        c.tbC         = ((tb & 0b0000001000000000) != 0);
        c.tbG         = ((tb & 0b0000010000000000) != 0);
        c.tbBs        = ((tb & 0b0000100000000000) != 0);
        c.tbShift     = ((tb & 0b0001000000000000) != 0);
        c.tbD         = ((tb & 0b0010000000000000) != 0);
        c.tbH         = ((tb & 0b0100000000000000) != 0);
        c.tbEnt       = ((tb & 0b1000000000000000) != 0);
           
        c.sbLCtrl     = ((sbMod  & 0b00000001) != 0);
        c.sbLShift    = ((sbMod  & 0b00000010) != 0);
        c.sbLAlt      = ((sbMod  & 0b00000100) != 0);
        c.sbLGui      = ((sbMod  & 0b00001000) != 0);
        c.sbRCtrl     = ((sbMod  & 0b00010000) != 0);
        c.sbRShift    = ((sbMod  & 0b00100000) != 0);
        c.sbRAlt      = ((sbMod  & 0b01000000) != 0);
        c.sbRGui      = ((sbMod  & 0b10000000) != 0);
        
        c.sbIsStr     = ((sbMod  & 0b11111111) == 0b11111111);
        c.sbRemainder =  (sbMain & 0b11111111);
        
        return c;
    }

    public String getTbString() {
        String s = "";
        s += tbNum ? "N" : "";
        s += tbAlt ? "A" : "";
        s += tbCtrl ? "C" : "";
        s += tbShift ? "S" : "";
        
        s += " ";
        
        String t;
        
        t = "";
        t += tbSp ? "L" : "";
        t += tbE ? "M" : "";
        t += tbA ? "R" : "";
        if (t.length() > 1) {
            t = "("+t+")";
        }
        s += t;

        t = "";
        t += tbDel ? "L" : "";
        t += tbF ? "M" : "";
        t += tbB ? "R" : "";
        if (t.length() > 1) {
            t = "("+t+")";
        }
        s += t;
        
        t = "";
        t += tbBs ? "L" : "";
        t += tbG ? "M" : "";
        t += tbC ? "R" : "";
        if (t.length() > 1) {
            t = "("+t+")";
        }
        s += t;

        t = "";
        t += tbEnt ? "L" : "";
        t += tbH ? "M" : "";
        t += tbD ? "R" : "";
        if (t.length() > 1) {
            t = "("+t+")";
        }
        s += t;
        
        return s;
    }
    
    public String getSbString(ArrayList<String> stringTable) {
        if (sbIsStr) {
            return stringTable.get(sbRemainder);
        } else {
            return UsbHidKeys.scanCodeToString(sbLShift || sbRShift, sbRemainder);
        }
    }

    public String getString(ArrayList<String> stringTable) {
        return getTbString() + " -> " + getSbString(stringTable);
    }
}
