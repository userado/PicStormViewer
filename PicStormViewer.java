package imageutils;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PicStorm Viewer
 *
 * A lightweight, fast image viewer designed for older computers.
 * Key design principle:
 *      - Switching images must remain instant.
 *      - Optional features (zoom, rotation, fit-to-window, dark mode)
 *        are applied ONLY when the user requests them.
 *
 * This file is heavily commented for future developers.
 */
public class PicStormViewer {

    // UI state toggles
    private static boolean fullscreen = false;
    private static boolean fitToWindow = false;
    private static boolean darkMode = false;

    // Image transformation state
    private static double zoom = 1.0;   // Zoom multiplier (1.0 = no zoom)
    private static int rotation = 0;    // Rotation in degrees (0, 90, 180, 270)

    /**
     * Opens a file chooser and returns the selected image file.
     * Only image formats are allowed.
     */
    public static File openImage() {
        JFileChooser fileChooser = new JFileChooser();

        // Restrict file chooser to image formats
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() ||
                        file.getName().toLowerCase().endsWith(".jpg") ||
                        file.getName().toLowerCase().endsWith(".jpeg") ||
                        file.getName().toLowerCase().endsWith(".png") ||
                        file.getName().toLowerCase().endsWith(".gif");
            }

            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });

        int result = fileChooser.showOpenDialog(null);
        return (result == JFileChooser.APPROVE_OPTION)
                ? fileChooser.getSelectedFile()
                : null;
    }

    /**
     * Applies dark mode or light mode to the UI.
     * This affects:
     *      - Frame background
     *      - Label background
     *      - ScrollPane viewport
     *
     * NOTE: Does NOT affect the image itself.
     */
    private static void applyDarkMode(JFrame frame, JLabel label, JScrollPane scrollPane) {
        if (darkMode) {
            frame.getContentPane().setBackground(Color.BLACK);
            label.setBackground(Color.BLACK);
            label.setForeground(Color.WHITE);
            scrollPane.getViewport().setBackground(Color.BLACK);
        } else {
            frame.getContentPane().setBackground(Color.WHITE);
            label.setBackground(Color.WHITE);
            label.setForeground(Color.BLACK);
            scrollPane.getViewport().setBackground(Color.WHITE);
        }
        frame.repaint();
    }

    /**
     * Loads and displays an image with optional transformations.
     *
     * PERFORMANCE NOTE:
     *      - If no transformations are active (fitToWindow=false, zoom=1.0, rotation=0),
     *        the image is displayed directly with NO processing.
     *      - This keeps switching images extremely fast.
     *
     * Transformations are applied ONLY when the user requests them.
     */
    private static void updateImage(JLabel label, File file, JFrame frame) {
        try {
            BufferedImage img = ImageIO.read(file);

            // FAST PATH: no scaling, no rotation, no zoom
            if (!fitToWindow && zoom == 1.0 && rotation == 0) {
                label.setIcon(new ImageIcon(img));
                return;
            }

            // --- ROTATION ---
            BufferedImage rotated = img;
            if (rotation != 0) {
                int w = img.getWidth();
                int h = img.getHeight();

                // Create a new buffer with swapped dimensions for 90/270 degrees
                rotated = new BufferedImage(
                        rotation % 180 == 0 ? w : h,
                        rotation % 180 == 0 ? h : w,
                        BufferedImage.TYPE_INT_ARGB
                );

                Graphics2D g2 = rotated.createGraphics();
                g2.rotate(Math.toRadians(rotation), rotated.getWidth() / 2.0, rotated.getHeight() / 2.0);
                g2.drawImage(img,
                        (rotated.getWidth() - w) / 2,
                        (rotated.getHeight() - h) / 2,
                        null);
                g2.dispose();
            }

            int finalW = rotated.getWidth();
            int finalH = rotated.getHeight();

            // --- FIT TO WINDOW ---
            if (fitToWindow) {
                Dimension view = frame.getContentPane().getSize();
                double scale = Math.min((double) view.width / finalW, (double) view.height / finalH);
                finalW = (int)(finalW * scale);
                finalH = (int)(finalH * scale);
            }

            // --- ZOOM ---
            if (zoom != 1.0) {
                finalW = (int)(finalW * zoom);
                finalH = (int)(finalH * zoom);
            }

            // Scale the image smoothly
            Image scaled = rotated.getScaledInstance(finalW, finalH, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main entry point.
     * Loads the first image, sets up UI, and configures keyboard controls.
     */
    public static void main(String[] args) {

        // Ask user for an image
        File imageFile = openImage();
        if (imageFile == null) return;

        // Load all images in the same folder
        File folder = imageFile.getParentFile();
        File[] files = folder.listFiles();
        if (files == null) return;

        List<File> images = Arrays.stream(files)
                .filter(f -> {
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".jpg") || name.endsWith(".jpeg")
                            || name.endsWith(".png") || name.endsWith(".gif");
                })
                .sorted()
                .collect(Collectors.toList());

        if (images.isEmpty()) return;

        // Track current image index
        final int[] index = { images.indexOf(imageFile) };
        if (index[0] < 0) index[0] = 0;

        // --- UI SETUP ---
        JFrame frame = new JFrame("PicStorm Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setOpaque(true); // Needed for dark mode

        JScrollPane scrollPane = new JScrollPane(label);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Apply initial theme
        applyDarkMode(frame, label, scrollPane);

        // Load first image
        updateImage(label, images.get(index[0]), frame);

        // Reapply fit-to-window when resizing
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (fitToWindow)
                    updateImage(label, images.get(index[0]), frame);
            }
        });

        // --- KEYBOARD CONTROLS ---
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                switch (key) {

                    // --- FAST IMAGE SWITCHING ---
                    case KeyEvent.VK_RIGHT:
                        index[0] = (index[0] + 1) % images.size();
                        zoom = 1.0;
                        rotation = 0;
                        updateImage(label, images.get(index[0]), frame);
                        break;

                    case KeyEvent.VK_LEFT:
                        index[0] = (index[0] - 1 + images.size()) % images.size();
                        zoom = 1.0;
                        rotation = 0;
                        updateImage(label, images.get(index[0]), frame);
                        break;

                    // --- FIT TO WINDOW ---
                    case KeyEvent.VK_F:
                        fitToWindow = !fitToWindow;
                        zoom = 1.0; // Reset zoom when fitting
                        updateImage(label, images.get(index[0]), frame);
                        break;

                    // --- ROTATION ---
                    case KeyEvent.VK_R:
                        rotation = (rotation + 90) % 360;
                        updateImage(label, images.get(index[0]), frame);
                        break;

                    case KeyEvent.VK_L:
                        rotation = (rotation - 90 + 360) % 360;
                        updateImage(label, images.get(index[0]), frame);
                        break;

                    // --- ZOOM ---
                    case KeyEvent.VK_ADD:
                    case KeyEvent.VK_EQUALS:
                        zoom *= 1.25;
                        updateImage(label, images.get(index[0]), frame);
                        break;

                    case KeyEvent.VK_MINUS:
                        zoom /= 1.25;
                        updateImage(label, images.get(index[0]), frame);
                        break;

                    case KeyEvent.VK_0:
                        zoom = 1.0;
                        updateImage(label, images.get(index[0]), frame);
                        break;

                    // --- DARK MODE ---
                    case KeyEvent.VK_D:
                        darkMode = !darkMode;
                        applyDarkMode(frame, label, scrollPane);
                        break;

                    // --- FULLSCREEN ---
                    case KeyEvent.VK_F11:
                        fullscreen = !fullscreen;
                        frame.dispose();
                        frame.setUndecorated(fullscreen);
                        frame.setVisible(true);
                        applyDarkMode(frame, label, scrollPane);
                        updateImage(label, images.get(index[0]), frame);
                        break;
                }
            }
        });
    }
}
