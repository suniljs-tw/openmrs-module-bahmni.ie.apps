package org.bahmni.module.bahmni.ie.apps.util.pdf.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmni.ie.apps.config.PdfFormConfig;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.ParserImpl;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class BahmniPDFFormImpl implements BahmniPDFForm {
    private static final String DEFAULT_PDF_FOLDER_PATH = "/home/bahmni/pdf/";
    private static final String BAHMNI_FORM_PATH_PDF = "bahmni.pdf.directory";

    private String title;
    private final Document document;
    private final PdfWriter writer;
    private final String filename;
    private String html = "";

    PdfFormConfig pdfFormConfig;
    private Logger logger = Logger.getLogger(ParserImpl.class);

    public BahmniPDFFormImpl() throws IOException, DocumentException {
        filename = createDirsAndGetFilePath();
        document = new Document();
        pdfFormConfig = new PdfFormConfig();
        writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
    }

    @Override
    public String create() throws IOException {
        addTitle();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                new ByteArrayInputStream(html.getBytes()));
        document.close();
        return filename;
    }

    private String createDirsAndGetFilePath() throws IOException {
        String pathPrefix = System.getProperty(BAHMNI_FORM_PATH_PDF, DEFAULT_PDF_FOLDER_PATH);
        String filename = UUID.randomUUID().toString();
        String pathSuffix = ".pdf";
        String fullPath = pathPrefix + filename + pathSuffix;

        Files.createDirectories(Paths.get(pathPrefix));

        return fullPath;
    }

    private void addTitle() {
        if (title != null) {
            html = "<center><h2>" + title + "</h2></center>" + html;
        }
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void addLabel(String labelText) {
        html += "<p>" + labelText + "</p>";
    }

    @Override
    public void addTextField(String textFieldLabel) {

        pdfFormConfig.setMaximumWidth(100);
        pdfFormConfig.setWidth(100);
        String TableStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(35);
        String labelColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(5);
        String emptyColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(60);
        pdfFormConfig.setContentFieldBox(1, "ridge", "black");
        String contentColumnStyles = pdfFormConfig.getStyles();


        String labelColumn = "<td " + labelColumnStyles + ">" + textFieldLabel + "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";
        String contentColumn = "<td " + contentColumnStyles + "></td>";

        html += "<table " + TableStyles + "><tr>" + labelColumn + emptyColumn + contentColumn + "</tr></table>";
        logger.warn(html);
    }

    @Override
    public void addNumericField(String numericFieldLabel, String unit) {
        String blank = "______________";
        html += "<table><tr><td style=\"width: 30%;\">" + numericFieldLabel + "</td><td>" + blank + "</td><td style=\"width: 30%;\">" + unit + "</td></tr></table>";
    }

    @Override
    public void beginSection(String sectionTitle) {
        html += "<h4>" + sectionTitle + "</h4>";
        html += "<table style=\"width: 100%; border: 1px solid black;\"><tr><td>";
    }

    @Override
    public void endSection() {
        html += "</td></tr></table>";
    }

    @Override
    public void addDateTimeField(String dateTimeFieldLabel) {
        String dateTimeblank = "__/___/____ , __:__";
        html += "<table><tr><td style=\"width: 30%;\">" + dateTimeFieldLabel + "</td><td>" + dateTimeblank + "</td><td style=\"width: 30%;\">" + "AM/PM" + "</td></tr></table>";
    }

    @Override
    public void addBooleanField(String booleanFieldLabel) {
        String checkBoxStyle = "\"float: left;height: 20px;width: 20px;margin-bottom: 15px;border: 1px solid black;clear: both;\"";
        html += "<table><tr><td style=\"width: 30%;\">" + booleanFieldLabel + "</td><td style=" + checkBoxStyle + "> </td> <td>Yes</td> <td style=" + checkBoxStyle + "</td> <td>No</td> </tr></table>";
    }

    @Override
    public void addCodedField(String codedFieldLabel, List<String> codes) {
        html += "<table style=\"width: 100%; max-width: 100%;\"><tr><td style=\"width: 30%;\">" + codedFieldLabel + "</td>" + generateDynamicCode(codes) + "</tr></table>";
    }

    private String generateDynamicCode(List<String> codes) {
        String checkBoxStyle = "\"float: left;height: 20px;width: 20px;margin-bottom: 15px;border: 1px solid black;clear: both;\"";
        StringBuilder codeHtml = new StringBuilder();
        for (int index = 0; index < codes.size(); index++) {
            if (index % 2 == 0) {
                if (index != 0) {
                    codeHtml.append("<td></td>");
                }
                codeHtml.append("<td style=").append(checkBoxStyle).append("> </td> <td>").append(codes.get(index)).append("</td>");
            } else {
                codeHtml.append("<td style=").append(checkBoxStyle).append("> </td> <td>").append(codes.get(index)).append("</td>");
                codeHtml.append("</tr><tr>");
            }
        }
        return codeHtml.toString();
    }
}