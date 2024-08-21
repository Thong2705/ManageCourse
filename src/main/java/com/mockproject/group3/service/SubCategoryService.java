package com.mockproject.group3.service;

import com.mockproject.group3.dto.SubCategoryDTO;
import com.mockproject.group3.model.SubCategory;
import com.mockproject.group3.repository.SubCategoryRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubCategoryService {
    private final SubCategoryRepository subCategoryRepository;


    public SubCategoryService(SubCategoryRepository subCategoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
    }
    public List<SubCategory> getAllSubCategories() {
        return subCategoryRepository.findAll();
    }
    public SubCategory getSubCategoryById(int id) {
        return subCategoryRepository.findById(id).orElse(null);
    }
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public SubCategory saveSubCategory(SubCategoryDTO subCategory) {
        SubCategory newSubCategory = new SubCategory();
        newSubCategory.setName(subCategory.getName());
        newSubCategory.setDescription(subCategory.getDescription());
        return subCategoryRepository.save(newSubCategory);
    }
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public SubCategory updateSubCategory(int id, SubCategory subCategory) {
        SubCategory optionalSubCategory = subCategoryRepository.findById(id).orElse(null);
        if (optionalSubCategory == null) {
            return null;
        }
        optionalSubCategory.setName(subCategory.getName());
        optionalSubCategory.setDescription(subCategory.getDescription());
        return subCategoryRepository.save(optionalSubCategory);
    }
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public void deleteSubCategory(int id) {
        subCategoryRepository.deleteById(id);
    }
}
