package com.company;

import com.company.hsv_lab.Lab;
import com.company.hsv_lab.HSV;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImagePanel extends JPanel {

    public ImagePanel() {

        this.addComponentListener(new ComponentAdapter() {
            //перерисовывает картинку в случае изменения размеров фрейма...
            @Override
            public void componentResized(ComponentEvent ce) {
                repaint();
            }
        });
        setLayout(null);

        this.addMouseMotionListener(new MouseMotionListener() {
            //рисуем прямоугольник, который показывает что выделяется
            @Override
            public void mouseDragged(MouseEvent e) {
                double x = 0, y = 0, w = 0, h = 0;
                double eX = e.getX(), eY = e.getY(), pX = press.getX(), pY = press.getY();
                double minX = 0, maxX = 0, minY = 0, maxY = 0;

                if (eX > pX) {
                    minX = pX;
                    maxX = eX;
                } else {
                    minX = eX;
                    maxX = pX;
                }

                if (eY > pY) {
                    minY = pY;
                    maxY = eY;
                } else {
                    minY = eY;
                    maxY = pY;
                }
                minX = Math.max(minX, xImg);
                minY = Math.max(minY, yImg);
                maxX = Math.min(maxX, xImg + wImg);
                maxY = Math.min(maxY, yImg + hImg);

                x = Math.floor(minX / zoom) * zoom;
                y = Math.floor(minY / zoom) * zoom;
                w = Math.ceil((maxX - x) / zoom) * zoom;
                h = Math.ceil((maxY - y) / zoom) * zoom;

                if (w > 0 && h > 0) {
                    setRect(new Rectangle2D.Double(x, y, w, h));
                }
            }

            public void mouseMoved(MouseEvent e) {
               /* GUI.message.setText(GUI.emptyMessage);
                RGB rgb = getRGB(e.getLocationOnScreen());
                Point p = getMousePosition();
                if (!(p.x < xImg || p.y < yImg ||
                        p.x > xImg + wImg || p.y > yImg + hImg)) {

                    String text = getPointForImageInString(p.x, p.y) + "\n" +
                            rgb.outInteger() + ";\n" + new HSV(rgb).outInteger() + ";\n" + new Lab(rgb).outInteger();
                    GUI.message.setText(text);
                }*/
            }
        });

        this.addMouseListener(new MouseListener() {
                                  public void mouseEntered(MouseEvent e) {
                                  }

                                  public void mouseExited(MouseEvent e) {
                                  }

                                  public void mouseClicked(MouseEvent e) {
                                      GUI.message.setText(GUI.emptyMessage);
                                      RGB rgb = getRGB(e.getLocationOnScreen());
                                      Point p = getMousePosition();
                                      Lab lab = new Lab(rgb);
                                      HSV hsv = new HSV(rgb);
                                      String text = getPointForImageInString(p.x, p.y) + "\n" +
                                              rgb.outInteger() + ";\n" + hsv.outInteger() + ";\n" + lab.outInteger();
                                      GUI.message.setText(text);

                                  }

                                  private RGB getRGB(Point p) {
                                      RGB rgb = new RGB(-1, -1, -1);
                                      try {
                                          Robot robot = new Robot();
                                          Color colors = robot.getPixelColor(p.x, p.y);
                                          rgb.setRGB(colors.getRed(), colors.getGreen(), colors.getBlue());
                                          return rgb;

                                      } catch (AWTException ex) {
                                          System.err.println(ex.getMessage() + "problem click");
                                          return rgb;
                                      }
                                  }

                                  public void mousePressed(MouseEvent e) {
                                      press = e.getPoint();
                                      pressedBtn = true;
                                  }

                                  public void mouseReleased(MouseEvent e) {
                                      GUI.message.setText(GUI.emptyMessage);
                                      pressedBtn = false;
                                      Rectangle2D rect = getRect();
                                      //вычисляем координаты выделенной области
                                      if (rect.getWidth() <= 0 || rect.getHeight() <= 0) return;

                                      double rectX1 = rect.getMinX() + zoom / 2;
                                      double rectY1 = Math.ceil(rect.getMinY()) + zoom / 2;
                                      double rectX2 = rect.getMaxX();
                                      double rectY2 = Math.ceil(rect.getMaxY());
                                      BufferedImage bImage = (BufferedImage) createImage(getWidth(), getHeight()); // загружаем картинку в буффер
                                      paint(bImage.getGraphics()); // для изменения при очередном клике, без этого неверно обращение к пикселю по координате

                                      // здесь будем записывать в файл текст сообщения с координатами, rgb и т.д.
                                      String path = ".\\colors_of_rect.txt";

                                      RGB rgb = new RGB();
                                      Lab lab = new Lab();
                                      HSV hsv = new HSV();
                                      try (FileWriter writer = new FileWriter(path, false)) {
                                          int x, y, argb;
                                          for (double i = rectX1; i <= rectX2; i += zoom) {
                                              for (double j = rectY1; j <= rectY2; j += zoom) {
                                                  x = (int) i;
                                                  y = (int) j;
                                                  argb = new Color(bImage.getRGB(x, y)).getRGB();
                                                  rgb.setRGB((argb >> 16) & 0xff, (argb >> 8) & 0xff, argb & 0xff);
                                                  lab.setLab(rgb);
                                                  hsv.setHSV(rgb);
                                                  writer.write(getPointForImageInString(x, y) + ":         " +
                                                          rgb.outInteger() + ";      " +
                                                          hsv.outInteger() + ";      " +
                                                          lab.outInteger() + "");

                                                  writer.append('\n');
                                              }
                                              writer.append('\n');
                                          }
                                          writer.flush();
                                      } catch (IOException ex) {
                                          System.out.println(ex.getMessage());
                                      }

                                      // считываем из файла текст в панель сообщений
                                      try {
                                          GUI.message.read(new FileReader(path), null);
                                      } catch (IOException ex) {
                                          System.out.println("problem accessing file" + path);
                                      }

                                      repaint();

                                  }
                              }
        );

    }

    private RGB getRGB(Point p) {
        RGB rgb = new RGB(-1, -1, -1);
        try {
            Robot robot = new Robot();
            Color colors = robot.getPixelColor(p.x, p.y);
            rgb.setRGB(colors.getRed(), colors.getGreen(), colors.getBlue());
            return rgb;

        } catch (AWTException ex) {
            System.err.println(ex.getMessage() + "problem click");
            return rgb;
        }
    }

    // для получения и изменения rect
    public Rectangle2D getRect() {
        return rect;
    }

    public void setRect(Rectangle2D rect) {
        this.rect = rect;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        if (img != null) {
            //сжимаем до удобного размера
            g2.drawImage(img, 0, 0, widthPanel, heightPanel,
                    null);
            if (pressedBtn) {
                g2.setColor(new Color (2, 5, 50, 50));
                g2.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
                g2.setColor(new Color (2, 5, 50, 100));
                g2.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    public void screenSizeTransfer(int requiedWidth, int requiedHeigth, int widthChangePanel, int heighButton) {
        this.requiedWidth = requiedWidth;
        this.requiedHeigth = requiedHeigth;
        this.startImageY = heighButton;
        this.startImageX = widthChangePanel;
    }

    // устанавливаем значения переменных для текущего изображения по умолчанию
    public void setImage(String path) {
        try {
            img = ImageIO.read(new File(path));
            BufferedImage image = ImageIO.read(new File(path));

            zoom = Math.min((double) requiedWidth / image.getWidth(), (double) requiedHeigth / image.getHeight());
            widthPanel = (int) (image.getWidth() * zoom);
            heightPanel = (int) (image.getHeight() * zoom);
            xImg = 0;
            yImg = 0;
            wImg = (int) ((double) image.getWidth() * zoom);
            hImg = (int) ((double) image.getHeight() * zoom);
            setBounds(startImageX, startImageY, widthPanel, heightPanel);
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // для получения координат с учетом zoom
    public String getPointForImageInString(int x, int y) {
        return "x = " + Integer.toString((int) (x / zoom)) +
                ",  y = " + Integer.toString((int) (y / zoom));
    }

    public int getWidthPanel(){return widthPanel;}
    public int getHeightPanel(){return heightPanel;}

    private int xImg, yImg, wImg, hImg; // xImg, yImg, wImg, hImg - координаты первого и второго угла прямоугольника,
    // который показываем.
    private Rectangle2D rect = new Rectangle2D.Double();
    private Image img = null;
    private Point2D press = new Point2D.Double(0, 0);
    private boolean pressedBtn = false;
    private int heightPanel, widthPanel;
    private int requiedWidth, requiedHeigth, startImageY, startImageX;
    private double zoom;
}
