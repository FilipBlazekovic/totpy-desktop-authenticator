package com.filipblazekovic.totpy.gui.dialog;

import com.filipblazekovic.totpy.model.inout.ExportPassword;
import com.filipblazekovic.totpy.utils.GUICommon;
import java.util.Optional;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class PasswordDialog {

  private PasswordDialog() {
  }

  public static Optional<char[]> showPasswordInputDialog(JDialog parent) {
    val enterPasswordLabel = new JLabel("Enter password:");
    val enterPasswordField = new JPasswordField(50);

    val rootPane = new JPanel(new MigLayout("wrap 1, width 100%"));
    rootPane.setBorder(GUICommon.getRootBorder());
    rootPane.add(enterPasswordLabel);
    rootPane.add(enterPasswordField);

    val options = new String[] {"OK", "Cancel"};
    int selectedOption = JOptionPane.showOptionDialog(
        parent,
        rootPane,
        GUICommon.WINDOW_TITLE,
        JOptionPane.NO_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        options[0]
    );

    if (selectedOption != 0) {
      return Optional.empty();
    }

    return Optional.of(enterPasswordField.getPassword());
  }


  public static Optional<ExportPassword> showPasswordDefineDialog(JDialog parent) {
    val enterPasswordLabel = new JLabel("Enter password:");
    val enterPasswordField = new JPasswordField(50);
    val confirmPasswordLabel = new JLabel("Confirm password:");
    val confirmPasswordField = new JPasswordField(50);

    val rootPane = new JPanel(new MigLayout("wrap 1, width 100%"));
    rootPane.setBorder(GUICommon.getRootBorder());
    rootPane.add(enterPasswordLabel);
    rootPane.add(enterPasswordField);
    rootPane.add(confirmPasswordLabel);
    rootPane.add(confirmPasswordField);

    val options = new String[] {"OK", "Cancel"};

    int selectedOption = JOptionPane.showOptionDialog(
        parent,
        rootPane,
        GUICommon.WINDOW_TITLE,
        JOptionPane.NO_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        options[0]
    );

    if (selectedOption != 0) {
      return Optional.empty();
    }

    return Optional.of(
        new ExportPassword(
            enterPasswordField.getPassword(),
            confirmPasswordField.getPassword()
        )
    );
  }

}