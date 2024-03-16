package com.filipblazekovic.totpy.utils;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.crypto.CryptoHandler;
import com.filipblazekovic.totpy.crypto.LoginHandler;
import com.filipblazekovic.totpy.db.DBConnection;
import com.filipblazekovic.totpy.db.DBHandler;
import com.filipblazekovic.totpy.exception.InvalidLoginCredentialsException;
import com.filipblazekovic.totpy.exception.PasswordsDoNotMatchException;
import com.filipblazekovic.totpy.exception.WeakPasswordException;
import com.filipblazekovic.totpy.gui.dialog.ChangePasswordDialog;
import com.filipblazekovic.totpy.gui.dialog.ExportDialog;
import com.filipblazekovic.totpy.gui.dialog.ImportDialog;
import com.filipblazekovic.totpy.gui.screen.InitializationScreen;
import com.filipblazekovic.totpy.gui.screen.TokensScreen;
import com.filipblazekovic.totpy.model.inout.ExportLocked;
import com.filipblazekovic.totpy.model.inout.ExportLockingPublicKey;
import com.filipblazekovic.totpy.model.inout.ExportPassword;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.internal.WorkerResponse;
import java.io.FileNotFoundException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import lombok.val;

public class BGWorkers {

  public static void init(InitializationScreen parent, String keyFilePath, char[] password, char[] passwordConfirmation) {
    new SwingWorker<WorkerResponse, Void>() {

      @Override
      protected WorkerResponse doInBackground() {
        try {
          if (keyFilePath != null) {
            LoginHandler.initialize(keyFilePath, password, passwordConfirmation);
          } else {
            LoginHandler.initialize(password, passwordConfirmation);
          }
        } catch (PasswordsDoNotMatchException | WeakPasswordException | FileNotFoundException e) {
          return WorkerResponse.error(e.getMessage());
        } catch (Exception e) {
          return WorkerResponse.error(GUICommon.GENERIC_ERROR_MESSAGE);
        }
        return WorkerResponse.success(Collections.emptyList());
      }

      @Override
      protected void done() {
       Totpy.getInstance().hideProgressPane();
        try {
          handleResponse(parent, get());
        } catch (Exception ignored) {
        }
      }

    }.execute();
  }

  public static void login(JPanel parent, String keyFilePath, char[] password) {
    new SwingWorker<WorkerResponse, Void>() {

      @Override
      protected WorkerResponse doInBackground() {
        try {
          if (keyFilePath != null) {
            LoginHandler.login(keyFilePath, password);
          } else {
            LoginHandler.login(password);
          }
        } catch (FileNotFoundException | InvalidLoginCredentialsException e) {
          return WorkerResponse.error(e.getMessage());
        } catch (Exception e) {
          return WorkerResponse.error(GUICommon.GENERIC_ERROR_MESSAGE);
        }
        return WorkerResponse.success(
            DBHandler
                .getTokens()
                .stream()
                .map(CryptoHandler::unlockToken)
                .toList()
        );
      }

      @Override
      protected void done() {
        Totpy.getInstance().hideProgressPane();
        try {
          handleResponse(parent, get());
        } catch (Exception ignored) {
        }
      }

    }.execute();
  }

