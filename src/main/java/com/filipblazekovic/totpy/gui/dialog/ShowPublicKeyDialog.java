package com.filipblazekovic.totpy.gui.dialog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.gui.panel.ShowQRCodePanel;
import com.filipblazekovic.totpy.model.inout.ExportLockingPublicKey;
import com.filipblazekovic.totpy.model.shared.CoreIcon;
import com.filipblazekovic.totpy.utils.GUICommon;
import com.filipblazekovic.totpy.utils.IconStore;
import com.filipblazekovic.totpy.utils.QRCode;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import lombok.SneakyThrows;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class ShowPublicKeyDialog extends JDialog {
  private final ShowPublicKeyDialog referenceToThis;

  @SneakyThrows
  public ShowPublicKeyDialog(ExportLockingPublicKey exportLockingPublicKey) {
    referenceToThis = this;

    val publicKeyPem = exportLockingPublicKey.publicKeyPem();
    val resultString = new ObjectMapper().writeValueAsString(exportLockingPublicKey);
    val qrCodePanel = new ShowQRCodePanel(QRCode.generate(resultString));

    // The last newline is removed from PEM for GUI display
    val pemStringArea = new JTextArea(publicKeyPem.substring(0, publicKeyPem.length()-1));
    pemStringArea.setBorder(GUICommon.getSectionBorder());

    pemStringArea.setFont(new Font("Verdana", Font.PLAIN, 11));
    pemStringArea.setEditable(false);

    val copyToClipboardButton = new JButton(IconStore.get(CoreIcon.CONTENT_COPY));
    copyToClipboardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    copyToClipboardButton.addActionListener(e -> Toolkit.getDefaultToolkit()
        .getSystemClipboard()
        .setContents(new StringSelection(resultString), null)
    );

    val saveToFileButton = new JButton(IconStore.get(CoreIcon.FILE_SAVE));
    saveToFileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    saveToFileButton.addActionListener(e -> {
      val currentFilePath = System.getProperty("user.dir") + File.separator + "public_key.json";
      try (val output = new FileOutputStream(currentFilePath)) {
        output.write(resultString.getBytes(StandardCharsets.UTF_8));
        JOptionPane.showMessageDialog(
            referenceToThis,
            "Public key saved to " + currentFilePath,
            GUICommon.WINDOW_TITLE,
            JOptionPane.INFORMATION_MESSAGE
        );
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            referenceToThis,
            "Error saving public key to file",
            GUICommon.WINDOW_TITLE,
            JOptionPane.ERROR_MESSAGE
        );
      }
    });

    val rootPane = new JPanel(new MigLayout());
    rootPane.setBorder(GUICommon.getRootBorder());

    val buttonsPane = new JPanel(new GridLayout(1, 3, 10, 10));
    buttonsPane.add(copyToClipboardButton);
    buttonsPane.add(saveToFileButton);

    rootPane.add(qrCodePanel, "width 100%, alignx center, wrap");
    rootPane.add(new JSeparator(), "wrap");
    rootPane.add(pemStringArea, "width 100%, alignx center, wrap");
    rootPane.add(new JSeparator(), "wrap");
    rootPane.add(buttonsPane, "alignx center, wrap");

    this.setTitle(GUICommon.WINDOW_TITLE);
    this.setContentPane(rootPane);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(500, 600);
    this.setModal(true);
    this.setResizable(false);
    this.setLocationRelativeTo(Totpy.getInstance());
    this.setVisible(true);
  }

}
