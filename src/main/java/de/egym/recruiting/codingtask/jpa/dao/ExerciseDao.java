package de.egym.recruiting.codingtask.jpa.dao;

import de.egym.recruiting.codingtask.jpa.domain.Enums;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import java.util.Date;

public interface ExerciseDao extends BaseDao<Exercise> {

	/**
	 * Returns a list of exercises with the given description
	 *
	 * @param description
	 *            of the exercise
	 * @return filters list of exercise
	 */
	@Nonnull
	List<Exercise> findByDescription(@Nullable String description);
        
        /**
         * Returns a list of exercises filtered by user id, type, and date
         * @param userId
         *              the user's id
         * @param type
         *              the type of exercises (optional)
         * @param date
         *              the date of the exercises (optional)
         * @return filtered list of exercises
         */
        @Nonnull
        List<Exercise> findByUserAndTypeAndDate(@Nonnull Long userId, @Nullable Enums.ExerciseType type, @Nullable Date date);
        
        /**
         * Returns an exercise for a user in the given time range
         * @param userId
         *              the user's id
         * @param startTime
         *              the start of the time period
         * @param endTime
         *              the end of the time period
         * @return the filtered exercise, if it exists
         */
        @Nullable
        Exercise findByUserIdAndTimeRange(@Nonnull Long userId, @Nonnull Date startTime, @Nonnull Date endTime);
        
        /**
         * Returns a list of exercises for the given users in the past month
         * @param userIds
         *              the user ids
         * @return filtered list of exercises
         */
        @Nonnull
        List<Exercise> findForLastMonth(@Nonnull List<Long> userIds);
}
