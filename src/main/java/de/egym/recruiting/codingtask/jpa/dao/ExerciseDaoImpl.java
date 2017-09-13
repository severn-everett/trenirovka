package de.egym.recruiting.codingtask.jpa.dao;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import de.egym.recruiting.codingtask.jpa.domain.Enums;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Query;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional
public class ExerciseDaoImpl extends AbstractBaseDao<Exercise>implements ExerciseDao {
    
    private static final Logger log = LoggerFactory.getLogger(ExerciseDaoImpl.class);

	@Inject
	ExerciseDaoImpl(final Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider, Exercise.class);
	}

	@Nonnull
	@Override
	public List<Exercise> findByDescription(@Nullable String description) {
		if (description == null) {
			return Collections.emptyList();
		}

		description = description.toLowerCase();

		try {
			return getEntityManager()
					.createQuery("SELECT e FROM Exercise e WHERE LOWER(e.description) = :description")
					.setParameter("description", description)
					.getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
	}   

    @Override
    public List<Exercise> findByUserAndTypeAndDates(Long userId, String type, String startTime, String endTime) {
        StringBuilder queryBuilder = new StringBuilder("SELECT e FROM Exercise e WHERE e.userId = :userId");
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", userId);
        if (type != null) {
            queryBuilder.append(" AND e.type = :parsedType");
            paramsMap.put("parsedType", Enums.ExerciseType.valueOf(type));
        }
        if ((startTime != null) && (endTime != null)) {
            queryBuilder.append(" AND (e.startTime BETWEEN :startTime AND :endTime) OR (e.endTime BETWEEN :startTime AND :endTime)");
            try {
                paramsMap.put("startTime", Exercise.parseDate(startTime));
                paramsMap.put("endTime", Exercise.parseDate(endTime));
            } catch (ParseException ex) {
                log.error("Invalid StartTime format provided.");
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }
        final Query query = getEntityManager().createQuery(queryBuilder.toString());
        paramsMap.entrySet().forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Nullable
    @Override
    public Exercise findByUserIdAndTimeRange(Long userId, Date startTime, Date endTime) {
        try {
            return (Exercise) getEntityManager()
                    .createQuery(
                            "SELECT e FROM Exercise e WHERE e.userId = :userId AND e.startTime <= :endTime "
                                    + "AND e.endTime >= :startTime")
                    .setParameter("userId", userId)
                    .setParameter("startTime", startTime)
                    .setParameter("endTime", endTime)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
        
    @Nonnull
    @Override
    public List<Exercise> findForLastMonth() {
        Date now = new Date();
        Date monthAgo = DateUtils.addWeeks(now, -4);
        try {
            return getEntityManager()
                    .createQuery("SELECT e FROM Exercise e WHERE e.startTime BETWEEN :startTime AND :endTime ORDER BY e.startTime")
                    .setParameter("startTime", monthAgo)
                    .setParameter("endTime", now)
                    .getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }
}
