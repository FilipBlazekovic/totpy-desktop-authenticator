package com.filipblazekovic.totpy.gui.screen;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.utils.BGWorkers;
import com.filipblazekovic.totpy.utils.GUICommon;
import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class LoginScreen extends JPanel {

  private final LoginScreen referenceToThis;

  private final JPanel rootPane;
  private final JPanel optionalPanel;

  public LoginScreen() {
    referenceToThis = this;

    val enterPasswordLabel = new JLabel("Enter password:");
    val enterPasswordField = new JPasswordField();
    val loadedKeyFileLabel = new JLabel("Selected file:");
    val loadedKeyFileField = new JTextField(50);

    loadedKeyFileField.setEditable(false);

    val loadKeyFileButton = new JButton("Load Keyfile");
    loadKeyFileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    loadKeyFileButton.addActionListener(a -> {
      val fileChooser = new JFileChooser();
      fileChooser.setMultiSelectionEnabled(false);
      if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        loadedKeyFileField.setText(fileChooser.getSelectedFile().getPath());
      }
    });

    val loginButton = new JButton("LOGIN");
    loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

    loginButton.addActionListener(a -> {
      Totpy.getInstance().showProgressPane("Checking login credentials...");
      BGWorkers.login(
          referenceToThis,
          Totpy.getCurrentConfig().usesKeyFile2Fa() ? loadedKeyFileField.getText() : null,
          enterPasswordField.getPassword()
      );
      enterPasswordField.setText("");
    });

    rootPane = new JPanel(new MigLayout());
    rootPane.setBorder(GUICommon.getSectionBorder());

    val formPanel = new JPanel(new MigLayout("wrap 1"));
    formPanel.setBorder(GUICommon.getSectionBorder());

    formPanel.add(enterPasswordLabel, "width 100%");
    formPanel.add(enterPasswordField, "width 100%");

    val filePanel = new JPanel(new MigLayout("wrap 2"));
    filePanel.add(loadedKeyFileLabel);
    filePanel.add(loadedKeyFileField);

    optionalPanel = new JPanel(new MigLayout("wrap 1"));
    optionalPanel.add(new JSeparator());
    optionalPanel.add(loadKeyFileButton, "width 100%");
    optionalPanel.add(new JSeparator());
    optionalPanel.add(filePanel, "width 100%");
    formPanel.add(optionalPanel, "width 100%");

    rootPane.add(formPanel, "alignx center, width 100%, wrap");
    rootPane.add(new JSeparator(), "wrap");
    rootPane.add(loginButton, "align center, width 100%, wrap");

    rootPane.setSize(450, 200);

    this.setLayout(new MigLayout("wrap 1, alignx center, aligny center, width 100%"));
    this.add(rootPane);
  }

  public void setUsesKeyFileProtection(boolean usesKeyFileProtection) {
    optionalPanel.setVisible(usesKeyFileProtection);
    rootPane.setSize(450, usesKeyFileProtection ? 270 : 200);
  }

}
