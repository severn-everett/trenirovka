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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
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
    public List<Exercise> findByUserAndTypeAndDate(Long userId, Enums.ExerciseType type, Date date) {
        StringBuilder queryBuilder = new StringBuilder("SELECT e FROM Exercise e WHERE e.userId = :userId");
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", userId);
        if (type != null) {
            queryBuilder.append(" AND e.type = :parsedType");
            paramsMap.put("parsedType", type);
        }
        final Query query = getEntityManager().createQuery(queryBuilder.toString());
        paramsMap.entrySet().forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
        try {
            List<Exercise> resultsList = query.getResultList();
            if (date != null) {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                return resultsList.stream().filter(exercise -> {
                    return fmt.format(date).equals(fmt.format(exercise.getStartTime()));
                }).collect(Collectors.toList());
            } else {
                return resultsList;
            }
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Nullable
    @Override
    public Exercise findByUserIdAndTimeRange(Long userId, Date startTime, Date endTime) {
        try {
            List<Exercise> exercises = getEntityManager()
                    .createQuery(
                            "SELECT e FROM Exercise e WHERE e.userId = :userId")
                    .setParameter("userId", userId)
                    .getResultList();
            return exercises.stream().filter(exercise -> {
                Date exerciseEndTime = DateUtils.addSeconds(exercise.getStartTime(), exercise.getDuration());
                return ((exercise.getStartTime().compareTo(endTime) <= 0) && (exerciseEndTime.compareTo(startTime) >= 0));
            }).findFirst().orElse(null);
        } catch (NoResultException e) {
            return null;
        }
    }
        
    @Nonnull
    @Override
    public List<Exercise> findForLastMonth(List<Long> userIds) {
        Date now = new Date();
        Date monthAgo = DateUtils.addWeeks(now, -4);
        if (!userIds.isEmpty()) {
            try {
                return getEntityManager()
                        .createQuery("SELECT e FROM Exercise e WHERE e.userId IN :userIds AND e.startTime BETWEEN :startTime AND :endTime ORDER BY e.startTime")
                        .setParameter("userIds", userIds)
                        .setParameter("startTime", monthAgo)
                        .setParameter("endTime", now)
                        .getResultList();
            } catch (NoResultException e) {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }
}
