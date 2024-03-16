package com.filipblazekovic.totpy.gui.dialog;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.gui.panel.ProgressPanel;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.CoreIcon;
import com.filipblazekovic.totpy.utils.BGWorkers;
import com.filipblazekovic.totpy.utils.GUICommon;
import com.filipblazekovic.totpy.utils.IOHandler;
import com.filipblazekovic.totpy.utils.IconStore;
import java.awt.Cursor;
import java.awt.Font;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class ExportDialog extends JDialog {

  private static final ProgressPanel progressPane = new ProgressPanel("Generating export...");

  private final ExportDialog referenceToThis;

  public ExportDialog(List<Token> tokens) {
    referenceToThis = this;

    val protectWithPublicKeyLabel = new JLabel("Protect with public key");
    val protectWithPasswordLabel = new JLabel("Protect with password");

    val font = protectWithPublicKeyLabel.getFont();
    protectWithPublicKeyLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
    protectWithPasswordLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

    val protectWithPublicKeyDescriptionArea = new JTextArea("""
        Use to migrate tokens to a 2'nd authenticator.
        Requires a scan or upload of the public key
        from the second device."""
    );

    val protectWithPasswordDescriptionArea = new JTextArea("Use to create a password-protected backup");

    protectWithPublicKeyDescriptionArea.setEditable(false);
    protectWithPasswordDescriptionArea.setEditable(false);

    val loadPublicKeyFileButton = new JButton(IconStore.get(CoreIcon.FILE_OPEN));
    loadPublicKeyFileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    loadPublicKeyFileButton.addActionListener(e -> {
      try {
        IOHandler
            .loadPublicKeyFromFile(referenceToThis)
            .ifPresent(publicKey -> {
              showProgressPane();
              BGWorkers.export(referenceToThis, tokens, null, publicKey);
            });
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(referenceToThis, GUICommon.GENERIC_ERROR_MESSAGE, GUICommon.WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
      }
    });

    val scanPublicKeyQRCodeButton = new JButton(IconStore.get(CoreIcon.QR_CODE_SCANNER));
    scanPublicKeyQRCodeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    scanPublicKeyQRCodeButton.addActionListener(e -> new ScanQRCodeDialog(referenceToThis, tokens));

    val protectWithPasswordButton = new JButton(IconStore.get(CoreIcon.PASSWORD));
    protectWithPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    protectWithPasswordButton.addActionListener(e -> PasswordDialog.showPasswordDefineDialog(this).ifPresent(exportPassword -> {
      showProgressPane();
      BGWorkers.export(referenceToThis, tokens, exportPassword,  null);
    }));

    val rootPane = new JPanel(new MigLayout("alignx center"));
    rootPane.setBorder(GUICommon.getRootBorder());

    val publicKeyButtonsPane = new JPanel(new MigLayout("wrap 2"));
    publicKeyButtonsPane.add(scanPublicKeyQRCodeButton);
    publicKeyButtonsPane.add(loadPublicKeyFileButton);

    rootPane.add(publicKeyButtonsPane, "alignx center, wrap");
    rootPane.add(protectWithPublicKeyLabel, "alignx center, wrap");
    rootPane.add(protectWithPublicKeyDescriptionArea, "alignx center, wrap");

    rootPane.add(new JSeparator(), "alignx center, wrap");

    rootPane.add(protectWithPasswordButton, "alignx center, wrap");
    rootPane.add(protectWithPasswordLabel, "alignx center, wrap");
    rootPane.add(protectWithPasswordDescriptionArea, "alignx center, wrap");

    this.setTitle(GUICommon.WINDOW_TITLE);
    this.setContentPane(rootPane);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(450, 350);
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
