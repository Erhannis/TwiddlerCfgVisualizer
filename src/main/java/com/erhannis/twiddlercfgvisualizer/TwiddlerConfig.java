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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
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
        ByteBuffer bb = ByteBuffer.wrap(Files.readAllBytes(f.toPath()));
        bb.order(ByteOrder.LITTLE_ENDIAN);
        // Read header
        this.version = bb.get() & 0xFF;
        this.optionsA = bb.get() & 0xFF;
        this.oaKeyRepeat    = ((optionsA & 0b00000001) != 0);
        this.oaDirectKey    = ((optionsA & 0b00000010) != 0);
        this.oaJoyLeftClick = ((optionsA & 0b00000100) != 0);
        this.oaBTOff        = ((optionsA & 0b00001000) != 0);
        this.oaStickyNum    = ((optionsA & 0b00010000) != 0);
        this.oaNA1          = ((optionsA & 0b00100000) != 0);
        this.oaNA2          = ((optionsA & 0b01000000) != 0);
        this.oaStickyShift  = ((optionsA & 0b10000000) != 0);
        this.nChords           = bb.getShort() & 0xFFFF;
        this.sleepTimeout      = bb.getShort() & 0xFFFF; // Seconds
        this.leftMouse         = bb.getShort() & 0xFFFF;
        this.middleMouse       = bb.getShort() & 0xFFFF;
        this.rightMouse        = bb.getShort() & 0xFFFF;
        this.mouseAcceleration = bb.get() & 0xFF;
        this.repeatDelay       = bb.get() & 0xFF;
        this.optionsB          = bb.get() & 0xFF;
        this.optionsC          = bb.get() & 0xFF;
        this.ocHaptic          = ((optionsC & 0b00000001) != 0);

        // Chord table
        this.chords = new ArrayList<>();
        this.strings = new ArrayList<>();
        for (int i = 0; i < nChords; i++) {
            Chord c = Chord.fromInt(bb.getShort() & 0xFFFF, bb.get() & 0xFF, bb.get() & 0xFF);
            chords.add(c);
            if (c.sbIsStr) {
                while (this.strings.size() < (c.sbRemainder+1)) {
                    this.strings.add(null);
                }
                if (this.strings.get(c.sbRemainder) == null) {
                    int prevPos = bb.position();
                    
                    bb.position(16 + 4*nChords + 4*c.sbRemainder);
                    int stringPos = bb.getInt();
                    bb.position(stringPos);
                    
                    int len = ((bb.getShort() & 0xFFFF) - 2) / 2;
                    String s = "";
                    for (int j = 0; j < len; j++) {
                        int modifier = bb.get() & 0xFF; //TODO Something
                        boolean lCtrl     = ((modifier  & 0b00000001) != 0);
                        boolean lShift    = ((modifier  & 0b00000010) != 0);
                        boolean lAlt      = ((modifier  & 0b00000100) != 0);
                        boolean lGui      = ((modifier  & 0b00001000) != 0);
                        boolean rCtrl     = ((modifier  & 0b00010000) != 0);
                        boolean rShift    = ((modifier  & 0b00100000) != 0);
                        boolean rAlt      = ((modifier  & 0b01000000) != 0);
                        boolean rGui      = ((modifier  & 0b10000000) != 0);

                        int b = bb.get() & 0xFF;
                        s += UsbHidKeys.scanCodeToString(lShift || rShift, b);
                    }
                    this.strings.set(c.sbRemainder, s);
                    
                    bb.position(prevPos);
                }
            }
        }
    }
}
