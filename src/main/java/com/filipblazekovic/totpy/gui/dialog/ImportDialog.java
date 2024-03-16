package com.filipblazekovic.totpy.gui.dialog;

import static com.filipblazekovic.totpy.utils.GUICommon.TOGGLE_BUTTON_DIMENSION;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.gui.panel.ProgressPanel;
import com.filipblazekovic.totpy.model.inout.ExportLockingMethod;
import com.filipblazekovic.totpy.utils.BGWorkers;
import com.filipblazekovic.totpy.utils.GUICommon;
import com.filipblazekovic.totpy.utils.IOHandler;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class ImportDialog extends JDialog {

  private static final ProgressPanel progressPane = new ProgressPanel("Processing import...");

  private final ImportDialog referenceToThis;

  public ImportDialog(String importFilePath) {
    referenceToThis = this;

    val clearDatabaseToggleButton = new JToggleButton("OFF");
    val clearDatabaseLabel = new JLabel("Clear current database");
    val overwriteExistingTokensButton = new JToggleButton("OFF");
    val overwriteExistingTokensLabel = new JLabel("Overwrite existing tokens");

    clearDatabaseToggleButton.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        clearDatabaseToggleButton.setText("ON");
        overwriteExistingTokensButton.setSelected(false);
      } else {
        clearDatabaseToggleButton.setText("OFF");
      }
    });

    overwriteExistingTokensButton.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        overwriteExistingTokensButton.setText("ON");
        clearDatabaseToggleButton.setSelected(false);
      } else {
        overwriteExistingTokensButton.setText("OFF");
      }
    });

    clearDatabaseToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    overwriteExistingTokensButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

    clearDatabaseToggleButton.setPreferredSize(TOGGLE_BUTTON_DIMENSION);
    overwriteExistingTokensButton.setPreferredSize(TOGGLE_BUTTON_DIMENSION);

    val importButton = new JButton("IMPORT");
    importButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    importButton.addActionListener(e -> {
      try {
        val exportLocked = IOHandler.loadExport(importFilePath);
        Optional<char[]> password = Optional.empty();
        if (exportLocked.exportLockingMethod() == ExportLockingMethod.PASSWORD) {
          password = PasswordDialog.showPasswordInputDialog(referenceToThis);
          if (password.isEmpty() || password.get().length == 0) {
            return;
          }
        }
        showProgressPane();
        BGWorkers.processImport(
            referenceToThis,
            exportLocked,
            password.orElse(null),
            clearDatabaseToggleButton.isSelected(),
            overwriteExistingTokensButton.isSelected()
        );
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(referenceToThis, "Could not import tokens!", GUICommon.WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
        dispose();
      }
    });

    val cancelButton = new JButton("CANCEL");
    cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    cancelButton.addActionListener(a -> this.dispose());

    val rootPane = new JPanel(new MigLayout());
    rootPane.setBorder(GUICommon.getRootBorder());

    val formPanel = new JPanel(new MigLayout("width 100%"));
    formPanel.setBorder(GUICommon.getSectionBorder());

    val buttonsPanel = new JPanel(new MigLayout("wrap 2"));
    buttonsPanel.add(importButton);
    buttonsPanel.add(cancelButton);

    formPanel.add(clearDatabaseToggleButton, "width 15%");
    formPanel.add(clearDatabaseLabel, "width 85%, wrap");
    formPanel.add(overwriteExistingTokensButton, "width 15%");
    formPanel.add(overwriteExistingTokensLabel, "width 85%, wrap");

    rootPane.add(formPanel, "alignx center, width 100%, wrap");
    rootPane.add(new JSeparator(), "wrap");
    rootPane.add(buttonsPanel, "alignx center, wrap");

    this.setTitle(GUICommon.WINDOW_TITLE);
    this.setContentPane(rootPane);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(450, 220);
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
