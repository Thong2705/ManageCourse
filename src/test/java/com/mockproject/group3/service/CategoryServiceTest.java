package com.mockproject.group3.service;

import com.mockproject.group3.dto.CategoryDTO;
import com.mockproject.group3.dto.SubCategoryDTO;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.model.Category;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.SubCategory;
import com.mockproject.group3.repository.CategoryRepository;
import com.mockproject.group3.repository.SubCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private SubCategoryService subCategoryService;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    public void setUp() {
        Category category1 = new Category();
        category1.setName("Technology");
        category1.setDescription("Courses on technology and programming");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Business");
        category2.setDescription("Courses on business and management");
//			category2.setParentCategory(category1);
        categoryRepository.save(category2);
    }

    @Test
    public void testGetAllCategories() {
        // Create two Category objects and add them to the categories list
        Category category1 = new Category();
        category1.setName("Category 1");
        Category category2 = new Category();
        category2.setName("Category 2");
        List<Category> categories = new ArrayList<>(Arrays.asList(category1, category2));

        // Setup the mock to return the populated list
        when(categoryRepository.findAll()).thenReturn(categories);

        // Call the method under test
        List<Category> result = categoryService.getAllCategories();

        // Assertions
        assertNotNull(result, "The result should not be null");
        assertEquals(2, result.size(), "The result list should contain 2 categories");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void testGetCategoryById() {
        Category category = new Category();
        category.setId(1);
        categoryRepository.save(category);

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById(1);

        assertTrue(result.isPresent());
        assertEquals(category.getId(), result.get().getId());
        verify(categoryRepository, times(1)).findById(anyInt());
    }

    @Test
    public void testGetCourseByCategoryId() {
        Category category = new Category();
        Set<Course> courses = new HashSet<>();
        courses.add(new Course());
        category.setCourses(courses);

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));

        Set<Course> result = categoryService.getCourseByCategoryId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findById(anyInt());
    }

    @Test
    public void testSaveCategory() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        categoryDTO.setDescription("Test Description");

        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Description");

        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.saveCategory(categoryDTO);

        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        verify(categoryRepository, times(3)).save(any(Category.class));
    }

    @Test
    public void testUpdateCategory() {

        // Arrange
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Updated Category");
        categoryDTO.setDescription("Updated Description");

        Category existingCategory = new Category();
        existingCategory.setId(1);
        existingCategory.setName("Old Category");
        existingCategory.setDescription("Old Description");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            // Return the category with updated values
            Category categoryToSave = invocation.getArgument(0);
            categoryToSave.setId(1); // Ensure ID is retained
            return categoryToSave;
        });

        // Act
        Category result = categoryService.updateCategory(1, categoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Category", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(categoryRepository, times(3)).save(any(Category.class));
    }
    @Test
    public void testGetSubCategoryByCategoryId() {
        // Arrange
        int categoryId = 1;
        Category category = new Category();
        SubCategory subCategory1 = new SubCategory();
        SubCategory subCategory2 = new SubCategory();
        Set<SubCategory> subCategories = new HashSet<>(Arrays.asList(subCategory1, subCategory2));
        category.setSubCategories(subCategories);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Set<SubCategory> result = categoryService.getSubCategoryByCategoryId(categoryId);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "SubCategory set size should be 2");
        verify(categoryRepository, times(1)).findById(categoryId);
    }
    @Test
    public void testSaveSubCategory() {
        // Arrange
        SubCategoryDTO subCategoryDTO = new SubCategoryDTO();
        subCategoryDTO.setName("New SubCategory");
        subCategoryDTO.setDescription("SubCategory Description");

        Category category = new Category();
        category.setId(1);
        category.setName("Category Name");

        SubCategory newSubCategory = new SubCategory();
        newSubCategory.setName(subCategoryDTO.getName());
        newSubCategory.setDescription(subCategoryDTO.getDescription());
        newSubCategory.setCategory(category);

        // Mock the categoryRepository to return the category when queried
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        // Mock the subCategoryRepository to return the new subCategory when saved
        when(subCategoryRepository.existsByName(subCategoryDTO.getName())).thenReturn(false); // No existing subcategory with the same name
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(newSubCategory);

        // Act
        SubCategory result = categoryService.saveSubCategory(1, subCategoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(subCategoryDTO.getName(), result.getName());
        assertEquals(subCategoryDTO.getDescription(), result.getDescription());
        assertEquals(category, result.getCategory());
        verify(subCategoryRepository, times(1)).save(any(SubCategory.class));
    }

    @Test
    public void testDeleteCategory() {
        Category category = new Category();
        category.setId(1);

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(anyInt());

        boolean result = categoryService.deleteCategory(1);

        assertTrue(result);
        verify(categoryRepository, times(1)).deleteById(anyInt());
    }

    @Test
    public void testDeleteSubCategory() {
        SubCategory subCategory = new SubCategory();
        subCategory.setId(1);

        when(subCategoryRepository.findById(anyInt())).thenReturn(Optional.of(subCategory));
        doNothing().when(subCategoryRepository).deleteById(anyInt());

        boolean result = categoryService.deleteSubCategory(1);

        assertTrue(result);
        verify(subCategoryRepository, times(1)).deleteById(anyInt());
    }
}
