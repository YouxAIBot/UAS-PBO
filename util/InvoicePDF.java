package projekPBO.projek.util;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class InvoicePDF {

    public static void generateInvoice(
            String film,
            int qty,
            String snacks,
            int total,
            String payment,
            int uang,
            String transactionId
    ) {
        try {
            Document doc = new Document(PageSize.A5);
            PdfWriter.getInstance(doc, new FileOutputStream("invoice.pdf"));
            doc.open();

            // Header
            Paragraph header = new Paragraph("CINEPLEX CINEMA");
            header.setAlignment(Element.ALIGN_CENTER);
            header.setFont(new Font(Font.HELVETICA, 16, Font.BOLD));
            doc.add(header);

            Paragraph subheader = new Paragraph("Online Booking System");
            subheader.setAlignment(Element.ALIGN_CENTER);
            subheader.setFont(new Font(Font.HELVETICA, 10));
            doc.add(subheader);

            Paragraph divider1 = new Paragraph("════════════════════════════");
            divider1.setAlignment(Element.ALIGN_CENTER);
            doc.add(divider1);

            // Transaction Details
            doc.add(new Paragraph(" "));
            doc.add(createDetailLine("Transaction ID", transactionId));
            doc.add(createDetailLine("Date & Time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));

            // Film & Tickets
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("BOOKING DETAILS", new Font(Font.HELVETICA, 11, Font.BOLD)));
            doc.add(createDetailLine("Film", film));
            doc.add(createDetailLine("Tickets", qty + " pcs"));

            // Snacks/Consumptions
            if (snacks != null && !snacks.equals("None")) {
                doc.add(new Paragraph(" "));
                doc.add(new Paragraph("SNACKS & DRINKS", new Font(Font.HELVETICA, 11, Font.BOLD)));
                doc.add(createDetailLine("Items", snacks));
            }

            // Payment Details
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("PAYMENT INFORMATION", new Font(Font.HELVETICA, 11, Font.BOLD)));
            doc.add(createDetailLine("Payment Method", payment));
            doc.add(createDetailLine("Total Amount", "Rp " + String.format("%,d", total)));

            // Cash Payment - Show Change
            if (payment.toLowerCase().contains("cash") || payment.toLowerCase().contains("tunai")) {
                int change = uang - total;
                doc.add(createDetailLine("Amount Paid", "Rp " + String.format("%,d", uang)));
                doc.add(createDetailLine("Change", "Rp " + String.format("%,d", change >= 0 ? change : 0)));
            }

            Paragraph divider2 = new Paragraph("════════════════════════════");
            divider2.setAlignment(Element.ALIGN_CENTER);
            doc.add(new Paragraph(" "));
            doc.add(divider2);

            // Footer
            Paragraph footer = new Paragraph("Thank you for booking with CINEPLEX!\n\nPlease keep this receipt for your records.");
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setFont(new Font(Font.HELVETICA, 9));
            doc.add(new Paragraph(" "));
            doc.add(footer);

            doc.close();
            AppLogger.info("PDF Invoice generated: invoice.pdf - Transaction: " + transactionId);
        } catch (Exception e) {
            AppLogger.severe("Failed to generate invoice PDF", e);
        }
    }

    /**
     * Helper method to create formatted detail lines
     */
    private static Paragraph createDetailLine(String label, String value) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " : ", new Font(Font.HELVETICA, 10, Font.BOLD)));
        p.add(new Chunk(value, new Font(Font.HELVETICA, 10)));
        return p;
    }
}

