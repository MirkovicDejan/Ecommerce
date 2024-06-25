package com.mirkovictechnology.ecommerce.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.mirkovictechnology.ecommerce.dto.ItemDTO;
import com.mirkovictechnology.ecommerce.exception.ItemAdminException;
import com.mirkovictechnology.ecommerce.model.Item;
import com.mirkovictechnology.ecommerce.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
	
	private final ItemRepository itemRepository;
	private final EntityManager entityManager;
	
	public void saveItem(ItemDTO itemDto) {
		Item item = DTOtoModel(itemDto);
		if (Objects.equals(item.getPrice(), BigDecimal.valueOf(0))){
			throw new ItemAdminException("Price can't be 0.", HttpStatus.BAD_REQUEST);
		}
		if (item.getName() != null && !item.getName().isEmpty() && item.getDescription() != null && !item.getDescription().isEmpty()
				&& item.getCategory() != null && !item.getCategory().isEmpty() && item.getPrice() != null && item.getImage().clone().length != 0) {
			itemRepository.save(item);
		}
		else {
			throw new ItemAdminException("Please fill all fields.", HttpStatus.BAD_REQUEST);
		}
	}
	
	private Item DTOtoModel(final ItemDTO itemDto) {
		try {
			Item item = new Item();
			item.setId(itemDto.getId());
			item.setName(itemDto.getName());
			item.setCategory(itemDto.getCategory());
			item.setPrice(itemDto.getPrice());
			item.setDescription(itemDto.getDescription());
			if (itemDto.getImage() != null && !itemDto.getImage().isEmpty()) {
				byte[] byteArr = itemDto.getImage().getBytes();
				item.setImage(byteArr);
			}
			return item;
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public Page<ItemDTO> getAllItems(Pageable pageable) {
		return itemRepository.findAll(pageable)
				.map(this::modelToDTO);
	}
	
	public Page<ItemDTO> getItemsByCategory(final String category, final Pageable pageable) {
		Page<Item> itemsPage = itemRepository.findByCategory(category, pageable);
		return itemsPage.map(this::modelToDTO);
	}
	
	private ItemDTO modelToDTO(Item item) {
		ItemDTO itemDto = new ItemDTO();
		itemDto.setId(item.getId());
		itemDto.setName(item.getName());
		itemDto.setCategory(item.getCategory());
		itemDto.setPrice(item.getPrice());
		itemDto.setDescription(item.getDescription());
		String base64Image = Base64.getEncoder().encodeToString(item.getImage());
		itemDto.setBase64Image(base64Image);
		return itemDto;
	}
	
	public void updateItem(final ItemDTO itemDTO) {
		Item item = DTOtoModel(itemDTO);
		if (Objects.equals(item.getPrice(), BigDecimal.valueOf(0))){
			throw new ItemAdminException("Price can't be 0.", HttpStatus.BAD_REQUEST);
		}
		Item originalItem = itemRepository.findById(item.getId()).orElseThrow(() -> new ItemAdminException("Item not found", HttpStatus.NOT_FOUND));
		if (itemDTO.getImage() == null || itemDTO.getImage().isEmpty()) {
			item.setImage(originalItem.getImage());
		}
		itemRepository.save(item);
	}
	
	public void deleteItem(final Long itemId) {
		itemRepository.deleteById(itemId);
	}
	
	public Page<ItemDTO> searchItems(String searchKeyword, Pageable pageable) {
		Session session = entityManager.unwrap(Session.class);
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		
		String searchLower = "%" + searchKeyword.toLowerCase() + "%";
		
		Predicate namePredicate = cb.like(cb.lower(root.get("name")), "%" + searchKeyword.toLowerCase() + "%");
		Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), "%" + searchKeyword.toLowerCase() + "%");
		Predicate pricePredicate = cb.like(cb.lower(root.get("price").as(String.class)), searchLower);
		Predicate categoryPredicate = cb.like(cb.lower(root.get("category")), "%" + searchKeyword.toLowerCase() + "%");
		
		Predicate searchPredicate = cb.or(namePredicate, descriptionPredicate, pricePredicate, categoryPredicate);
		cq.where(searchPredicate);
		Query<Item> query = session.createQuery(cq);
		int firstResult = pageable.getPageNumber() * pageable.getPageSize();
		query.setFirstResult(firstResult);
		query.setMaxResults(pageable.getPageSize());
		List<Item> resultList = query.getResultList();
		List<ItemDTO> itemDTOList = resultList.stream().map(this::modelToDTO).collect(Collectors.toList());
		return new PageImpl<>(itemDTOList, pageable, query.getResultList().size());
		
	}
	
	public Item findItemById(final Long itemId) {
		return itemRepository.findById(itemId).orElseThrow(() -> new ItemAdminException("Item not found", HttpStatus.NOT_FOUND));
	}
}