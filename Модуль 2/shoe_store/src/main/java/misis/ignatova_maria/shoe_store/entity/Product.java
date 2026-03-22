package misis.ignatova_maria.shoe_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @Column(name = "product_article", length = 50)
    private String article;

    @Column(name = "product_name", nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "current_discount")
    private BigDecimal discount;  // изменено с Double на BigDecimal

    @Column(name = "quantity_in_stock")
    private Integer quantity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "photo", length = 255)
    private String photo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return article != null && article.equals(product.article);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(article);
    }
}