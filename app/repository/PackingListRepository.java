package repository;

import models.*;
import org.apache.commons.collections4.CollectionUtils;
import play.Logger;
import play.db.jpa.JPA;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Olga on 07.02.2016.
 */
public class PackingListRepository {
    private static final Logger.ALogger LOGGER = Logger.of(PackingListRepository.class);


    public PackingList savePackingList(PackingList packingList) {
        EntityManager em = JPA.em();
        em.persist(packingList);
        em.flush();
        em.refresh(packingList);
        return packingList;
    }

    public ProductInPackingList saveProductInPackingList( ProductInPackingList packingList) {
        EntityManager em = JPA.em();
        em.persist(packingList);
        em.flush();
        em.refresh(packingList);
        return packingList;
    }


    public List<PackingList> getPackingLists(long id, int count, boolean ascOrder, Long companyId) {
        LOGGER.debug("Get packingLists: {}, {}, {}", id, count, ascOrder);
        EntityManager em = JPA.em();
        StringBuilder stringBuilder = new StringBuilder("SELECT pl FROM PackingList pl WHERE pl.status = 'CREATED' AND ");
        if (ascOrder) {
            stringBuilder.append("pl.id >= ? AND pl.dispatcher.company.id = ? ORDER BY pl.id ASC");
        } else {
            stringBuilder.append("pl.id < ? AND pl.dispatcher.company.id = ? ORDER BY pl.id DESC");
        }
        Query query = em.createQuery(stringBuilder.toString());
        query.setParameter(1, id);
        query.setParameter(2, companyId);
        query.setMaxResults(count);
        List<PackingList> packingLists = query.getResultList();
        if (CollectionUtils.isEmpty(packingLists))
            return new ArrayList<>();
        if (!ascOrder)
            Collections.reverse(packingLists);
        return packingLists;
    }
}