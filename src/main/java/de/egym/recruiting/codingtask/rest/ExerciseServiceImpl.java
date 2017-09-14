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
import java.util.Date;
import java.util.stream.Collectors;
import javax.validation.Validation;
import javax.validation.Validator;
import org.apache.commons.lang3.time.DateUtils;

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
    public Exercise updateExercise(@Nonnull final Exercise exercise) {
        log.debug("Update exercise.");
        validateExercise(exercise);
        Exercise oldExercise = findExercise(exercise.getId());
        if ((exercise.getUserId().equals(oldExercise.getUserId())) && (exercise.getType().equals(oldExercise.getType()))) {
            return exerciseDao.update(exercise);
        } else {
            throw new IllegalArgumentException("'userId' and 'type' cannot change in an update.");
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
        @Nonnull final Exercise exercise
    ) throws AlreadyExistsException {
        log.debug("Create exercise.");
        validateExercise(exercise);
        Date endTime = DateUtils.addSeconds(exercise.getStartTime(), exercise.getDuration());
        if (exerciseDao.findByUserIdAndTimeRange(exercise.getUserId(), exercise.getStartTime(), endTime) == null) {
            return exerciseDao.create(exercise);
        } else {
            throw new AlreadyExistsException(exercise.getUserId(), exercise.getStartTime(), DateUtils.addSeconds(exercise.getStartTime(), exercise.getDuration()), exercise.getDescription());
        }
    }
    
    @Nonnull
    @Override
    public List<Exercise> getExercisesByUser (
        @Nonnull final Long userId,
        final Enums.ExerciseType type,
        final String date) throws ParseException {
        log.debug("Get exercises by user, type, and dates");
        Date parsedDate = (date != null) ?
                DateUtils.parseDate(date, "yyyy-MM-dd") :
                null;
        return exerciseDao.findByUserAndTypeAndDate(userId, type, parsedDate);
    }
    
    @Override
    public List<Long> getUserRankings(@Nonnull final List<Long> userIds) {
        log.debug("Get user rankings");
        return sortingService.sortUsersByActivity(exerciseDao.findForLastMonth(userIds));
    }
    
    @Nonnull
    @Override
    public void deleteExercise(@Nonnull final Long exerciseId) {
        log.debug("Delete exercise.");
        exerciseDao.deleteById(exerciseId);
    }
    
    private Exercise findExercise(final Long exerciseId) {
        final Exercise exercise = exerciseDao.findById(exerciseId);
        if (exercise == null) {
            throw new NotFoundException("Exercise with id = " + exerciseId + " could not be found.");
        }

        return exercise;
    }
    
    private void validateExercise(Exercise exercise) {
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
    }
}
