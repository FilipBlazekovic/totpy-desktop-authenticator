package com.filipblazekovic.totpy.gui.panel;

import com.filipblazekovic.totpy.model.shared.IssuerIcon;
import com.filipblazekovic.totpy.utils.IconStore;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

public class IconPanel extends JPanel {

  private static final Dimension DIMENSION = new Dimension(70, 70);

  private Image image;

  public IconPanel(IssuerIcon issuerIcon) {
    this.image = IconStore.get(issuerIcon);
    this.setPreferredSize(DIMENSION);
    this.setMaximumSize(DIMENSION);
    this.setMinimumSize(DIMENSION);
  }

  public void setIcon(IssuerIcon issuerIcon) {
    this.image = IconStore.get(issuerIcon);
    this.setPreferredSize(DIMENSION);
    this.setMaximumSize(DIMENSION);
    this.setMinimumSize(DIMENSION);

  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      g.drawImage(image, 5, 5, getWidth()-10, getHeight()-10, this);
    }
  }

}