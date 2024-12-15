//package com.category.category.service;
//
//import com.category.category.dto.CategoryDTO;
//import com.category.category.model.Category;
//import com.category.category.repo.CategoryRepo;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class CategoryServiceTest {
//
//    @Mock
//    private CategoryRepo categoryRepo;
//
//    @InjectMocks
//    private CategoryService categoryService;
//
//    private Category category;
//    private CategoryDTO categoryDTO;
//    private AutoCloseable mocks;
//
//
//    @BeforeEach
//    void setUp() {
//        mocks = MockitoAnnotations.openMocks(this);
//
//        category = new Category();
//        category.setCategoryId(1);
//        category.setCategoryName("Test Category");
//        category.setDescription("Test description");
//        category.setIsActive(true);
//
//        categoryDTO = new CategoryDTO();
//        categoryDTO.setCategoryId(1);
//        categoryDTO.setCategoryName("Test Category");
//        categoryDTO.setDescription("Test description");
//        categoryDTO.setIsActive(true);
//    }
//
//    @Test
//    void testAddCategory() {
//        // Arrange
//        when(categoryRepo.save(any(Category.class))).thenReturn(category);
//
//        // Act
//        CategoryDTO createdCategory = categoryService.addCategory(categoryDTO);
//
//        // Assert
//        assertNotNull(createdCategory);
//        assertEquals("Test Category", createdCategory.getCategoryName());
//        assertEquals("Test description", createdCategory.getDescription());
//        assertTrue(createdCategory.getIsActive());
//        verify(categoryRepo, times(1)).save(any(Category.class));
//    }
//
//    @Test
//    void testGetCategoryByIdFound() {
//        // Arrange
//        when(categoryRepo.findByCategoryIdAndIsActiveTrue(1)).thenReturn(Optional.of(category));
//
//        // Act
//        CategoryDTO foundCategory = categoryService.getCategoryById(1);
//
//        // Assert
//        assertNotNull(foundCategory);
//        assertEquals("Test Category", foundCategory.getCategoryName());
//        verify(categoryRepo, times(1)).findByCategoryIdAndIsActiveTrue(1);
//    }
//
//    @Test
//    void testGetCategoryByIdNotFound() {
//        // Arrange
//        when(categoryRepo.findByCategoryIdAndIsActiveTrue(1)).thenReturn(Optional.empty());
//
//        // Act
//        CategoryDTO foundCategory = categoryService.getCategoryById(1);
//
//        // Assert
//        assertNull(foundCategory);
//        verify(categoryRepo, times(1)).findByCategoryIdAndIsActiveTrue(1);
//    }
//
//    @Test
//    void testUpdateCategoryFound() {
//        // Arrange
//        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
//        when(categoryRepo.save(any(Category.class))).thenReturn(category);
//
//        // Act
//        CategoryDTO updatedCategory = categoryService.updateCategory(1, categoryDTO);
//
//        // Assert
//        assertNotNull(updatedCategory);
//        assertEquals("Test Category", updatedCategory.getCategoryName());
//        assertEquals("Test description", updatedCategory.getDescription());
//        verify(categoryRepo, times(1)).findById(1);
//        verify(categoryRepo, times(1)).save(any(Category.class));
//    }
//
//    @Test
//    void testUpdateCategoryNotFound() {
//        // Arrange
//        when(categoryRepo.findById(1)).thenReturn(Optional.empty());
//
//        // Act
//        CategoryDTO updatedCategory = categoryService.updateCategory(1, categoryDTO);
//
//        // Assert
//        assertNull(updatedCategory);
//        verify(categoryRepo, times(1)).findById(1);
//    }
//
//    @Test
//    void testSoftDeleteCategoryFound() {
//        // Arrange
//        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
//
//        // Act
//        boolean isDeleted = categoryService.softDeleteOrRestoreCategory(1);
//
//        // Assert
//        assertTrue(isDeleted);
//        assertFalse(category.getIsActive());
//        verify(categoryRepo, times(1)).findById(1);
//        verify(categoryRepo, times(1)).save(any(Category.class));
//    }
//
//    @Test
//    void testSoftDeleteCategoryNotFound() {
//        // Arrange
//        when(categoryRepo.findById(1)).thenReturn(Optional.empty());
//
//        // Act
//        boolean isDeleted = categoryService.softDeleteOrRestoreCategory(1);
//
//        // Assert
//        assertFalse(isDeleted);
//        verify(categoryRepo, times(1)).findById(1);
//    }
//
//    @Test
//    void testGetAllCategories() {
//        // Arrange
//        Category category1 = new Category();
//        category1.setCategoryId(2);
//        category1.setCategoryName("Category 2");
//        category1.setDescription("Description 2");
//        category1.setIsActive(true);
//
//        when(categoryRepo.findAll()).thenReturn(List.of(category, category1));
//
//        // Act
//        List<CategoryDTO> allCategories = categoryService.getAllCategories();
//
//        // Assert
//        assertNotNull(allCategories);
//        assertEquals(2, allCategories.size());
//        assertEquals("Category 2", allCategories.get(1).getCategoryName());
//        verify(categoryRepo, times(1)).findAll();
//    }
//}
