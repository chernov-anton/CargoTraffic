package repository;

import models.User;
import models.UserRole;
import org.apache.commons.collections4.CollectionUtils;
import play.Logger;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Anton Chernov on 1/21/2016.
 */
public class UserRepository {
    private static final Logger.ALogger LOGGER = Logger.of(UserRepository.class);

    public User find(long id) {
        EntityManager em = JPA.em();
        return em.find(User.class, id);
    }

    public User findByUsername(String name) {
        EntityManager em = JPA.em();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> u = query.from(User.class);
        query.select(u).where(builder.equal(u.get("username"), name));
        TypedQuery<User> q = em.createQuery(query);
        List<User> userList = q.getResultList();
        if (CollectionUtils.isNotEmpty(userList)) return userList.get(0);
        return null;
    }

    public List<User> findAll() {
        EntityManager em = JPA.em();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> from = criteriaQuery.from(User.class);

        CriteriaQuery<User> select = criteriaQuery.select(from);
        TypedQuery<User> q = em.createQuery(select);

        return q.getResultList();
    }

    public void update(User user) {
        EntityManager em = JPA.em();
        em.merge(user);
    }

    public List<User> getList(long companyId, long id, int count, boolean ascOrder) {
        EntityManager em = JPA.em();
        StringBuilder stringBuilder = new StringBuilder("SELECT u FROM User u WHERE u.company.id = ? AND u.deleted = false AND ");
        if (ascOrder) {
            stringBuilder.append("u.id >= ? ORDER BY u.id ASC");
        } else {
            stringBuilder.append("u.id < ? ORDER BY u.id DESC");
        }
        Query query = em.createQuery(stringBuilder.toString());
        query.setParameter(1, companyId);
        query.setParameter(2, id);
        query.setMaxResults(count);
        List<User> users = query.getResultList();
        if (!ascOrder)
            Collections.reverse(users);
        return users;
    }

    public List<UserRole> getRoleByName(String name){
        EntityManager em = JPA.em();
        StringBuilder stringBuilder = new StringBuilder("SELECT r FROM UserRole r WHERE r.name = ?");
        Query query = em.createQuery(stringBuilder.toString());
        query.setParameter(1, name);
        return query.getResultList();
    }

    public User addUser(User user) {
        EntityManager em = JPA.em();
        if(!Objects.isNull(user.userRoleList) && user.userRoleList.size() > 0){
            List<UserRole> roles = getRoleByName(user.userRoleList.get(0).name);
            user.setRoles(roles);
            em.persist(user);
            em.flush();
            em.refresh(user);
        }
        return user;
    }

    public void removeUser(List<Long> ids) {
        LOGGER.debug("Remove employees: {}", Arrays.toString(ids.toArray()));
        EntityManager em = JPA.em();
        StringBuilder stringBuilder = new StringBuilder("UPDATE User u SET u.deleted = true WHERE u.id in (");
        for (Long id : ids) {
            stringBuilder.append(id);
            stringBuilder.append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")");
        Query query = em.createQuery(stringBuilder.toString());
        query.executeUpdate();
    }

    public User getUser(long id) {
        EntityManager em = JPA.em();
        StringBuilder stringBuilder = new StringBuilder("SELECT u FROM User u WHERE u.id = ? AND u.deleted = false");
        Query query = em.createQuery(stringBuilder.toString());
        query.setParameter(1, id);
        List<User> users = query.getResultList();
        return (CollectionUtils.isNotEmpty(users)) ? users.get(0) : null;
    }

    public List<String> getPassword(Long id){
        EntityManager em = JPA.em();
        StringBuilder stringBuilder = new StringBuilder("SELECT password FROM User u WHERE u.id = ?");
        Query query = em.createQuery(stringBuilder.toString());
        query.setParameter(1, id);
        return query.getResultList();
    }
}
