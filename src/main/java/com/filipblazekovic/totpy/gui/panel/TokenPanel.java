package com.filipblazekovic.totpy.gui.panel;

import com.filipblazekovic.totpy.gui.dialog.AddEditTokenDialog;
import com.filipblazekovic.totpy.gui.dialog.ShowQRCodeDialog;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.CoreIcon;
import com.filipblazekovic.totpy.utils.IconStore;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class TokenPanel extends JPanel {

  @Getter
  private final Token token;

  private final JCheckBox selectCheckBox;
  private final JTextField otpField;
  private final JTextField remainingSecondsField;

  public TokenPanel(Token token) {
    this.token = token;

    selectCheckBox = new JCheckBox();

    otpField = new JTextField("");
    val font = otpField.getFont();
    otpField.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

    otpField.setHorizontalAlignment(SwingUtilities.CENTER);
    otpField.setEditable(false);

    remainingSecondsField = new JTextField(2);
    remainingSecondsField.setText("");
    remainingSecondsField.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
    remainingSecondsField.setHorizontalAlignment(SwingUtilities.CENTER);
    remainingSecondsField.setEditable(false);

    val otpAndRemainingSecondsPanel = new JPanel(new MigLayout("wrap 2, alignx center, aligny center, width 100%"));
    otpAndRemainingSecondsPanel.add(otpField);
    otpAndRemainingSecondsPanel.add(remainingSecondsField);

    val issuerIcon = new IconPanel(token.issuer().icon());
    val issuerLabel = new JLabel(token.issuer().name());
    val accountLabel = new JLabel(token.account());

    otpField.setHorizontalAlignment(SwingUtilities.CENTER);
    issuerLabel.setHorizontalAlignment(SwingUtilities.CENTER);
    accountLabel.setHorizontalAlignment(SwingUtilities.CENTER);

    otpField.setCursor(new Cursor(Cursor.HAND_CURSOR));

    val buttonDimension = new Dimension(30, 30);

    val copyToClipboardButton = new JButton(IconStore.get(CoreIcon.CONTENT_COPY));
    copyToClipboardButton.setMinimumSize(buttonDimension);
    copyToClipboardButton.setPreferredSize(buttonDimension);
    copyToClipboardButton.setMaximumSize(buttonDimension);
    copyToClipboardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    copyToClipboardButton.addActionListener(e -> Toolkit.getDefaultToolkit()
        .getSystemClipboard()
        .setContents(new StringSelection(otpField.getText()), null));

    val editButton = new JButton(IconStore.get(CoreIcon.EDIT));
    editButton.setMinimumSize(buttonDimension);
    editButton.setPreferredSize(buttonDimension);
    editButton.setMaximumSize(buttonDimension);
    editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    editButton.addActionListener(e -> new AddEditTokenDialog(token));

    val showQRCodeButton = new JButton(IconStore.get(CoreIcon.QR_CODE_SHOW));
    showQRCodeButton.setMinimumSize(buttonDimension);
    showQRCodeButton.setPreferredSize(buttonDimension);
    showQRCodeButton.setMaximumSize(buttonDimension);
    showQRCodeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    showQRCodeButton.addActionListener(e -> new ShowQRCodeDialog(token));

    val coreDataPanel = new JPanel(new MigLayout("wrap 1"));
    coreDataPanel.add(otpAndRemainingSecondsPanel, "alignx center, width 100%");
    coreDataPanel.add(issuerLabel, "alignx center, width 100%");
    coreDataPanel.add(accountLabel, "alignx center, width 100%");

    val buttonsPanel = new JPanel(new MigLayout("aligny center, wrap 1"));
    buttonsPanel.add(copyToClipboardButton);
    buttonsPanel.add(editButton);
    buttonsPanel.add(showQRCodeButton);

    val rootPane = new JPanel(new BorderLayout());
    rootPane.setBorder(token.category().getBorder());

    rootPane.add(issuerIcon, BorderLayout.WEST);
    rootPane.add(coreDataPanel, BorderLayout.CENTER);
    rootPane.add(buttonsPanel, BorderLayout.EAST);

    this.setLayout(new BorderLayout());
    this.add(rootPane, BorderLayout.CENTER);
    this.add(selectCheckBox, BorderLayout.WEST);

    this.setMinimumSize(new Dimension(470, 135));
    this.setPreferredSize(new Dimension(480, 135));
    this.setMaximumSize(new Dimension(480, 135));

    selectCheckBox.setVisible(false);
  }

  public String getOtp() {
    return otpField.getText();
  }

  public void setOtp(String otp) {
    otpField.setText(otp);
  }

  public void setRemainingSeconds(int remainingSeconds) {
    remainingSecondsField.setText(String.valueOf(remainingSeconds));
    if (remainingSeconds < 10) {
      remainingSecondsField.setForeground(Color.RED);
    } else {
      remainingSecondsField.setForeground(Color.GREEN);
    }
  }

  public void showSelectCheckbox() {
    selectCheckBox.setVisible(true);
  }

  public void hideSelectCheckbox() {
    selectCheckBox.setSelected(false);
    selectCheckBox.setVisible(false);
  }

  public void select() {
    selectCheckBox.setSelected(true);
  }

  public void deselect() {
    selectCheckBox.setSelected(false);
  }

  public boolean selected() {
    return selectCheckBox.isSelected();
  }

}
