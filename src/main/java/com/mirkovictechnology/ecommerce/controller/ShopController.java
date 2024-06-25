package com.mirkovictechnology.ecommerce.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.mirkovictechnology.ecommerce.dto.ItemDTO;
import com.mirkovictechnology.ecommerce.model.Bill;
import com.mirkovictechnology.ecommerce.model.BillItems;
import com.mirkovictechnology.ecommerce.model.User;
import com.mirkovictechnology.ecommerce.service.BillService;
import com.mirkovictechnology.ecommerce.service.ItemService;
import com.mirkovictechnology.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
@Slf4j
public class ShopController {
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
	
	private final BillService billService;
	private final ItemService itemService;
	private final UserService userService;
	
	@GetMapping("/shop-panel")
	public String showShop(@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
			Model model, @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
		
		Pageable pageable = PageRequest.of(page, 4);
		Page<ItemDTO> itemsPage;
		int checkSize = 0;
		if (searchKeyword != null && !searchKeyword.isEmpty()) {
			itemsPage = itemService.searchItems(searchKeyword, pageable);
			checkSize = itemsPage.getNumberOfElements();
		}
		else if (category != null) {
			itemsPage = itemService.getItemsByCategory(category, pageable);
			checkSize = itemsPage.getNumberOfElements();
		}
		else {
			itemsPage = itemService.getAllItems(pageable);
			checkSize = itemsPage.getNumberOfElements();
		}
		model.addAttribute("userLogin", userDetails.getUsername());
		model.addAttribute("itemsPage", itemsPage);
		model.addAttribute("items", itemsPage.getContent());
		model.addAttribute("checkSize", checkSize);
		model.addAttribute("category", category);
		model.addAttribute("searchKeyword", searchKeyword);
		
		List<BillItems> billItems = (List<BillItems>) session.getAttribute("billItems");
		if (billItems == null) {
			billItems = new ArrayList<>();
		}
		
		model.addAttribute("billItems", billItems);
		model.addAttribute("totalAmount", billService.totalAmount(billItems));
		Long billId = (Long) session.getAttribute("billId");
		model.addAttribute("billId", billId);
		if(billId != null) {
			Optional<Bill> bill = billService.findById(billId);
			bill.ifPresent(value -> model.addAttribute("billTime", value.getCreatedDateTime().format(FORMATTER)));
		}
		return "shop";
	}
	
	@PostMapping("/item-to-bill")
	public String addToCBillDetails(@RequestParam Long itemId, @RequestParam int quantity, Model model,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
			@RequestParam(defaultValue = "0") int page, HttpSession session) {
		
		List<BillItems> billItems = (List<BillItems>) session.getAttribute("billItems");
		if (billItems == null) {
			billItems = new ArrayList<>();
		}
		
		billService.addItemToBill(billItems, itemId, quantity);
		session.setAttribute("billItems", billItems);
		return "redirect:" + billService.redirectUrl(page, category, searchKeyword);
	}
	
	@PostMapping("/remove-from-bill")
	public String removeFromBillDetails(@RequestParam Long itemId, HttpSession session,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
			@RequestParam(defaultValue = "0") int page) {
		
		List<BillItems> billItems = (List<BillItems>) session.getAttribute("billItems");
		if (billItems != null) {
			billService.removeItemFromBill(billItems, itemId);
			session.setAttribute("billItems", billItems);
		}
		return "redirect:" + billService.redirectUrl(page, category, searchKeyword);
	}
	
	@PostMapping("/generate-bill")
	public String generateBill(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
		List<BillItems> billItems = (List<BillItems>) session.getAttribute("billItems");
		if (billItems == null) {
			billItems = new ArrayList<>();
		}
		User user = userService.findByUsername(userDetails.getUsername());
		Bill generatedBill = billService.generateBill(billItems, user);
		session.setAttribute("billId", generatedBill.getId());
		session.setAttribute("billItems", new ArrayList<>());
		
		return "redirect:" + billService.redirectUrl(0,null,null);
	}
	
	@GetMapping("/download")
	public void downloadBillPdf(@RequestParam Long billId, HttpServletResponse response) {
		Optional<Bill> bill = billService.findById(billId);
		if (bill.isPresent()) {
			String pdf = billService.generateBillPdf(bill.get());
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdf);
			
			try (InputStream is = new FileInputStream(pdf)) {
				org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
				response.flushBuffer();
			}
			catch (IOException e) {
				throw new RuntimeException("Failed to read PDF file", e);
			}
		}else {
			log.error("Bill id {} not found", billId);
		}
	}
}