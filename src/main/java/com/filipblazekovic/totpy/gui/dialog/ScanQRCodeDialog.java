package com.filipblazekovic.totpy.gui.dialog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.crypto.CryptoHandler;
import com.filipblazekovic.totpy.db.DBHandler;
import com.filipblazekovic.totpy.gui.screen.TokensScreen;
import com.filipblazekovic.totpy.model.inout.ExportLockingPublicKey;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.utils.BGWorkers;
import com.filipblazekovic.totpy.utils.GUICommon;
import com.filipblazekovic.totpy.utils.OTPAuth;
import com.filipblazekovic.totpy.utils.QRCode;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import lombok.val;
import net.miginfocom.swing.MigLayout;

public class ScanQRCodeDialog extends JDialog {

  private final AtomicBoolean started = new AtomicBoolean(false);

  private Webcam webcam = null;

  public ScanQRCodeDialog() {
    try {

      val size = WebcamResolution.QVGA.getSize();
      webcam = Webcam.getDefault();
      webcam.setViewSize(size);

      webcam.addWebcamListener(new WebcamListener() {
        @Override
        public void webcamOpen(WebcamEvent webcamEvent) {
        }
        @Override
        public void webcamClosed(WebcamEvent webcamEvent) {
        }
        @Override
        public void webcamDisposed(WebcamEvent webcamEvent) {
        }
        @Override
        public void webcamImageObtained(WebcamEvent webcamEvent) {
          parseWebcamImage(webcamEvent.getImage()).ifPresent(result -> {
            try {
              val token = OTPAuth.parseTotpUri(result);
              if (!started.get()) {
                started.set(true);

                val dbToken = DBHandler.getToken(
                    token.account(),
                    token.issuer() == null
                        ? null
                        : token.issuer().name()
                );

                if (dbToken.isEmpty()) {
                  DBHandler.insertToken(CryptoHandler.lockToken(token));
                } else {
                  DBHandler.updateToken(
                      dbToken.get().merge(CryptoHandler.lockToken(token))
                  );
                }

                TokensScreen.getInstance().refresh(
                    DBHandler
                        .getTokens()
                        .stream()
                        .map(CryptoHandler::unlockToken)
                        .toList()
                );

                dispose();
              }
            } catch (Exception ignored) {
              started.set(false);
            }
          });
        }
      });

      this.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          webcam.close();
        }
      });

      applyCommonSetup(size);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          this,
          "Web camera not available!",
          GUICommon.WINDOW_TITLE,
          JOptionPane.ERROR_MESSAGE
      );
      dispose();
    }
  }

  public ScanQRCodeDialog(ExportDialog parent, List<Token> tokens) {
    try {

      val size = WebcamResolution.QVGA.getSize();
      webcam = Webcam.getDefault();
      webcam.setViewSize(size);

      webcam.addWebcamListener(new WebcamListener() {
        @Override
        public void webcamOpen(WebcamEvent webcamEvent) {
        }
        @Override
        public void webcamClosed(WebcamEvent webcamEvent) {
        }
        @Override
        public void webcamDisposed(WebcamEvent webcamEvent) {
        }
        @Override
        public void webcamImageObtained(WebcamEvent webcamEvent) {
          parseWebcamImage(webcamEvent.getImage()).ifPresent(result -> {
            try {
              val exportLockingPublicKey = new ObjectMapper().readValue(
                  result.getBytes(StandardCharsets.UTF_8),
                  ExportLockingPublicKey.class
              );
              if (!started.get()) {
                started.set(true);
                BGWorkers.export(parent, tokens, null, exportLockingPublicKey);
                dispose();
              }
            } catch(Exception ignored) {
              started.set(false);
            }
          });
        }
      });

      this.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          webcam.close();
        }
      });

      applyCommonSetup(size);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          this,
          "Web camera not available!",
          GUICommon.WINDOW_TITLE,
          JOptionPane.ERROR_MESSAGE
      );
      dispose();
    }
  }

  private void applyCommonSetup(Dimension size) {
    val panel = new WebcamPanel(webcam);
    panel.setPreferredSize(size);
    panel.setFPSDisplayed(true);
    panel.setMirrored(false);

    val rootPane = new JPanel(new MigLayout("wrap 1, alignx center, width 100%"));
    rootPane.setBorder(GUICommon.getRootBorder());
    rootPane.add(panel, "alignx center");

    this.setTitle(GUICommon.WINDOW_TITLE);
    this.setContentPane(rootPane);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(500, 260);
    this.setModal(true);
    this.setResizable(false);
    this.setLocationRelativeTo(Totpy.getInstance());
    this.setVisible(true);
  }

  private Optional<String> parseWebcamImage(BufferedImage image) {
    try {
      return Optional.of(QRCode.parse(image));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

}
