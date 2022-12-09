package com.company;

import com.company.filters.FilterPanel;
import com.company.hsv_lab.HSVLabPanel;
import com.company.segmentation.SegmentationPanel;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import static java.lang.Double.min;
import static java.lang.StrictMath.max;

public class GUI extends JFrame {
    public GUI() {
        frameDesign();
        insertMenuBar();
        insertImagePanel();
        insertMessagePanel();
        insertButtonSelectImage();
        insertButtonCancelAllChanges();
    }

    private void insertImagePanel() {
        image = new ImagePanel();
        image.setBackground(cuteColor.darker());
        image.setBounds(widthChangePanel, heightButton, screenSize.width - widthChangePanel, screenSize.height - heightMessagePanel - heightButton);
        add(image);
    }

    private void chooseImage() {
        lastChange = "";
        isImageSelected = false;
        JFileChooser chooser = new JFileChooser();
        if (chooser.showDialog(null, "Select image") == JFileChooser.APPROVE_OPTION) {
            image.screenSizeTransfer(requiedImageWidth - 1, requiedImageHeight, widthChangePanel + 1, 0); // передаем классу ImagePanel параметры экрана
            pathImageSource = chooser.getSelectedFile().getPath();
            returnSourceImage();
            message.setText(emptyMessage);
        }
    }

