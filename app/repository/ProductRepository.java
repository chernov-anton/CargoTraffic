package repository;

import models.MeasureUnit;
import models.Product;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Olga on 08.02.2016.
 */
public class ProductRepository {

    public Product save(Product product){
        EntityManager em = JPA.em();
        List<MeasureUnit> units = getMeasureUnit(product.measureUnit.name);
        if(!units.isEmpty()) product.measureUnit = units.get(0);
        else {
            em.persist(product.measureUnit);
            em.flush();
            em.refresh(product.measureUnit);
        }
        em.persist(product);
        em.flush();
        em.refresh(product);
        return product;
    }

    public List<MeasureUnit> getMeasureUnit(String name){
        EntityManager em = JPA.em();
        StringBuilder stringBuilder = new StringBuilder("SELECT u FROM MeasureUnit u WHERE u.name = ?");
        Query query = em.createQuery(stringBuilder.toString());
        query.setParameter(1, name);
        return query.getResultList();
    }

}
