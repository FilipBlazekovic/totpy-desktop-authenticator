package com.filipblazekovic.totpy.gui.panel;

import com.filipblazekovic.totpy.gui.dialog.ScanQRCodeDialog;
import com.filipblazekovic.totpy.model.shared.CoreIcon;
import com.filipblazekovic.totpy.utils.IconStore;
import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JPanel;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class ScanQRCodePanel extends JPanel {

  public ScanQRCodePanel() {
    val scanQRCodeButton = new JButton(IconStore.get(CoreIcon.QR_CODE_SCANNER));
    scanQRCodeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    scanQRCodeButton.addActionListener(e -> new ScanQRCodeDialog());

    this.setLayout(new MigLayout("wrap 1, width 100%"));
    this.add(scanQRCodeButton, "push, al right, wrap");
  }

}