    private void insertButtonSelectImage() {
        JButton button = new JButton("☆*:.｡. Select image .｡.:*☆");
        button.setBackground(cuteColor);
        button.setBounds(0, 0, widthChangePanel, heightButton);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseImage();
            }

        });
        add(button);
    }

    private void insertButtonCancelAllChanges() {
        JButton buttonClear = new JButton("☆*:.｡. Cancel all changes .｡.:*☆");
        buttonClear.setBackground(cuteColor);
        buttonClear.setBounds(0, heightButton, widthChangePanel + 1, heightButton);
        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnSourceImage();
            }
        });
        add(buttonClear);
    }

    private void insertMenuBar() {
        JMenuBar menuBarToSelectPanel = new JMenuBar();
        menuBarToSelectPanel.setBounds(0, 2 * heightButton, widthChangePanel, heightButton);
        menuBarToSelectPanel.setBackground(cuteColor);
        this.add(menuBarToSelectPanel);
        JMenu menuToSelectPanel = new JMenu("        ☆*:.｡. Select  panel .｡.:*☆        ");
        menuBarToSelectPanel.add(menuToSelectPanel);

        panelForChanges = new JPanel(new CardLayout());
        panelForChanges.setBounds(dimensionsForChangePanel);
        this.add(panelForChanges);

        menuToSelectPanel.add(new Item("Segmentation panel"));
        SegmentationPanel panelForSegmentation = new SegmentationPanel();
        panelForChanges.add(panelForSegmentation, "Segmentation panel");

        menuToSelectPanel.add(new Item("Filter panel"));
        FilterPanel panelForFilters = new FilterPanel();
        panelForChanges.add(panelForFilters, "Filter panel");

        menuToSelectPanel.add(new Item("HSV/Lab panel"));
        panelForHSVLAB = new HSVLabPanel();
        panelForChanges.add(panelForHSVLAB, "HSV/Lab panel");

    }

    private class Item extends JMenuItem{
        public Item(String name){
            super(name);
            this.setBackground(cuteColor);
            this.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((CardLayout) panelForChanges.getLayout()).show(panelForChanges, name);
                }
            });

        }
    }

    private void insertMessagePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        message = new JTextArea();
        message.setBackground(cuteColor);
        message.setText("☆*:.｡.(＾• ω •＾).｡.:*☆\n\n\n");
        message.setAutoscrolls(true);
        message.setEditable(false); // заблокировано редактирование текста
        panel.add(message);
        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane);
        scrollPane.setBounds(widthChangePanel, screenSize.height - heightMessagePanel, screenSize.width - widthChangePanel - 6, heightMessagePanel);
    }

    public static void normalizationImage() {
        double mn = 0, mx = 255;
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                mn = min(min(mn, matrixForImage[x][y].getB()), min(matrixForImage[x][y].getR(), matrixForImage[x][y].getG()));
                mx = max(max(mx, matrixForImage[x][y].getB()), max(matrixForImage[x][y].getR(), matrixForImage[x][y].getG()));
            }
        }

        for (int x = 0; x < bufferedImage.getWidth() && (mn < 0 || mx > 255); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                RGB rgb = (matrixForImage[x][y].addRGB(new RGB(-mn, -mn, -mn))).multiplyDouble(255. / (mx - mn));
                matrixForImage[x][y] = rgb;
            }
        }
    }

    public static void overwritingSavedChange() {
        try {
            ImageIO.write(ImageIO.read(new File(pathLastChange)), "jpg", new File(pathSavedChange));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void repaintLastChange(String name, String mes) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int argb = new Color(bufferedImage.getRGB(x, y)).getRGB();
                bufferedImage.setRGB(x, y, matrixForImage[x][y].getIntRGB((argb >> 24) & 0xff));
            }
        }
        try {
            ImageIO.write(bufferedImage, "jpg", new File(GUI.pathLastChange));
            image.setImage(pathLastChange);
            lastChange = name;
            message.setText(mes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void returnSourceImage() {
        image.setImage(pathImageSource);
        try {
            ImageIO.write(ImageIO.read(new File(pathImageSource)), "jpg", new File(pathSavedChange));
            ImageIO.write(ImageIO.read(new File(pathImageSource)), "jpg", new File(pathLastChange));
            isImageSelected = true;
            message.setText(emptyMessage);
            panelForHSVLAB.resetSliders();
            FilterPanel.textSigma.setText("2");
            FilterPanel.textTheta.setSelectedIndex(0);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void frameDesign() {
        setTitle("                                     Digital image processing");
        Image icon = Toolkit.getDefaultToolkit().getImage("src\\icon.png");
        setIconImage(icon);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.setSize(screenSize.width, screenSize.height - 40);
        heightButton = (int) (screenSize.height * 0.04);
        heightMessagePanel = (int) (screenSize.height * 0.2);
        widthChangePanel = (int) (screenSize.width * 0.16);

        setSize(screenSize.width, screenSize.height);
        requiedImageHeight = screenSize.height - heightMessagePanel;
        requiedImageWidth = screenSize.width - widthChangePanel;
        dimensionsForChangePanel = new Rectangle(0, 3 * heightButton, widthChangePanel, screenSize.height - 4 * heightButton);
        JPanel startPanel = new JPanel();
        startPanel.setBounds(dimensionsForChangePanel);
        startPanel.setBackground(cuteColor);

        setLayout(null);
        setBackground(cuteColor.darker());
        getContentPane().setBackground(cuteColor.darker());
        setResizable(false);
    }

    public static boolean isInteger(String str) {
        for (int i=0; i<str.length(); i++)
            if (!(str.charAt(i)>='0' && str.charAt(i)<='9'))
                return false;
        return true;
    }

    public static boolean isDouble(String str) {
        int kol=0;
        for (int i=0; i<str.length(); i++) {
            if (!(str.charAt(i) >= '0' && str.charAt(i) <= '9' || str.charAt(i) == '.')) {
                return false;
            }
            if (str.charAt(i) == '.')
                kol++;
        }
        return kol==1;
    }

    public static RGB matrixForImage[][];
    public static BufferedImage bufferedImage;
    public static ImagePanel image;
    public static JTextArea message;
    public static String pathImageSource, pathSavedChange = ".\\saved_change.jpg", pathLastChange = ".\\last_change.jpg";

    private JPanel panelForChanges;
    private HSVLabPanel panelForHSVLAB;

    private Dimension screenSize;
    public static int heightButton, heightMessagePanel, widthChangePanel;
    private int requiedImageWidth, requiedImageHeight;
    public static Rectangle dimensionsForChangePanel;

    public static String lastChange = "";
    public static boolean isImageSelected = false;
    public static String emptyMessage = "";
    public static Color cuteColor = Color.decode("#F8F8FF");
}
