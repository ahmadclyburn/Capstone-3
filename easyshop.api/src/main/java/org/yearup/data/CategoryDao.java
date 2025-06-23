package org.yearup.data;

import org.yearup.models.Category;

import java.util.ArrayList;
import java.util.List;

public interface CategoryDao
{
    List<Category> getAllCategories();
    Category getById(int categoryId);
    Category add(Category category);
    void update(int categoryId, Category category);
    void delete(int categoryId);
}