  public static void changePassword(
      ChangePasswordDialog parent,
      char[] currentPassword,
      String keyFilePath,
      char[] newPassword,
      char[] newPasswordConfirmation
  ) {
    new SwingWorker<Optional<String>, Void>() {

      @Override
      protected Optional<String> doInBackground() {
        try {
          LoginHandler.authenticate(currentPassword);
          if (keyFilePath != null) {
            LoginHandler.changePassword(keyFilePath, newPassword, newPasswordConfirmation);
            Totpy.updateCurrentConfig(true);
          } else {
            CryptoHandler.clearKeyFile();
            LoginHandler.changePassword(newPassword, newPasswordConfirmation);
            Totpy.updateCurrentConfig(false);
          }
          return Optional.empty();
        } catch (FileNotFoundException | InvalidLoginCredentialsException | PasswordsDoNotMatchException | WeakPasswordException e) {
          return Optional.of(e.getMessage());
        } catch (Exception e) {
          return Optional.of(GUICommon.GENERIC_ERROR_MESSAGE);
        }
      }

      @Override
      protected void done() {
        parent.hideProgressPane();
        try {
          val errorMessage = get();
          if (errorMessage.isPresent()) {
            JOptionPane.showMessageDialog(parent, errorMessage.get(), GUICommon.WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
          }
          parent.dispose();
        } catch (Exception ignored) {
        }
      }

    }.execute();
  }

  public static void export(ExportDialog parent, List<Token> tokens, ExportPassword password, ExportLockingPublicKey publicKey) {
    new SwingWorker<Optional<String>, Void>() {

      @Override
      protected Optional<String> doInBackground() {
        try {
          if (password != null) {
            IOHandler.saveExport(CryptoHandler.generateLockedExport(tokens, password));
          } else {
            IOHandler.saveExport(CryptoHandler.generateLockedExport(tokens, publicKey));
          }
          Totpy.updateCurrentConfig(OffsetDateTime.now());
        } catch (Exception e) {
          return Optional.of(GUICommon.GENERIC_ERROR_MESSAGE);
        }
        return Optional.empty();
      }

      @Override
      protected void done() {
        parent.hideProgressPane();
        try {
          val errorMessage = get();
          if (errorMessage.isPresent()) {
            JOptionPane.showMessageDialog(parent, errorMessage, GUICommon.WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
          }
          JOptionPane.showMessageDialog(
              parent,
              "Export saved to " + IOHandler.getExportPath(),
              GUICommon.WINDOW_TITLE,
              JOptionPane.INFORMATION_MESSAGE
          );
          parent.dispose();
        } catch (Exception ignored) {
        }
      }

    }.execute();
  }

  public static void processImport(
      ImportDialog parent,
      ExportLocked exportLocked,
      char[] password,
      boolean clearDatabase,
      boolean overwriteExistingTokens
  ) {
    new SwingWorker<WorkerResponse, Void>() {

      @Override
      protected WorkerResponse doInBackground() {
        try {

          val export = (password != null)
              ? CryptoHandler.unlockExport(exportLocked, password)
              : CryptoHandler.unlockExport(exportLocked);

          DBConnection.setAutocommit(false);

          if (clearDatabase) {
            DBHandler.deleteAllTokens();
          }

          Token.from(export.tokens()).forEach(t -> {
            if (overwriteExistingTokens) {
              val dbToken = DBHandler.getToken(t.account(), t.issuer() == null ? null : t.issuer().name());
              if (dbToken.isEmpty()) {
                DBHandler.insertToken(CryptoHandler.lockToken(t));
              } else {
                DBHandler.updateToken(
                    dbToken.get().merge(CryptoHandler.lockToken(t))
                );
              }
            } else {
              DBHandler.insertToken(CryptoHandler.lockToken(t));
            }
          });

          DBConnection.commit();

          return WorkerResponse.success(
              DBHandler
                  .getTokens()
                  .stream()
                  .map(CryptoHandler::unlockToken)
                  .toList()
          );

        } catch (Exception e) {
          DBConnection.rollback();
          DBConnection.setAutocommit(true);
          return WorkerResponse.error("Could not import tokens!");
        }
      }

      @Override
      protected void done() {
        parent.hideProgressPane();
        try {

          val response = get();
          val errorMessage = response.errorMessage();
          if (errorMessage != null) {
            JOptionPane.showMessageDialog(parent, errorMessage, GUICommon.WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
            parent.dispose();
            return;
          }

          TokensScreen.getInstance().refresh(response.tokens());
          parent.dispose();

        } catch (Exception ignored) {
        }
      }

    }.execute();
  }

  private static void handleResponse(JPanel parent, WorkerResponse response) {
    val errorMessage = response.errorMessage();
    if (errorMessage != null) {
      JOptionPane.showMessageDialog(parent, errorMessage, GUICommon.WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
      return;
    }
    Totpy.getInstance().showTokensScreen(response.tokens());
  }

}
