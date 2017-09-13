package de.egym.recruiting.codingtask.rest;

import java.util.List;

import javax.annotation.Nonnull;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Enums;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import java.text.ParseException;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

@Singleton
public class ExerciseServiceImpl implements ExerciseService {

    private static final Logger log = LoggerFactory.getLogger(ExerciseServiceImpl.class);
    
    private final ExerciseDao exerciseDao;
    
    private final Validator validator;

    @Inject
    ExerciseServiceImpl(final ExerciseDao exerciseDao) {
        this.exerciseDao = exerciseDao;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nonnull
    @Override
    public Exercise getExerciseById(@Nonnull final Long exerciseId) {
        log.debug("Get exercise by id.");
        return findExercise(exerciseId);
    }

    @Override
    public Exercise updateExercise(Long exerciseId, Long userId, String description, String type, String startTime, Integer duration, Integer distance, Integer calories) {
        log.debug("Update exercise.");
        final Exercise exercise = findExercise(exerciseId);
        populateExercise(exercise, userId, description, type, startTime, duration, distance, calories);
        return exerciseDao.update(exercise);
    }

    @Nonnull
    @Override
    public List<Exercise> getExerciseByDescription(@Nonnull final String description) {
        log.debug("Get exercise by description.");

        return exerciseDao.findByDescription(description);
    }

    @Override
    public Exercise createExercise(
        @Nonnull final Long userId,
        @Nonnull final String description,
        @Nonnull final String type,
        @Nonnull final String startTime,
        @Nonnull final Integer duration,
        @Nonnull final Integer distance,
        @Nonnull final Integer calories
    ) {
        log.debug("Create exercise.");
        Exercise newExercise = new Exercise();
        populateExercise(newExercise, userId, description, type, startTime, duration, distance, calories);
        return exerciseDao.create(newExercise);
    }
    
    private Exercise findExercise(final Long exerciseId) {
        final Exercise exercise = exerciseDao.findById(exerciseId);
        if (exercise == null) {
            throw new NotFoundException("Exercise with id = " + exerciseId + " could not be found.");
        }

        return exercise;
    }
    
    private void populateExercise(
        Exercise exercise,
        Long userId,
        String description,
        String type,
        String startTime,
        Integer duration,
        Integer distance,
        Integer calories
    ){
        if ((userId != null) &&
            (description != null) &&
            (type != null) &&
            (startTime != null) &&
            (duration != null) &&
            (distance != null) &&
            (calories != null)) {
            try {
                exercise.setUserId(userId);
                exercise.setDescription(description);
                exercise.setType(Enums.ExerciseType.valueOf(type));
                exercise.setStartTime(Exercise.parseDate(startTime));
                exercise.setDuration(duration);
                exercise.setDistance(distance);
                exercise.setCalories(calories);
                String validationErrors = validator.validate(exercise)
                        .stream()
                        .map(constraintViolation -> {
                            return String.format("'%s' value '%s' %s",
                                    constraintViolation.getPropertyPath(),
                                    constraintViolation.getInvalidValue(),
                                    constraintViolation.getMessage());
                         })
                        .collect(Collectors.joining("; "));
                if (!validationErrors.isEmpty()) {
                    log.error("Validation errors: " + validationErrors);
                    throw new IllegalArgumentException(validationErrors);
                }
            } catch (ParseException ex) {
                log.error("Invalid StartTime format provided.");
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        } else {
            log.error("At least one provided Exercise parameter is null.");
            throw new IllegalArgumentException("'userId', 'description', 'type', 'startTime', 'duration', 'distance', and 'calories' must not be null.");
        }
    }
}
