package com.filipblazekovic.totpy.gui.dialog;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.gui.panel.ProgressPanel;
import com.filipblazekovic.totpy.utils.BGWorkers;
import com.filipblazekovic.totpy.utils.GUICommon;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class ChangePasswordDialog extends JDialog {

  private static final ProgressPanel progressPane = new ProgressPanel("Relocking keys...");

  private final ChangePasswordDialog referenceToThis;

  public ChangePasswordDialog() {
    referenceToThis = this;

    val currentPasswordLabel = new JLabel("Current password:");
    val currentPasswordField = new JPasswordField();

    val newPasswordLabel = new JLabel("New password:");
    val newPasswordField = new JPasswordField();

    val confirmNewPasswordLabel = new JLabel("Confirm new password:");
    val confirmNewPasswordField = new JPasswordField();

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

    val filePanel = new JPanel(new MigLayout("wrap 2"));
    filePanel.add(loadedKeyFileLabel);
    filePanel.add(loadedKeyFileField);
    filePanel.setVisible(false);

    val changePasswordButton = new JButton("APPLY");
    val cancelButton = new JButton("CANCEL");

    changePasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

    val buttonsPanel = new JPanel(new MigLayout("wrap 2"));
    buttonsPanel.add(changePasswordButton);
    buttonsPanel.add(cancelButton);

    useKeyFileButton.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        loadKeyFileButton.setVisible(true);
        filePanel.setVisible(true);
        useKeyFileButton.setText("ON");
      }
      else {
        loadKeyFileButton.setVisible(false);
        filePanel.setVisible(false);
        loadedKeyFileField.setText("");
        useKeyFileButton.setText("OFF");
      }
    });

    loadKeyFileButton.addActionListener(a -> {
      val fileChooser = new JFileChooser();
      fileChooser.setMultiSelectionEnabled(false);
      if (fileChooser.showOpenDialog(referenceToThis) == JFileChooser.APPROVE_OPTION) {
        loadedKeyFileField.setText(fileChooser.getSelectedFile().getPath());
      }
    });

    cancelButton.addActionListener(e -> dispose());
    changePasswordButton.addActionListener(e -> {
      showProgressPane();
      BGWorkers.changePassword(
          referenceToThis,
          currentPasswordField.getPassword(),
          useKeyFileButton.isSelected() ? loadedKeyFileField.getText() : null,
          newPasswordField.getPassword(),
          confirmNewPasswordField.getPassword()
      );
    });

    val rootPane = new JPanel(new MigLayout());
    rootPane.setBorder(GUICommon.getRootBorder());

    val formPanel = new JPanel(new MigLayout("wrap 1"));
    formPanel.setBorder(GUICommon.getSectionBorder());

    formPanel.add(currentPasswordLabel, "width 100%");
    formPanel.add(currentPasswordField, "width 100%");
    formPanel.add(newPasswordLabel, "width 100%");
    formPanel.add(newPasswordField, "width 100%");
    formPanel.add(confirmNewPasswordLabel, "width 100%");
    formPanel.add(confirmNewPasswordField, "width 100%");
    formPanel.add(new JSeparator());
    formPanel.add(toggleButtonPanel, "width 100%");
    formPanel.add(loadKeyFileButton, "width 100%");
    formPanel.add(filePanel);

    rootPane.add(formPanel, "alignx center, wrap");
    rootPane.add(new JSeparator(), "wrap");
    rootPane.add(buttonsPanel, "align center, wrap");

    this.setTitle(GUICommon.WINDOW_TITLE);
    this.setContentPane(rootPane);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(450, 450);
    this.setModal(true);
    this.setResizable(false);
    this.setLocationRelativeTo(Totpy.getInstance());
    this.setVisible(true);
  }

  public void showProgressPane() {
    setGlassPane(progressPane);
    progressPane.setVisible(true);
  }

  public void hideProgressPane() {
    progressPane.setVisible(false);
  }

}
