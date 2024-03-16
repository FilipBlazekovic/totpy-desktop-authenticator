package com.filipblazekovic.totpy.utils;

import com.filipblazekovic.totpy.model.shared.CoreIcon;
import com.filipblazekovic.totpy.model.shared.IssuerIcon;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import lombok.SneakyThrows;
import lombok.val;

public class IconStore {

  private static final Map<CoreIcon, Image> coreIcons = new HashMap<>();
  private static final Map<IssuerIcon, Image> issuerIcons = new HashMap<>();

  private IconStore() {
  }

  @SneakyThrows
  public static void initialize() {
    for (CoreIcon icon : CoreIcon.values()) {
      try (val inputStream = IconStore.class.getClassLoader().getResourceAsStream("icons/core/" + icon.name().toLowerCase() + ".png")) {
        assert inputStream != null;
        coreIcons.put(icon, new ImageIcon(inputStream.readAllBytes()).getImage());
      }
    }
    for (IssuerIcon icon : IssuerIcon.values()) {
      try (val inputStream = IconStore.class.getClassLoader().getResourceAsStream("icons/issuer/" + icon.name().toLowerCase() + ".png")) {
        assert inputStream != null;
        issuerIcons.put(icon, new ImageIcon(inputStream.readAllBytes()).getImage());
      }
    }
  }

  public static ImageIcon get(CoreIcon coreIcon) {
    val image = coreIcons.get(coreIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    return new ImageIcon(image);
  }

  public static Image get(IssuerIcon issuer) {
    return issuerIcons.get(issuer);
  }


}
