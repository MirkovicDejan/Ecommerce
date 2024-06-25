package com.mirkovictechnology.ecommerce.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.mirkovictechnology.ecommerce.dto.ItemDTO;
import com.mirkovictechnology.ecommerce.service.ItemService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final ItemService itemService;
	
	@GetMapping("/admin-panel")
	public String showAdminPanel(@RequestParam(value = "category", required = false) String category, @RequestParam(value = "searchKeyword", required = false) String searchKeyword, Model model, @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal UserDetails userDetails) {
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
		return "admin-products";
	}
	
	@PostMapping("/add-item")
	public String addNewItem(@ModelAttribute("item") ItemDTO itemDTO) {
		itemService.saveItem(itemDTO);
		return "redirect:/admin/admin-panel";
	}
	
	@PostMapping("/edit-item")
	public String editItem(@ModelAttribute("item") ItemDTO itemDTO) {
		itemService.updateItem(itemDTO);
		return "redirect:/admin/admin-panel";
	}
	
	@GetMapping("/delete-item/{itemId}")
	public String deleteItem(@PathVariable Long itemId) {
		itemService.deleteItem(itemId);
		return "redirect:/admin/admin-panel";
	}
	
}