package org.yearup.controllers;

import com.sun.source.tree.BreakTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import javax.annotation.Resource;
import java.util.List;

// add the annotations to make this a REST controller
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
// add annotation to allow cross site origin requests
@CrossOrigin
@RestController
@RequestMapping("/categories")
public class CategoriesController
{
    private final CategoryDao categoryDao;
    private ProductDao productDao;


    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // add the appropriate annotation for a get action
    @GetMapping
    public List<Category> getAll() {
        return categoryDao.getAllCategories();
    }
        // find and return all categories


    // add the appropriate annotation for a get action
    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable int id)
    {
        // get the category by id

        Category category = categoryDao.getById(id);

        if(category == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category);
    }


    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    public ResponseEntity<List<Product>> getProductsById(@PathVariable int categoryId)
    {   Category category = categoryDao.getById(categoryId);
        if(category == null) {
            return ResponseEntity.notFound().build();
        }

        // get a list of product by categoryId
        List<Product> products = productDao.listByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category category)
    {
        // insert the category
        Category created = categoryDao.create(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        // update the category by id
        Category existing = categoryDao.getById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        categoryDao.update(id, category);
        return ResponseEntity.noContent().build();
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id)
    {
        // delete the category by id
        Category existing = categoryDao.getById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        categoryDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
