package com.filipblazekovic.totpy.gui.panel;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.gui.dialog.ExportDialog;
import com.filipblazekovic.totpy.gui.screen.TokensScreen;
import com.filipblazekovic.totpy.model.shared.CoreIcon;
import com.filipblazekovic.totpy.utils.GUICommon;
import com.filipblazekovic.totpy.utils.IconStore;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class ExportPanel extends JPanel {

  public ExportPanel() {

    this.setLayout(new MigLayout("wrap 3, width 100%"));

    val selectAllCheckBox = new JCheckBox("Select all");
    selectAllCheckBox.setSelected(false);
    selectAllCheckBox.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        TokensScreen.getInstance().selectAll();
      }
      else {
        TokensScreen.getInstance().deselectAll();
      }
    });

    val exportButton = new JButton(IconStore.get(CoreIcon.FILE_SAVE));
    exportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    exportButton.addActionListener(e -> {

      val selectedTokens = TokensScreen
          .getInstance()
          .getDisplayedTokenPanels()
          .stream()
          .filter(TokenPanel::selected)
          .map(TokenPanel::getToken)
          .toList();

      if (selectedTokens.isEmpty()) {
        JOptionPane.showMessageDialog(
            Totpy.getInstance(),
            "Select tokens you wish to export to continue!",
            GUICommon.WINDOW_TITLE,
            JOptionPane.INFORMATION_MESSAGE
        );
        return;
      }

      selectAllCheckBox.setSelected(false);

      new ExportDialog(selectedTokens);
      TokensScreen.getInstance().showScanQRCodePanel();
    });

    val closeButton = new JButton(IconStore.get(CoreIcon.CLOSE));
    closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    closeButton.addActionListener(e -> TokensScreen.getInstance().showScanQRCodePanel());

    this.add(selectAllCheckBox, "push, al left");
    this.add(closeButton, "alignx center");
    this.add(exportButton, "alignx center");
  }

}
