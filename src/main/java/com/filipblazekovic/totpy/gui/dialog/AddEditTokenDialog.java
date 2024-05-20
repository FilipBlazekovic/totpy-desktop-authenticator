package com.filipblazekovic.totpy.gui.dialog;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.crypto.CryptoHandler;
import com.filipblazekovic.totpy.db.DBHandler;
import com.filipblazekovic.totpy.exception.MandatoryParameter;
import com.filipblazekovic.totpy.exception.MissingMandatoryParameterException;
import com.filipblazekovic.totpy.gui.panel.IconPanel;
import com.filipblazekovic.totpy.gui.screen.TokensScreen;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.Category;
import com.filipblazekovic.totpy.model.shared.CoreIcon;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import com.filipblazekovic.totpy.model.shared.Issuer;
import com.filipblazekovic.totpy.model.shared.IssuerIcon;
import com.filipblazekovic.totpy.utils.GUICommon;
import com.filipblazekovic.totpy.utils.IOHandler;
import com.filipblazekovic.totpy.utils.IconStore;
import com.filipblazekovic.totpy.utils.OTPAuth;
import com.filipblazekovic.totpy.utils.QRCode;
import java.awt.Cursor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import lombok.SneakyThrows;
import lombok.val;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.codec.binary.Base32;

public class AddEditTokenDialog extends JDialog {

  private static final String[] SUPPORTED_ALGORITHMS = { "SHA1", "SHA256", "SHA512" };
  private static final Integer[] SUPPORTED_DIGITS = { 6, 8 };
  private static final Integer[] SUPPORTED_PERIODS = { 30, 60, 90, 120 };

  private final IconPanel issuerIcon;

  private final JTextField issuerField;
  private final JTextField accountField;
  private final JComboBox<Category> categoryField;
  private final JComboBox<String> algorithmField;
  private final JComboBox<Integer> digitsField;
  private final JComboBox<Integer> periodField;
  private final JTextField secretField;

  public AddEditTokenDialog(Token token) {

    issuerIcon = new IconPanel(IssuerIcon.UNKNOWN);
    issuerIcon.setBorder(BorderFactory.createRaisedBevelBorder());

    val issuerLabel = new JLabel("Issuer:");
    val accountLabel = new JLabel("Account:");
    val categoryLabel = new JLabel("Category:");
    val algorithmLabel = new JLabel("Algorithm:");
    val digitsLabel = new JLabel("Digits:");
    val periodLabel = new JLabel("Period (s):");
    val secretLabel = new JLabel("Secret:");

    issuerField = new JTextField("", 50);
    accountField = new JTextField("", 50);
    categoryField = new JComboBox<>(Category.values());
    algorithmField = new JComboBox<>(SUPPORTED_ALGORITHMS);
    digitsField = new JComboBox<>(SUPPORTED_DIGITS);
    periodField = new JComboBox<>(SUPPORTED_PERIODS);
    secretField = new JTextField("", 50);

    if (token != null) {
      issuerField.setText(token.issuer().name());
      issuerIcon.setIcon(IssuerIcon.from(token.issuer().name()));
      accountField.setText(token.account());
      categoryField.setSelectedItem(token.category());
      algorithmField.setSelectedItem(token.algorithm().name());
      digitsField.setSelectedItem(token.digits());
      periodField.setSelectedItem(token.period());
      secretField.setText(new Base32().encodeAsString(token.secret()));
    }

    val enterOtpAuthUriLabel = new JLabel("Load token from URI string");
    val enterOtpAuthUriButton = new JButton(IconStore.get(CoreIcon.LINK));
    enterOtpAuthUriButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    enterOtpAuthUriButton.addActionListener(e -> loadFromUriString());

    val loadQRCodeImageLabel = new JLabel("Load token from QR code image file");
    val loadQRCodeImageButton = new JButton(IconStore.get(CoreIcon.QR_CODE_ADD));
    loadQRCodeImageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    loadQRCodeImageButton.addActionListener(e -> loadFromFile());

    val cancelButton = new JButton("CANCEL");
    cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    cancelButton.addActionListener(e -> this.dispose());

    val saveButton = new JButton("SAVE");
    saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    saveButton.addActionListener(e -> save(token));

    val rootPane = new JPanel(new MigLayout());
    rootPane.setBorder(GUICommon.getRootBorder());

    val formPanel = new JPanel(new MigLayout("wrap 2"));
    formPanel.setBorder(GUICommon.getSectionBorder());

    formPanel.add(issuerLabel, "width 170");
    formPanel.add(issuerField, "width 230");
    formPanel.add(accountLabel, "width 170");
    formPanel.add(accountField, "width 230");
    formPanel.add(categoryLabel, "width 170");
    formPanel.add(categoryField, "width 230");
    formPanel.add(algorithmLabel, "width 170");
    formPanel.add(algorithmField, "width 230");
    formPanel.add(digitsLabel, "width 170");
    formPanel.add(digitsField, "width 230");
    formPanel.add(periodLabel, "width 170");
    formPanel.add(periodField, "width 230");
    formPanel.add(secretLabel, "width 170");
    formPanel.add(secretField, "width 230");

    final JPanel qrCodeButtonPanel = new JPanel(new MigLayout("wrap 2"));
    qrCodeButtonPanel.add(enterOtpAuthUriButton);
    qrCodeButtonPanel.add(enterOtpAuthUriLabel);
    qrCodeButtonPanel.add(loadQRCodeImageButton);
    qrCodeButtonPanel.add(loadQRCodeImageLabel);

    final JPanel buttonsPanel = new JPanel(new MigLayout("wrap 2"));
    buttonsPanel.add(saveButton);
    buttonsPanel.add(cancelButton);

    rootPane.add(issuerIcon, "alignx center, wrap");
    rootPane.add(formPanel, "alignx center, wrap");
    rootPane.add(new JSeparator(), "wrap");
    rootPane.add(qrCodeButtonPanel, "alignx center, wrap");
    rootPane.add(new JSeparator(), "wrap");
    rootPane.add(buttonsPanel, "alignx center, wrap");

    this.setTitle(token == null ? "Add Token" : "Edit Token");
    this.setContentPane(rootPane);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(450, 580);
    this.setModal(true);
    this.setResizable(false);
    this.setLocationRelativeTo(Totpy.getInstance());
    this.setVisible(true);
  }

