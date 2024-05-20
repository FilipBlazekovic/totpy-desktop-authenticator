package com.filipblazekovic.totpy.model.shared;

import com.google.common.base.CaseFormat;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import lombok.val;

public enum Category {

  DEFAULT,
  EDUCATION,
  EMAIL,
  FINANCE,
  PERSONAL,
  SOCIAL_NETWORK,
  STEM,
  TRAVEL,
  VOIP,
  VPN,
  WORK;

  public Border getBorder() {
    val titledBorder = BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
        getDisplayName()
    );

    switch (this) {
      case DEFAULT -> titledBorder.setTitleColor(new Color(235, 219, 178));
      case EDUCATION -> titledBorder.setTitleColor(new Color(69, 133, 136));
      case EMAIL -> titledBorder.setTitleColor(new Color(177, 98, 134));
      case FINANCE -> titledBorder.setTitleColor(new Color(104, 157, 106));
      case PERSONAL -> titledBorder.setTitleColor(new Color(204, 36, 29));
      case SOCIAL_NETWORK -> titledBorder.setTitleColor(new Color(108, 24, 181));
      case STEM -> titledBorder.setTitleColor(new Color(24, 29, 181));
      case TRAVEL -> titledBorder.setTitleColor(new Color(214, 94, 14));
      case VOIP -> titledBorder.setTitleColor(new Color(39, 181, 24));
      case VPN -> titledBorder.setTitleColor(new Color(99, 4, 21));
      case WORK -> titledBorder.setTitleColor(new Color(152, 151, 26));
    }

    return titledBorder;
  }

  public String getDisplayName() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, this.name());
  }

  public static Category from(String category) {
    try {
      return Category.valueOf(category);
    } catch (Exception e) {
      return Category.DEFAULT;
    }
  }
}
