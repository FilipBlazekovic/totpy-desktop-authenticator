package com.filipblazekovic.totpy.gui.screen;

import static com.filipblazekovic.totpy.utils.GUICommon.ACTIONS_PANEL_DIMENSION;
import static com.filipblazekovic.totpy.utils.GUICommon.TOOLBAR_DIMENSION;
import static com.filipblazekovic.totpy.utils.GUICommon.TOOLBAR_MARGIN;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.crypto.CryptoHandler;
import com.filipblazekovic.totpy.gui.dialog.AboutDialog;
import com.filipblazekovic.totpy.gui.dialog.AddEditTokenDialog;
import com.filipblazekovic.totpy.gui.dialog.ChangePasswordDialog;
import com.filipblazekovic.totpy.gui.dialog.ImportDialog;
import com.filipblazekovic.totpy.gui.dialog.ScanQRCodeDialog;
import com.filipblazekovic.totpy.gui.dialog.ShowPublicKeyDialog;
import com.filipblazekovic.totpy.gui.panel.DeletePanel;
import com.filipblazekovic.totpy.gui.panel.ExportPanel;
import com.filipblazekovic.totpy.gui.panel.ScanQRCodePanel;
import com.filipblazekovic.totpy.gui.panel.TokenPanel;
import com.filipblazekovic.totpy.model.inout.ExportLockingPublicKey;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;
import com.filipblazekovic.totpy.model.shared.CoreIcon;
import com.filipblazekovic.totpy.utils.IconStore;
import com.filipblazekovic.totpy.utils.OTPRecalculationTask;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class TokensScreen extends JPanel {

  private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

  private static ScheduledFuture<?> otpRecalculationTaskController;

  private static final String SCAN_QR_CODE_PANEL = "Scan";
  private static final String DELETE_AUTHENTICATORS_PANEL = "Delete";
  private static final String EXPORT_AUTHENTICATORS_PANEL = "Export";

  private static TokensScreen tokensScreen;

  public static TokensScreen getInstance() {
    return tokensScreen;
  }

  private final JPanel mainPane;
  private final JPanel actionsPanel;
  private final JScrollPane scrollPane;
  private final List<TokenPanel> tokenPanels;

  public TokensScreen() {
    tokensScreen = this;
    tokenPanels = new ArrayList<>();

    val scanQRCodePanel = new ScanQRCodePanel();
    val deletePanel = new DeletePanel();
    val exportPanel = new ExportPanel();

    val toolbar = generateToolbar();

    actionsPanel = new JPanel(new CardLayout());
    actionsPanel.add(SCAN_QR_CODE_PANEL, scanQRCodePanel);
    actionsPanel.add(DELETE_AUTHENTICATORS_PANEL, deletePanel);
    actionsPanel.add(EXPORT_AUTHENTICATORS_PANEL, exportPanel);

    mainPane = new JPanel();
    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

    scrollPane = new JScrollPane(
        mainPane,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );

    scrollPane.getVerticalScrollBar().setUnitIncrement(20);

    actionsPanel.setMinimumSize(ACTIONS_PANEL_DIMENSION);
    actionsPanel.setMaximumSize(ACTIONS_PANEL_DIMENSION);
    actionsPanel.setPreferredSize(ACTIONS_PANEL_DIMENSION);

    scrollPane.setMinimumSize(new Dimension(500, 600));

    val rootPane = new JPanel(new MigLayout("wrap 1, width 100%"));
    rootPane.add(toolbar);
    rootPane.add(scrollPane);
    rootPane.add(actionsPanel);

    this.setLayout(new MigLayout("wrap 1, alignx center, aligny center, width 100%, height 100%"));
    this.add(rootPane);
  }

  private JPopupMenu generatePopupMenu() {
    val popupMenu = new JPopupMenu();
    val font = popupMenu.getFont();
    popupMenu.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
    popupMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

    val scanQRCodeMenuItem = new JMenuItem("Scan QR Code");
    val addTokenMenuItem = new JMenuItem("Add Token");
    val importMenuItem = new JMenuItem("Import");
    val exportMenuItem = new JMenuItem("Export");
    val deleteMenuItem = new JMenuItem("Delete");
    val showPublicKeyMenuItem = new JMenuItem("Show Public Key");
    val changePasswordMenuItem = new JMenuItem("Change Password");
    val aboutMenuItem = new JMenuItem("About");
    val logoutMenuItem = new JMenuItem("Logout");

    scanQRCodeMenuItem.addActionListener(e -> new ScanQRCodeDialog());

    addTokenMenuItem.addActionListener(e -> new AddEditTokenDialog(null));

    importMenuItem.addActionListener(e -> {
      val fileChooser = new JFileChooser();
      fileChooser.setMultiSelectionEnabled(false);
      if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        new ImportDialog(fileChooser.getSelectedFile().getPath());
      }
    });

    exportMenuItem.addActionListener(e -> {
      tokenPanels.forEach(TokenPanel::showSelectCheckbox);
      showExportTokensPanel();
    });

    deleteMenuItem.addActionListener(e -> {
      tokenPanels.forEach(TokenPanel::showSelectCheckbox);
      showDeleteTokensPanel();
    });

    showPublicKeyMenuItem.addActionListener(e -> new ShowPublicKeyDialog(
        new ExportLockingPublicKey(
            CryptoHandler.getPublicKeyPem(),
            AsymmetricKeyAlgorithm.RSA
        )
    ));

    changePasswordMenuItem.addActionListener(e -> new ChangePasswordDialog());

    aboutMenuItem.addActionListener(e -> new AboutDialog());

    logoutMenuItem.addActionListener(e -> {
      clear();
      CryptoHandler.clear();
      Totpy.getInstance().showLoginScreen();
    });

    popupMenu.add(scanQRCodeMenuItem);
    popupMenu.add(addTokenMenuItem);
    popupMenu.add(importMenuItem);
    popupMenu.add(exportMenuItem);
    popupMenu.add(deleteMenuItem);
    popupMenu.add(showPublicKeyMenuItem);
    popupMenu.add(changePasswordMenuItem);
    popupMenu.add(aboutMenuItem);
    popupMenu.add(logoutMenuItem);

    return popupMenu;
  }

  private JToolBar generateToolbar() {
    val toolbar = new JToolBar();
    toolbar.setFloatable(false);

    val popupMenu = generatePopupMenu();

    val searchField = new JTextField();
    val font = searchField.getFont();
    val dimension = new Dimension(300, 30);
    searchField.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
    searchField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
    searchField.setMinimumSize(dimension);
    searchField.setPreferredSize(dimension);
    searchField.setMaximumSize(dimension);
    searchField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          TokensScreen.getInstance().filter(searchField.getText());
        }
      }
    });

    val menuButton = new JButton(IconStore.get(CoreIcon.MENU));
    menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    menuButton.addActionListener(e -> popupMenu.show(toolbar, 300, menuButton.getHeight()));

    val searchButton = new JButton(IconStore.get(CoreIcon.SEARCH));
    searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    searchButton.addActionListener(e -> TokensScreen.getInstance().filter(searchField.getText()));

    toolbar.add(Box.createHorizontalGlue());
    toolbar.add(searchField);
    toolbar.add(searchButton);
    toolbar.addSeparator(new Dimension(30, toolbar.getHeight()));
    toolbar.add(menuButton);

    toolbar.setMinimumSize(TOOLBAR_DIMENSION);
    toolbar.setMaximumSize(TOOLBAR_DIMENSION);
    toolbar.setPreferredSize(TOOLBAR_DIMENSION);

    toolbar.setMargin(TOOLBAR_MARGIN);
    toolbar.setOrientation(SwingConstants.HORIZONTAL);

    return toolbar;
  }

  public void showScanQRCodePanel() {
    tokenPanels.forEach(TokenPanel::hideSelectCheckbox);
    val layout = (CardLayout) actionsPanel.getLayout();
    layout.show(actionsPanel, SCAN_QR_CODE_PANEL);
  }

  private void showDeleteTokensPanel() {
    val layout = (CardLayout) actionsPanel.getLayout();
    layout.show(actionsPanel, DELETE_AUTHENTICATORS_PANEL);
  }

  private void showExportTokensPanel() {
    val layout = (CardLayout) actionsPanel.getLayout();
    layout.show(actionsPanel, EXPORT_AUTHENTICATORS_PANEL);
  }

  public List<TokenPanel> getDisplayedTokenPanels() {
    return tokenPanels;
  }

  public void selectAll() {
    tokenPanels.forEach(TokenPanel::select);
  }

  public void deselectAll() {
    tokenPanels.forEach(TokenPanel::deselect);
  }

  public void refresh(List<Token> authenticators) {
    clear();
    tokenPanels.addAll(authenticators.stream().map(TokenPanel::new).toList());
    tokenPanels.forEach(mainPane::add);
    mainPane.validate();
    scrollPane.validate();

    otpRecalculationTaskController = executor.scheduleAtFixedRate(
        new OTPRecalculationTask(tokensScreen.getDisplayedTokenPanels()),
        0,
        1,
        TimeUnit.SECONDS
    );
  }

  public void clear() {
    if (otpRecalculationTaskController != null) {
      otpRecalculationTaskController.cancel(true);
    }
    tokenPanels.forEach(tp -> {
      Arrays.fill(tp.getToken().secret(), (byte) '\0');
      tp.setOtp("******");
    });
    tokenPanels.clear();
    mainPane.removeAll();
  }

  public void filter(String searchPhrase) {
    val finalSearchPhrase = searchPhrase.toLowerCase().trim();

    if (finalSearchPhrase.isEmpty()) {
      tokenPanels.forEach(tp -> tp.setVisible(true));
      return;
    }

    tokenPanels.forEach(tp -> {
      val t = tp.getToken();
      val account = t.account().toLowerCase();
      val category = t.category().getDisplayName().toLowerCase();

      if (category.equals(finalSearchPhrase)) {
        tp.setVisible(true);
        return;
      }

      if (account.contains(finalSearchPhrase)) {
        tp.setVisible(true);
        return;
      }

      if (t.issuer() != null) {
        val issuerName = t.issuer().name().toLowerCase();
        if (issuerName.contains(finalSearchPhrase)) {
          tp.setVisible(true);
          return;
        }
      }

      tp.setVisible(false);
    });
  }

}
