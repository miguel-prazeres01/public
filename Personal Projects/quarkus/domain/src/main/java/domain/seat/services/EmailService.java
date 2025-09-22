package domain.seat.services;


import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.AreaBreakType;

import brevo.ApiException;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailAttachment;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import domain.email.BrevoClientProvider;
import domain.seat.entity.Seat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

@ApplicationScoped
public class EmailService {

     @Inject
    BrevoClientProvider brevoClientProvider;

    public void sendTicketPdfAsync(Seat seat) {
        try {
            byte[] pdf = generateTicketPdf(seat);

            SendSmtpEmailTo to = new SendSmtpEmailTo()
                    .email(seat.getPerson().getContactEmail())
                    .name(seat.getPerson().getName());

            SendSmtpEmailAttachment attachment = new SendSmtpEmailAttachment()
                    .name("ticket.pdf")
                    .content(pdf);

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(new SendSmtpEmailSender().email("no-reply@cpminde.pt"))
                    .to(Collections.singletonList(to))
                    .subject("Your ticket for \"" + seat.getShow().getName() + "\"")
                    .textContent("Thank you! Your ticket is attached as PDF.")
                    .attachment(Collections.singletonList(attachment));

            brevoClientProvider.getTransactionalEmailsApi().sendTransacEmail(email);
            
        } catch (ApiException e) {
            System.err.println("Error response from Brevo:");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response body: " + e.getResponseBody());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send one email with a single multi‐page PDF containing all tickets and an embedded event poster.
     */
    public void sendTicketsPdfAsync(List<Seat> seats) {
        if (seats.isEmpty()) return;

        try {
            byte[] pdf = generateTicketsPdf(seats);

            Seat first = seats.get(0);
            String recipientEmail = first.getPerson().getContactEmail();
            String recipientName = first.getPerson().getName();

            // Load and encode the poster image
            String showString = "Cartaz" + first.getShow().getId() + ".jpg";
            byte[] imageBytes = Files.readAllBytes(Paths.get(showString));  // Adjust the path as needed
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Prepare HTML email content with embedded image
            String htmlContent = "<p>Thank you! Your tickets are attached as a single PDF.</p>"
                    + "<img src='data:image/jpeg;base64," + base64Image + "' alt='Event Poster' style='max-width:100%;'/>";

            SendSmtpEmailTo to = new SendSmtpEmailTo()
                    .email(recipientEmail)
                    .name(recipientName);

            // Prepare the PDF attachment; note that we removed the call to contentType
            SendSmtpEmailAttachment attachment = new SendSmtpEmailAttachment()
                    .name("tickets.pdf")
                    .content(pdf);

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(new SendSmtpEmailSender().email("no-reply@cpminde.pt"))
                    .to(Collections.singletonList(to))
                    .subject("Your tickets for \"" + first.getShow().getName() + "\"")
                    .htmlContent(htmlContent)
                    .attachment(Collections.singletonList(attachment));

            brevoClientProvider.getTransactionalEmailsApi().sendTransacEmail(email);

        } catch (ApiException e) {
            System.err.println("Brevo API error: " + e.getCode() + " " + e.getResponseBody());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Merge individual ticket pages into one PDF.
     * Each page uses a background image (Captura.png) with seat info added in.
     */
    private byte[] generateTicketsPdf(List<Seat> seats) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            // Load the background image for each ticket page
            String showString = "Bilhete_email.jpg";
            ImageData bgImage = ImageDataFactory.create(showString);  // Adjust the path as needed

            for (int i = 0; i < seats.size(); i++) {
                Seat seat = seats.get(i);

                // Create a new page and add the background image
                PdfPage page = pdfDoc.addNewPage();
                Image bg = new Image(bgImage);
                bg.scaleToFit(pdfDoc.getDefaultPageSize().getWidth()-80, pdfDoc.getDefaultPageSize().getHeight()-80);
                bg.setFixedPosition(0, 0);
                doc.add(bg);

                // Add seat information on top of the background image.
                // Adjust the coordinates (250, 280) and width (100) to fit the designated area ("LUGAR" box).

                String seatText = seat.getRow() + seat.getSeatNumber() + " " + seat.getSeatType().name(); 
                //seat.getShow().getDateTime().format(formatterDate); // + "\n\n" + seat.getShow().getDateTime().format(formatterTime) + "\n\n" + seat.getRow() + seat.getSeatNumber() + " " + seat.getSeatType().name(); 
                Paragraph seatInfo = new Paragraph(seatText)
                    .setFontSize(12)
                    .setBold()
                    //.setFontColor(ColorConstants.WHITE)
                    .setFixedPosition(22, 28, 300);
                doc.add(seatInfo);

                Locale locale = new Locale("pt", "PT");

                DateTimeFormatter formatterDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
                DateTimeFormatter formatterTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale);

                String hourText = seat.getShow().getDateTime().format(formatterTime); 
                //seat.getShow().getDateTime().format(formatterDate); // + "\n\n" + seat.getShow().getDateTime().format(formatterTime) + "\n\n" + seat.getRow() + seat.getSeatNumber() + " " + seat.getSeatType().name(); 
                Paragraph seatInfo1 = new Paragraph(hourText)
                    .setFontSize(12)
                    .setBold()
                    //.setFontColor(ColorConstants.WHITE)
                    .setFixedPosition(22, 74, 300);
                doc.add(seatInfo1);

                String dateText = seat.getShow().getDateTime().format(formatterDate); 
                //seat.getShow().getDateTime().format(formatterDate); // + "\n\n" + seat.getShow().getDateTime().format(formatterTime) + "\n\n" + seat.getRow() + seat.getSeatNumber() + " " + seat.getSeatType().name(); 
                Paragraph seatInfo2 = new Paragraph(dateText)
                    .setFontSize(12)
                    .setBold()
                    //.setFontColor(ColorConstants.WHITE)
                    .setFixedPosition(22, 122, 300);
                doc.add(seatInfo2);

                /*String seatText = seat.getRow() + seat.getSeatNumber() + "\n" + seat.getSeatType().name();
                Paragraph seatInfo = new Paragraph(seatText)
                    .setFontSize(12)
                    .setBold()
                    .setFixedPosition(200, 220, 100);
                doc.add(seatInfo);*/

                // Insert a page break between tickets, but not after the last one.
                if (i < seats.size() - 1) {
                    doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }

            doc.close();
            return baos.toByteArray();
        }
    }

    private byte[] generateTicketPdf(Seat seat) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            doc.add(new Paragraph("Show: " + seat.getShow().getName()));
            doc.add(new Paragraph("Date: " + seat.getShow().getDateTime()));
            doc.add(new Paragraph("Seat: row " + seat.getRow()
                                  + ", number " + seat.getSeatNumber()));
            doc.close();

            return baos.toByteArray();
        }
    }
}
