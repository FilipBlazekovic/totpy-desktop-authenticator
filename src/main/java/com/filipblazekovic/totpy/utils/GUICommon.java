package com.filipblazekovic.totpy.utils;

import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

public class GUICommon {

  private GUICommon() {
  }

  public static final String WINDOW_TITLE = "Totpy";
  public static final String GENERIC_ERROR_MESSAGE = "Something went wrong!";

  public static final Dimension TOGGLE_BUTTON_DIMENSION = new Dimension(50, 30);
  public static final Dimension ACTIONS_PANEL_DIMENSION = new Dimension(500, 45);
  public static final Dimension TOOLBAR_DIMENSION = new Dimension(500, 40);
  public static final Insets TOOLBAR_MARGIN = new Insets(5,5,5,5);

  public static Border getSectionBorder() {
    return new CompoundBorder(
        BorderFactory.createEtchedBorder(),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    );
  }

  public static Border getRootBorder() {
    return new CompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        BorderFactory.createRaisedBevelBorder()
    );
  }

}

