package whatever;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Whatever {

    public static void main(String[] args) throws Throwable {
        File file = new File("D:\\development\\minecraft\\git\\ModJam5\\src\\main\\resources\\assets\\crystals\\textures\\misc\\laser.png");
        File out = new File("D:\\development\\minecraft\\git\\ModJam5\\src\\main\\resources\\assets\\crystals\\textures\\misc\\laser_transparent.png");
        BufferedImage bi = ImageIO.read(file);
        BufferedImage outImg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = outImg.getGraphics();
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                int color = bi.getRGB(x, y) & 0xFF;
                g.setColor(new Color(255, 255, 255, color));
                g.drawRect(x, y, 1, 1);
            }
        }
        g.dispose();
        ImageIO.write(outImg, "PNG", out);
    }

}
