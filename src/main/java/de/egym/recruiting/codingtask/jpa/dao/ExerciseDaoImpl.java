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
import de.egym.recruiting.codingtask.jpa.domain.Exercise;

@Transactional
public class ExerciseDaoImpl extends AbstractBaseDao<Exercise>implements ExerciseDao {

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
}
