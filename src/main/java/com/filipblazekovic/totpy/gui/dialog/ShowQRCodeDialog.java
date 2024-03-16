package com.filipblazekovic.totpy.gui.dialog;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.gui.panel.ShowQRCodePanel;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.utils.GUICommon;
import com.filipblazekovic.totpy.utils.OTPAuth;
import com.filipblazekovic.totpy.utils.QRCode;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import lombok.val;

public class ShowQRCodeDialog extends JDialog {

  public ShowQRCodeDialog(Token token) {

    val qrCodePanel = new ShowQRCodePanel(
        QRCode.generate(OTPAuth.generateTotpUri(token))
    );

    qrCodePanel.setBorder(GUICommon.getSectionBorder());

    val rootPane = new JPanel(new BorderLayout());
    rootPane.setBorder(GUICommon.getRootBorder());

    rootPane.add(qrCodePanel, BorderLayout.CENTER);

    this.setTitle(GUICommon.WINDOW_TITLE);
    this.setContentPane(rootPane);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(300, 350);
    this.setModal(true);
    this.setResizable(false);
    this.setLocationRelativeTo(Totpy.getInstance());
    this.setVisible(true);
  }

}