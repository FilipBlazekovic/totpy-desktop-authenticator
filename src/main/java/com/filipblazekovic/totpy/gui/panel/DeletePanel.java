package com.filipblazekovic.totpy.gui.panel;

import com.filipblazekovic.totpy.crypto.CryptoHandler;
import com.filipblazekovic.totpy.db.DBHandler;
import com.filipblazekovic.totpy.gui.screen.TokensScreen;
import com.filipblazekovic.totpy.model.shared.CoreIcon;
import com.filipblazekovic.totpy.utils.IconStore;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class DeletePanel extends JPanel {

  public DeletePanel() {

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

    val deleteButton = new JButton(IconStore.get(CoreIcon.DELETE));
    deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    deleteButton.addActionListener(e -> {

      if (selectAllCheckBox.isSelected()) {
        DBHandler.deleteAllTokens();
      } else {

        val selectedTokens = TokensScreen
            .getInstance()
            .getDisplayedTokenPanels()
            .stream()
            .filter(TokenPanel::selected)
            .map(tp -> tp.getToken().id())
            .toList();

        if (selectedTokens.isEmpty()) {
          TokensScreen.getInstance().showScanQRCodePanel();
          return;
        }

        DBHandler.deleteTokens(selectedTokens);
      }

      selectAllCheckBox.setSelected(false);

      TokensScreen.getInstance().refresh(
          DBHandler
              .getTokens()
              .stream()
              .map(CryptoHandler::unlockToken)
              .toList()
      );

      TokensScreen.getInstance().showScanQRCodePanel();
    });

    val closeButton = new JButton(IconStore.get(CoreIcon.CLOSE));
    closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    closeButton.addActionListener(e -> {
      selectAllCheckBox.setSelected(false);
      TokensScreen.getInstance().showScanQRCodePanel();
    });

    this.add(selectAllCheckBox, "push, al left");
    this.add(deleteButton, "alignx center");
    this.add(closeButton, "alignx center");
    this.add(closeButton, "alignx center");
  }

}
