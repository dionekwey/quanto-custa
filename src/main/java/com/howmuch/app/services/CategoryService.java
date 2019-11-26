package com.howmuch.app.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.howmuch.app.dto.CategoryDTO;
import com.howmuch.app.entities.Category;
import com.howmuch.app.entities.Product;
import com.howmuch.app.repositories.CategoryRepository;
import com.howmuch.app.repositories.ProductRepository;
import com.howmuch.app.services.exceptions.DatabaseException;
import com.howmuch.app.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Autowired
	private ProductRepository productRepository;
	
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();
		return list.stream().map(e -> new CategoryDTO(e)).collect(Collectors.toList());
	}
	
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException(id));
		
		return new CategoryDTO(entity);
	}
	
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = dto.toEntity();
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(id);
		} catch (DataIntegrityViolationException e) {
			new DatabaseException(e.getMessage());
		}
	}
	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
			Category entity = repository.getOne(id);
			updateData(entity, dto);
			entity = repository.save(entity);
			return new CategoryDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	private void updateData(Category entity, CategoryDTO dto) {
		entity.setName(dto.getName());
	}
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findByProduct(Long productId) {
		Product product = productRepository.getOne(productId);
		Set<Category> set = product.getCategories();
		return set.stream().map(e -> new CategoryDTO(e)).collect(Collectors.toList());
	}

}