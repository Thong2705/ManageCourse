package com.mockproject.group3.controller;

import com.mockproject.group3.dto.CategoryDTO;
import com.mockproject.group3.dto.SubCategoryDTO;
import com.mockproject.group3.model.Category;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.SubCategory;
import com.mockproject.group3.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable int id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/courses")
        public ResponseEntity<Set<Course>> getCoursesByCategoryId(@PathVariable int id) {
            Set<Course> courses = categoryService.getCourseByCategoryId(id);
            return courses != null ? ResponseEntity.ok(courses) : ResponseEntity.notFound().build();
    }
    @GetMapping("/{id}/subcategory")
    public ResponseEntity<Set<SubCategory>> getSubCategoryByCategoryId(@PathVariable int id) {
        Set<SubCategory> subCategories = categoryService.getSubCategoryByCategoryId(id);
        return subCategories != null ? ResponseEntity.ok(subCategories) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("{id}/createSubcategory")
    public  ResponseEntity<SubCategory> addSubCategory(@PathVariable int id, @RequestBody SubCategoryDTO subCategory) {
        SubCategory subCategories = categoryService.saveSubCategory(id, subCategory);
        return subCategories != null ? ResponseEntity.ok(subCategories) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.ok(category);
        //return categoryService.saveCategory(categoryDTO);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable int id, @RequestBody CategoryDTO categoryDTO) {
        Category updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return updatedCategory != null ? ResponseEntity.ok(updatedCategory) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        boolean isDeleted = categoryService.deleteCategory(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.status(500).build();
    }
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{id}/subcategory/{subId}")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable int subId, @PathVariable String id) {
        Category category = categoryService.getCategoryById(Integer.parseInt(id)).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        else
        {
            category.getSubCategories().removeIf(subCategory -> subCategory.getId() == subId);
        }
        boolean isDeleted = categoryService.deleteSubCategory(subId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.status(500).build();
    }
}
