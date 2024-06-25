package com.mirkovictechnology.ecommerce.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mirkovictechnology.ecommerce.exception.ItemShopException;
import com.mirkovictechnology.ecommerce.model.Bill;
import com.mirkovictechnology.ecommerce.model.BillItems;
import com.mirkovictechnology.ecommerce.model.Item;
import com.mirkovictechnology.ecommerce.model.User;
import com.mirkovictechnology.ecommerce.repository.BillRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class BillService {
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
	
	@Value("${export-pdf-path}")
	private String exportPdfPath;
	
	private final BillRepository billRepository;
	private final ItemService itemService;
	
	@PostConstruct
	public void ini() {
		File toCreate = new File(exportPdfPath);
		if (!toCreate.exists()) {
			log.info("Pdf directory does not yet exists. Creating it ...");
			boolean dirCreated = toCreate.mkdirs();
			log.info("Creation of directory was successful: {}", dirCreated);
		}
	}
	
	@Scheduled(cron = "${scheduling.cron.start-process}")
	void startSchedulingProcess(){
		log.debug("Starting scheduling process...");
		deletePdfFilesFromApplicationFileSystem();
		log.debug("Scheduling process finished.");
	}
	
	public Optional<Bill> findById(Long id) {
		return billRepository.findById(id);
	}
	
	public void addItemToBill(List<BillItems> billItems, Long itemId, int quantity) {
		Item item = itemService.findItemById(itemId);
		boolean itemExists = false;
		
		for (BillItems bi : billItems) {
			if (bi.getItem().getId().equals(itemId)) {
				bi.setQuantity(bi.getQuantity() + quantity);
				itemExists = true;
				break;
			}
		}
		
		if (!itemExists) {
			BillItems billItem = new BillItems();
			billItem.setItem(item);
			billItem.setQuantity(quantity);
			billItem.setItemPrice(item.getPrice());
			billItems.add(billItem);
		}
	}
	
	public void removeItemFromBill(List<BillItems> billItems, Long itemId) {
		for (int i = 0; i < billItems.size(); i++) {
			BillItems bi = billItems.get(i);
			if (bi.getItem().getId().equals(itemId)) {
				if (bi.getQuantity() > 1) {
					bi.setQuantity(bi.getQuantity() - 1);
				}
				else {
					billItems.remove(i);
				}
				break;
			}
		}
	}
	
	public BigDecimal totalAmount(List<BillItems> billItems) {
		return billItems.stream()
				.map(bi -> bi.getItem().getPrice().multiply(BigDecimal.valueOf(bi.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	public String redirectUrl(int page, String category, String searchKeyword) {
		String redirectUrl = "/shop/shop-panel?page=" + page;
		
		if (category != null && !category.isEmpty()) {
			redirectUrl += "&category=" + category;
		}
		if (searchKeyword != null && !searchKeyword.isEmpty()) {
			redirectUrl += "&searchKeyword=" + searchKeyword;
		}
		
		return redirectUrl;
	}
	
	public Bill generateBill(List<BillItems> billItems, User user) {
		Bill bill = new Bill();
		bill.setUser(user);
		bill.setTotalAmount(totalAmount(billItems));
		bill.setCreatedDateTime(LocalDateTime.now());
		billItems.forEach(billItems1 -> billItems1.setBill(bill));
		bill.setBillItems(new ArrayList<>(billItems));
		saveBill(bill);
		log.info("User: {} generated bill. Bill: {}", user.getUsername(), bill);
		return bill;
	}
	
	public void saveBill(Bill bill) {
		if (Objects.equals(bill.getTotalAmount(), BigDecimal.valueOf(0))) {
			throw new ItemShopException("Can't generate empty bill", HttpStatus.BAD_REQUEST);
		}
		else {
			billRepository.save(bill);
		}
	}
	
	public String generateBillPdf(Bill bill) {
		DateTimeFormatter pathFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");
		String dateTime = bill.getCreatedDateTime().format(pathFormatter);
		String pdfPath = exportPdfPath + File.separator + "Bill_" + dateTime + "_" + "user_" + bill.getUser().getUsername() + "_.pdf";
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
			document.open();
			
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
			Paragraph title = new Paragraph("BILL\n\n", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			
			Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
			document.add(new Paragraph("USER: " + bill.getUser().getUsername(), infoFont));
			document.add(new Paragraph("Creation Date: " + bill.getCreatedDateTime().format(FORMATTER), infoFont));
			document.add(new Paragraph("\n"));
			
			PdfPTable table = new PdfPTable(3);
			table.setWidthPercentage(100);
			table.setSpacingBefore(10f);
			table.setSpacingAfter(10f);
			
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
			
			PdfPCell header1 = new PdfPCell(new Paragraph("Product", headerFont));
			header1.setBackgroundColor(BaseColor.GRAY);
			header1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(header1);
			
			PdfPCell header2 = new PdfPCell(new Paragraph("Price", headerFont));
			header2.setBackgroundColor(BaseColor.GRAY);
			header2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(header2);
			
			PdfPCell header3 = new PdfPCell(new Paragraph("Quantity", headerFont));
			header3.setBackgroundColor(BaseColor.GRAY);
			header3.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(header3);
			
			Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
			for (BillItems bi : bill.getBillItems()) {
				table.addCell(new PdfPCell(new Paragraph(bi.getItem().getName(), rowFont)));
				table.addCell(new PdfPCell(new Paragraph(bi.getItemPrice().toString(), rowFont)));
				table.addCell(new PdfPCell(new Paragraph(bi.getQuantity().toString(), rowFont)));
			}
			
			document.add(table);
			
			document.add(new Paragraph("\nTotal Amount: " + bill.getTotalAmount(), infoFont));
			
			Font disclaimerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.RED);
			Paragraph disclaimer = new Paragraph("\nThis bill has no legal liability! It is made for demonstration purposes only.", disclaimerFont);
			disclaimer.setAlignment(Element.ALIGN_CENTER);
			document.add(disclaimer);
			
			document.close();
			
			return pdfPath;
		}
		catch (DocumentException | FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void deletePdfFilesFromApplicationFileSystem() {
		Path directory = Paths.get(exportPdfPath);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.pdf")) {
			for (Path entry : stream) {
				Files.delete(entry);
				log.info("Deleted file: " + entry.getFileName());
			}
		} catch (IOException e) {
			log.error("Error while deleting files pdf files. Error message: {}", e.getMessage());
		}
	}
}