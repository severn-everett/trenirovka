package de.egym.recruiting.codingtask.rest;

import java.util.List;

import javax.annotation.Nonnull;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;

@Singleton
public class ExerciseServiceImpl implements ExerciseService {

	private static final Logger log = LoggerFactory.getLogger(ExerciseServiceImpl.class);

	private final ExerciseDao exerciseDao;

	@Inject
	ExerciseServiceImpl(final ExerciseDao exerciseDao) {
		this.exerciseDao = exerciseDao;
	}

	@Nonnull
	@Override
	public Exercise getExerciseById(@Nonnull final Long exerciseId) {
		log.debug("Get exercise by id.");

		final Exercise exercise = exerciseDao.findById(exerciseId);
		if (exercise == null) {
			throw new NotFoundException("Exercise with id = " + exerciseId + " could not be found.");
		}

		return exercise;
	}

	@Nonnull
	@Override
	public List<Exercise> getExerciseByDescription(@Nonnull final String description) {
		log.debug("Get exercise by description.");

		return exerciseDao.findByDescription(description);
	}
}
