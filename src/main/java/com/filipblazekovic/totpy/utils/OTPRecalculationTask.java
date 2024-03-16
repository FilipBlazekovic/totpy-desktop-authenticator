package com.filipblazekovic.totpy.utils;

import com.filipblazekovic.totpy.crypto.CryptoHandler;
import com.filipblazekovic.totpy.gui.panel.TokenPanel;
import java.util.List;
import javax.swing.SwingUtilities;
import lombok.val;

public class OTPRecalculationTask implements Runnable {

  private final List<TokenPanel> tokenPanels;

  private boolean firstRun = true;

  public OTPRecalculationTask(List<TokenPanel> tokenPanels) {
    this.tokenPanels = tokenPanels;
  }

  @Override
  public void run() {

    tokenPanels.forEach(tp -> {
      val token = tp.getToken();

      val remainingSeconds = CryptoHandler.getRemainingOTPSeconds(token.period());
      val calculatedOtp = (firstRun || CryptoHandler.shouldRecalculateOTP(token.period()))
          ? CryptoHandler.calculateOTP(token)
          : tp.getOtp();

      SwingUtilities.invokeLater(() -> {
        tp.setRemainingSeconds(remainingSeconds);
        tp.setOtp(calculatedOtp);
      });
    });

    firstRun = false;
  }

}
