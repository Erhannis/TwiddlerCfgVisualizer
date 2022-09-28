/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erhannis.twiddlercfgvisualizer;

import com.google.common.io.LittleEndianDataInputStream;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author erhannis
 */
public class TwiddlerConfig {
    public int version;
    public int optionsA;

    public boolean oaKeyRepeat   ;
    public boolean oaDirectKey   ;
    public boolean oaJoyLeftClick;
    public boolean oaBTOff       ;
    public boolean oaStickyNum   ;
    public boolean oaNA1         ;
    public boolean oaNA2         ;
    public boolean oaStickyShift ;

    public int nChords           ;
    public int sleepTimeout      ;
    public int leftMouse         ;
    public int middleMouse       ;
    public int rightMouse        ;
    public int mouseAcceleration ;
    public int repeatDelay       ;
    public int optionsB          ;
    public int optionsC          ;
    public boolean ocHaptic      ;

    public ArrayList<Chord> chords;
    public ArrayList<String> strings;
    
    
    
    
    public TwiddlerConfig() {
    }
    
    public static TwiddlerConfig fromConfig(File f) throws IOException {
        TwiddlerConfig tc = new TwiddlerConfig();
        tc.readConfig(f);
        return tc;
    }
    
    public void readConfig(File f) throws FileNotFoundException, IOException {
        try (LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new FileInputStream(f))) { // Relies on DIS being big-endian
            // Read header
            this.version = dis.read();
            this.optionsA = dis.read();
            this.oaKeyRepeat    = ((optionsA & 0b00000001) != 0);
            this.oaDirectKey    = ((optionsA & 0b00000010) != 0);
            this.oaJoyLeftClick = ((optionsA & 0b00000100) != 0);
            this.oaBTOff        = ((optionsA & 0b00001000) != 0);
            this.oaStickyNum    = ((optionsA & 0b00010000) != 0);
            this.oaNA1          = ((optionsA & 0b00100000) != 0);
            this.oaNA2          = ((optionsA & 0b01000000) != 0);
            this.oaStickyShift  = ((optionsA & 0b10000000) != 0);
            this.nChords           = dis.readUnsignedShort();
            this.sleepTimeout      = dis.readUnsignedShort(); // Seconds
            this.leftMouse         = dis.readUnsignedShort();
            this.middleMouse       = dis.readUnsignedShort();
            this.rightMouse        = dis.readUnsignedShort();
            this.mouseAcceleration = dis.read();
            this.repeatDelay       = dis.read();
            this.optionsB          = dis.read();
            this.optionsC          = dis.read();
            this.ocHaptic          = ((optionsC & 0b00000001) != 0);
            
            // Chord table
            this.chords = new ArrayList<>();
            for (int i = 0; i < nChords; i++) {
                chords.add(Chord.fromInt(dis.readUnsignedShort(), dis.readUnsignedByte(), dis.readUnsignedByte()));
            }
            
            // String table
            this.strings = new ArrayList<>();
            int maxStringIndex = -1;
            for (int i = 0; i < nChords; i++) {
                Chord c = chords.get(i);
                if (c.sbIsStr) {
                    maxStringIndex = Math.max(maxStringIndex, c.sbRemainder);
                }
            }
            for (int i = 0; i <= maxStringIndex; i++) {
                dis.readInt();
            }
            for (int i = 0; i <= maxStringIndex; i++) {
                int len = (dis.readUnsignedShort() - 2) / 2;
                String s = "";
                for (int j = 0; j < len; j++) {
                    int modifier = dis.read(); //TODO Something
                    boolean lCtrl     = ((modifier  & 0b00000001) != 0);
                    boolean lShift    = ((modifier  & 0b00000010) != 0);
                    boolean lAlt      = ((modifier  & 0b00000100) != 0);
                    boolean lGui      = ((modifier  & 0b00001000) != 0);
                    boolean rCtrl     = ((modifier  & 0b00010000) != 0);
                    boolean rShift    = ((modifier  & 0b00100000) != 0);
                    boolean rAlt      = ((modifier  & 0b01000000) != 0);
                    boolean rGui      = ((modifier  & 0b10000000) != 0);
                    
                    int b = dis.read();
                    s += UsbHidKeys.scanCodeToString(lShift || rShift, b);
                }
                strings.add(s);
            }
        }
    }
}
