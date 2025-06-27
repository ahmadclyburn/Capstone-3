package org.yearup.data;

import org.yearup.models.Category;

import java.util.ArrayList;
import java.util.List;

public interface CategoryDao
{
    List<Category> getAll();
    Category getById(int categoryId);
    Category add(Category category);
    Category update(int categoryId, Category category);
    void delete(int categoryId);
}
