package com.martianLife.htmltopdf;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.UUID;
import java.nio.charset.Charset;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.FontProvider;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import android.os.Environment;

public class RNHTMLtoPDFModule extends ReactContextBaseJavaModule {

  private Promise promise;
  private final ReactApplicationContext mReactContext;

  public RNHTMLtoPDFModule(ReactApplicationContext reactContext) {
    super(reactContext);
    mReactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNHTMLtoPDF";
  }

  @ReactMethod
  public void convert(final ReadableMap options, final Promise promise) {
    try {
      File destinationFile;
      String htmlString = options.hasKey("html") ? options.getString("html") : null;
      if (htmlString == null) return;

      String fileName;
      if (options.hasKey("fileName")) {
        fileName = options.getString("fileName");
      } else {
        fileName = UUID.randomUUID().toString();
      }

      if (options.hasKey("directory") && options.getString("directory").equals("docs")) {
        File path = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
        if (!path.exists()) path.mkdir();
        destinationFile = new File(path, fileName + ".pdf");
      } else {
        destinationFile = getTempFile(fileName);
      }

      String filePath = convertToPDF(htmlString, destinationFile);
      String base64 = "";

      if (options.hasKey("base64") && options.getBoolean("base64") == true) {
        base64 = Base64.encodeFromFile(filePath);
      }

      WritableMap resultMap = Arguments.createMap();
      resultMap.putString("filePath", filePath);
      resultMap.putString("base64", base64);

      promise.resolve(resultMap);
    } catch (Exception e) {
      promise.reject(e.getMessage());
    }
  }

  private String convertToPDF(String htmlString, File file) throws Exception {
    try {
      Document document = new Document();
      InputStream in = new ByteArrayInputStream(htmlString.getBytes());

      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
      document.open();
      //document.add(new Paragraph("testpdf~~成功哩", setChineseFont()));
      XMLWorkerHelper.getInstance().parseXHtml(writer, document, in, null, Charset.forName("UTF-8"), new ChinaFontProvide());
      document.close();
      in.close();

      String absolutePath = file.getAbsolutePath();

      return absolutePath;
    } catch (Exception e) {
      throw new Exception(e);
    }
  }

  private File getTempFile(String fileName) throws Exception {
    try {
      File outputDir = getReactApplicationContext().getCacheDir();
      File outputFile = File.createTempFile("PDF_" + UUID.randomUUID().toString(), ".pdf", outputDir);

      return outputFile;

    } catch (Exception e) {
      throw new Exception(e);
    }
  }

      // 产生PDF字体
      public static Font setChineseFont() {
          BaseFont bf = null;
          Font fontChinese = null;
          try {
              bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
              fontChinese = new Font(bf, 12, Font.NORMAL);
          } catch (DocumentException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
          return fontChinese;
      }

      /**
      *
      *  提供中文
      *
      */
      public static final class ChinaFontProvide implements FontProvider{
         @Override
         public Font getFont(String arg0, String arg1, boolean arg2, float arg3,
             int arg4, BaseColor arg5) {
           BaseFont bfChinese =null;
           try {
             bfChinese=BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
           } catch (Exception e) {
             e.printStackTrace();
           }
           Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
           return FontChinese;
         }


         @Override
         public boolean isRegistered(String arg0) {
           return false;
         }
       }


}