  private void loadFromUriString() {
    val uri = JOptionPane.showInputDialog("Enter OTPAuth URI:");
    if (uri != null && !uri.isBlank()) {
      try {
        populateFields(
            OTPAuth.parseTotpUri(uri)
        );
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            "Could not load token from URI string!",
            GUICommon.WINDOW_TITLE,
            JOptionPane.ERROR_MESSAGE
        );
      }
    }
  }

  private void loadFromFile() {
    val fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(false);
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        populateFields(
            OTPAuth.parseTotpUri(
                QRCode.parse(
                    IOHandler.readImage(
                        fileChooser.getSelectedFile().getPath()
                    )
                )
            )
        );
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            "Could not load token from QR code image file!",
            GUICommon.WINDOW_TITLE,
            JOptionPane.ERROR_MESSAGE
        );
      }
    }
  }

  private void save(Token token) {
    try {
      val newToken = validateAndGet(token == null ? null : token.id());

      if (token != null) {
        DBHandler.updateToken(CryptoHandler.lockToken(newToken));
      } else {
        val dbToken = DBHandler.getToken(
            newToken.account(),
            newToken.issuer() == null
                ? null
                : newToken.issuer().name()
        );
        if (dbToken.isEmpty()) {
          DBHandler.insertToken(CryptoHandler.lockToken(newToken));
        } else {
          DBHandler.updateToken(
              dbToken.get().merge(CryptoHandler.lockToken(newToken))
          );
        }
      }

      TokensScreen.getInstance().refresh(
          DBHandler
              .getTokens()
              .stream()
              .map(CryptoHandler::unlockToken)
              .toList()
      );

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this,
          ex.getMessage() == null ? GUICommon.GENERIC_ERROR_MESSAGE : ex.getMessage(),
          GUICommon.WINDOW_TITLE,
          JOptionPane.ERROR_MESSAGE
      );
    }

    this.dispose();
  }

  @SneakyThrows
  private Token validateAndGet(Integer id) {
    val issuer = Issuer.from(issuerField.getText().trim());

    val account = accountField.getText().trim();
    if (account.isEmpty()) {
      throw new MissingMandatoryParameterException(MandatoryParameter.ACCOUNT);
    }

    val category = (Category) categoryField.getSelectedItem();

    val secret = secretField.getText().trim();
    if (secret.isEmpty()) {
      throw new MissingMandatoryParameterException(MandatoryParameter.SECRET);
    }

    val algorithmFieldIndex = algorithmField.getSelectedIndex();
    val digitsFieldIndex = digitsField.getSelectedIndex();
    val periodFieldIndex = periodField.getSelectedIndex();

    return new Token(
        id,
        issuer,
        account,
        category,
        com.filipblazekovic.totpy.model.shared.Type.TOTP,
        HashAlgorithm.from(SUPPORTED_ALGORITHMS[algorithmFieldIndex]),
        SUPPORTED_DIGITS[digitsFieldIndex],
        SUPPORTED_PERIODS[periodFieldIndex],
        new Base32().decode(secret)
    );
  }

  private void populateFields(Token loadedToken) {
    issuerIcon.setIcon(loadedToken.issuer().icon());
    issuerField.setText(loadedToken.issuer().name());
    accountField.setText(loadedToken.account());
    categoryField.setSelectedItem(
        loadedToken.category() == null
            ? Category.DEFAULT
            : loadedToken.category()
    );
    algorithmField.setSelectedItem(loadedToken.algorithm().name());
    digitsField.setSelectedItem(loadedToken.digits());
    periodField.setSelectedItem(loadedToken.period());
    secretField.setText(new Base32().encodeAsString(loadedToken.secret()));
  }

}
