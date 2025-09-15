package com.intellicart.service;

import com.intellicart.model.Product;
import com.intellicart.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product createProduct(Product p) {
        if (p.getStockQuantity() == null) p.setStockQuantity(0);
        return repo.save(p);
    }

    public Optional<Product> getProduct(Long id) {
        return repo.findById(id);
    }

    public Product updateProduct(Long id, Product update) {
        Product p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (update.getName() != null) p.setName(update.getName());
        if (update.getCategory() != null) p.setCategory(update.getCategory());
        if (update.getPrice() != null) p.setPrice(update.getPrice());
        if (update.getStockQuantity() != null) p.setStockQuantity(update.getStockQuantity());
        return repo.save(p);
    }

    public void deleteProduct(Long id) {
        repo.deleteById(id);
    }

    public List<Product> listAll() {
        return repo.findAll();
    }

    public List<Product> findByCategory(String category) {
        return repo.findByCategoryIgnoreCase(category);
    }

    /**
     * Binary search based product name lookup.
     * Fetches products ordered by name, then performs case-insensitive binary search for exact match or near matches.
     * Structured so Trie-based search can be slotted later.
     */
    public List<Product> searchByName(String q) {
        if (q == null || q.isBlank()) return Collections.emptyList();
        List<Product> sorted = repo.findAllByOrderByNameAsc();
        q = q.strip().toLowerCase();
        int idx = binarySearchByName(sorted, q);
        if (idx >= 0) {
            // return exact match plus neighbours that start with same prefix
            List<Product> res = new ArrayList<>();
            // expand left
            int l = idx;
            while (l >= 0 && sorted.get(l).getName().toLowerCase().startsWith(q)) {
                l--;
            }
            l++;
            int r = idx;
            while (r < sorted.size() && sorted.get(r).getName().toLowerCase().startsWith(q)) {
                r++;
            }
            for (int i = l; i < r; i++) res.add(sorted.get(i));
            return res;
        } else {
            // not exact; return items that contain q anywhere (fallback)
            List<Product> out = new ArrayList<>();
            for (Product p : sorted) {
                if (p.getName().toLowerCase().contains(q)) out.add(p);
            }
            return out;
        }
    }

    private int binarySearchByName(List<Product> sorted, String q) {
        int lo = 0, hi = sorted.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            String name = sorted.get(mid).getName().toLowerCase();
            int cmp = name.compareTo(q);
            if (cmp == 0) return mid;
            if (cmp < 0) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    /**
     * Decrement stock safely for given product id and quantity. Uses repository-level PESSIMISTIC_WRITE lock.
     * Throws IllegalStateException if insufficient stock.
     */
    @Transactional
    public void decrementStock(Long productId, int qty) {
        var opt = repo.findByIdForUpdate(productId);
        var product = opt.orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getStockQuantity() < qty) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getName());
        }
        product.setStockQuantity(product.getStockQuantity() - qty);
        repo.save(product);
    }

    @Transactional
    public void incrementStock(Long productId, int qty) {
        var opt = repo.findByIdForUpdate(productId);
        var product = opt.orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setStockQuantity(product.getStockQuantity() + qty);
        repo.save(product);
    }
}
