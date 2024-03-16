package com.filipblazekovic.totpy.gui.panel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class ShowQRCodePanel extends JPanel {

  private static final Dimension DIMENSION = new Dimension(250, 250);

  private final BufferedImage image;

  public ShowQRCodePanel(BufferedImage image) {
    this.image = image;
    this.setMinimumSize(DIMENSION);
    this.setPreferredSize(DIMENSION);
    this.setMaximumSize(DIMENSION);
    this.setSize(DIMENSION);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
  }

}
