package com.filipblazekovic.totpy.gui.dialog;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.utils.GUICommon;
import com.filipblazekovic.totpy.utils.IOHandler;
import java.awt.Font;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class AboutDialog extends JDialog {

  public AboutDialog() {
    val aboutConfig = IOHandler.loadAboutConfig();

    val titleAndVersionlabel = new JLabel(aboutConfig.title() + " " + aboutConfig.version(), SwingConstants.CENTER);

    val font = titleAndVersionlabel.getFont();
    titleAndVersionlabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

    val licenceLabel = new JLabel("Licenced under " + aboutConfig.licence());
    val developedByLabel = new JLabel("Developed by " + aboutConfig.author());
    val sourceCodeLabel = new JLabel("Source code available at:");
    val sourceCodeLinkLabel = new JLabel(aboutConfig.sourceCodeUrl());

    val rootPane = new JPanel(new MigLayout("wrap 1"));
    rootPane.setBorder(GUICommon.getRootBorder());

    rootPane.add(titleAndVersionlabel, "center");
    rootPane.add(new JSeparator());
    rootPane.add(licenceLabel);
    rootPane.add(developedByLabel);
    rootPane.add(sourceCodeLabel);
    rootPane.add(sourceCodeLinkLabel);

    this.setTitle(GUICommon.WINDOW_TITLE);
    this.setContentPane(rootPane);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(450, 200);
    this.setModal(true);
    this.setResizable(false);
    this.setLocationRelativeTo(Totpy.getInstance());
    this.setVisible(true);
  }

}
