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
        
        @Nonnull
        List<Exercise> findByUserAndTypeAndDate(@Nonnull Long userId, @Nullable Enums.ExerciseType type, @Nullable Date date);
        
        @Nullable
        Exercise findByUserIdAndTimeRange(@Nonnull Long userId, @Nonnull Date startTime, @Nonnull Date endTime);
        
        @Nonnull
        List<Exercise> findForLastMonth(@Nonnull List<Long> userIds);
}
