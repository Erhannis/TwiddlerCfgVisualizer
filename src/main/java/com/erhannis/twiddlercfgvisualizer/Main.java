/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.erhannis.twiddlercfgvisualizer;

import com.erhannis.mathnstuff.MeUtils;
import com.erhannis.mathnstuff.components.ImageFrame;
import com.erhannis.mathnstuff.components.ImagePanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author erhannis
 */
public class Main extends javax.swing.JFrame {
    private HashMap<Integer, ImageFrame> renderFrames = new HashMap<>();
    
    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();
        
        jPanel3.setDropTarget(new DropTarget() { //TODO Kindof a weird configuration if we're not rendering anything there
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        loadFile(file);
                        break; // only support one
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void loadFile(File f) throws IOException {
        System.out.println("file: " + f);
        
//        TwiddlerConf tc;
//        if (f.getName().endsWith(".csv") || f.getName().endsWith(".tsv")) {
//            tc = TwiddlerConf.parseFromTextFile(f.toPath());
//        } else {
//            tc = TwiddlerConf.parseFromBinaryFile(f.toPath());
//        }
        
//        Cfg tc = Cfg.read(f);
        
        TwiddlerConfig tc = TwiddlerConfig.fromConfig(f);

        System.out.println("conf: " + tc);
        for (Chord c : tc.chords) {
            System.out.println(c.getString(tc.strings));
        }
        System.out.println("loaded");
        
        for (ImageFrame frame : renderFrames.values()) {
            if (frame != null) {
                frame.setVisible(false);
            }
        }
        
        for (int i = 0b0000; i <= 0b1111; i++) {
            boolean num   = ((i & 0b0001) != 0);
            boolean ctrl  = ((i & 0b0010) != 0);
            boolean alt   = ((i & 0b0100) != 0);
            boolean shift = ((i & 0b1000) != 0);
            
            List<Chord> subset = tc.chords.stream().filter((c) -> (c.tbNum == num && c.tbCtrl == ctrl && c.tbAlt == alt && c.tbShift == shift)).collect(Collectors.toList());
            if (!subset.isEmpty()) {
                ImageFrame frame = demandRenderFrame(i);
                frame.setImage(renderChords(subset, tc.strings));
                frame.setVisible(true);
            }
        }
    }

    private static final float FONT = 10;
    private static final Color CO = MeUtils.interpolateColors(Color.GRAY, Color.WHITE, 0.5);
    private static final Color CL = MeUtils.interpolateColors(Color.RED, Color.WHITE, 0.5);
    private static final Color CM = MeUtils.interpolateColors(Color.BLUE, Color.WHITE, 0.5);
    private static final Color CR = MeUtils.interpolateColors(Color.GREEN, Color.WHITE, 0.5);
    private static final Color[] COLORS = {CO, CL, CM, CR};
    private static final double SX = 1600;
    private static final double SY = 600;
    private static final double DY = SY / 4;
    
    private BufferedImage renderChords(List<Chord> chords, ArrayList<String> stringTable) {
        double DX = SX / (4*4*4*3);
        
        BufferedImage bi = new BufferedImage((int)SX, (int)SY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, (int)SX, (int)SY);
        g.setFont(g.getFont().deriveFont(FONT));
        
        subRender(g, 0, 0, SX / 4, 4);
        
        int multichords = 0;
        g.setColor(Color.BLACK);
        AffineTransform at = (AffineTransform)g.getTransform().clone();
        for (Chord c : chords) {
            if (c.isMultichord()) {
                multichords++;
                continue;
            }
            
            double x = getChordX(c) * DX;
            double y = (c.getLowestRow()-1) * DY;
            
            g.translate(x+1, y);
            g.rotate(Math.PI/2);
            g.drawString(c.getString(stringTable), 0, 0);
            g.setTransform((AffineTransform)at.clone());
            
        }
        System.out.println("Multichords unhandled: " + multichords);
        
        return bi;
    }
    
    private int getChordX(Chord c) { // Does not work for multichords
        int x = 0;

        if (c.tbSp) x += 1*4*4*3;
        if (c.tbE) x += 2*4*4*3;
        if (c.tbA) x += 3*4*4*3;
        
        if (c.tbDel) x += 1*4*3;
        if (c.tbF) x += 2*4*3;
        if (c.tbB) x += 3*4*3;
        
        if (c.tbBs) x += 1*3;
        if (c.tbG) x += 2*3;
        if (c.tbC) x += 3*3;
        
        if (c.tbEnt) x += 0;
        if (c.tbH) x += 1;
        if (c.tbD) x += 2;
        
        return x;
    }
    
    private void subRender(Graphics2D g, double left, double top, double dx, int levels) {
        if (levels == 0) {
            return;
        }
        if (levels > 1) {
            for (int i = 0; i < 4; i++) {
                g.setColor(COLORS[i]);
                g.fillRect((int)(left+i*dx), (int)top, (int)dx+1, (int)DY);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                g.setColor(COLORS[i+1]);
                g.fillRect((int)(left+i*dx), (int)top, (int)dx+1, (int)DY);
            }
        }
        for (int i = 0; i < 4; i++) {
            double div = (levels == 2) ? 3 : 4;
            subRender(g, left + dx*i, top+DY, dx/div, levels-1);
        }
    }
    
    private ImageFrame demandRenderFrame(int modifiers) {
        ImageFrame frame = renderFrames.get(modifiers);
        if (frame == null) {
            frame = new ImageFrame();
            
            boolean num   = ((modifiers & 0b0001) != 0);
            boolean ctrl  = ((modifiers & 0b0010) != 0);
            boolean alt   = ((modifiers & 0b0100) != 0);
            boolean shift = ((modifiers & 0b1000) != 0);

            frame.setTitle("Modifiers: [" + (num ? "N" : "") + (ctrl ? "C" : "") + (alt ? "A" : "") + (shift ? "S" : "") + "]");
            
            renderFrames.put(modifiers, frame);
        }
        return frame;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Twiddler Cfg Visualizer");

        jLabel1.setText("Drag a v5 Twiddler .cfg file here to render it.");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(133, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(274, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
