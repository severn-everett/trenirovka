package de.egym.recruiting.codingtask.rest;

import java.util.List;

import javax.annotation.Nonnull;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.egym.recruiting.codingtask.exceptions.AlreadyExistsException;

import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Enums;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.sort.SortingService;
import java.text.ParseException;
import java.util.stream.Collectors;
import javax.validation.Validation;
import javax.validation.Validator;

@Singleton
public class ExerciseServiceImpl implements ExerciseService {

    private static final Logger log = LoggerFactory.getLogger(ExerciseServiceImpl.class);
    
    private final ExerciseDao exerciseDao;
    
    private final SortingService sortingService;
    
    private final Validator validator;

    @Inject
    ExerciseServiceImpl(final ExerciseDao exerciseDao, final SortingService sortingService) {
        this.exerciseDao = exerciseDao;
        this.sortingService = sortingService;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nonnull
    @Override
    public Exercise getExerciseById(@Nonnull final Long exerciseId) {
        log.debug("Get exercise by id.");
        return findExercise(exerciseId);
    }

    @Override
    public Exercise updateExercise(
            @Nonnull Long exerciseId,
            @Nonnull Long userId,
            @Nonnull String description,
            @Nonnull String type,
            @Nonnull String startTime,
            @Nonnull Integer duration,
            @Nonnull Integer distance,
            @Nonnull Integer calories
        ) {
        log.debug("Update exercise.");
        validateNotNull(userId, description, type, startTime, duration, distance, calories);
        Enums.ExerciseType exerciseType = Enums.ExerciseType.valueOf(type);
        final Exercise exercise = findExercise(exerciseId);
        if ((exercise.getUserId().equals(userId)) && (exercise.getType().equals(exerciseType))) {
            populateExercise(exercise, userId, description, exerciseType, startTime, duration, distance, calories);
            return exerciseDao.update(exercise);
        } else {
            throw new IllegalArgumentException("'userId' and 'type' cannot change in an update");
        }
    }

    @Nonnull
    @Override
    public List<Exercise> getExerciseByDescription(@Nonnull final String description) {
        log.debug("Get exercise by description.");

        return exerciseDao.findByDescription(description);
    }

    @Nonnull
    @Override
    public Exercise createExercise(
        @Nonnull final Long userId,
        @Nonnull final String description,
        @Nonnull final String type,
        @Nonnull final String startTime,
        @Nonnull final Integer duration,
        @Nonnull final Integer distance,
        @Nonnull final Integer calories
    ) throws AlreadyExistsException {
        log.debug("Create exercise.");
        validateNotNull(userId, description, type, startTime, duration, distance, calories);
        Exercise newExercise = new Exercise();
        populateExercise(newExercise, userId, description, Enums.ExerciseType.valueOf(type), startTime, duration, distance, calories);
        if (exerciseDao.findByUserIdAndTimeRange(userId, newExercise.getStartTime(), newExercise.getEndTime()) == null) {
            return exerciseDao.create(newExercise);
        } else {
            throw new AlreadyExistsException(userId, newExercise.getStartTime(), newExercise.getEndTime(), newExercise.getDescription());
        }
    }
    
    @Nonnull
    @Override
    public List<Exercise> getExercisesByUser(
        @Nonnull final Long userId,
        final String type,
        final String startTime,
        final String endTime) {
        log.debug("Get exercises by user, type, and dates");
        return exerciseDao.findByUserAndTypeAndDates(userId, type, startTime, endTime);
    }
    
    @Override
    public List<Long> getUserRankings() {
        return sortingService.sortUsersByActivity(exerciseDao.findForLastMonth());
    }
    
    @Nonnull
    @Override
    public void deleteExercise(@Nonnull final Long exerciseId) {
        log.debug("Delete exercise.");
        exerciseDao.deleteById(exerciseId);
    }
    
    private void validateNotNull (
        Long userId,
        String description,
        String type,
        String startTime,
        Integer duration,
        Integer distance,
        Integer calories
    ) {
        if ((userId == null) ||
            (description == null) ||
            (type == null) ||
            (startTime == null) ||
            (duration == null) ||
            (distance == null) ||
            (calories == null)) {
            log.error("At least one provided Exercise parameter is null.");
            throw new IllegalArgumentException("'userId', 'description', 'type', 'startTime', 'duration', 'distance', and 'calories' must not be null.");
        }
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
        Enums.ExerciseType type,
        String startTime,
        Integer duration,
        Integer distance,
        Integer calories
    ){
        try {
            exercise.setUserId(userId);
            exercise.setDescription(description);
            exercise.setType(type);
            exercise.setStartTime(Exercise.parseDate(startTime));
            exercise.setDuration(duration);
            exercise.setEndTime(Exercise.calculateEndTime(exercise.getStartTime(), duration));
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
    }
}
