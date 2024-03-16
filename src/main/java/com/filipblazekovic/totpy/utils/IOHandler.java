package com.filipblazekovic.totpy.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipblazekovic.totpy.model.inout.AboutConfig;
import com.filipblazekovic.totpy.model.inout.Config;
import com.filipblazekovic.totpy.model.inout.ExportLocked;
import com.filipblazekovic.totpy.model.inout.ExportLockingPublicKey;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import lombok.SneakyThrows;
import lombok.val;

public class IOHandler {

  private static final String EXPORT_PATH = System.getProperty("user.dir") + File.separator + "export.json";
  private static final String CONFIG_PATH = System.getProperty("user.home") + File.separator + ".totpy" + File.separator + "totpy.json";
  private static final String DB_PATH = System.getProperty("user.home") + File.separator + ".totpy" + File.separator + "totpy.db";

  private IOHandler() {
  }

  public static void generateProjectDirectory() {
    new File(System.getProperty("user.home") + File.separator + ".totpy").mkdirs();
  }

  public static boolean configExists() {
    return new File(CONFIG_PATH).exists();
  }

  public static String getDBPath() {
    return DB_PATH;
  }

  public static String getExportPath() {
    return EXPORT_PATH;
  }

  @SneakyThrows
  public static AboutConfig loadAboutConfig() {
    return new ObjectMapper().readValue(
        IOHandler.class.getClassLoader().getResourceAsStream("about.json"),
        AboutConfig.class
    );
  }

  @SneakyThrows
  public static Config loadConfig() {
    try (val input = new FileInputStream(CONFIG_PATH)) {
      return new ObjectMapper().readValue(input, Config.class);
    }
  }

  @SneakyThrows
  public static void saveConfig(Config config) {
    try (val output = new FileOutputStream(CONFIG_PATH)) {
      output.write(new ObjectMapper().writeValueAsString(config).getBytes(StandardCharsets.UTF_8));
    }
  }

  @SneakyThrows
  public static ExportLocked loadExport(String path) {
    try (val input = new FileInputStream(path)) {
      return new ObjectMapper().readValue(input, ExportLocked.class);
    }
  }

  @SneakyThrows
  public static void saveExport(ExportLocked export) {
    try (val output = new FileOutputStream(EXPORT_PATH)) {
      output.write(new ObjectMapper().writeValueAsString(export).getBytes(StandardCharsets.UTF_8));
    }
  }

  @SneakyThrows
  public static BufferedImage readImage(String path) {
    return ImageIO.read(new File(path));
  }

  @SneakyThrows
  public static Optional<ExportLockingPublicKey> loadPublicKeyFromFile(JDialog parent) {
    val fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(false);
    if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
      val path = fileChooser.getSelectedFile().getPath();
      try (val input = new FileInputStream(path)) {
        return Optional.of(
            new ObjectMapper().readValue(input, ExportLockingPublicKey.class)
        );
      }
    }
    return Optional.empty();
  }

}
