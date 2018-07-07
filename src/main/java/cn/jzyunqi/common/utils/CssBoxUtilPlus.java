package cn.jzyunqi.common.utils;

import cz.vutbr.web.css.MediaSpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DefaultDOMSource;
import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.io.StreamDocumentSource;
import org.fit.cssbox.layout.BrowserCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class CssBoxUtilPlus {
    private CssBoxUtilPlus() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CssBoxUtilPlus.class);

    public static void renderHtmlStr(String htmlStr, OutputStream os, int width, int height) throws Exception {
        Dimension windowSize = new Dimension(width, height);

        //Open the network connection
        DocumentSource docSource = new StreamDocumentSource(new ByteArrayInputStream(htmlStr.getBytes("UTF-8")), null, "text/html");

        //Parse the input document
        DOMSource parser = new DefaultDOMSource(docSource);
        Document doc = parser.parse();

        //create the media specification
        MediaSpec media = new MediaSpec("screen");
        media.setDimensions(windowSize.width, windowSize.height);
        media.setDeviceDimensions(windowSize.width, windowSize.height);

        //Create the CSS analyzer
        DOMAnalyzer da = new DOMAnalyzer(doc, docSource.getURL());
        da.setMediaSpec(media);
        da.attributesToStyles(); //convert the HTML presentation attributes to inline styles
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the standard style sheet
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the additional style sheet
        da.addStyleSheet(null, CSSNorm.formsStyleSheet(), DOMAnalyzer.Origin.AGENT); //render form fields using css
        da.getStyleSheets(); //load the author style sheets

        BrowserCanvas contentCanvas = new BrowserCanvas(da.getRoot(), da, docSource.getURL());
        contentCanvas.setAutoMediaUpdate(false); //we have a correct media specification, do not update
        contentCanvas.getConfig().setClipViewport(false);
        contentCanvas.getConfig().setLoadImages(true);
        contentCanvas.getConfig().setLoadBackgroundImages(true);
        contentCanvas.getConfig().setImageLoadTimeout(5000);

        contentCanvas.createLayout(windowSize);

        ImageIO.write(contentCanvas.getImage(), "jpg", os);

        docSource.close();
    }

    public static String getBase64Image(String filePath) throws IOException {
        if (filePath.startsWith("http:") || filePath.startsWith("https:")) {
            byte[] img = IOUtils.toByteArray(new URL(filePath).openStream());
            return "data:image/jpeg;base64," + Base64.encodeBase64String(img);
        } else {
            BufferedImage image = ImageIO.read(new File(filePath));
            return getBase64Image(image);
        }
    }

    public static String getBase64Image(BufferedImage image) throws IOException {
        ByteArrayOutputStream bos = null;
        try {
            //转换二维码到base64
            bos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", bos);
            byte[] bytes = bos.toByteArray();
            bos.close();
            return "data:image/jpeg;base64," + Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            LOGGER.error("====image to base64 string error:", e);
            return StringUtilPlus.EMPTY;
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }
}
