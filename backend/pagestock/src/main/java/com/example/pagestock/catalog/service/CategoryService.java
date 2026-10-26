package com.example.pagestock.catalog.service;

import com.example.pagestock.catalog.dto.category.CategoryRequest;
import com.example.pagestock.catalog.dto.category.CategoryResponse;
import com.example.pagestock.exceptions.CategoryAlreadyExistsException;
import com.example.pagestock.exceptions.CategoryNotEmptyException;
import com.example.pagestock.exceptions.ItemNotFoundException;
import com.example.pagestock.catalog.model.Category;
import com.example.pagestock.catalog.repository.BookRepository;
import com.example.pagestock.catalog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Business logic voor het beheren van categorieën in de catalogus.
 * <p>
 * Zorgt ervoor dat categorienamen uniek blijven (ongeacht hoofdletters) en dat een categorie
 * niet kan worden verwijderd zolang er nog boeken in zitten.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    /**
     * Maakt de service aan met de benodigde repositories.
     *
     * @param categoryRepository repository voor categorieën
     * @param bookRepository     repository om te controleren of een categorie nog boeken bevat
     */
    @Autowired
    public CategoryService(CategoryRepository categoryRepository,  BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Haalt alle categorieën op, gesorteerd op id.
     *
     * @return lijst van alle categorieën; leeg als er geen categorieën zijn
     */
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAllByOrderByIdAsc().stream().map(this::toResponse).toList();
    }

    /**
     * Zoekt een categorie op basis van haar id.
     *
     * @param id id van de categorie
     * @return de gevonden categorie, of {@link Optional#empty()} als er geen categorie met dit id bestaat
     */
    public Optional<CategoryResponse> findById(Long id) {
        return categoryRepository.findById(id).map(this::toResponse);
    }

    /**
     * Zoekt categorieën waarvan de naam de opgegeven tekst bevat, ongeacht hoofdletters.
     *
     * @param name (deel van de) naam om op te zoeken
     * @return lijst van overeenkomende categorieën; leeg als er geen overeenkomsten zijn
     */
    public List<CategoryResponse> findByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name).stream().map(this::toResponse).toList();
    }

    /**
     * Maakt een nieuwe categorie aan. Spaties vooraan en achteraan de naam worden verwijderd.
     *
     * @param request gegevens van de nieuwe categorie
     * @return de opgeslagen categorie
     * @throws CategoryAlreadyExistsException als er al een categorie met dezelfde naam bestaat (ongeacht hoofdletters)
     */
    public CategoryResponse save(CategoryRequest request) {

        String cleanName = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCase(cleanName)) {
            throw new CategoryAlreadyExistsException(cleanName);
        }
        Category category = new Category(cleanName);
        return toResponse(categoryRepository.save(category));
    }

    /**
     * Wijzigt de naam van een bestaande categorie. Spaties vooraan en achteraan de naam worden verwijderd.
     *
     * @param request nieuwe gegevens van de categorie
     * @param id      id van de categorie die moet worden bijgewerkt
     * @return de bijgewerkte categorie
     * @throws ItemNotFoundException          als er geen categorie met dit id bestaat
     * @throws CategoryAlreadyExistsException als een andere categorie al dezelfde naam heeft (ongeacht hoofdletters)
     */
    public CategoryResponse update (CategoryRequest request, Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Could not find category with id: " + id));

        String cleanName = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(cleanName, id)) {
            throw new CategoryAlreadyExistsException(cleanName);
        }
        category.setName(cleanName);
        return toResponse(categoryRepository.save(category));
    }

    /**
     * Verwijdert een categorie, op voorwaarde dat er geen boeken meer in zitten.
     *
     * @param id id van de categorie die moet worden verwijderd
     * @throws ItemNotFoundException     als er geen categorie met dit id bestaat
     * @throws CategoryNotEmptyException als er nog boeken aan de categorie gekoppeld zijn
     */
    public void delete(Long id) {

        if (!categoryRepository.existsById(id)){
            throw new ItemNotFoundException("Could not find category with id: " + id);
        }

        if(bookRepository.existsByCategoryId(id)) {
            throw new CategoryNotEmptyException(id, bookRepository.countByCategoryId(id));
        }

        categoryRepository.deleteById(id);

    }

    /**
     * Zet een {@link Category}-entiteit om naar een {@link CategoryResponse}.
     *
     * @param category de om te zetten categorie
     * @return de response-representatie van de categorie
     */
    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                 category.getId(), category.getName()
        );
    }

}
