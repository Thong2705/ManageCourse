package com.mockproject.group3.service;

import com.mockproject.group3.dto.CategoryDTO;
import com.mockproject.group3.dto.SubCategoryDTO;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Category;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.SubCategory;
import com.mockproject.group3.repository.CategoryRepository;
import com.mockproject.group3.repository.SubCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final SubCategoryService subCategoryService;


    public CategoryService(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository, SubCategoryService subCategoryService) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.subCategoryService = subCategoryService;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(int id) {
        return categoryRepository.findById(id);
    }


    public Set<Course> getCourseByCategoryId(int id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(Category::getCourses).orElse(null);
    }
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Category saveCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXIST);
        }
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        return categoryRepository.save(category);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Category updateCategory(int id, CategoryDTO categoryDTO) {
        Category optionalCategory = categoryRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        optionalCategory.setName(categoryDTO.getName());
        optionalCategory.setDescription(categoryDTO.getDescription());
        return categoryRepository.save(optionalCategory);
    }
    public Set<SubCategory> getSubCategoryByCategoryId(int id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(Category::getSubCategories).orElse(null);
    }


    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public SubCategory saveSubCategory(int id, SubCategoryDTO subCategory) {
        Category category = categoryRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        SubCategory newSubCategory = new SubCategory();
        if(subCategoryRepository.existsByName(subCategory.getName())){
            throw new AppException(ErrorCode.SUBCATEGORY_EXIST);
        }
        newSubCategory.setName(subCategory.getName());
        newSubCategory.setDescription(subCategory.getDescription());
        newSubCategory.setCategory(category);
        return subCategoryRepository.save(newSubCategory);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @Transactional
    public boolean deleteCategory(int id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            try {
                    Category category = optionalCategory.get();
                    categoryRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                // Log the exception
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @Transactional
    public boolean deleteSubCategory(int id) {
        Optional<SubCategory> optionalCategory = subCategoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            try {
                SubCategory category = optionalCategory.get();
                subCategoryRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                // Log the exception
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
