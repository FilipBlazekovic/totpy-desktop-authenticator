package com.filipblazekovic.totpy.gui.panel;

import com.filipblazekovic.totpy.utils.GUICommon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class ProgressPanel extends JPanel {

  private final JLabel textDescription;

  public ProgressPanel(String message) {

    val backgroundColor = new Color(40, 40, 40);
    val secondaryBackgroundColor = new Color(50, 48, 47);
    val accentColor = new Color(251, 241, 199);
    val progressBarDimension = new Dimension(400, 30);

    val progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setMinimumSize(progressBarDimension);
    progressBar.setPreferredSize(progressBarDimension);
    progressBar.setMaximumSize(progressBarDimension);
    progressBar.setBackground(secondaryBackgroundColor);
    progressBar.setForeground(accentColor);

    textDescription = new JLabel(message, SwingConstants.CENTER);
    val font = textDescription.getFont();
    textDescription.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
    textDescription.setForeground(accentColor);

    this.setLayout(new MigLayout("wrap 1, width 100%, alignx center, aligny center"));
    this.setBorder(GUICommon.getSectionBorder());
    this.setBackground(backgroundColor);

    this.add(progressBar, "alignx center");
    this.add(new JSeparator());
    this.add(textDescription, "alignx center");
  }

  public void setMessage(String message) {
    textDescription.setText(message);
  }

}
