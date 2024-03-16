package com.filipblazekovic.totpy.gui.screen;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.utils.BGWorkers;
import com.filipblazekovic.totpy.utils.GUICommon;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class InitializationScreen extends JPanel {

  private final InitializationScreen referenceToThis;

  public InitializationScreen() {
    referenceToThis = this;

    val enterPasswordLabel = new JLabel("Enter password:");
    val enterPasswordField = new JPasswordField();
    val confirmPasswordLabel = new JLabel("Confirm password:");
    val confirmPasswordField = new JPasswordField();

    val useKeyFileLabel = new JLabel("Use Keyfile");
    val useKeyFileButton = new JToggleButton("OFF");
    useKeyFileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    useKeyFileButton.setSelected(false);

    val toggleButtonPanel = new JPanel(new MigLayout("wrap 2"));
    toggleButtonPanel.add(useKeyFileButton);
    toggleButtonPanel.add(useKeyFileLabel);

    val loadKeyFileButton = new JButton("Load Keyfile");
    loadKeyFileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    loadKeyFileButton.setVisible(false);

    val loadedKeyFileLabel = new JLabel("Selected file:");
    val loadedKeyFileField = new JTextField(50);
    loadedKeyFileField.setEditable(false);

    val initializeButton = new JButton("INITIALIZE");
    initializeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

    val filePanel = new JPanel(new MigLayout("width 100%"));
    filePanel.add(loadedKeyFileLabel);
    filePanel.add(loadedKeyFileField);
    filePanel.setVisible(false);

    useKeyFileButton.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        loadKeyFileButton.setVisible(true);
        filePanel.setVisible(true);
        useKeyFileButton.setText("ON");
        return;
      }
      loadKeyFileButton.setVisible(false);
      filePanel.setVisible(false);
      useKeyFileButton.setText("OFF");
      loadedKeyFileField.setText("");
    });

    loadKeyFileButton.addActionListener(a -> {
      val fileChooser = new JFileChooser();
      fileChooser.setMultiSelectionEnabled(false);
      if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        loadedKeyFileField.setText(fileChooser.getSelectedFile().getPath());
      }
    });

    initializeButton.addActionListener(e -> {
      Totpy.getInstance().showProgressPane("Initializing...");
      BGWorkers.init(
          referenceToThis,
          useKeyFileButton.isSelected() ? loadedKeyFileField.getText() : null,
          enterPasswordField.getPassword(),
          confirmPasswordField.getPassword()
      );
    });

    val rootPane = new JPanel(new MigLayout());
    rootPane.setBorder(GUICommon.getSectionBorder());

    val formPanel = new JPanel(new MigLayout("wrap 1"));
    formPanel.setBorder(GUICommon.getSectionBorder());

    formPanel.add(enterPasswordLabel, "width 100%");
    formPanel.add(enterPasswordField, "width 100%");
    formPanel.add(confirmPasswordLabel, "width 100%");
    formPanel.add(confirmPasswordField, "width 100%");
    formPanel.add(new JSeparator());
    formPanel.add(toggleButtonPanel, "width 100%");
    formPanel.add(loadKeyFileButton, "width 100%");
    formPanel.add(filePanel, "width 100%");

    rootPane.add(formPanel, "alignx center, width 100%, wrap");
    rootPane.add(new JSeparator(), "wrap");
    rootPane.add(initializeButton, "align center, wrap");

    rootPane.setSize(450, 550);

    this.setLayout(new MigLayout("wrap 1, alignx center, aligny center, width 100%"));
    this.add(rootPane);
  }

}
