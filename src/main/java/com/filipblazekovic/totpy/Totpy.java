package com.filipblazekovic.totpy;

import com.filipblazekovic.totpy.db.DBConnection;
import com.filipblazekovic.totpy.gui.panel.ProgressPanel;
import com.filipblazekovic.totpy.gui.screen.InitializationScreen;
import com.filipblazekovic.totpy.gui.screen.LoginScreen;
import com.filipblazekovic.totpy.gui.screen.TokensScreen;
import com.filipblazekovic.totpy.model.inout.Config;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.utils.GUICommon;
import com.filipblazekovic.totpy.utils.IOHandler;
import com.filipblazekovic.totpy.utils.IconStore;
import com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.Security;
import java.time.OffsetDateTime;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import lombok.val;

public class Totpy extends JFrame {

  private static final String INITIALIZATION_SCREEN = "Initialization";
  private static final String LOGIN_SCREEN = "Login";
  private static final String TOKENS_SCREEN = "Tokens";

  private static final ProgressPanel progressPane = new ProgressPanel("Operation in progress...");

  private static Config config;
  private static Totpy totpy;

  private final JPanel rootPanel;
  private final InitializationScreen initializationScreen;
  private final LoginScreen loginScreen;
  private final TokensScreen tokensScreen;

  private Totpy() {
    rootPanel = new JPanel();

    initializationScreen = new InitializationScreen();
    loginScreen = new LoginScreen();
    tokensScreen = new TokensScreen();

    val layout = new CardLayout();
    rootPanel.setLayout(layout);
    rootPanel.add(INITIALIZATION_SCREEN, initializationScreen);
    rootPanel.add(LOGIN_SCREEN, loginScreen);
    rootPanel.add(TOKENS_SCREEN, tokensScreen);

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        DBConnection.close();
        super.windowClosing(e);
      }
    });

    this.setContentPane(rootPanel);
    this.setTitle(GUICommon.WINDOW_TITLE);
    this.setSize(500, 750);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    this.setVisible(true);
  }

  public void showInitializationScreen() {
    val layout = (CardLayout) rootPanel.getLayout();
    layout.show(rootPanel, INITIALIZATION_SCREEN);
  }

  public void showLoginScreen() {
    loginScreen.setUsesKeyFileProtection(config.usesKeyFile2Fa());
    val layout = (CardLayout) rootPanel.getLayout();
    layout.show(rootPanel, LOGIN_SCREEN);
  }

  public void showTokensScreen(List<Token> tokens) {
    tokensScreen.refresh(tokens);
    val layout = (CardLayout) rootPanel.getLayout();
    layout.show(rootPanel, TOKENS_SCREEN);
  }

  public void showProgressPane(String message) {
    progressPane.setMessage(message);
    setGlassPane(progressPane);
    progressPane.setVisible(true);
  }

  public void hideProgressPane() {
    progressPane.setVisible(false);
  }

  public static void main(String[] args) {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    IconStore.initialize();

    if (IOHandler.configExists()) {
      config = IOHandler.loadConfig();
    }

    try {
      FlatGruvboxDarkMediumIJTheme.installLafInfo();
      FlatGruvboxDarkMediumIJTheme.setup();

    } catch (Exception ignored) {
    }

    SwingUtilities.invokeLater(() -> {
      totpy = new Totpy();
      if (config != null) {
        totpy.showLoginScreen();
      } else {
        totpy.showInitializationScreen();
      }
    });
  }

  public static Totpy getInstance() {
    return totpy;
  }

  public static Config getCurrentConfig() {
    return config;
  }

  public static void updateCurrentConfig(OffsetDateTime lastExportDateTime) {
    config = new Config(config.usesKeyFile2Fa(), lastExportDateTime);
    IOHandler.saveConfig(config);
  }

  public static void updateCurrentConfig(boolean usesKeyFile2Fa) {
    config = new Config(usesKeyFile2Fa, config == null ? null : config.lastExportDateTime());
    IOHandler.saveConfig(config);
  }

}
